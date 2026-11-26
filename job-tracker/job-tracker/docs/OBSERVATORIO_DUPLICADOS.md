# Identidad y duplicados del observatorio

Incremento 5, 2026-11-14. [Plan y avance](PLAN_OBSERVATORIO_MERCADO.md).

## Uso

Desde `/observatory`, abre **Revisar duplicados** (`/observatory/duplicates`). Los candidatos muestran por qué dos registros podrían corresponder al mismo anuncio. Abre la comparación y sus historiales antes de decidir:

- **Unir para el análisis**: los registros pertenecen a un grupo común de identidad.
- **Mantener como anuncios distintos**: se registra la diferencia y se suprime esa pareja de candidatos.
- **Deshacer decisión y volver a revisión**: se añade una nueva decisión que revoca el vínculo o la exclusión anterior. No se borra el historial.

Cada decisión exige motivo y fija las versiones de ambos anuncios que se revisaron. Si después cambian sus datos, la pantalla indica que la revisión utilizó versiones anteriores. No se elimina automáticamente una decisión humana por cambiar el contenido.

La unión es analítica: nunca traslada ni elimina observaciones, fuentes, protocolos, requisitos o correcciones. El listado de vacantes conserva cada registro. La futura analítica deberá contar identidades resueltas bajo su fecha de corte, en lugar de sumar todos los registros del grupo.

## Identidad exacta al registrar

El alta admite `externalId` opcional, propio de la fuente. La fuente se normaliza con espacios exteriores eliminados y minúsculas. El identificador externo se recorta, admite hasta 300 caracteres y conserva mayúsculas/minúsculas. No se extraen IDs por adivinación de URLs ni se equiparan nombres de plataformas distintos.

Una coincidencia de **fuente + identificador externo**, o **fuente + URL normalizada**, devuelve 409 con `matchingPostingIds`. No crea registros ni observaciones, ni sustituye el texto del anuncio encontrado. La pantalla conserva los datos escritos y ofrece enlaces al registro existente; para nueva evidencia debe utilizarse una ejecución de captura.

Una URL o un identificador pueden reutilizarse para otro anuncio. Si el usuario verificó que es diferente, puede explicar el motivo en `identityConflictReason` y volver a guardar. Se conserva un registro independiente con ese motivo en su identidad. Esa excepción no une los anuncios ni los marca automáticamente como distintos: siguen siendo candidatos para revisión explícita.

Normalización conservadora de URLs:

- Esquema y host en minúsculas; puertos HTTP 80 y HTTPS 443 implícitos eliminados.
- Ruta vacía representada como `/`.
- Eliminación de parámetros cuyo nombre empieza por `utm_` y de `gclid`, `fbclid`, `msclkid`.
- Preservación de otros parámetros, su orden, rutas, mayúsculas de ruta, codificación, fragmentos e identidad HTTP/HTTPS. Los fragmentos pueden identificar vacantes en aplicaciones web.

El original siempre se conserva. No se deduce equivalencia de URLs distintas ni de sitios distintos. El comportamiento es un contrato inicial; al incorporar conectores se revisarán sus identificadores y reglas específicas.

## Candidatos: reglas deterministas, no probabilidades

Las reglas actuales generan señales de revisión:

| Señal | Condición |
| --- | --- |
| SOURCE_URL | Misma fuente y URL normalizada. Permite revisar conflictos históricos o altas independientes justificadas. |
| SOURCE_EXTERNAL_ID | Misma fuente e identificador externo. |
| EXACT_CONTENT | SHA-256 idéntico del texto completo actual, con al menos 80 caracteres. No demuestra que sean el mismo puesto. |
| TITLE_COMPANY | Título y empresa conocidos iguales tras normalizar mayúsculas y espacios. No se propone identidad sólo por un título frecuente si falta empresa. |

La evidencia utilizada es la de la revisión actual del registro, no necesariamente la primera observación. No se calcula un porcentaje de confianza ni se unen coincidencias automáticamente. Las parejas ya unidas transitivamente o declaradas distintas no se muestran como nuevos candidatos. Una decisión deshecha permite volver a mostrar la pareja si todavía satisface las reglas.

Esta primera versión lee snapshots ligeros de los anuncios, agrupa claves de coincidencia y pagina la salida. La generación interna no está paginada en base de datos; se deberá medir y optimizar antes de grandes volúmenes. No se implementó similitud semántica, clasificación probabilística ni generación de candidatos entre habilidades del catálogo.

## Uniones transitivas, contradicciones y separación

Las uniones activas forman componentes conectados sin enlaces redundantes (un bosque). Si A se une con B y B con C, los tres pertenecen a una identidad común. No se permite añadir una unión A–C redundante: así, deshacer una unión realmente divide sus componentes y no deja un camino alternativo que los mantenga unidos.

Una decisión de que A y C son distintos impide que una unión posterior los agrupe indirectamente mediante B. Antes de contradecirla hay que revisarla o deshacerla explícitamente.

Deshacer un vínculo crea otra revisión UNREVIEWED. Cambiar directamente el vínculo MERGED a DISTINCT también separa sus componentes y registra la diferencia. Si dos registros siguen unidos mediante otros vínculos, hay que deshacer el vínculo que los conecta; no se elimina todo un grupo de forma implícita.

El ID canónico de un grupo es el UUID de menor orden textual entre sus miembros. Es una referencia de identidad, no un anuncio que sobrescriba a los demás; puede cambiar al unir o separar grupos. No debe persistirse como sustituto irreversible de los IDs originales.

## Auditoría y fecha de corte

Las revisiones de decisiones son inmutables, con motivo, fecha UTC, versiones de los dos anuncios y huella del comando `DUPLICATE_REVIEW_V1`. Se conserva la pareja original, su estado anterior y cada cambio. Las fechas de decisiones se guardan con precisión de microsegundos para que el instante devuelto pueda usarse como fecha de corte exacta en PostgreSQL/H2.

`GET /api/observatory/postings/{id}/identity?at=INSTANTE_UTC` reconstruye los vínculos activos en esa fecha. Sólo considera registros existentes hasta entonces. Sirve para que una revisión actual no cambie silenciosamente el número histórico de identidades bajo un corte anterior. Esta operación devuelve identidad y miembros; no reconstruye métricas, salarios ni versiones de datos históricas. Esa selección de evidencia corresponde a las cohortes de la fase 3.

El mismo comando de revisión, repetido inmediatamente con la revisión esperada anterior, devuelve la decisión ya creada si coincide su huella. Una solicitud obsoleta con otros datos se rechaza. Cambiar el esquema de la huella en el futuro requiere mantener o versionar su compatibilidad.

## Persistencia y concurrencia

V14 agrega `observatory_source_postings`, registro de exclusión mutua, parejas y revisiones de decisiones. Migra los datos anteriores conservando todas las coincidencias: no añade un índice único que elimine o impida cargar conflictos históricos. Esos conflictos se convierten en candidatos.

El registro de identidad usa un bloqueo transaccional compartido por altas y decisiones. Las altas comprueban coincidencias y adjuntan su identidad en la misma transacción que el anuncio y la primera observación. Las revisiones bloquean ambos anuncios en orden estable, verifican las versiones vistas por el usuario y comprueban contradicciones antes de guardar.

La cobertura funcional incluye una prueba con dos altas simultáneas en H2: sólo una confirma el alta, la otra recibe la coincidencia y no deja evidencia parcial. La validación de concurrencia y migración sobre PostgreSQL real sigue pendiente. No hay fusiones destructivas ni cambios en el seguimiento de postulaciones.

## API nueva

Todas las rutas bajo `/api/observatory`:

| Método y ruta | Resultado |
| --- | --- |
| GET /postings/{id}/identity?at=... | Fuente/identificador, ID canónico y miembros bajo el corte opcional. |
| GET /duplicate-candidates?limit=25&offset=0 | Parejas y señales; límite 1–100. |
| GET /duplicate-groups | Grupos actuales con más de un registro. |
| GET /duplicate-reviews | Decisión actual por pareja y aviso de cambios posteriores en datos. |
| GET /duplicate-reviews/{id}/history | Historial de esa pareja. |
| POST /duplicate-reviews | Crear, cambiar o deshacer una decisión. |

Ejemplo de decisión:

```json
{
  "leftId": "UUID-ANUNCIO-A",
  "rightId": "UUID-ANUNCIO-B",
  "expectedRevision": 0,
  "leftRevision": 1,
  "rightRevision": 1,
  "decision": "MERGED",
  "reason": "Misma publicación confirmada en dos fuentes"
}
```

`expectedRevision=0` corresponde a una pareja sin revisión. Para cambiar o deshacer, usar la revisión actual. Las versiones de anuncios corresponden a los IDs enviados; el servicio ordena internamente la pareja sin intercambiar su evidencia. Los marcadores UUID deben sustituirse por IDs reales.

El alta existente acepta `externalId` e `identityConflictReason` opcionales. El error 409 incluye `{error, matchingPostingIds}`. Los demás contratos del alta permanecen iguales; el ID externo se consulta en el endpoint de identidad y no se modifica mediante una corrección de datos del puesto.

## Pendiente

Cerrar la fase 2 requiere auditar y adaptar conectores, permitir altas directamente dentro de una captura, definir filtros ejecutables por portal, ejecutar capturas automáticas y programarlas de forma independiente. También queda validar PostgreSQL real y optimizar la revisión para grandes volúmenes. El dashboard y el uso de identidades resueltas en métricas son parte de la fase 3, todavía pendiente.
