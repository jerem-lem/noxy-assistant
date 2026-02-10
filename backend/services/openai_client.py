from __future__ import annotations

import os
from typing import Optional

from openai import AsyncOpenAI, OpenAIError

from backend.models import PersonalityProfile

GPT_DEFAULT_MODEL = "gpt-4.1-mini"
GPT_MODEL = os.getenv("OPENAI_MODEL", GPT_DEFAULT_MODEL)


def get_openai_client() -> AsyncOpenAI:
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise RuntimeError("OPENAI_API_KEY is not configured")
    return AsyncOpenAI(api_key=api_key)


async def generate_noxy_reply(
    user_id: str,
    message: str,
    memory_context: str,
    personality: PersonalityProfile,
    mood: Optional[str] = None,
    mode: Optional[str] = None,
) -> str:
    """
    Construit un prompt propre pour Noxy et appelle OpenAI pour générer une réponse.
    """

    system_prompt_lines = [
        "Tu es Noxy, un assistant personnel dans l’univers Nexchat.",
        f"Style : {personality.style}",
        f"Ton : {personality.tone}",
        f"Traits : {', '.join(personality.traits)}.",
        "Tu es bienveillant, clair, parfois un peu cosmique/futuriste, mais tu restes concret.",
        "Tu dois aider l’utilisateur sans inventer de faits techniques.",
        "Mémoire de l’utilisateur :",
        memory_context or "(aucune mémoire disponible)",
    ]

    if mode:
        mode = mode.upper()
        if mode == "GUARDIAN":
            system_prompt_lines.append(
                "Tu réponds en tant que module de sécurité, concis, axé sur la protection et les conseils pratiques."
            )
        elif mode == "COUPLE":
            system_prompt_lines.append(
                "Tu réponds de manière bienveillante sur la relation, sans te substituer à un professionnel."
            )
        elif mode == "DEV":
            system_prompt_lines.append("Tu es plus technique, tu aides à coder ou architecturer.")
        elif mode == "SCHOOL":
            system_prompt_lines.append("Tu expliques les notions simplement pour un étudiant.")

    user_message = f"Message de l'utilisateur : {message}\nHumeur actuelle : {mood or 'inconnue'}"

    try:
        client = get_openai_client()
        response = await client.chat.completions.create(
            model=GPT_MODEL,
            messages=[
                {"role": "system", "content": "\n".join(system_prompt_lines)},
                {"role": "user", "content": user_message},
            ],
            user=user_id,
        )
        return response.choices[0].message.content or ""
    except (OpenAIError, RuntimeError) as exc:  # pragma: no cover - defensive path
        return "Noxy a rencontré une erreur en générant la réponse. Merci de réessayer plus tard." + f" ({exc})"
