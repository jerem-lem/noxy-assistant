from typing import Any, Dict, List, Optional
from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel
from threading import Lock


class DeviceRegistration(BaseModel):
    user_id: str
    device_id: str
    device_name: Optional[str] = None


class GuardianCommandPushRequest(BaseModel):
    from_user_id: str
    to_user_id: str
    target_device_id: str
    type: str  # "SAFE_MODE", "ALARM", "SEND_LOCATION", "LOCK_NOXY"
    payload: Optional[Dict[str, Any]] = None


class TrustedContact(BaseModel):
    owner_user_id: str
    contact_user_id: str
    allowed_commands: List[str]


class GuardianCommand(BaseModel):
    from_user_id: str
    to_user_id: str
    device_id: str
    type: str
    payload: Optional[Dict[str, Any]] = None


ALLOWED_COMMANDS = {"SAFE_MODE", "ALARM", "SEND_LOCATION", "LOCK_NOXY"}

DEVICES_BY_USER: Dict[str, List[str]] = {}
DEVICE_METADATA: Dict[str, DeviceRegistration] = {}
TRUSTED_CONTACTS_BY_USER: Dict[str, List[TrustedContact]] = {}
COMMANDS_BY_DEVICE: Dict[str, List[GuardianCommand]] = {}

_lock = Lock()

app = FastAPI(title="Noxy Guardian Backend")


def _validate_command_type(command_type: str) -> None:
    if command_type not in ALLOWED_COMMANDS:
        raise HTTPException(status_code=400, detail="Unsupported command type")


def _get_trusted_contact(owner_user_id: str, contact_user_id: str) -> Optional[TrustedContact]:
    contacts = TRUSTED_CONTACTS_BY_USER.get(owner_user_id, [])
    for contact in contacts:
        if contact.contact_user_id == contact_user_id:
            return contact
    return None


@app.post("/guardian/register_device")
def register_device(registration: DeviceRegistration):
    with _lock:
        devices = DEVICES_BY_USER.setdefault(registration.user_id, [])
        if registration.device_id not in devices:
            devices.append(registration.device_id)
        DEVICE_METADATA[registration.device_id] = registration
    return {
        "status": "registered",
        "devices": [
            {
                "device_id": device_id,
                "device_name": DEVICE_METADATA.get(device_id).device_name
                if DEVICE_METADATA.get(device_id)
                else None,
            }
            for device_id in DEVICES_BY_USER.get(registration.user_id, [])
        ],
    }


@app.get("/guardian/devices")
def list_devices(user_id: str = Query(..., alias="user_id")):
    device_ids = DEVICES_BY_USER.get(user_id, [])
    return {
        "devices": [
            {
                "device_id": device_id,
                "device_name": DEVICE_METADATA.get(device_id).device_name
                if DEVICE_METADATA.get(device_id)
                else None,
            }
            for device_id in device_ids
        ]
    }


@app.post("/guardian/add_trusted_contact")
def add_trusted_contact(contact: TrustedContact):
    filtered_commands = [cmd for cmd in contact.allowed_commands if cmd in ALLOWED_COMMANDS]
    if not filtered_commands:
        raise HTTPException(status_code=400, detail="No valid allowed commands provided")
    clean_contact = TrustedContact(
        owner_user_id=contact.owner_user_id,
        contact_user_id=contact.contact_user_id,
        allowed_commands=filtered_commands,
    )
    with _lock:
        contacts = TRUSTED_CONTACTS_BY_USER.setdefault(contact.owner_user_id, [])
        TRUSTED_CONTACTS_BY_USER[contact.owner_user_id] = [
            c for c in contacts if c.contact_user_id != contact.contact_user_id
        ]
        TRUSTED_CONTACTS_BY_USER[contact.owner_user_id].append(clean_contact)
    return {"status": "saved", "trusted_contacts": TRUSTED_CONTACTS_BY_USER[contact.owner_user_id]}


@app.get("/guardian/trusted_contacts")
def list_trusted_contacts(owner_user_id: str = Query(..., alias="owner_user_id")):
    return {"trusted_contacts": TRUSTED_CONTACTS_BY_USER.get(owner_user_id, [])}


@app.post("/guardian/push_command")
def push_command(request: GuardianCommandPushRequest):
    _validate_command_type(request.type)

    with _lock:
        if request.target_device_id not in DEVICES_BY_USER.get(request.to_user_id, []):
            raise HTTPException(status_code=404, detail="Target device not registered for user")

        if request.from_user_id != request.to_user_id:
            trusted_contact = _get_trusted_contact(request.to_user_id, request.from_user_id)
            if not trusted_contact or request.type not in trusted_contact.allowed_commands:
                raise HTTPException(status_code=403, detail="Command not allowed for this contact")

        command = GuardianCommand(
            from_user_id=request.from_user_id,
            to_user_id=request.to_user_id,
            device_id=request.target_device_id,
            type=request.type,
            payload=request.payload,
        )
        COMMANDS_BY_DEVICE.setdefault(request.target_device_id, []).append(command)

    return {"status": "queued", "device_id": request.target_device_id}


@app.get("/guardian/commands")
def get_commands(device_id: str = Query(..., alias="device_id")):
    with _lock:
        commands = COMMANDS_BY_DEVICE.pop(device_id, [])
    return {"commands": commands}


@app.get("/health")
def health():
    return {"status": "ok"}
