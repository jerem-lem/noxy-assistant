"""Noxy backend FastAPI application entry point."""
from datetime import datetime
from typing import Dict, List, Optional

from fastapi import Body, FastAPI, HTTPException
from pydantic import BaseModel, Field


class Device(BaseModel):
    """Represents a hardware device enrolled in Noxy."""

    id: str = Field(..., description="Unique device identifier")
    model: Optional[str] = Field(None, description="Device model name")
    platform: Optional[str] = Field(None, description="Operating platform (iOS, Android, etc.)")


class User(BaseModel):
    """Minimal user representation."""

    id: str = Field(..., description="Unique user identifier")
    username: Optional[str] = Field(None, description="Display name")
    device: Optional[Device] = Field(None, description="Primary device associated with the user")


class Mood(BaseModel):
    """Mood status shared by a user."""

    user_id: str = Field(..., description="Identifier of the user reporting mood")
    mood: str = Field(..., description="Current mood description")
    recorded_at: datetime = Field(default_factory=datetime.utcnow, description="When the mood was recorded")


class CoupleMessage(BaseModel):
    """Message exchanged between couple members."""

    sender_id: str = Field(..., description="Identifier of the sender")
    receiver_id: str = Field(..., description="Identifier of the receiver")
    message: str = Field(..., description="Message content")
    sent_at: datetime = Field(default_factory=datetime.utcnow, description="Timestamp for when the message was sent")


class GuardianCommand(BaseModel):
    """Command pushed to a guardian-managed device."""

    device_id: str = Field(..., description="Target device identifier")
    command: str = Field(..., description="Command payload or type")
    created_at: datetime = Field(default_factory=datetime.utcnow, description="When the command was created")


class Location(BaseModel):
    """Location payload for guardian tracking."""

    user_id: str = Field(..., description="Identifier of the user")
    latitude: float = Field(..., description="Latitude value")
    longitude: float = Field(..., description="Longitude value")
    recorded_at: datetime = Field(default_factory=datetime.utcnow, description="When the location was captured")


app = FastAPI(title="Noxy Backend", version="1.0.0", description="Noxy backend FastAPI v1")

_guardian_commands: List[GuardianCommand] = []
_couple_messages: List[CoupleMessage] = []
_user_moods: Dict[str, Mood] = {}


@app.get("/")
async def root() -> Dict[str, str]:
    """Health endpoint for Render."""

    return {"message": "Noxy backend v1"}


@app.post("/noxy/web")
async def noxy_webhook(payload: Dict[str, object] = Body(..., description="Generic webhook payload")) -> Dict[str, object]:
    """Placeholder endpoint for Noxy webhook integrations."""

    return {"received": payload}


@app.post("/guardian/register")
async def register_guardian(user: User) -> Dict[str, User]:
    """Register a user/device for guardian features."""

    return {"registered": user}


@app.post("/guardian/push_command")
async def push_guardian_command(command: GuardianCommand) -> Dict[str, GuardianCommand]:
    """Store an incoming guardian command for later retrieval."""

    _guardian_commands.append(command)
    return {"queued": command}


@app.get("/guardian/commands", response_model=List[GuardianCommand])
async def list_guardian_commands() -> List[GuardianCommand]:
    """List queued guardian commands."""

    return _guardian_commands


@app.post("/guardian/location")
async def update_location(location: Location) -> Dict[str, Location]:
    """Receive location updates from guardian devices."""

    return {"location": location}


@app.post("/couple/send")
async def send_couple_message(message: CoupleMessage) -> Dict[str, CoupleMessage]:
    """Send a message between couple members."""

    _couple_messages.append(message)
    return {"sent": message}


@app.get("/couple/last_message")
async def last_couple_message() -> Dict[str, CoupleMessage]:
    """Retrieve the last message exchanged by the couple."""

    if not _couple_messages:
        raise HTTPException(status_code=404, detail="No messages available")
    return {"last_message": _couple_messages[-1]}


@app.post("/user/set_mood")
async def set_user_mood(mood: Mood) -> Dict[str, Mood]:
    """Save the current mood for a user."""

    _user_moods[mood.user_id] = mood
    return {"mood": mood}


@app.get("/user/mood")
async def get_user_mood(user_id: str) -> Dict[str, Mood]:
    """Get the latest mood for a user."""

    if user_id not in _user_moods:
        raise HTTPException(status_code=404, detail="Mood not set for user")
    return {"mood": _user_moods[user_id]}


# Render start command: uvicorn main:app --host 0.0.0.0 --port $PORT
