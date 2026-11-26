# Contexto del puesto y aptitud para comparaciones

Incremento 3, 2026-11-14. Contrato implementado en dominio, API y pantalla `/observatory`. Complementa el [plan](PLAN_OBSERVATORIO_MERCADO.md).

## Datos e historial

`data.context` es opcional. Los registros anteriores y solicitudes que lo omitan conservan contexto desconocido, sin deducirlo de `location`, título ni moneda. Los datos nuevos se guardan por revisión en columnas de `observatory_posting_versions` mediante V12; una corrección no modifica las revisiones previas.

```json
{
  "publishedAt": "2026-01-01T12:00:00.123Z",
  "countryCode": "MX",
  "region": "Jalisco",
  "city": "Guadalajara",
  "workMode": "ONSITE",
  "weeklyHours": 40,
  "contractType": "PERMANENT",
  "seniority": "SENIOR",
  "remoteScope": null,
  "remoteDetails": null,
  "evidence": "MX Jalisco Guadalajara presencial 40 horas indefinido senior"
}
```

El bloque se envía dentro de `data` en alta/corrección. PUT sigue siendo reemplazo completo de la transcripción: incluir el contexto y los requisitos que se deseen conservar. No enviar únicamente el campo modificado.

- `publishedAt`: instante conocido de publicación, distinto de observación y registro. No puede ser posterior a la observación, tampoco al corregir. La pantalla convierte la hora local a UTC y conserva milisegundos. Si sólo se conoce el día o una expresión como «hace 3 días», conservar ese texto original y dejar el instante desconocido; aún no se representan fechas aproximadas ni precisiones parciales.
- `countryCode`: país del mercado salarial indicado en el anuncio, ISO de dos letras; se normaliza a mayúsculas y valida contra los países de Java. No se infiere de la moneda ni equivale al permiso para trabajar allí.
- `region`, `city`: transcripción manual. No existe todavía un catálogo geográfico o geocodificación; homónimos y variantes de escritura requieren revisión.
- `workMode`: ONSITE, HYBRID, REMOTE o desconocida.
- `weeklyHours`: mayor que cero y hasta 168, máximo dos decimales; no asumir 40 por decir «tiempo completo». No se convierten salarios por horas implícitas.
- `contractType`: PERMANENT, FIXED_TERM, INDEPENDENT, INTERNSHIP o desconocida; no implica equivalencia legal entre países.
- `seniority`: TRAINEE, JUNIOR, MID, SENIOR, LEAD o desconocido. Es transcripción manual, no clasificación automática por años o título.
- `remoteScope`: COUNTRY_ONLY, WORLDWIDE, RESTRICTED o desconocido. Requiere modalidad remota o híbrida. COUNTRY_ONLY requiere país; RESTRICTED requiere `remoteDetails`; los detalles no se aceptan con otro alcance.
- `evidence`: fragmento exacto del original. Obligatorio cuando hay datos de contexto. Verifica procedencia textual, no la veracidad ni que cada valor sea una interpretación semántica correcta. Para hechos separados puede copiarse un bloque más amplio del original que los incluya.

No se identifica elegibilidad personal a partir de estos campos: permisos de trabajo, huso horario, residencia y otras restricciones necesitan evaluación posterior.

## Calidad calculada

`data.quality` mantiene `skillAnalysisReady` y `salaryMissingFields` y añade:

- `scope=CONTEXT_COMPLETENESS_ONLY`: son reglas de completitud, no confianza estadística.
- `salaryCohortReady`: indica que existen los metadatos mínimos para una agrupación salarial conservadora.
- `publicationDateKnown`: indica si se conoce el instante de publicación. No es condición de aptitud salarial; las cohortes temporales deberán aplicar su filtro de fechas por separado.

Campos salariales mínimos: al menos un extremo, moneda, periodo, bruto/neto, país, modalidad, horas semanales, contratación y seniority. Para presencial/híbrido también región y ciudad; para remoto/híbrido, alcance remoto conocido. Los límites abiertos siguen siendo válidos; no se inventa el importe faltante.

La pantalla enumera los campos faltantes. Un registro sin contexto sigue sirviendo como evidencia y para habilidades revisadas, pero no se considera listo para agrupar salarios. Un contexto completo tampoco demuestra que dos trabajos sean equivalentes o que los salarios se hayan pagado.

## Comparación entre registros

En el dominio, `salaryCohortKey()` devuelve una clave sólo si hay aptitud contextual. `canCompareSalaryWith(other)` exige aptitud en ambos registros y coincidencia de:

- Título transcrito, moneda, periodo y bruto/neto.
- País, región y ciudad si se proporcionan; modalidad, horas, contratación y nivel.
- Alcance remoto y detalles restrictivos.

Se normalizan mayúsculas/minúsculas y espacios de textos para comparar; las horas 40 y 40.00 son iguales. No se equiparan sinónimos de puestos, ciudades o restricciones, ni se convierten monedas, periodos o bruto/neto. Se usa una regla deliberadamente conservadora: anuncios equivalentes con títulos diferentes aún no se agrupan. No compara importes como requisito de pertenencia porque sus distribuciones son lo que se analizará.

La clave es una capacidad de dominio para la fase 3, no un dashboard ni un endpoint nuevo de métricas. No corrige sesgos de selección ni controla prestaciones, sector u otras diferencias. Las comparaciones por familia normalizada y segmentos requerirán un contrato posterior explícito.

## Casos verificables

| Caso | Resultado |
| --- | --- |
| 30000 MXN mensuales brutos sin jornada o país | Registro válido; no apto para cohorte salarial. |
| Mismo contexto, horas 40 y 40.00 | Misma clave. |
| Mismo contexto, jornadas 20 y 40 | Claves diferentes. |
| Mismo contexto, MXN y USD | Claves diferentes; sin conversión implícita. |
| Mismo contexto, indefinido e independiente | Claves diferentes. |
| Remoto sin alcance conocido | Registro válido si tiene evidencia; falta remoteScope. |
| Remoto nacional y mundial | Claves diferentes. |
| Publicación posterior a observación | Error 400; no se crea registro ni versión. |
| Corrección que elimina la jornada | Versión anterior conservada; nueva versión no apta. |
| Contexto sin cita o cita inexistente | Error 400 sin persistencia parcial. |

Pendientes de etapas posteriores: fechas aproximadas, catálogos geográficos, familias de puesto y experiencia estructurada, idiomas/estudios, condiciones laborales más detalladas y evaluación de elegibilidad personal.
