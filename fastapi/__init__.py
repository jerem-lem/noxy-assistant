import inspect
from typing import Any, Callable, Dict, Optional

from pydantic import BaseModel


class HTTPException(Exception):
    def __init__(self, status_code: int, detail: str):
        self.status_code = status_code
        self.detail = detail
        super().__init__(detail)


def Query(default: Any = None, alias: Optional[str] = None):
    return default


class FastAPI:
    def __init__(self, title: str = ""):
        self.title = title
        self.routes: Dict[str, Dict[str, Callable[..., Any]]] = {"GET": {}, "POST": {}}

    def get(self, path: str):
        def decorator(func: Callable[..., Any]):
            self.routes["GET"][path] = func
            return func

        return decorator

    def post(self, path: str):
        def decorator(func: Callable[..., Any]):
            self.routes["POST"][path] = func
            return func

        return decorator

    def _execute(
        self,
        method: str,
        path: str,
        params: Optional[Dict[str, Any]] = None,
        body: Any = None,
    ):
        if path not in self.routes.get(method, {}):
            raise HTTPException(status_code=404, detail="Route not found")

        func = self.routes[method][path]
        params = params or {}
        signature = inspect.signature(func)
        bound_values = signature.bind_partial(**params)

        if body is not None:
            param_name, param = next(iter(signature.parameters.items()))
            body_obj = body
            if isinstance(body, dict) and isinstance(param.annotation, type) and issubclass(
                param.annotation, BaseModel
            ):
                body_obj = param.annotation(**body)
            bound_values.arguments[param_name] = body_obj

        return func(*bound_values.args, **bound_values.kwargs)


class _SimpleResponse:
    def __init__(self, status_code: int, data: Any):
        self.status_code = status_code
        self._data = data

    def json(self):
        return self._data


from fastapi import testclient  # noqa: E402,F401  # type: ignore
