from typing import Any, Dict


class BaseModel:
    def __init__(self, **data: Any):
        annotations = getattr(self, "__annotations__", {})
        for key in annotations:
            setattr(self, key, data.get(key))
        # store any extra
        for key, value in data.items():
            if not hasattr(self, key):
                setattr(self, key, value)

    def dict(self) -> Dict[str, Any]:
        annotations = getattr(self, "__annotations__", {})
        result: Dict[str, Any] = {}
        for key in annotations:
            result[key] = getattr(self, key)
        return result
