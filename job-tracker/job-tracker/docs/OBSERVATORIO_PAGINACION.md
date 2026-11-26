# Paginación y evidencia por página

Incremento 8, 2026-11-14. Complementa el [plan](PLAN_OBSERVATORIO_MERCADO.md) y la [recolección](OBSERVATORIO_RECOLECCION.md).

## Configuración y compatibilidad

En Capturas, configurar **Máximo de páginas** al guardar una nueva versión de un protocolo ejecutable. Se admiten 1–20 páginas y hasta 1000 elementos por captura; ambos son límites máximos, no cantidades prometidas. Los protocolos anteriores conservan una página y los horarios permanecen fijados a su revisión anterior hasta editarlos explícitamente.

`collectionPlan.maxPages` es opcional en la API: su ausencia equivale a 1. El campo descriptivo `pagination` debe coincidir: `Primera página` para 1 o `Hasta N páginas` para varias. La migración aditiva V17 incorpora el límite y el archivo de páginas; no modifica el texto histórico ni inventa evidencia de ejecuciones anteriores.

## Navegación conservadora

El recolector `OBSERVATORY_BROWSER_V2` sigue enlaces siguientes observados. Comprueba origen, ruta, filtros y consulta; sólo permite cambiar el cursor numérico correspondiente a la fuente: `start` en LinkedIn/Indeed, `page` en OCC y `p` en Computrabajo. No inventa URLs ni pulsa botones de navegación sin destino verificable. Enlaces ambiguos, filtros que desaparecen o destinos incompatibles detienen la captura.

Reconoce controles `rel=next` y controles siguientes dentro de las regiones de navegación conocidas por fuente. Las interfaces que sólo ofrecen botones JavaScript, desplazamiento infinito u otra paginación aún no implementada se detienen con diagnóstico. Los selectores y cursores requieren validación con los portales reales; los escenarios locales no prueban que su interfaz actual coincida.

Detiene el recorrido por límite de páginas o elementos, URL repetida, huella repetida de identidades de tarjetas, acceso no confirmado, ausencia de tarjetas, interrupción, navegación fallida o falta de continuidad verificable. Un control siguiente desactivado se registra como `END_CONTROL_OBSERVED`; su ausencia se registra como `NAVIGATION_UNKNOWN`. Ninguno convierte la captura en censo del mercado. Se mantiene `EXPLORATORY` y el resultado con datos es parcial.

Los anuncios que reaparecen en varias páginas se importan una vez por identidad exacta dentro de la ejecución. Las tarjetas repetidas permanecen visibles en el HTML de la página; la captura siguiente puede producir una nueva observación del mismo anuncio. Las claves de elemento son distintas entre páginas y no dependen de que el anuncio ocupe la misma posición en capturas futuras.

## Evidencia y fallos

`observatory_collection_pages` conserva por ejecución y número de página: URL efectiva, fecha de observación, HTML, cantidad de tarjetas reconocidas, huella, siguiente enlace observado, resultado de navegación y versión del recolector. Se guarda antes de importar las tarjetas o consultar sus detalles. El límite contabiliza elementos únicos enviados a importación, incluidos los que requieren revisión.

`GET /api/observatory/runs/{id}/collection` añade `pages` a `attempt` e `items`. La pantalla permite abrir cada página y leer el HTML como texto, sin ejecutarlo. El preview indica `BOUNDED_EXPLORATORY`. Un fallo posterior conserva las páginas e importaciones ya confirmadas y el conteo de páginas recibidas, incluso durante recuperación automática. Un destino que falla antes de recibir página se documenta en el diagnóstico, sin inventar su HTML.

Alcanzar el límite marca truncamiento. Un bloqueo, un selector desconocido o un control no compatible informa cobertura desconocida, nunca cero vacantes. El motivo registrado en la página describe la navegación y límites observados; el cierre del intento documenta cualquier fallo posterior durante procesamiento.

## Verificación

- Suite Java/H2: validación de límites, contratos de protocolo, filtros/cursor, destinos inseguros, navegación ambigua, falta de controles, lectura/escritura histórica, conservación ante fallo de segunda página y migración V16→V17.
- Verificación de Chromium: once escenarios de recorrido con respuestas HTTP interceptadas y cuatro escenarios de selección por fuente. Comprueba solapamientos sin importación doble, páginas repetidas, límites, bloqueos, interrupción, error de navegación, ausencia de controles y ausencia de tarjetas.
- Flujo de interfaz con un protocolo de tres páginas, temporizador real, HTML de prueba, diagnóstico de segunda página y horario pausado con historial.

La herramienta reproducible de navegador es `src/test/java/com/projects/job_tracker/infrastructure/observatory/collection/PageBrowserVerification.java`; su `main` recibe la ruta de Chromium, usando el classpath de pruebas. Se ejecuta explícitamente para no depender de navegadores instalados en la suite ordinaria. Todas sus solicitudes se satisfacen o abortan mediante interceptación. No se consultan portales externos.

Pendiente: validar PostgreSQL y sesiones/respuestas reales por portal, y adaptar los controles no compatibles que revele esa validación. El dashboard pertenece a la fase 3.
