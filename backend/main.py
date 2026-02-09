import logging
import os
from typing import Dict, List, Optional

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from openai import OpenAI
from pydantic import BaseModel

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

app = FastAPI(title="Noxy Backend")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class MemoryItem(BaseModel):
    content: str


class PersonalityProfile(BaseModel):
    style: str
    tone: str
    traits: List[str]


class NoxyWebRequest(BaseModel):
    user_id: str
    device_id: str
    message: str
    mood: Optional[str] = None


class NoxyWebResponse(BaseModel):
    reply: str
    used_memory_summary: Optional[str] = None
    personality_style: Optional[str] = None
    sources: Optional[List[str]] = None


MEMORY_BY_USER: Dict[str, List[MemoryItem]] = {
    "demo-user": [
        MemoryItem(content="Aime les voyages interstellaires imaginaires."),
        MemoryItem(content="Préfère les réponses concises mais chaleureuses."),
    ]
}

PERSONALITY_BY_USER: Dict[str, PersonalityProfile] = {
    "demo-user": PersonalityProfile(
        style="cosmique et bienveillant",
        tone="chaleureux et clair",
        traits=["rêveur", "pragmatique", "optimiste"],
    )
}

DEFAULT_PROFILE = PersonalityProfile(
    style="futuriste et empathique",
    tone="calme et encourageant",
    traits=["attentif", "clair", "créatif"],
)


def get_openai_client() -> OpenAI:
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise RuntimeError("Missing OPENAI_API_KEY environment variable")
    return OpenAI(api_key=api_key)


def build_memory_context(memory: List[MemoryItem]) -> Optional[str]:
    if not memory:
        return None
    last_items = memory[-5:]
    return "\n".join(f"- {item.content}" for item in last_items)


def build_system_prompt(profile: PersonalityProfile, memory_context: Optional[str]) -> str:
    memory_details = memory_context or "(aucune mémoire pour l'instant)"
    traits_formatted = ", ".join(profile.traits)
    return (
        "Tu es Noxy, un assistant personnel dans l'univers Nexchat.\n"
        f"Style : {profile.style}\n"
        f"Ton : {profile.tone}\n"
        f"Traits : {traits_formatted}\n\n"
        "Tu parles de manière bienveillante, claire, parfois un peu cosmique/futuriste, "
        "mais toujours utile et concrète.\n"
        "Tu connais les informations suivantes sur l'utilisateur :\n"
        f"{memory_details}"
    )


@app.post("/noxy/web", response_model=NoxyWebResponse)
async def noxy_web(request: NoxyWebRequest) -> NoxyWebResponse:
    profile = PERSONALITY_BY_USER.get(request.user_id, DEFAULT_PROFILE)
    user_memory = MEMORY_BY_USER.get(request.user_id, [])
    memory_context = build_memory_context(user_memory)

    system_prompt = build_system_prompt(profile, memory_context)
    user_prompt = request.message
    if request.mood:
        user_prompt += f"\n(Humeur actuelle de l'utilisateur : {request.mood})"

    try:
        client = get_openai_client()
        completion = client.chat.completions.create(
            model=os.getenv("OPENAI_MODEL", "gpt-4o-mini"),
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
        )
        reply_text = completion.choices[0].message.content if completion.choices else ""
    except Exception as exc:  # broad but we log and return safe response
        logger.exception("Erreur lors de l'appel à OpenAI: %s", exc)
        reply_text = "Désolé, je ne peux pas répondre pour le moment."

    return NoxyWebResponse(
        reply=reply_text,
        used_memory_summary=memory_context,
        personality_style=profile.style,
        sources=None,
    )


@app.get("/health")
async def health() -> Dict[str, str]:
    return {"status": "ok"}
