from __future__ import annotations

from typing import Dict, List

from fastapi import FastAPI, HTTPException

from backend.models import NoxyWebRequest, NoxyWebResponse, PersonalityProfile
from backend.services.openai_client import generate_noxy_reply

app = FastAPI(title="Noxy Assistant API")

# Placeholder in-memory data. In a real setup, this would come from a data store.
PERSONALITY_BY_USER: Dict[str, PersonalityProfile] = {
    "default": PersonalityProfile(
        style="chaleureux et concret",
        tone="positif",
        traits=["bienveillant", "clair", "orienté action"],
    )
}

MEMORY_BY_USER: Dict[str, List[str]] = {}
MEMORY_MAX_ITEMS = 10


@app.post("/noxy/web", response_model=NoxyWebResponse)
async def post_noxy_web(request: NoxyWebRequest) -> NoxyWebResponse:
    personality = PERSONALITY_BY_USER.get(request.user_id) or PERSONALITY_BY_USER["default"]

    user_memory = MEMORY_BY_USER.get(request.user_id, [])
    memory_context = "\n".join(user_memory[-MEMORY_MAX_ITEMS:])

    if not request.message:
        raise HTTPException(status_code=400, detail="Le message utilisateur est requis.")

    reply = await generate_noxy_reply(
        user_id=request.user_id,
        message=request.message,
        memory_context=memory_context,
        personality=personality,
        mood=request.mood,
        mode=request.mode,
    )

    return NoxyWebResponse(
        reply=reply,
        used_memory_summary=memory_context or "(aucune mémoire disponible)",
        personality_style=personality.style,
    )
