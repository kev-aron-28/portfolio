# API Reference

Base URL: `http://localhost:8080`

Content-Type: `application/json`

## Jobs

### `GET /jobs`

Lista todas las vacantes.

**Response `200`:**

```json
[
  {
    "id": 1,
    "title": "Java Developer",
    "companyId": 1,
    "description": "...",
    "location": "Monterrey",
    "salaryMin": 50000,
    "salaryMax": 80000,
    "source": "occ",
    "url": "https://...",
    "createdAt": "2026-07-06T12:00:00Z"
  }
]
```

### `GET /jobs/{id}`

Obtiene una vacante por ID. Responde `404` si no existe.

### `POST /jobs`

Crea una vacante. Responde `201` con header `Location: /jobs/{id}`.

**Body:**

| Campo | Tipo | Requerido |
|-------|------|-----------|
| title | string | sí |
| companyName | string | sí |
| companyWebsite | string | no |
| description | string | no |
| location | string | no |
| salaryMin | number | no |
| salaryMax | number | no |
| source | string | sí |
| url | string | sí |

## Applications

### `POST /applications`

Registra una aplicación a una vacante.

| Campo | Tipo | Requerido |
|-------|------|-----------|
| jobId | number | sí |
| status | enum | no (default: `APPLIED`) |
| appliedAt | ISO-8601 | no (default: now) |
| notes | string | no |

### `PATCH /applications/{id}`

Actualización parcial de estado y/o notas.

| Campo | Tipo | Requerido |
|-------|------|-----------|
| status | enum | no |
| notes | string | no |

## Search Profiles

### `GET /profiles`

Lista perfiles de búsqueda.

### `POST /profiles`

| Campo | Tipo | Requerido |
|-------|------|-----------|
| name | string | sí |
| keywords | string | sí |
| filters | string (JSON) | no |

## Scraping

### `POST /scraping/run`

Ejecuta scraping manual.

| Campo | Tipo | Requerido |
|-------|------|-----------|
| profileId | number | no* |
| keywords | string | no* |
| location | string | no |
| platforms | string[] | no (default: `["occ","linkedin"]`) |
| maxResults | number | no (default: 20) |

\* Se requiere `profileId` o `keywords`.

**Response:**

```json
{
  "scraped": 10,
  "imported": 8,
  "duplicates": 2,
  "errors": []
}
```

## Schedules

### `GET /schedules`

Lista schedules de scraping automático.

### `POST /schedules`

| Campo | Tipo | Requerido |
|-------|------|-----------|
| profileId | number | sí |
| platforms | string[] | no |
| intervalMinutes | number | no (default: 60) |
| maxResults | number | no (default: 20) |

### `PATCH /schedules/{id}`

| Campo | Tipo | Requerido |
|-------|------|-----------|
| enabled | boolean | no |

### `POST /schedules/{id}/run`

Ejecuta un schedule inmediatamente.

### `POST /schedules/run-due`

Ejecuta todos los schedules vencidos.

## Web UI (HTML)

| Método | Ruta | Accept |
|--------|------|--------|
| GET | `/` | text/html |
| GET | `/jobs` | text/html |
| GET | `/jobs/{id}` | text/html |
| POST | `/jobs/{id}/apply` | form |

## Error responses

| Status | Cuándo |
|--------|--------|
| 400 | Validación fallida |
| 404 | Recurso no encontrado |

```json
{ "error": "Job not found: 99" }
```

## Enums

**ApplicationStatus:** `APPLIED`, `SCREENING`, `INTERVIEWING`, `OFFER`, `REJECTED`, `WITHDRAWN`

**Platforms:** `occ`, `linkedin`
