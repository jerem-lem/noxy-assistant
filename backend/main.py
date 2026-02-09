from typing import Dict, List, Optional
from uuid import uuid4
import time

from fastapi import FastAPI
from pydantic import BaseModel, Field


class MemoryItem(BaseModel):
    id: str
    user_id: str
    content: str
    created_at: float


class MemoryCreateRequest(BaseModel):
    user_id: str
    content: str


class PersonalityProfile(BaseModel):
    user_id: str
    style: str
    tone: str
    traits: List[str] = Field(default_factory=list)


class PersonalityUpdateRequest(BaseModel):
    user_id: str
    style: Optional[str] = None
    tone: Optional[str] = None
    traits: Optional[List[str]] = None


class SummaryResponse(BaseModel):
    summary: str


app = FastAPI()

MEMORY_BY_USER: Dict[str, List[MemoryItem]] = {}
PERSONALITY_BY_USER: Dict[str, PersonalityProfile] = {}


def get_default_personality(user_id: str) -> PersonalityProfile:
    return PersonalityProfile(
        user_id=user_id,
        style="cosmic_friendly",
        tone="calm_supportive",
        traits=["helpful", "curious"],
    )


@app.post("/memory/add", response_model=MemoryItem)
def add_memory(req: MemoryCreateRequest) -> MemoryItem:
    item = MemoryItem(
        id=str(uuid4()),
        user_id=req.user_id,
        content=req.content,
        created_at=time.time(),
    )
    MEMORY_BY_USER.setdefault(req.user_id, []).append(item)
    return item


@app.get("/memory/list", response_model=List[MemoryItem])
def list_memory(user_id: str) -> List[MemoryItem]:
    return MEMORY_BY_USER.get(user_id, [])


@app.get("/memory/summary", response_model=SummaryResponse)
def memory_summary(user_id: str) -> SummaryResponse:
    items = MEMORY_BY_USER.get(user_id, [])
    summary = "\n".join(item.content for item in items[-5:])
    return SummaryResponse(summary=summary)


@app.get("/personality/get", response_model=PersonalityProfile)
def get_personality(user_id: str) -> PersonalityProfile:
    if user_id not in PERSONALITY_BY_USER:
        PERSONALITY_BY_USER[user_id] = get_default_personality(user_id)
    return PERSONALITY_BY_USER[user_id]


@app.post("/personality/update", response_model=PersonalityProfile)
def update_personality(req: PersonalityUpdateRequest) -> PersonalityProfile:
    profile = PERSONALITY_BY_USER.get(req.user_id) or get_default_personality(req.user_id)

    if req.style is not None:
        profile.style = req.style
    if req.tone is not None:
        profile.tone = req.tone
    if req.traits is not None:
        profile.traits = req.traits

    PERSONALITY_BY_USER[req.user_id] = profile
    return profile


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
