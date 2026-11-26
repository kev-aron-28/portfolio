# Programación y recuperación del observatorio

Fecha: 2026-11-14. Incremento 7 del [plan](PLAN_OBSERVATORIO_MERCADO.md).

## Uso

En `/observatory/schedules`, crear un horario asociado a una versión concreta de un protocolo ejecutable. Elegir inicio, reintentos y espera inicial; los horarios se crean pausados por defecto en la interfaz. Activar explícitamente para ejecutar capturas. La frecuencia procede del protocolo y se muestra en pantalla.

Cada cambio exige motivo y revisión vigente; conserva una versión inmutable. Editar el protocolo no actualiza silenciosamente los horarios: hay que seleccionar su nueva revisión. Renombrar o pausar conserva el siguiente intervalo; cambiar protocolo o inicio restablece el calendario. Una pausa cancela trabajo asignado que aún no tenga captura; una captura ya iniciada puede terminar y se puede interrumpir desde Capturas.

La frecuencia mide minutos transcurridos desde el inicio, almacenado en UTC. No es un calendario de hora local sujeto a cambios de horario estacional. Tras una caída se captura únicamente el intervalo vencido más reciente y se registra cuántos se omitieron: no se reconstruyen observaciones históricas que nunca ocurrieron.

## Ejecución y recuperación

- Temporizador independiente del seguimiento de postulaciones, con trabajadores limitados y sin acumular consultas en una cola local ilimitada.
- Tareas persistentes con ventana, revisión de horario, intento, token de propietario y vencimiento. El identificador de tarea es la clave idempotente de la captura; una entrega repetida no crea otra ejecución.
- Señales periódicas de actividad para tareas y recolectores, recuperación automática tras vencimiento y límite máximo de duración. La recuperación vuelve a comprobar la actividad bajo bloqueo antes de cerrar.
- La evidencia recibida se conserva antes de importarla. Una recuperación marca los elementos pendientes como error y bloquea importaciones tardías. El cierre de captura y su motivo se confirman en una misma transacción.
- Una captura con evidencia sigue siendo parcial y no se reintenta automáticamente. Tampoco se reintentan errores de importación ni interrupciones solicitadas por el usuario.
- Fallos de ejecución o ausencia de resultados confirmados pueden reintentarse: entre cero y cinco reintentos, espera inicial de 1–1440 minutos, crecimiento exponencial limitado a 24 horas y siempre antes de la siguiente ventana regular. Una ventana regular nueva reemplaza un reintento pendiente vencido. Un cambio de configuración cancela reintentos de la versión anterior.

Una tarea `SUCCEEDED` significa que concluyó con evidencia disponible; no declara que el portal se haya recorrido completamente. Una captura vacía sigue siendo un fallo de cobertura desconocida, no una medición de cero vacantes.

## Configuración

| Propiedad | Valor predeterminado | Efecto |
| --- | --- | --- |
| `observatory.automation.enabled` | `true` | Activa temporizador y mantenimiento; `false` los desactiva. |
| `observatory.scheduling.enabled` | `true` | Permite despachar horarios; `false` mantiene la recuperación sin iniciar nuevas tareas. |
| `observatory.automation.poll-ms` | `10000` | Periodo de mantenimiento y despacho. |
| `observatory.automation.lease-seconds` | `90` | Vencimiento sin actividad; mínimo 30 segundos. |
| `observatory.automation.max-runtime-seconds` | `1800` | Duración máxima de una captura; al menos la duración de la concesión. |
| `observatory.automation.workers` | `1` | Trabajadores locales, entre 1 y 4. |

El periodo debe ser al menos un segundo y como máximo un tercio del vencimiento. El perfil de pruebas desactiva la automatización. La migración V16 no activa horarios ni inventa tareas para ejecuciones anteriores; inicializa su actividad desde la fecha original del intento.

## API

Base `/api/observatory`:

- `GET /automation`: configuración operativa efectiva.
- `GET /schedules`, `GET /schedules/{id}`: horarios y estado actual.
- `POST /schedules`: alta con `requestKey` UUID idempotente, `config` y `reason`.
- `PUT /schedules/{id}`: nueva revisión con `expectedRevision`, `config` completo y `reason`.
- `GET /schedules/{id}/history`: revisiones inmutables.
- `GET /schedules/{id}/tasks?limit=25&offset=0`: tareas, intentos, vencimientos y enlace mediante `runId` a su captura.

`config` contiene `name`, `protocolId`, `protocolRevision`, `enabled`, `startAt`, `maxRetries` y `backoffMinutes`. Las capturas y su evidencia siguen disponibles en los endpoints y pantallas de recolección existentes.

## Alcance y validación

Se mantiene el muestreo exploratorio de primera página. La programación no convierte el protocolo en censo ni habilita inferencias sobre todo el mercado. Faltan paginación verificable, validación operativa por portal y PostgreSQL real.

Pruebas de integración: idempotencia, concurrencia, tokens, versiones fijadas, pausas, intervalos omitidos, reintentos limitados, recuperación sin pérdida de evidencia, actividad renovada, límite máximo, trabajo fuera del hilo de mantenimiento y cierre transaccional. Migración desde V15 con un intento previo. Prueba de navegador con temporizador real, colector de HTML de muestra, captura enlazada, pausa, historial y pantalla móvil de 390 px. No se consultaron portales reales.
