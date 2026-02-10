from __future__ import annotations

from typing import List, Optional

from pydantic import BaseModel, Field


class PersonalityProfile(BaseModel):
    style: str = Field(..., description="Brief description of the assistant's style")
    tone: str = Field(..., description="Typical tone of replies")
    traits: List[str] = Field(default_factory=list, description="Key personality traits")


class NoxyWebRequest(BaseModel):
    user_id: str
    message: str
    mood: Optional[str] = None
    mode: Optional[str] = None


class NoxyWebResponse(BaseModel):
    reply: str
    used_memory_summary: str
    personality_style: str
