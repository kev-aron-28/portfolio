# Recolección bajo demanda y evidencia de conectores

Incremento 6, 2026-11-14. [Plan general](PLAN_OBSERVATORIO_MERCADO.md).

## Qué está implementado

En `/observatory/captures` se puede crear una versión de protocolo con **Configurar captura bajo demanda**. Elige portal, ubicación, modalidad y antigüedad de publicación. El formulario genera una descripción de filtros que coincide con el plan ejecutable; fija orden del portal, primera página y muestra exploratoria. La frecuencia continúa siendo configuración, sin programación automática.

Después de iniciar una ejecución vacía, **Recolectar primera página del portal** consulta el portal mediante Playwright y las sesiones que el proyecto ya configura. Los lectores del seguimiento normal permanecen intactos. El nuevo recolector reutiliza parsers y constructores de búsqueda, pero conserva evidencia y resultados de error por separado.

También se puede **Registrar anuncio nuevo en esta captura** sin pasar por un alta exploratoria previa. La primera observación y revisión se vinculan directamente a esa ejecución, en una sola transacción. Si se identifica exactamente un registro existente por fuente y URL/ID, se añade la observación a ese registro. Una identidad ambigua entre varios registros requiere revisión; no se elige uno arbitrariamente ni se fusionan historiales.

## Auditoría del flujo anterior

Revisión realizada sobre el código local, no mediante consultas a portales reales. Los siguientes componentes viven en `src/main/java/com/projects/job_tracker/infrastructure/scraping/`.

| Portal | Evidencia y comportamiento anteriores | Adaptación del observatorio |
| --- | --- | --- |
| LinkedIn | `LinkedInJobFetcher` analiza el listado y navega a detalles. Ante error devuelve el resumen; descarta HTML y causa por anuncio. `LinkedInSearchUrlBuilder` añade una semana si la antigüedad se omite y no traduce filtros salariales. | DOM de tarjeta y descripción conservados. Detalle en página independiente, verificación de dominio e identidad de navegación; antigüedad explícita obligatoria. No se ofrecen filtros salariales que el constructor ignore. |
| Indeed | `IndeedJobFetcher` pulsa tarjetas, enriquece desde panel y vuelve al resumen ante fallos. Los desafíos se registran en logs, no en un contrato de cobertura. El constructor sólo reconoce remoto para modalidad; el parser puede inferirla a partir de ubicación. | Tarjeta y detalle conservados. Se usa página independiente de la URL del anuncio para evitar reutilizar un panel de otro puesto. Sólo se acepta el filtro remoto; no se promueve la modalidad inferida a contexto confirmado. |
| OCC | `OccJobFetcher` puede devolver el parseo de una página tras un timeout o descartar una tarjeta cuyo detalle falla. No conserva el HTML ni declara cobertura. | Se conservan tarjetas, paneles y errores. Se espera la identidad del panel y se vuelve a comprobar su ID antes de enriquecer; si no coincide, el texto analítico usa únicamente la tarjeta. |
| Computrabajo | `ComputrabajoJobFetcher` conserva resúmenes al fallar detalle. El constructor sustituye ubicación por modalidad y aproxima antigüedad a intervalos del portal. | Se comprueba `data-oi` del panel. Se rechazan combinaciones ubicación+modalidad y días que el constructor aproximaría; panel no verificado se archiva sin añadir su texto a la evidencia utilizable. |

El contrato común anterior `ScrapedJob` no incluye HTML original, hora de observación del navegador, calidad de recuperación, moneda, periodicidad ni bruto/neto. Por eso el observatorio no convierte una lista devuelta por los lectores antiguos en una muestra completa.

## Plan de filtros

`ProtocolSpec.collectionPlan` es opcional y versionado junto con el resto del protocolo. Los protocolos existentes permanecen manuales y no reciben ajustes automáticos al migrar.

| Portal | Ubicación | Modalidad | Antigüedad |
| --- | --- | --- | --- |
| LinkedIn | Texto enviado al buscador | Remota, híbrida o presencial | Obligatoria, 1–365 días. |
| Indeed | Texto enviado al buscador | Sólo remota o sin filtro | Opcional, 1–365 días. |
| OCC | Texto traducido a ruta por el constructor existente | Remota, híbrida o presencial | Opcional, 1–365 días. |
| Computrabajo | Texto, sin combinar con modalidad | Remota o híbrida; sin ubicación simultánea | Opcional: exactamente 1, 3, 7, 15 o 30 días. |

Estas reglas aseguran que el código no omita o aproxime silenciosamente un filtro admitido. Falta comprobar en vivo cómo cada portal aplica actualmente los parámetros enviados. No se supone que los filtros del segmento o su país estén aplicados a la consulta: sólo se ejecuta el plan explícito. La pertenencia de una observación a una ejecución sigue siendo procedencia de muestreo, no clasificación confirmada del puesto.

Cuando se configura el plan:

- `source` debe coincidir con el portal.
- `samplingMode` es EXPLORATORY. El modo sistemático programado aún no se habilita.
- `ordering` es `Orden del portal` y `pagination` es `Primera página`; no se afirma orden cronológico.
- `filters` debe coincidir con `collectionPlan.filterDescription()`; la pantalla lo genera.
- El límite automático es de hasta 100 elementos. No se ofrece todavía navegación entre páginas ni desplazamiento para cargar todos los resultados.
- Moneda, jornada, país del puesto, contratación y seniority no se rellenan a partir de filtros, dominio del portal o ubicación de búsqueda.

## Evidencia y extracción

Cada elemento conserva los fragmentos HTML del DOM de su tarjeta y, si se obtuvo, su detalle; fecha observada, versión de colector, estado de importación y diagnóstico. No son una copia byte a byte de la respuesta HTTP ni incluyen necesariamente toda la página. El texto de la observación se deriva con Jsoup de la tarjeta y del detalle verificado, separados por un salto de línea. El HTML queda disponible bajo **Ver HTML de origen**, sin ejecutarse en la interfaz.

Los detalles sólo enriquecen datos cuando su identidad es verificable:

- LinkedIn/Indeed: navegación independiente dentro del dominio del portal, con URL/ID compatible.
- OCC: ID del panel parseado igual al ID de la tarjeta.
- Computrabajo: `data-oi` igual al identificador de la tarjeta.

Un detalle sin verificar se archiva como tal y no se incluye en el texto utilizado para la transcripción. Una tarjeta no identificable o una excepción de extracción conserva su HTML y se registra como elemento que necesita revisión, sin inventar un anuncio.

Los datos básicos extraídos utilizan `AUTO_CAPTURE_V1`; la pantalla los identifica como pendientes de revisión. Se transcriben título, empresa/ubicación cuando existan, requisitos textuales e importes sugeridos por los parsers. No se asume moneda, periodo, bruto/neto ni contexto, y las habilidades quedan sin revisar. Las reglas de aptitud evitan considerarlos automáticamente salarios comparables o requisitos humanos verificados.

Si el texto de un anuncio existente no cambió, se conserva su interpretación humana y se añade una observación sin sustituir correcciones por datos del parser. Si cambió, se crea otra revisión automática pendiente de revisión, vinculada a la nueva evidencia.

## Transacciones, errores y estados

El navegador trabaja fuera de una transacción de base de datos. La evidencia se guarda primero en `observatory_collected_items`; después se intenta importar en otra transacción. El cambio de esa fila a IMPORTED se confirma junto con la observación/anuncio. Si la importación falla, la transacción se revierte y la evidencia se marca ERROR mediante una operación independiente.

Estados por elemento: PENDING, IMPORTED, ERROR. IMPORTED significa que se registró en el observatorio, no que todos sus campos sean correctos o aptos para análisis. Un parser puede entregar información incompleta; el diagnóstico y la evidencia permanecen consultables.

`observatory_collection_attempts` impide lanzar dos recolectores para la misma ejecución. Repetir la solicitud devuelve la ejecución existente; una captura nueva requiere otra ejecución. No se mezclan altas/observaciones manuales con un intento del recolector ya iniciado, y no se permite finalizarlo manualmente mientras trabaja.

Con registros disponibles, la recolección termina PARTIAL, porque sólo se observó la primera página. Si no se pudo importar ningún elemento, se declara FAILED (o parcial si se alcanzó un límite), con motivo. Un listado vacío o un selector ausente no produce una conclusión de cero vacantes. Alcanzar el límite se registra como truncamiento. Las páginas visitadas se conservan cuando se conocen; una interrupción puede dejar el dato desconocido.

Si el portal falla después de importar un anuncio, se conserva lo confirmado y se informa captura parcial. La sesión, los desafíos, los cambios de selectores o la falta de navegador se describen como problemas de recolección, no cambios del mercado.

### Interrupción y recuperación

La interfaz permite **Interrumpir captura** con motivo. La bitácora se cierra como parcial si ya había observaciones disponibles, o fallida si no las había; el intento queda finalizado. Los elementos pendientes se conservan como errores revisables.

La operación del navegador que ya está en curso puede tardar en terminar. La cancelación es cooperativa entre elementos: no se admiten observaciones posteriores al cierre. Si llega evidencia de una operación en vuelo, se conserva como ERROR sin importarla. El recolector no sobrescribe el motivo de interrupción.

Este mecanismo también permite cerrar un intento RUNNING abandonado tras reiniciar la aplicación. No se relanza automáticamente ni se reanuda desde un punto desconocido; se debe crear otra ejecución. La recuperación automática con leases, política de reintentos y programación es trabajo posterior.

## API adicional

Bajo `/api/observatory`:

| Método y ruta | Acción |
| --- | --- |
| GET /protocols/{id}/collection-preview?revision=N | URL efectiva y alcance FIRST_PAGE_ONLY de esa versión. |
| POST /runs/{id}/collect | Ejecutar recolección bajo demanda; responde con la ejecución. Un reintento no lanza otra consulta. |
| GET /runs/{id}/collection | Intento y elementos con evidencia, diagnóstico y enlaces a registros importados. |
| POST /runs/{id}/collection/interrupt | Cerrar/interrumpir con `{reason}`. |
| POST /runs/{id}/items | Alta o nueva observación por identidad exacta dentro de una captura manual. |

Ejemplo de plan dentro de `spec`:

```json
{
  "source": "indeed",
  "filters": "Ubicación: sin filtro; modalidad: remota; antigüedad: 3 días",
  "ordering": "Orden del portal",
  "pagination": "Primera página",
  "samplingMode": "EXPLORATORY",
  "collectionPlan": {
    "platform": "INDEED",
    "location": null,
    "workMode": "REMOTE",
    "postedWithinDays": 3
  }
}
```

El resto de campos del protocolo —segmento, nombre, consulta, frecuencia y límite— siguen siendo obligatorios. Los ejemplos no son solicitudes completas.

Ejemplo de alta directa en una ejecución:

```json
{
  "itemKey": "elemento-manual-1",
  "url": "https://example.com/anuncio",
  "externalId": "anuncio-1",
  "originalText": "Vacante Java publicada",
  "observedAt": "INSTANTE_UTC_DURANTE_LA_CAPTURA",
  "data": {"title": "Java"}
}
```

La fuente se toma del protocolo, no de la solicitud. El instante debe sustituirse por uno real dentro de la ejecución. La clave es estable para reintentar ese elemento; datos distintos con la misma clave se rechazan. Al reutilizar un registro existente, la nueva observación conserva la URL suministrada, sin modificar la identidad original.

## Migración y comprobaciones

V15 añade el plan a cada versión de protocolo y las tablas de intentos/evidencia. No activa protocolos anteriores ni crea tareas. Hay una prueba V14→V15 que verifica la preservación de un protocolo manual.

Las pruebas utilizan H2, fixtures de HTML de los cuatro portales y un colector de prueba para verificar el flujo sin conexión. Se cubren: filtros rechazados si se omiten/aproximan, URL efectiva, panel de otro anuncio, persistencia de HTML, importación inválida, fallo después de un elemento, repetición sin sobrescribir revisión humana, alta manual directa e idempotente, exclusión de ejecución simultánea, interrupción y recuperación de intentos abandonados.

La prueba en Chromium utiliza ese colector de prueba: verifica configuración, recolección, evidencia desplegable, estado parcial y alta directa. **No se consultaron portales reales ni se aplicaron migraciones a datos personales.** Falta validar sesiones/selectores en vivo y PostgreSQL real antes de declarar cerrada la integración operativa.

## Qué falta

1. Programación independiente del observatorio, recuperación automática y política de reintentos.
2. Paginación y reglas de fin de resultados verificables por portal, sin convertir cambios de cobertura en señales de mercado.
3. Pruebas operativas por portal y PostgreSQL, con sus errores de sesión y recuperación.
4. Fase 3: cohortes y dashboard que usen identidad resuelta, estado de captura y aptitud de datos. Las fases 4–7 permanecen pendientes.
