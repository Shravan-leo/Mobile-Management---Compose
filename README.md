# Mobile Models Management — Complete Compose starter

## What's included
- Kotlin + Jetpack Compose + Material 3 UI matching the five-screen mockup
- MVVM + StateFlow ViewModel
- Clean Architecture domain repository contract
- Retrofit + OkHttp API client
- Hilt dependency injection
- Model listing, adding, deleting
- PDF upload (multipart) and download
- Generated report download
- Android document picker for selecting and saving PDFs
- Loading, success, and error UI states
- Setup and API contract notes

## Open in Android Studio
1. Extract the ZIP.
2. Open the `MobileModelsCompose_Complete` folder (the one containing `settings.gradle.kts`).
3. Use JDK 17.
4. Allow Gradle sync.
5. Start the FastAPI server.
6. Check `app/src/main/java/com/example/mobilemodels/di/NetworkModule.kt`.
7. Run on an Android emulator.

## Configure the API URL
Default is:
`http://10.0.2.2:8000/`

This works when the Android emulator reaches FastAPI on the development computer. For a physical device, use the computer's LAN IP. For a tunnel, use the HTTPS origin and keep the trailing slash. Retrofit route annotations already include `api/...`, so don't put `/api` in the base URL.

The manifest allows cleartext HTTP for local development only. For production use HTTPS and disable cleartext.

## Backend endpoints
- `GET /api/models`
- `POST /api/model`
- `DELETE /api/model/{model_id}/delete`
- `GET /api/reports/pdf`
- `POST /api/models/{model_id}/pdf` multipart field `file`
- `GET /api/models/{model_id}/pdf`

## Backend response assumptions
The `GET /api/models` route should return objects:
`[{"id":101,"name":"OPPO","price":15000,"device_names":[]}]`
The `device_names` field is optional. It is not in the Pydantic model shown in the supplied backend code. The UI cannot display associated device models unless the API returns them.

The report endpoint must return actual PDF bytes with `Content-Type: application/pdf`. If `download_pdf()` returns a path or JSON, update the backend to return a FastAPI `Response`.

## Before production
- Enforce PDF size limits server-side too.
- Add authentication/authorization if the API is exposed to users.
- Use HTTPS, avoid exposing database credentials to the client, and don't log sensitive payloads.
- Add tests for ViewModel, repository, and Compose UI.
- Verify dependency compatibility with your installed Android Studio and Gradle versions.

## Note
This is a complete starter implementation of the main app flows, but it has not been compiled against your machine's Android SDK or tested against a live instance of your backend. Sync/build and adjust any version/API contract differences before treating it as production-ready.
