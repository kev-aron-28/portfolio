# Job Tracker — Guía de la plataforma

Herramienta local-first para automatizar la búsqueda de empleo: scraping multi-plataforma, centralización de vacantes, seguimiento de aplicaciones y dashboard con métricas.

## Inicio rápido

```bash
# 1. Infraestructura
docker compose up -d postgres rabbitmq

# 2. Aplicación
cd job-tracker
./mvnw spring-boot:run
```

| Recurso | URL |
|---------|-----|
| Dashboard web | http://localhost:8080 |
| Listado de vacantes | http://localhost:8080/jobs |
| **Panel de scraping** | **http://localhost:8080/scraping** |
| API REST | http://localhost:8080 (JSON) |
| RabbitMQ Management | http://localhost:15672 (admin / admin) |

## Arquitectura

```
Presentation (Thymeleaf + REST)
        ↓
Application (Use Cases)
        ↓
Domain (modelos + puertos)
        ↓
Infrastructure (JPA, Flyway, Scraping, RabbitMQ)
```

Principios: Clean Architecture, dominio sin dependencias de framework, DTOs solo en la capa de presentación.

## Stack

| Capa | Tecnología |
|------|------------|
| Backend | Java 21, Spring Boot 4 |
| Base de datos | PostgreSQL 16, Flyway |
| UI | Thymeleaf, HTML5, CSS |
| Scraping | HttpClient + Jsoup (OCC), Playwright (LinkedIn) |
| Mensajería | RabbitMQ |
| Build | Maven |

## Interfaz web

| Ruta | Descripción |
|------|-------------|
| `GET /` | Dashboard: métricas, gráficos por fuente/estado, vacantes recientes |
| `GET /jobs` | Listado con filtros (keyword, source, location, salario, estado de aplicación) |
| `GET /jobs/{id}` | Detalle de vacante + formulario para aplicar |
| `POST /jobs/{id}/apply` | Crear/actualizar aplicación desde el navegador |
| **`GET /scraping`** | **Panel de control de scraping (búsqueda, config, perfiles, schedules)** |
| `POST /scraping/run` | Ejecutar búsqueda manual (formulario) |
| `POST /scraping/settings` | Guardar configuración de scraping |
| `POST /scraping/profiles` | Crear perfil de búsqueda |
| `POST /scraping/schedules` | Crear schedule automático |
| `POST /scraping/schedules/{id}/run` | Ejecutar schedule ahora |
| `POST /scraping/schedules/{id}/toggle` | Activar/pausar schedule |

### Panel de scraping (`/scraping`)

Todo el scraping se controla desde la aplicación:

1. **Buscar ahora** — botón que ejecuta el scraping al instante. Puedes usar un perfil guardado o keywords/ubicación manuales, elegir plataformas y máximo de resultados.
2. **Configuración** — rate limit, plataformas por defecto, sesión LinkedIn, timeout, scheduler on/off e intervalo. Se guarda en BD y aplica en caliente.
3. **Perfiles** — crear y listar perfiles de búsqueda.
4. **Schedules** — crear, pausar/activar, ejecutar manualmente.

Los endpoints web comparten ruta con la API (`/jobs`) pero responden `text/html`. La API responde `application/json`.

### Filtros disponibles en `/jobs`

| Parámetro | Descripción |
|-----------|-------------|
| `keyword` | Busca en título, descripción y nombre de empresa |
| `source` | `occ` o `linkedin` |
| `location` | Texto parcial en ubicación |
| `minSalary` | Salario mínimo |
| `applicationStatus` | `APPLIED`, `SCREENING`, `INTERVIEWING`, `OFFER`, `REJECTED`, `WITHDRAWN` |
| `onlyUnapplied` | `true` para vacantes sin aplicación |

## API REST

Todas las respuestas de error devuelven JSON: `{"error": "mensaje"}`.

### Jobs

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/jobs` | Listar vacantes |
| `GET` | `/jobs/{id}` | Obtener vacante |
| `POST` | `/jobs` | Crear vacante manualmente |

**POST /jobs** — cuerpo de ejemplo:

```json
{
  "title": "Java Developer",
  "companyName": "TechCorp",
  "companyWebsite": "https://techcorp.com",
  "description": "Spring Boot role",
  "location": "Monterrey",
  "salaryMin": 50000,
  "salaryMax": 80000,
  "source": "linkedin",
  "url": "https://example.com/job/1"
}
```

Campos requeridos: `title`, `companyName`, `source`, `url`.

### Applications

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/applications` | Registrar aplicación |
| `PATCH` | `/applications/{id}` | Actualizar estado o notas |

**POST /applications**:

```json
{
  "jobId": 1,
  "status": "APPLIED",
  "appliedAt": "2026-07-06T12:00:00Z",
  "notes": "Aplicado vía sitio web"
}
```

Si `status` se omite, por defecto es `APPLIED`.

**PATCH /applications/{id}**:

```json
{
  "status": "INTERVIEWING",
  "notes": "Phone screen programada"
}
```

### Search Profiles

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/profiles` | Listar perfiles |
| `POST` | `/profiles` | Crear perfil |

**POST /profiles**:

```json
{
  "name": "Backend Remote",
  "keywords": "java,spring,remote",
  "filters": "{\"location\":\"monterrey\"}"
}
```

El campo `filters` es JSON en texto. Soporta `location` para scraping y schedules.

### Scraping

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/scraping/run` | Ejecutar scraping manual |

**POST /scraping/run**:

```json
{
  "profileId": 1,
  "keywords": "desarrollador java",
  "location": "monterrey",
  "platforms": ["occ", "linkedin"],
  "maxResults": 20
}
```

Usa `profileId` **o** `keywords` + `location`. Si `platforms` se omite, usa OCC y LinkedIn.

Respuesta:

```json
{
  "scraped": 15,
  "imported": 12,
  "duplicates": 3,
  "errors": []
}
```

### Schedules (automatización)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/schedules` | Listar schedules |
| `POST` | `/schedules` | Crear schedule |
| `PATCH` | `/schedules/{id}` | Activar/desactivar |
| `POST` | `/schedules/{id}/run` | Ejecutar un schedule |
| `POST` | `/schedules/run-due` | Ejecutar todos los vencidos |

**POST /schedules**:

```json
{
  "profileId": 1,
  "platforms": ["occ"],
  "intervalMinutes": 30,
  "maxResults": 15
}
```

Defaults: `intervalMinutes=60`, `maxResults=20`, plataformas OCC + LinkedIn.

## Flujo típico de uso

```
1. Crear perfil de búsqueda     POST /profiles
2. Crear schedule               POST /schedules
3. (Automático) scraping        cada poll-interval-ms
4. Notificación RabbitMQ        cola job-imports
5. Revisar dashboard            GET /
6. Filtrar y aplicar            GET /jobs → POST /jobs/{id}/apply
```

## Scraping

| Plataforma | Fuente | Tecnología |
|------------|--------|------------|
| OCC | `occ` | Playwright (headless) + Jsoup |
| LinkedIn | `linkedin` | Playwright (headless) |

Pipeline: **Fetcher → Parser → Normalizer → Duplicate detection → Storage**

Duplicados se detectan por par único `(source, url)`.

### LinkedIn con sesión

#### Linux / macOS

```bash
cd job-tracker
sudo npx playwright install-deps chromium   # solo Chromium (no WebKit)
./mvnw exec:java@playwright-install
./scripts/save-linkedin-session.sh
```

#### WSL (Ubuntu 20.04 — aviso de WebKit)

Si ves *"frozen webkit browser ... ubuntu20.04-x64"*: **puedes ignorarlo**. Este proyecto solo usa **Chromium** para LinkedIn; WebKit no se usa.

En WSL 20.04 además Chromium a veces abre ventana en blanco. Lo más fiable es exportar desde **PowerShell o CMD en Windows** (no uses `sudo`; eso es de Linux).

**Requisito en Windows:** Node.js (incluye `npx`). Instálalo una vez:

```powershell
winget install OpenJS.NodeJS.LTS
```

Cierra y vuelve a abrir PowerShell. Verifica: `node -v` y `npx -v`.

```powershell
cd \\wsl.localhost\Ubuntu-20.04\home\dimedical\learning\projects\job-tracker\job-tracker
.\scripts\save-linkedin-session.cmd
```

Si PowerShell bloquea el `.ps1` por política de ejecución, usa el `.cmd` de arriba o:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\save-linkedin-session.ps1
```

En la app usa la ruta WSL del archivo, por ejemplo:

```
/mnt/c/Users/TU_USUARIO/linkedin-storage-state.json
```

Opcional: actualizar WSL a Ubuntu 22.04+ elimina el aviso de WebKit y mejora compatibilidad con Playwright (`wsl --install -d Ubuntu-24.04` o upgrade in-place).

#### Configuración

Pon la ruta del JSON en **http://localhost:8080/scraping** → *LinkedIn session file*.

### OCC con sesión

OCC requiere login para mostrar resultados. El flujo es el mismo que LinkedIn.

**Windows (recomendado en WSL):**

```powershell
cd \\wsl.localhost\Ubuntu-20.04\home\dimedical\learning\projects\job-tracker\job-tracker
.\scripts\save-occ-session.cmd
```

Login: https://secure.occ.com.mx/Account/Login

En la app → *OCC session file*:

```
/mnt/c/Users/TU_USUARIO/occ-storage-state.json
```

**Linux / macOS:**

```bash
cd job-tracker
sudo npx playwright install-deps chromium
./mvnw exec:java@playwright-install
./scripts/save-occ-session.sh
```

## Automatización y notificaciones

El scheduler revisa cada `poll-interval-ms` (default 60 s) qué schedules están vencidos (`last_run_at + interval_minutes`).

Cuando se importan vacantes nuevas, se publica un `JobImportNotification` en RabbitMQ:

| Propiedad | Default |
|-----------|---------|
| Exchange | `job-tracker.notifications` |
| Queue | `job-imports` |
| Routing key | `job.imports` |

## Configuración

La configuración de scraping se gestiona desde **`/scraping`** y se persiste en la tabla `scraping_settings`. Los cambios aplican en caliente (rate limit, LinkedIn, scheduler on/off).

Valores iniciales en `application.properties` (solo arranque en frío; luego manda la BD):

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/jobs
spring.datasource.username=admin
spring.datasource.password=admin

# Scraping (valores iniciales; editable en /scraping)
scraping.rate-limit-ms=1000
scraping.linkedin.storage-state-path=
scraping.linkedin.page-timeout-ms=30000

# Automatización (scheduler lee de BD; fallback en arranque)
automation.scheduling.enabled=true
automation.scheduling.poll-interval-ms=60000
automation.notifications.rabbitmq-enabled=true
```

## Base de datos

Migraciones Flyway en `src/main/resources/db/migration/`.

| Tabla | Propósito |
|-------|-----------|
| `companies` | Empresas |
| `jobs` | Vacantes (unique: source + url) |
| `applications` | Aplicaciones del usuario |
| `search_profiles` | Criterios de búsqueda |
| `scraping_schedules` | Scraping periódico por perfil |
| `scraping_settings` | Configuración global de scraping (singleton) |

## Modelo de dominio principal

| Entidad | Descripción |
|---------|-------------|
| `Job` | Vacante scrapeada o manual |
| `Company` | Empresa empleadora |
| `Application` | Interacción del usuario con una vacante |
| `SearchProfile` | Keywords y filtros de búsqueda |
| `ScrapingSchedule` | Configuración de scraping automático |

### Estados de aplicación

`APPLIED` · `SCREENING` · `INTERVIEWING` · `OFFER` · `REJECTED` · `WITHDRAWN`

## Tests

```bash
cd job-tracker
./mvnw test
```

Los tests de parsers usan HTML guardado (sin internet). Perfil `test` usa H2 en memoria.

## Documentación relacionada

- [API.md](API.md) — referencia rápida de endpoints
- [ARCHITECTURE.md](ARCHITECTURE.md) — capas y principios
- [SCRAPING.md](SCRAPING.md) — estrategia de scraping
- [DATABASE.md](DATABASE.md) — esquema de tablas
- [ROADMAP.md](ROADMAP.md) — fases del proyecto
