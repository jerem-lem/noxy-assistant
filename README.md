# Noxy Guardian Prototype

This repository contains a minimal backend implementing remote Guardian control using a lightweight FastAPI-like shim bundled in the repository. No external dependencies are required to run the included tests.

## Running logic locally

You can exercise the endpoints via the built-in test client or by importing `main.app` and calling the route handlers directly. The `fastapi` and `pydantic` packages provided here are small stand-ins to make the API shape match the intended implementation while staying offline-friendly.

## API overview

- `POST /guardian/register_device` – register a device for a user.
- `GET /guardian/devices?user_id=...` – list devices for a user.
- `POST /guardian/add_trusted_contact` – add or replace a trusted contact with allowed commands.
- `GET /guardian/trusted_contacts?owner_user_id=...` – list trusted contacts for a user.
- `POST /guardian/push_command` – queue a command for a target device with authorization checks.
- `GET /guardian/commands?device_id=...` – fetch and drain queued commands for a device.

Authorization rules:
- A user can target their own devices freely.
- A trusted contact can only send commands listed in their `allowed_commands` entry.

Allowed commands are limited to `SAFE_MODE`, `ALARM`, `SEND_LOCATION`, and `LOCK_NOXY`.
