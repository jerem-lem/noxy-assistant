from typing import Any, Dict, Optional
from fastapi import FastAPI, HTTPException, _SimpleResponse
from pydantic import BaseModel


def _serialize(data: Any) -> Any:
    if isinstance(data, BaseModel):
        return data.dict()
    if isinstance(data, list):
        return [_serialize(item) for item in data]
    if isinstance(data, dict):
        return {key: _serialize(value) for key, value in data.items()}
    return data


class TestClient:
    __test__ = False
    def __init__(self, app: FastAPI):
        self.app = app

    def _request(self, method: str, path: str, params: Optional[Dict[str, Any]] = None, json: Any = None):
        try:
            result = self.app._execute(method, path, params=params, body=json)
            return _SimpleResponse(status_code=200, data=_serialize(result))
        except HTTPException as exc:  # type: ignore
            return _SimpleResponse(status_code=exc.status_code, data={"detail": exc.detail})

    def get(self, path: str, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", path, params=params)

    def post(self, path: str, json: Any = None):
        return self._request("POST", path, json=json)
