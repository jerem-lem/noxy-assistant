import sys
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parent.parent))

from fastapi.testclient import TestClient

from main import (
    ALLOWED_COMMANDS,
    COMMANDS_BY_DEVICE,
    DEVICES_BY_USER,
    TRUSTED_CONTACTS_BY_USER,
    app,
)

client = TestClient(app)


def setup_function():
    DEVICES_BY_USER.clear()
    TRUSTED_CONTACTS_BY_USER.clear()
    COMMANDS_BY_DEVICE.clear()


def test_register_device_and_list_devices():
    response = client.post(
        "/guardian/register_device",
        json={"user_id": "user1", "device_id": "deviceA", "device_name": "Phone"},
    )
    assert response.status_code == 200
    assert response.json()["status"] == "registered"

    devices_resp = client.get("/guardian/devices", params={"user_id": "user1"})
    assert devices_resp.status_code == 200
    assert devices_resp.json()["devices"][0]["device_id"] == "deviceA"


def test_add_trusted_contact_and_push_command_authorized():
    client.post(
        "/guardian/register_device",
        json={"user_id": "userB", "device_id": "deviceB"},
    )
    add_resp = client.post(
        "/guardian/add_trusted_contact",
        json={
            "owner_user_id": "userB",
            "contact_user_id": "userA",
            "allowed_commands": ["SAFE_MODE", "ALARM"],
        },
    )
    assert add_resp.status_code == 200

    push_resp = client.post(
        "/guardian/push_command",
        json={
            "from_user_id": "userA",
            "to_user_id": "userB",
            "target_device_id": "deviceB",
            "type": "SAFE_MODE",
        },
    )
    assert push_resp.status_code == 200
    assert COMMANDS_BY_DEVICE["deviceB"][0].type == "SAFE_MODE"


def test_push_command_rejected_for_unauthorized_contact():
    client.post(
        "/guardian/register_device",
        json={"user_id": "owner", "device_id": "deviceX"},
    )

    push_resp = client.post(
        "/guardian/push_command",
        json={
            "from_user_id": "intruder",
            "to_user_id": "owner",
            "target_device_id": "deviceX",
            "type": "ALARM",
        },
    )
    assert push_resp.status_code == 403


def test_get_commands_drains_queue():
    client.post(
        "/guardian/register_device",
        json={"user_id": "userC", "device_id": "deviceC"},
    )
    client.post(
        "/guardian/push_command",
        json={
            "from_user_id": "userC",
            "to_user_id": "userC",
            "target_device_id": "deviceC",
            "type": "ALARM",
        },
    )

    first_fetch = client.get("/guardian/commands", params={"device_id": "deviceC"})
    assert first_fetch.status_code == 200
    assert len(first_fetch.json()["commands"]) == 1

    second_fetch = client.get("/guardian/commands", params={"device_id": "deviceC"})
    assert second_fetch.status_code == 200
    assert second_fetch.json()["commands"] == []


def test_invalid_command_type_rejected():
    client.post(
        "/guardian/register_device",
        json={"user_id": "userD", "device_id": "deviceD"},
    )

    resp = client.post(
        "/guardian/push_command",
        json={
            "from_user_id": "userD",
            "to_user_id": "userD",
            "target_device_id": "deviceD",
            "type": "UNKNOWN",  # not allowed
        },
    )
    assert resp.status_code == 400
    assert "Unsupported" in resp.json()["detail"]


def test_add_trusted_contact_filters_invalid_commands():
    resp = client.post(
        "/guardian/add_trusted_contact",
        json={
            "owner_user_id": "userE",
            "contact_user_id": "contactE",
            "allowed_commands": ["SAFE_MODE", "FAKE"],
        },
    )
    assert resp.status_code == 200
    contact = resp.json()["trusted_contacts"][0]
    assert "FAKE" not in contact["allowed_commands"]
    assert set(contact["allowed_commands"]).issubset(ALLOWED_COMMANDS)
