# Noxy Assistant

Prototype combinant un backend FastAPI et quelques artefacts Android pour l'endpoint `/noxy/web`.

## Backend
- `backend/main.py` : expose `/noxy/web` qui combine personnalité, mémoire et humeur de l'utilisateur, puis appelle OpenAI pour produire une réponse.
- Variables d'environnement :
  - `OPENAI_API_KEY` (obligatoire)
  - `OPENAI_MODEL` (optionnel, `gpt-4o-mini` par défaut)
- Dépendances : voir `backend/requirements.txt`. Lancer le serveur avec `uvicorn backend.main:app --reload`.

## Android (esquisse)
- Modèles de requête/réponse : `android/app/src/main/java/com/noxy/assistant/model/NoxyModels.kt`.
- API Retrofit : `android/app/src/main/java/com/noxy/assistant/api/NoxyApi.kt`.
- ViewModel + écran Compose simplifié : `android/app/src/main/java/com/noxy/assistant/viewmodel/NoxyChatViewModel.kt` et `android/app/src/main/java/com/noxy/assistant/ui/NoxyChatScreen.kt`.
