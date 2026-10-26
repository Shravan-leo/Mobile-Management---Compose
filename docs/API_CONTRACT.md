# API contract

| Action | Method | Path | Request / response |
|---|---|---|---|
| List models | GET | `/api/models` | JSON array of objects |
| Add model | POST | `/api/model` | JSON `{id,name,price}`; HTTP 201 |
| Delete model | DELETE | `/api/model/{model_id}/delete` | ID in path |
| Generated report | GET | `/api/reports/pdf` | PDF bytes |
| Upload model PDF | POST | `/api/models/{model_id}/pdf` | multipart part `file` |
| Download model PDF | GET | `/api/models/{model_id}/pdf` | PDF bytes |

The older `/models` endpoints in the supplied FastAPI code use the in-memory `models` object and are not used by this app.
