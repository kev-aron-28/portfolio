# Plan de implementación: observatorio del mercado laboral

Fecha: 2026-11-14.
Estado: fase 1 completada; fases 0 y 2 parciales; fases 3–7 pendientes.
Alcance actualizado: implementación incremental autorizada el 2026-11-14. Reportar qué se hizo y qué falta en cada entrega.

## 1. Objetivo y decisiones que debe apoyar

Crear dentro de job-tracker un observatorio personal del mercado laboral con vacantes independientes de las postulaciones. Su propósito es convertir observaciones de anuncios en evidencia para decidir:

1. Qué puestos, niveles, modalidades y regiones conviene explorar.
2. Qué habilidades aprender y en qué orden, considerando esfuerzo y oportunidades adicionales.
3. Qué rangos de compensación anunciada respaldan los puestos comparables.
4. Qué requisitos ganan o pierden presencia y si el cambio parece persistente.
5. Qué puede esperarse en horizontes cortos, cuando los datos permitan validar pronósticos.

El resultado no será solamente un conjunto de gráficas: cada insight debe relacionar hallazgo, evidencia, incertidumbre, implicación y acción posible. Se podrá abrir la muestra de anuncios que lo respalda.

## 2. Punto de partida verificado en el repositorio

La aplicación usa Java 21, Spring Boot, JPA, PostgreSQL, Flyway y Thymeleaf. Mantener esta arquitectura inicialmente; no introducir microservicios, Python ni otro frontend sin una necesidad medida.

Componentes existentes relevantes:

- `domain/model/Job.java`: representa las vacantes actuales. Tiene mínimos y máximos salariales, pero no moneda, periodicidad ni bruto/neto.
- `domain/model/MarketSegment.java` y migración `V8__market_segments.sql`: los segmentos actuales se relacionan con `jobs` mediante `job_market_segments`.
- `application/scraping/ScrapeJobsUseCase.java`: normaliza resultados y llama a `CreateJobUseCase`; los duplicados se omiten para creación y pueden asociarse a segmentos. Este flujo no constituye un historial de observaciones.
- `application/analytics/MarketInsightsBuilder.java`: construye agregados actuales, incluidos resúmenes de puntos medios salariales.
- `application/analytics/MarketTextAnalyzer.java`: detecta tecnologías y niveles mediante reglas de texto. Puede servir como referencia y fuente de casos, pero no como clasificador definitivo del observatorio.
- Hay conectores para LinkedIn, Indeed, OCC y Computrabajo, programación de capturas y pruebas de varias capas.
- Las migraciones observadas llegan a V9. Al implementar, verificar nuevamente la numeración disponible.

Las rutas Java anteriores son relativas a `src/main/java/com/projects/job_tracker/`.

## 3. Principios obligatorios de diseño

1. Separar almacenamiento y casos de uso del observatorio de `jobs` y `applications`. La captura del observatorio no crea postulaciones ni vacantes de seguimiento.
2. Reutilizar conectores mediante adaptadores, evitando acoplar la captura nueva a `CreateJobUseCase`.
3. Conservar originales y procedencia. Una normalización nunca reemplaza irreversiblemente el texto fuente.
4. Separar valor publicado, inferencia automática, corrección humana y dato desconocido. Registrar método y versión.
5. No interpretar ausencia de una mención como ausencia del requisito.
6. Distinguir anuncio, observación y puesto real. La unidad medible inicialmente es el anuncio canónico observado, no el número de contrataciones.
7. Guardar historial de contenido y de captura; no sobrescribir el pasado al recibir una actualización.
8. Toda métrica define población, unidad, filtros, fechas, denominador, exclusiones y versión del método.
9. Mostrar cobertura e incertidumbre. Los intervalos estadísticos no corrigen por sí mismos el sesgo de selección.
10. No prometer causalidad ni probabilidades de contratación a partir de anuncios.
11. La IA es opcional para extracción y explicación; las cifras se calculan con métodos reproducibles.
12. No habilitar modelos predictivos por un número arbitrario de vacantes: exigir evaluación temporal y cobertura suficiente para el caso.

## 4. Arquitectura propuesta

Implementar un módulo de observatorio dentro del monolito, siguiendo dominio, aplicación, infraestructura y presentación. Nombre técnico propuesto: `observatory`; etiquetas visibles en español.

Flujo:

```text
Registro manual / conectores existentes
  -> ejecución de captura y observación original
  -> validación y normalización versionadas
  -> identidad por fuente y resolución de duplicados
  -> catálogo e historial del observatorio
  -> consultas de cohortes y métricas
  -> insights, simulaciones y pronósticos validados
  -> interfaz con evidencia y acciones
```

Los resultados de captura deben poder persistirse aunque la extracción de un campo falle. Usar estados como borrador, necesita revisión y apto para determinados análisis. La aptitud es específica: un anuncio sin salario aún sirve para analizar tecnologías.

Casos de uso orientativos: `RegisterMarketPosting`, `RunMarketCapture`, `ReviewMarketObservation`, `ResolvePostingDuplicate`, `CompareMarketPeriods`, `GetMarketSalaryDistribution`, `SimulateSkillAcquisition` y `EvaluateMarketForecast`. Los nombres finales se ajustarán a las convenciones del repositorio.

## 5. Modelo de datos propuesto

Tablas nuevas con prefijo `observatory_`; nombres sujetos a revisión durante la implementación.

| Entidad | Responsabilidad |
| --- | --- |
| `segments`, `segment_versions` | Universo de estudio y versiones de filtros estructurados, geografía, seniority, modalidad y familias de puesto. |
| `capture_protocols`, `capture_runs` | Fuentes, consultas, frecuencia, filtros, límites, versiones, estado, errores, páginas y cantidades recuperadas. |
| `postings` | Identidad canónica de un anuncio, empresa normalizada, primera y última observación. |
| `source_postings` | Identidad específica por fuente, URL e identificador externo; vínculo a anuncio canónico. |
| `observations` | Evidencia original inmutable, fecha de observación, fecha de publicación si existe, hash, ejecución y resultado de disponibilidad. |
| `posting_versions` | Contenido normalizado versionado y su vigencia conocida; nuevas versiones cuando cambia el contenido o su interpretación. |
| `segment_memberships` | Pertenencia a un segmento y versión de su definición; evitar reclasificar silenciosamente la historia. |
| `skills`, `skill_aliases`, `posting_requirements` | Taxonomía versionada, alias y requisitos con evidencia textual, obligatoriedad, nivel y método de extracción. |
| `compensations` | Límites originales, moneda, periodicidad, bruto/neto, jornada y contratación; derivados comparables con transformación documentada. |
| `duplicate_reviews` | Candidatos, decisiones de unión/separación, evidencia y trazabilidad reversible. |
| `profiles`, `profile_skills` | Perfil personal, habilidades, evidencia de dominio, restricciones y esfuerzo estimado de aprendizaje. |
| `analysis_runs`, `insights` | Definición del análisis, versiones, fecha de corte, resultados y referencias a su cohorte de evidencia. |
| `forecast_runs`, `forecast_evaluations` | Predicciones emitidas, intervalos, horizontes y resultados posteriores; incorporar en la fase predictiva. |

No crear todas las tablas desde la primera entrega: introducirlas en las fases que las necesitan.

### Reglas de datos

- Para admitir un borrador: título o descripción identificable, procedencia y fecha de observación. Para admitirlo en una métrica: validaciones propias de esa métrica.
- Empresa desconocida es un estado explícito, no una empresa ficticia compartida.
- `posted_at`, `first_seen_at` y `observed_at` son conceptos distintos. Si la publicación se desconoce, una métrica por primera observación no se rotula como nuevas publicaciones.
- Conservar timestamps en UTC y presentar fechas según la zona configurada; fijar límites de periodos reproducibles.
- Salarios positivos y límites ordenados cuando ambos existen. Aceptar límites abiertos; no inventar el extremo faltante.
- No asumir mensualidad, MXN, bruto ni jornada completa. Los valores desconocidos limitan comparabilidad.
- Conversión monetaria o inflación requiere serie externa, fecha, fuente y versión. No equiparar bruto y neto mediante un porcentaje genérico.
- Indexar identidades por fuente, fechas y relaciones de cohortes. Diseñar idempotencia por ejecución y elemento, permitiendo observaciones de capturas posteriores.
- Una observación repetida puede referenciar el mismo contenido sin duplicar el texto, conservando evidencia de que se volvió a ver.
- La ausencia en una página de resultados no demuestra cierre ni contratación. Registrar disponibilidad desconocida y fallos de captura por separado.
- Una republicación requiere reglas explícitas: no contar automáticamente una URL nueva como nueva oportunidad ni unir anuncios sólo por similitud.
- Correcciones humanas conservan el original, autor o procedencia, fecha y motivo. Recalcular derivados con versión nueva.

## 6. Protocolo de captura y control de sesgos

Antes de comparar periodos, registrar un protocolo por segmento: fuentes, consultas, filtros, frecuencia, ordenamiento, paginación y límites. Mostrar rupturas cuando cambie.

Separar registros manuales exploratorios de capturas sistemáticas, permitiendo analizar ambos pero sin mezclarlos silenciosamente. Si se alcanza un límite de resultados, señalar truncamiento. Una captura fallida o parcial no produce un cero de demanda.

Comparar fuentes estables y estratos comparables. Mostrar cambios de composición por empresa, fuente, región y seniority. Ponderar sólo si existen totales de referencia defendibles; no crear pesos que aparenten eliminar un sesgo desconocido.

Permitir importar vacantes existentes como observaciones iniciales mediante una acción explícita y trazable. No inventar historial previo ni interpretar `created_at` como fecha de publicación. Los segmentos actuales permanecen operativos hasta definir una transición explícita.

## 7. Contrato inicial de métricas

| Métrica | Definición y precauciones |
| --- | --- |
| Nuevos anuncios observados | Anuncios canónicos cuya primera observación cae en el periodo; separar de nuevas publicaciones con fecha conocida. |
| Empresas anunciantes | Empresas identificadas distintas; informar cuántos anuncios tienen empresa desconocida. |
| Presencia de habilidad | Anuncios analizables con mención / anuncios analizables del segmento y periodo. Mostrar también total y cobertura de extracción. |
| Cambio de presencia | Diferencia en puntos porcentuales entre periodos comparables; mostrar niveles originales y tamaño de muestra. |
| Crecimiento relativo | Cambio relativo con tratamiento explícito de base cero y bases pequeñas; no sustituye el cambio absoluto. |
| Concentración | Participación de principales empresas y HHI sobre anuncios; acompañar con cobertura de empresa identificada. |
| Coocurrencia | Soporte conjunto, P(B dado A) y lift = P(A y B)/(P(A)P(B)); exigir soporte suficiente y evitar rankings de parejas rarísimas. |
| Salarios anunciados | Distribuciones separadas de mínimos y máximos, percentiles y amplitud entre anuncios comparables; informar límites abiertos y cobertura. |
| Barreras de entrada | Distribuciones de experiencia, seniority declarado/inferido, inglés, educación y requisitos obligatorios identificados. |
| Remoto accesible | Anuncios remotos compatibles con residencia/restricciones conocidas; separar elegibilidad desconocida. |
| Calidad | Faltantes por campo, errores, duplicados revisados, empresas/fuentes distintas, continuidad y capturas parciales. |

El punto medio salarial, si se ofrece, se etiqueta como resumen del rango anunciado y nunca como salario pagado o esperado. Definir e implementar correctamente el cálculo de cuantiles, incluido el caso de muestras pares.

No estimar competencia, escasez real de talento, tiempo hasta contratación ni tasa de contratación usando únicamente anuncios. Una desaparición sólo informa disponibilidad observada bajo un protocolo definido.

## 8. Métodos estadísticos y condiciones de uso

### Primera capa: descripción y comparación

Conteos, proporciones, percentiles, ventanas móviles y comparaciones estratificadas. Publicar incertidumbre de estimación donde proceda, explicando sus supuestos y que no representa incertidumbre total del mercado.

### Segunda capa: inferencia y señales

- Estimación beta-binomial de presencia de habilidades con prior documentado; evaluar sensibilidad al prior y a dependencia entre anuncios de la misma empresa.
- Bootstrap agrupado por empresa y/o bloque temporal cuando sea adecuado; evitar tratar republicaciones como observaciones independientes.
- Magnitud mínima de cambio de interés práctico, persistencia temporal y control de comparaciones múltiples para explorar muchas habilidades.
- Asociaciones salariales ajustadas por variables comparables. Verificar soporte, colinealidad, selección de anuncios con salario y estabilidad fuera de muestra. No convertir coeficientes en promesas causales.
- Detección de cambios y tecnologías emergentes después de descartar fallos de captura y cambios de taxonomía.

La puntuación de calidad se presentará por dimensiones. No usar una etiqueta genérica de confianza ni un porcentaje automático sin definición y validación.

### Tercera capa: simulaciones personales

Comparar perfil actual y alternativas bajo la misma cohorte. Clasificar compatibilidad como requisitos conocidos cubiertos, brechas conocidas y condiciones desconocidas.

Calcular conjuntos de anuncios adicionales compatibles, evitando sumarlos dos veces cuando dos habilidades abren los mismos anuncios. Distinguir requisitos obligatorios de deseables y respetar dependencias de aprendizaje. El esfuerzo lo proporciona o corrige el usuario.

Mostrar opciones no dominadas en acceso, compensación publicada, esfuerzo y estabilidad. Si se ofrece un ranking ponderado, exponer pesos y permitir cambiarlos. No llamarlo probabilidad de contratación ni retorno económico garantizado.

### Cuarta capa: pronósticos

Empezar por series agregadas semanales y horizontes cortos configurables. Comparar modelos candidatos con referencias de último valor y, cuando exista historia suficiente, referencia estacional.

Usar evaluación de origen móvil: entrenar sólo con información disponible antes del corte. Registrar fecha real de disponibilidad de datos y clasificaciones para evitar filtración del futuro. Medir MAE y cobertura/amplitud de intervalos; no depender de MAPE con ceros.

Habilitar un modelo sólo con mejora sostenida respecto a referencias, intervalos razonablemente calibrados y protocolo estable. Los umbrales se fijan antes de evaluar. Si no cumple, mostrar descripción o escenario sin presentarlo como pronóstico validado.

## 9. Interfaz propuesta

Entrada propia de navegación: Observatorio.

1. **Segmentos y captura:** configurar universo, protocolo, ejecuciones y salud de fuentes.
2. **Vacantes del observatorio:** alta manual, importación, filtros, revisión y duplicados.
3. **Detalle e historial:** original, datos normalizados, cambios y evidencia por requisito.
4. **Panorama y evolución:** comparación temporal, requisitos, empresas y cobertura.
5. **Compensación:** cohortes comparables, rangos y calidad de normalización.
6. **Mi perfil y simulaciones:** habilidades actuales, restricciones y alternativas.
7. **Insights y alertas:** hallazgos explicables, historial y evidencia.
8. **Pronósticos:** sólo series elegibles, rangos e historial de evaluación.

Toda vista analítica muestra segmento, periodo, fecha de corte, tamaño de muestra y cobertura. Los detalles metodológicos están disponibles al abrir la explicación, sin saturar el recorrido principal. Incluir estados vacíos, muestra insuficiente y captura incompleta.

Las alertas inicialmente quedan dentro de la aplicación. Canales externos requieren una configuración y autorización posterior específica.

## 10. Fases y criterios de aceptación

### Fase 0 — Contratos y diseño verificable

Entregables: glosario, diagrama relacional, contratos de métricas, estados de calidad, diseño de pantallas y datos de ejemplo. Elegir un primer segmento de prueba configurable; no codificarlo como único mercado permitido.

Aceptación: existen ejemplos acordados de anuncio duplicado, salario incompleto, requisito inferido, captura fallida y cambio de contenido; cada uno tiene un comportamiento esperado. Delimitar qué conectores conservan campos originales y cuáles requieren extensión.

### Fase 1 — Registro independiente e historial

Entregables: migraciones aditivas, entidades/repositorios, alta manual, listado, detalle, observaciones y versiones; validación de compensación y requisitos. Incorporar catálogos iniciales editables/versionados.

Aceptación: registrar y actualizar un anuncio conserva originales e historial; no crea filas de seguimiento ni altera postulaciones; los campos desconocidos se representan sin inventarlos; editar una interpretación mantiene trazabilidad.

### Fase 2 — Captura repetible y calidad

Depende de fase 1.

Entregables: adaptadores a conectores existentes, ejecución separada, programación, protocolos versionados, identidad por fuente, deduplicación inicial determinista y revisión de candidatos entre fuentes. Incorporar salud de captura.

Aceptación: repetir una ejecución no duplica anuncios ni observaciones de esa ejecución; una captura posterior registra que el anuncio se volvió a ver; cambios generan versiones; fallos parciales y límites no aparecen como caída de demanda. Unión y separación de duplicados son auditables y reversibles.

### Fase 3 — Primer dashboard para decisiones

Depende de fases 1 y 2.

Entregables: cohortes reproducibles, contratos de métricas implementados, evolución semanal, comparación de periodos, compensación comparable y panel de calidad. Acceso de cada cifra a sus registros fuente.

Aceptación: las métricas coinciden con un conjunto de referencia calculado independientemente; muestran denominadores y exclusiones; no mezclan monedas o periodos incompatibles; periodos incompletos y cambios del protocolo quedan señalados.

**Primer corte utilizable: fases 0–3.** Permite entender cambios y salarios observados mientras acumula historia para capacidades posteriores.

### Fase 4 — Señales estadísticas e insights

Depende de fase 3 y de una serie suficientemente continua para cada análisis.

Entregables: incertidumbre, cambios persistentes, coocurrencias y concentración; primeras asociaciones salariales sólo donde sean defendibles. Motor de insights basado en reglas verificables y resultados estadísticos.

Aceptación: cambios de captura no generan automáticamente señales de mercado; pruebas con datos sintéticos evalúan detección y falsas alertas; cada insight conserva evidencia, supuestos, magnitud, limitaciones y versión. Resultados exploratorios se identifican como tales.

### Fase 5 — Perfil y simulación de aprendizaje

Depende de fase 3; aprovecha fase 4 cuando esté disponible.

Entregables: perfil independiente, restricciones, brechas, escenarios individuales y combinados, esfuerzo y comparación de alternativas.

Aceptación: resultados reproducibles por perfil/cohorte; habilidades solapadas no duplican oportunidades; requisitos desconocidos permanecen visibles; recomendaciones cambian de forma explicable con restricciones y pesos; no se presentan probabilidades de contratación.

**Segundo corte utilizable: fases 4–5.** Añade apoyo personalizado a decisiones.

### Fase 6 — Pronósticos evaluados y alertas

Depende de fase 4 y de historia suficiente con captura estable; no depende de un calendario prometido.

Entregables: evaluación temporal, referencias simples, registro de predicciones, intervalos, seguimiento de errores y alertas internas configurables.

Aceptación: un modelo que no supera los criterios definidos no se publica como pronóstico validado; ninguna variable usa información futura; deterioro y ruptura de protocolo desactivan o degradan la capacidad predictiva de forma visible.

### Fase 7 — Operación y ampliaciones

Entregables: agregados o caché cuando se justifiquen, exportación de evidencia y metodología, respaldo/restauración, retención configurada sin perder historia analítica acordada, y observabilidad de ejecuciones.

Aceptación: consultas cumplen objetivos de rendimiento definidos con volumen representativo; restauración conserva trazabilidad; cambios de catálogos identifican análisis afectados. Extracción asistida por IA y preguntas en lenguaje natural se incorporan sólo después de validar resultados estructurados.

## 11. Estrategia de pruebas y liberación

- Dominio: validación de rangos, estados desconocidos, identidad, versiones, compatibilidad y duplicados.
- Integración con PostgreSQL: migraciones, restricciones, transacciones e idempotencia; elegir el mecanismo de base de pruebas compatible con el entorno disponible.
- Estadística: conjuntos pequeños con resultados independientes, casos degenerados, datos sintéticos y evaluación temporal reproducible.
- Extracción: corpus etiquetado de anuncios en español e inglés; precisión y exhaustividad por campo, alias ambiguos, negaciones y obligatoriedad. Versionar el corpus.
- Presentación: flujos de registro, revisión, navegación a evidencia y estados de insuficiencia; pruebas de accesibilidad y filtros principales.
- Regresión: ejecutar la suite relevante y verificar captura y seguimiento existentes. Ejecutar la suite completa antes de integrar hitos.
- Liberación incremental mediante rutas nuevas y activación controlada si se necesita. Migraciones aditivas; deshabilitar el módulo no elimina datos.
- No modificar migraciones ya aplicadas. Verificar respaldo y compatibilidad antes de cada cambio de esquema en un entorno con datos reales.

No se ejecutaron pruebas de aplicación por esta entrega de documentación. Las verificaciones anteriores corresponden a la implementación futura.

## 12. Riesgos y respuesta prevista

| Riesgo | Respuesta |
| --- | --- |
| Muestra pequeña o seleccionada por preferencias | Delimitar mercado observado, distinguir captura manual/sistemática y abstenerse de conclusiones sin soporte. |
| Cambio de portal, ordenamiento o límite | Registrar protocolo y salud; señalar discontinuidades y truncamiento. |
| Clasificación errónea | Evidencia textual, revisión, corpus etiquetado y extracción versionada. |
| Duplicación y republicación | Identidad por fuente, candidatos entre fuentes y análisis de sensibilidad a deduplicación. |
| Salarios no comparables o ausentes | Cohortes compatibles, exclusiones visibles y cobertura por segmento; no imputar como si fueran observados. |
| Demasiadas señales espurias | Soporte, magnitud práctica, persistencia y control de comparaciones múltiples. |
| Complejidad prematura | Monolito actual, entregas verticales y modelos que deben superar referencias sencillas. |
| Lenguaje convincente con cifras erróneas | Cálculo determinista, trazabilidad y explicaciones basadas exclusivamente en resultados estructurados. |

## 13. Decisiones tomadas y pendientes

Tomadas para orientar la implementación: separación del seguimiento; historial desde la primera fase; stack actual; desconocidos explícitos; captura trazable; evidencia accesible; pronósticos condicionados a validación; no exigir IA para funcionar.

Pendientes para resolver al iniciar las fases correspondientes: primer segmento operativo, fuentes activas, frecuencia/límites, taxonomía inicial, definición práctica de cambio relevante, fuentes externas de moneda/inflación y objetivos de rendimiento. Estas decisiones no impiden comenzar el diseño y registro manual.

No se asignan fechas de entrega aún: estimar después de fase 0 y de revisar la integridad de extracción de cada conector. El tiempo de acumular historia es distinto del tiempo de desarrollar software.

## 14. Continuidad y seguimiento del plan

Este archivo es la referencia de producto y de implementación del observatorio. Actualizarlo en el mismo cambio que implemente o altere una decisión relevante. Los estados siguientes representan entregables completos, no sólo código iniciado.

- [x] Propuesta y plan persistidos en el repositorio.
- [ ] Fase 0: contratos y diseño verificable.
- [x] Fase 1: registro independiente e historial (alcance manual, incrementos 1–3).
- [ ] Fase 2: captura repetible y calidad.
- [ ] Fase 3: dashboard y cohortes reproducibles.
- [ ] Fase 4: señales e insights estadísticos.
- [ ] Fase 5: simulaciones personales.
- [ ] Fase 6: pronósticos evaluados y alertas.
- [ ] Fase 7: operación y ampliaciones.

Al completar una fase: documentar fecha, cambios, migraciones, pruebas ejecutadas, límites conocidos y siguiente entrega. Si cambia el alcance, explicar motivo e impacto; no borrar decisiones previas sin dejar registro.

## 15. Fuentes metodológicas de referencia

Las siguientes fuentes sustentan las precauciones metodológicas; no representan una validación de los modelos propuestos ni de los datos propios:

- OCDE: [Skills for the Digital Transition: uso y límites de anuncios en línea](https://www.oecd.org/en/publications/skills-for-the-digital-transition_38c36777-en/full-report/component-5.html).
- OIT: [Methodological issues related to the use of online labour market data](https://webapps.ilo.org/static/english/intserv/working-papers/wp068/index.html).

Consultar fuentes primarias específicas al elegir e implementar cada método; documentar sus supuestos y la evidencia de validación local.


## 16. Bitácora de implementación

### 2026-11-14 — Incremento 1: registro manual e historial

Implementado:

- Migración aditiva V10 con tablas independientes de anuncios y versiones, sin relación con seguimiento.
- Dominio, puerto de repositorio, adaptador JDBC transaccional y servicio de registro/corrección.
- API de alta, listado paginado, detalle y correcciones completas con control de versión.
- Pantalla `/observatory` y entrada Observatorio en navegación; formulario, evidencia original e historial.
- Validaciones de título, fuente, fecha de observación, URL, importes, moneda y límites abiertos; desconocidos explícitos.
- Evidencia inicial inmutable desde la API; cada corrección conserva motivo, método, fecha y valores previos.
- [Contratos del incremento](OBSERVATORIO_CONTRATOS.md): esquema, API, casos de referencia y límites conocidos.

Verificación: suite Maven con 103 pruebas, sin fallos ni errores, incluidas 4 nuevas pruebas de integración del observatorio con H2 y Flyway. Verificación de sintaxis del JavaScript con Node. Mockito se ejecutó como agente explícito para evitar el bloqueo de autoacoplamiento en este entorno:

```sh
./mvnw test -q -DargLine=-javaagent:/home/dimedical/.m2/repository/org/mockito/mockito-core/5.23.0/mockito-core-5.23.0.jar
node --check src/main/resources/static/js/observatory.js
```

El path del agente es local al entorno, no una dependencia nueva del proyecto. No se verificó contra PostgreSQL real ni mediante interacción en navegador; la plantilla se renderizó en integración. La migración se aplicó sólo a la base de pruebas, no a una base con datos personales.

Qué falta: fases 0 y 1 siguen parciales. Completar auditoría de conectores y ejemplos numéricos de métricas; catálogos versionados de habilidades, requisitos estructurados con evidencia/obligatoriedad y calidad por análisis. Después integrar observaciones repetidas, deduplicación, protocolos y captura automática (fase 2). Las fases 3–7 permanecen pendientes. El usuario debe recibir este estado en cada incremento.


### 2026-11-14 — Incremento 2: habilidades, evidencia y calidad básica

Implementado:

- Migración aditiva V11: identidades de habilidades, versiones inmutables, alias y requisitos por versión del anuncio con claves foráneas compuestas. Semillas Java, SQL, Docker y Spring Boot.
- API y servicio de catálogo: alta, edición con motivo/control de revisión e historial. Renombrar habilidades conserva las referencias históricas.
- Requisitos estructurados con habilidad/versionado, obligatoriedad REQUIRED/DESIRED/UNKNOWN y cita exacta del original. Rechazo de citas inexistentes, versiones inexistentes y habilidades duplicadas dentro de una versión del anuncio.
- Revisión explícita de habilidades: datos heredados quedan sin revisar, una lista revisada vacía es distinta de una sin revisar. Registro/corrección y requisitos se guardan en una sola transacción.
- Calidad básica por revisión: estado de revisión de habilidades y campos salariales faltantes. No se presenta como confianza estadística ni como comparabilidad completa.
- Pantalla: editor del catálogo con historial, filas de habilidades/evidencia, selección de obligatoriedad, preservación de versiones históricas y explicación de campos faltantes.
- [Métricas de referencia](OBSERVATORIO_METRICAS_REFERENCIA.md) con [fixture CSV](fixtures/observatorio_metricas.csv): denominadores, cobertura, cambios absolutos/relativos, base cero, coocurrencia/lift, HHI, cuantiles y límites salariales abiertos. Estos cálculos son contratos para la fase 3, no un dashboard ya implementado.

Verificación:

- Suite completa: 107 pruebas, 0 fallos, 0 errores, 0 omitidas; incluye cuatro nuevas pruebas de integración de catálogo/requisitos con H2 y Flyway.
- Sintaxis JavaScript verificada con Node; renderizado de plantilla en integración.
- Prueba real con Chromium y servidor temporal usando perfil test/H2: registro con requisito y evidencia, corrección con conservación de ambas versiones y alta de habilidad desde la pantalla. Resultado `OBSERVATORY_BROWSER_SMOKE_OK`. Base efímera sin modificar datos personales. El sandbox impidió inicialmente abrir el puerto; la ejecución de la prueba con permisos ampliados terminó correctamente.
- Inspección visual de captura del formulario e historial; revisión independiente de medianas del fixture.
- No se aplicó V11 a PostgreSQL real ni se ejecutó scraping.

Qué falta:

- Fase 0: auditoría detallada de evidencia por conector y completar campos/contratos de experiencia, modalidad y elegibilidad; las métricas documentadas aún requieren implementación y pruebas numéricas en fase 3.
- Fase 1: fecha de publicación, jornada, contratación y geografía estructurada; estados de aptitud completos por análisis. El catálogo básico y los requisitos manuales ya están implementados; niveles de dominio, inferencias automáticas y resolución de identidades ambiguas quedan por desarrollar según la fase que los necesite.
- Fase 2: segmentos/protocolos propios, ejecuciones de captura, observaciones originales repetidas, salud de fuentes y deduplicación.
- Fases 3–7: dashboard, inferencia, simulaciones, pronósticos y operación ampliada siguen pendientes.

Siguiente incremento propuesto: completar los campos estructurados y su aptitud para comparaciones; después implementar segmentos/protocolos y las observaciones repetidas. Las fases 0 y 1 siguen marcadas como parciales hasta satisfacer todos sus criterios.


### 2026-11-14 — Incremento 3: contexto y comparabilidad salarial

Implementado:

- Migración aditiva V12: contexto por versión (publicación, país, región, ciudad, modalidad, horas semanales, contratación, nivel, alcance remoto, restricciones y evidencia).
- Validación de país ISO, jornada positiva hasta 168 horas, consistencia del alcance remoto y cita exacta del contexto. La publicación no puede superar la observación, ni siquiera mediante correcciones.
- Contexto desconocido para registros heredados y solicitudes anteriores; no se infiere a partir del título, ubicación libre o moneda.
- Calidad contextual con motivos de exclusión salarial, indicador de fecha de publicación conocida y aptitud para agrupación conservadora. Los límites salariales abiertos siguen siendo válidos.
- Claves de cohorte y comparación de dominio que separan títulos, monedas, periodos, bases, geografías, jornadas, modalidades, contratos, seniority y restricciones. No hay conversión ni agrupación semántica implícita.
- Formulario y visualización de historial para todos los nuevos campos; conversión local/UTC preservando milisegundos de publicación.
- [Contrato detallado de contexto y comparabilidad](OBSERVATORIO_CONTEXTO.md).

Verificación:

- Suite completa Maven: 112 pruebas, 0 fallos, 0 errores, 0 omitidas. Se añadieron tres pruebas de integración y dos de dominio; se actualizó la expectativa de campos faltantes para solicitudes anteriores (de 4 a 9).
- Integración con H2 y Flyway: persistencia, correcciones que conservan versiones, compatibilidad con contexto desconocido, fechas imposibles y datos inválidos sin persistencia parcial.
- Dominio: jornadas 40 y 40.00 compatibles; monedas, países, contratos, títulos o jornadas diferentes separados; remoto sin alcance excluido; nacional y mundial separados.
- Prueba real con Chromium/servidor local/perfil test: alta con contexto completo y requisito, corrección sin perder precisión de publicación, historial, catálogo y vista móvil de 390 px sin desbordamiento horizontal. Resultado `OBSERVATORY_CONTEXT_BROWSER_SMOKE_OK`. Inspección visual de captura móvil.
- Node verificó sintaxis JavaScript; revisión de espacios con git diff --check. Sin scraping real ni aplicación de migraciones a la base personal. PostgreSQL real sigue pendiente de verificación.

Estado: fase 1 completada en su alcance de registro manual, catálogo inicial editable/versionado, evidencia inicial, correcciones e historial. La evidencia inicial vive en el registro del anuncio; el modelo de múltiples observaciones independientes se implementará en fase 2. La aptitud actual representa completitud contextual y revisión humana, no equivalencia económica ni representatividad estadística.

Qué falta:

- Fase 0: auditoría detallada por conector, contratos de experiencia/idiomas/estudios y elegibilidad personal. Las familias de puesto y el catálogo geográfico requieren normalización explícita. No se declara la fase 0 completa.
- Fase 2 (siguiente): segmentos y protocolos propios versionados, capturas y observaciones repetidas, salud de fuentes, idempotencia, deduplicación y adaptación de conectores.
- Fases 3–7: dashboard y métricas ejecutables, inferencia, simulaciones, pronósticos y operación ampliada.
- Ampliaciones: fechas publicadas aproximadas o sin hora, normalización geográfica, niveles de habilidad y enriquecimiento automático con trazabilidad. No convertir fechas parciales en instantes inventados.

Siguiente incremento propuesto: segmentos/protocolos y base de ejecuciones/observaciones, conservando intactos los segmentos y el seguimiento actuales.


### 2026-11-14 — Incremento 4: segmentos, protocolos y observaciones repetidas

Implementado:

- Migración aditiva V13 con segmentos/versiones y protocolos/versiones independientes del seguimiento, ejecuciones y observaciones. Claves foráneas fijan cada revisión/configuración y su evidencia.
- Segmentos con familia de puesto, país, seniority y modalidad; protocolos con fuente, consulta, instrucciones de filtros, frecuencia prevista, límite, orden, paginación y tipo de muestra. Cambios con motivo y control de revisión.
- Bitácoras manuales con estados en curso/completa/parcial/fallida, páginas, truncamiento, notas y conteo de eventos. Cierres finales inmutables; capturas truncadas o con disponibilidad desconocida no se declaran completas.
- Inicio idempotente por protocolo/clave y observación idempotente por ejecución/elemento; reutilizar claves con otros datos falla. Reintentos idénticos funcionan incluso después del cierre.
- Observaciones repetidas con fecha, texto, hash y disponibilidad. Texto/interpretación sin cambios reutilizan revisión; cambios crean revisión nueva con su propia evidencia. Correcciones posteriores validan contra esa evidencia y conservan las anteriores.
- Fechas derivadas de primera/última observación disponible. Ausencias o fallos no equivalen a contratación, cierre ni demanda cero.
- Pantalla `/observatory/captures`: crear/editar configuraciones, revisar historial, iniciar y finalizar ejecuciones, seleccionar anuncios existentes y consultar evidencia. El editor de anuncios permite guardar una nueva observación desde una ejecución.
- Preservación de evidencia y correcciones existentes mediante observación inicial sin inventar ejecuciones. Normalización de escalas numéricas y orden de habilidades para evitar versiones artificiales por representación.
- [Contrato y guía de uso](OBSERVATORIO_CAPTURAS.md).

Verificación:

- Suite completa: 120 pruebas, 0 fallos, 0 errores, 0 omitidas. Siete nuevas pruebas de integración cubren configuración histórica, reintentos, cambios de evidencia, correcciones, fallos/truncamiento, fuente, fechas, atomicidad y rutas; una prueba adicional verifica migrar V12→V13 con evidencia y correcciones previas.
- Chromium con perfil test/H2: registro inicial; creación de segmento/protocolo; inicio de ejecución; observación sin cambios y otra con cambio salarial; conservación de revisiones; cierre; edición posterior de segmento sin cambiar la configuración de la captura. Resultado `OBSERVATORY_CAPTURE_BROWSER_SMOKE_OK`.
- Vista móvil de 390 px sin desbordamiento horizontal; inspección visual de captura de escritorio. Node verificó ambos JavaScript; git diff --check sin errores.
- Las pruebas usan base H2 efímera. No se ejecutaron scrapers ni se aplicaron migraciones a PostgreSQL personal. PostgreSQL real y pruebas específicas de concurrencia siguen pendientes.

Estado: fase 2 parcial. El alcance de este incremento está completo para el flujo manual de anuncios ya registrados. La relación muestra/segmento se conserva a través de la ejecución y de las versiones fijadas del protocolo; no se reclasifica automáticamente la historia. Las instrucciones de filtros son manuales, no filtros de scraping ejecutables. La frecuencia todavía no programa tareas.

Qué falta y siguiente entrega:

- Próximo incremento: identidad por fuente y deduplicación determinista; revisión de candidatos y unión/separación reversible con trazabilidad.
- Después: auditoría de evidencia por conector, alta directa desde capturas, filtros ejecutables, captura automática y programación independiente. Completar validación operativa con PostgreSQL.
- Fase 0 sigue parcial (auditoría de conectores y contratos especializados). Fases 3–7 siguen pendientes; el conteo de observaciones de una bitácora no es un dashboard de demanda.


### 2026-11-14 — Incremento 5: identidad por fuente y deduplicación reversible

Implementado:

- Migración aditiva V14: identidad de cada registro por fuente/URL/identificador externo, exclusión mutua transaccional y revisiones inmutables de decisiones por pareja. Conflictos históricos preservados, sin borrar ni unir automáticamente.
- Alta con identificador externo opcional y comprobación determinista de coincidencias. Respuesta 409 con enlaces a registros existentes; opción de registro independiente con motivo explícito ante URLs o identificadores reutilizados.
- Normalización conservadora de URLs: host/esquema/puerto por defecto y parámetros de seguimiento conocidos. Se preservan rutas, identificadores de consulta, fragmentos y diferencias HTTP/HTTPS.
- Candidatos por fuente+URL, fuente+ID, contenido exacto suficientemente largo o título+empresa conocidos. Las señales no se presentan como probabilidades ni disparan fusiones automáticas.
- Grupos de identidad analítica, decisiones de anuncios distintos y separación mediante nuevas revisiones. Capturas, originales, requisitos y correcciones permanecen en sus registros originales.
- Control de versiones revisadas; aviso cuando cambia la información tras una decisión. Rechazo de uniones contradictorias o redundantes para mantener separación efectiva al deshacer un vínculo.
- Consulta de identidad bajo fecha de corte y conservación de todos los cambios de decisión; reintentos idénticos sin duplicar auditoría.
- Pantalla `/observatory/duplicates`: candidatos, comparación, decisiones, historial y grupos. El formulario de alta explica coincidencias y conserva el motivo de alta independiente.
- [Contrato y guía de identidad/duplicados](OBSERVATORIO_DUPLICADOS.md).

Verificación:

- Suite completa: 129 pruebas, 0 fallos, 0 errores, 0 omitidas. Nueve pruebas nuevas: cinco de integración de identidad/revisión, dos de normalización, una de migración V13→V14 con conflictos previos y una de concurrencia con dos altas simultáneas.
- Integración: bloqueos de altas accidentales sin evidencia parcial, excepciones justificadas, grupos sin borrar originales, reversión, resolución histórica bajo el instante exacto de decisión, contradicciones transitivas y revisiones obsoletas.
- Migración: URLs históricas coincidentes permanecen como dos registros y un candidato; no se crean decisiones ni se elimina evidencia.
- Concurrencia H2: un alta confirma y otra recibe la coincidencia para una misma fuente/identidad. Los datos de esa prueba se limpian explícitamente.
- Chromium con servidor local y perfil test/H2: alta, conflicto por URL normalizada, alta independiente justificada, candidato, unión, separación, decisión de anuncios distintos e historial. Resultado `OBSERVATORY_DUPLICATE_BROWSER_SMOKE_OK`; comprobación móvil de 390 px sin desbordamiento e inspección visual de escritorio.
- Sintaxis JavaScript y git diff --check verificados. Sin scraping real ni migraciones sobre datos personales. PostgreSQL real continúa pendiente.

Estado: fase 2 en curso. La identidad y deduplicación están listas para revisión manual y para ser consumidas por la futura analítica; el listado conserva todos los registros y todavía no hay dashboard del observatorio. La fase no se marca completa porque faltan automatización y validación operativa.

Qué falta:

- Próximo bloque: auditoría de evidencia por conector, filtros ejecutables y altas directamente dentro de una captura; adaptación de los conectores para conservar evidencia y utilizar identidad/idempotencia del observatorio.
- Después: captura automática y programación independiente, validación real con PostgreSQL y pruebas de fallos por portal.
- Fase 3: dashboard, cohortes bajo fecha de corte y métricas reproducibles usando las identidades resueltas. Es el siguiente gran bloque después de cerrar fase 2.
- Fases 4–7: señales estadísticas, simulaciones personales, pronósticos y operación ampliada. La fase 0 mantiene pendientes especializados y la auditoría de conectores.

### 2026-11-14 — Incremento 6: recolección bajo demanda con evidencia

Implementado:

- Auditoría de lectores, parsers y filtros de los cuatro portales. Se documentaron retornos de resumen ante errores, pérdida de HTML y filtros ignorados/aproximados del flujo anterior.
- Migración aditiva V15: plan ejecutable por versión de protocolo, bitácora de intentos y archivo de HTML/evidencia por elemento. Los protocolos anteriores conservan su configuración manual.
- Plan estructurado de portal/ubicación/modalidad/antigüedad; rechazo explícito de filtros que la traducción actual omite o aproxima. Vista previa de URL y alcance real. El contexto del puesto no se infiere de esos filtros.
- Colector independiente de primera página para LinkedIn, Indeed, OCC y Computrabajo, reutilizando parsers, constructores de URL, sesiones y límites del proyecto. El seguimiento y sus lectores mantienen su flujo.
- Evidencia DOM de tarjeta y detalle, identidad comprobada antes de enriquecer, texto derivado y método versionado. Paneles sin verificar no contaminan la transcripción; fallos por elemento conservan el HTML recibido.
- Archivo de evidencia antes de importar, con transacciones breves fuera del trabajo de navegador. Los errores de datos dejan filas ERROR revisables; los importados apuntan a su anuncio y observación.
- Alta directa de un anuncio dentro de una ejecución con una sola observación inicial, y nuevas observaciones por identidad exacta. Reintentos idempotentes y rechazo de identidades ambiguas.
- Datos automáticos AUTO_CAPTURE_V1 pendientes de revisión, sin asumir moneda/periodicidad/bruto-neto ni habilidades revisadas. Un texto sin cambios no sobrescribe interpretaciones humanas.
- Ejecución bajo demanda desde la interfaz, seguimiento de progreso, cierre parcial/fallido y conservación de errores. La primera página no se presenta como cobertura completa ni un vacío de extracción como cero demanda.
- Exclusión de recolectores simultáneos por ejecución; interrupción con motivo, cierre de intentos abandonados y bloqueo de importaciones tardías. Evidencia recibida después de interrumpir se conserva como ERROR.
- [Guía y auditoría de recolección](OBSERVATORIO_RECOLECCION.md).

Verificación:

- Suite completa: 143 pruebas, 0 fallos, 0 errores, 0 omitidas. Catorce pruebas nuevas: tres de evidencia de parsers, dos de filtros/URLs, una de migración V14→V15 y ocho de integración del flujo nuevo.
- Fixtures de los cuatro portales: evidencia de listado, inclusión de detalle verificado y rechazo de paneles incompatibles. Reglas de filtros exactos y URLs efectivas comprobadas en código local.
- Integración con colector de prueba: no hay transacción durante recolección; HTML preservado ante importe inválido; fallo después de importar un elemento; resultado vacío sin conclusión de mercado; repetición sin perder revisión humana; alta directa e idempotente; solicitudes concurrentes; interrupción y recuperación de intento abandonado.
- Chromium con HTML de muestra y base H2: configuración de filtros, vista previa, ejecución parcial, evidencia desplegable, señal de datos automáticos, alta manual directa y finalización. Resultado `OBSERVATORY_COLLECTION_BROWSER_SMOKE_OK`; vista móvil de 390 px sin desbordamiento e inspección visual de escritorio.
- Node verificó JavaScript; git diff --check sin errores. No se contactaron portales reales ni se ejecutaron migraciones sobre datos personales. PostgreSQL y sesiones/selectores reales siguen pendientes.

Estado: fase 2 en curso. Se completó la integración inicial de recolección bajo demanda, limitada a una página y muestreo exploratorio. La frecuencia no ejecuta tareas. No se declara cobertura completa de los portales ni cierre de validación operativa.

Qué falta:

- Próximo incremento: programación independiente, recuperación automática y política de reintentos. La interrupción manual ya permite cerrar un intento abandonado sin borrar evidencia.
- Paginación y condiciones de final de resultados verificables; evaluación real por portal y PostgreSQL.
- Fase 3: dashboard y métricas con cohortes, cobertura, identidades y revisión de datos. Fases 4–7 siguen pendientes.

### 2026-11-14 — Incremento 7: programación independiente y recuperación automática

Implementado:

- Migración aditiva V16: horarios versionados, tareas persistentes, ventanas, reintentos, tokens y señales de actividad. Los intentos anteriores conservan su inicio; no se activan horarios automáticamente.
- Pantalla `/observatory/schedules`: alta pausada por defecto, activación/pausa con motivo, versión concreta de protocolo, calendario, historial y tareas enlazadas a capturas.
- Temporizador independiente del seguimiento y trabajadores limitados. Una clave idempotente por tarea evita repetir ejecuciones; los intervalos omitidos se registran sin inventar capturas pasadas.
- Reintentos con espera exponencial y límites por configuración y siguiente ventana. Las capturas con evidencia, errores de importación e interrupciones humanas no se reintentan automáticamente.
- Recuperación automática de tareas e intentos abandonados, renovación de actividad y límite máximo de duración. Evidencia conservada, importaciones tardías bloqueadas y cierre de captura/motivo en una transacción.
- Ediciones con control de revisión: no actualizan silenciosamente el protocolo fijado ni repiten una ventana por un cambio de nombre; pausar trabajo asignado sin captura evita iniciar el navegador.
- [Guía de programación y recuperación](OBSERVATORIO_PROGRAMACION.md).

Verificación:

- Suite completa: 162 pruebas, 0 fallos, 0 errores, 0 omitidas. Diecinueve pruebas nuevas: dieciocho de integración y una de migración V15→V16.
- Integración: altas idempotentes, despacho concurrente, tokens de propietario, calendario sin acumulación, pausas, versiones fijadas, reintentos limitados, recuperación, conservación de evidencia, señales renovadas, límite temporal, cierre atómico y recolección fuera del hilo de mantenimiento.
- Chromium con temporizador real y colector de HTML de prueba: creación/activación, tarea ejecutada, captura enlazada, pausa con historial y vista móvil de 390 px sin desbordamiento. Resultado `OBSERVATORY_SCHEDULE_TIMER_BROWSER_SMOKE_OK`.
- Sintaxis JavaScript y git diff --check verificados. Sin consultas reales a portales ni migraciones sobre datos personales. PostgreSQL real pendiente.

Estado: fase 2 en curso. Programación y recuperación automática implementadas; la cobertura sigue limitada a primera página y muestreo exploratorio. El dashboard todavía no se ha implementado.

Qué falta:

- Próximo bloque: paginación y condiciones verificables de fin de resultados; validación operativa por portal y PostgreSQL.
- Fase 3: dashboard y métricas reproducibles, con cohortes, cobertura, salarios comparables y evidencia.
- Contratos especializados de fase 0 y fases 4–7 siguen pendientes; los pronósticos requieren además historia suficiente.

### 2026-11-14 — Incremento 8: paginación limitada y evidencia por página

Implementado:

- Migración aditiva V17: máximo de páginas por revisión de protocolo y archivo de páginas recibidas. Compatibilidad con protocolos anteriores de una página, sin fabricar evidencia histórica.
- Límite configurable de 1–20 páginas y hasta 1000 elementos; descripción del alcance validada, visible en capturas y horarios. Los horarios existentes mantienen su revisión fijada.
- Recolector V2: sigue enlaces siguientes observados conservando origen, ruta, consulta y filtros. Detiene ante destinos incompatibles, controles ambiguos/no compatibles, URLs o páginas repetidas y límites explícitos.
- Diferencia control siguiente desactivado de continuidad desconocida. La falta de tarjetas o un bloqueo no se interpreta como demanda cero ni fin confirmado de resultados.
- Evidencia HTML por página, fecha, tarjetas, huella y diagnóstico, guardada antes de importar anuncios. Solapamientos de identidad exacta entre páginas no generan observaciones dobles en la misma captura.
- Fallos posteriores conservan páginas e importaciones previas; cierre y recuperación mantienen el conteo de páginas recibidas. Resultado exploratorio y parcial incluso con fin de navegación observado.
- [Guía de paginación y alcance](OBSERVATORIO_PAGINACION.md).

Verificación:

- Suite completa: 172 pruebas, 0 fallos, 0 errores, 0 omitidas. Diez nuevas: seis de política de navegación, una de límites/contrato, dos de integración y una de migración V16→V17.
- Chromium: once escenarios de recorrido (incluidos solapamiento, repetición, límites, bloqueo, interrupción, navegación fallida y continuidad desconocida) y cuatro escenarios de controles por fuente, con todas las respuestas interceptadas. Resultado `OBSERVATORY_PAGINATION_BROWSER_OK`.
- Interfaz con protocolo de tres páginas, temporizador real, captura parcial, diagnóstico y HTML de segunda página, pausa/historial y vista móvil de 390 px. Resultado `OBSERVATORY_PAGINATION_UI_SMOKE_OK`.
- Sintaxis JavaScript y git diff --check verificados. Sin portales reales ni PostgreSQL personal.

Estado: fase 2 en curso, con 10 de 11 bloques funcionales del listado completados. La paginación con enlaces verificables está implementada; botones JavaScript, desplazamiento infinito y selectores reales requieren validación y posibles adaptaciones. No se declara cobertura completa.

Qué falta:

- Cerrar validación operativa en PostgreSQL y portales reales, incluyendo fallos y controles de navegación no compatibles encontrados.
- Construir fase 3: cohortes reproducibles, dashboard, evolución, salarios comparables y métricas con evidencia.
- Completar contratos especializados y fases 4–7; los pronósticos además necesitan historia suficiente.

## 17. Estado actual y trabajo restante

Actualizado tras el incremento 8. Los estados representan entregables verificados. A petición del usuario se añade un índice porcentual por entregables; no estima horas restantes ni sustituye la validación operativa.

| Fase | Estado | Trabajo restante |
| --- | --- | --- |
| 0. Contratos y diseño | Parcial | Auditoría local completada; faltan validación real y contratos especializados de experiencia, idiomas, estudios y elegibilidad. |
| 1. Registro independiente e historial | Completada, alcance manual | Ampliaciones especializadas se incorporarán con los análisis que las necesiten. |
| 2. Captura repetible y calidad | En curso | Recolección, programación, recuperación y paginación limitada implementadas; falta verificación real de portales/PostgreSQL y adaptación de controles que revele. |
| 3. Dashboard para decisiones | Pendiente | Cohortes, métricas, evolución, salarios comparables, cobertura y acceso a evidencia. |
| 4. Señales e insights | Pendiente | Inferencia, incertidumbre, cambios persistentes y asociaciones validadas. |
| 5. Simulación personal | Pendiente | Perfil, brechas y comparación de aprendizajes. |
| 6. Pronósticos y alertas | Pendiente | Historia suficiente, evaluación temporal, calibración y publicación condicionada. |
| 7. Operación y ampliaciones | Pendiente | Rendimiento, exportación, respaldo/restauración y ampliaciones validadas. |

Dentro de fase 2:

- [x] Segmentos y protocolos propios versionados.
- [x] Ejecuciones manuales, estados, notas y truncamiento.
- [x] Observaciones repetidas, evidencia, revisiones e idempotencia.
- [x] Identidad por fuente y coincidencias deterministas.
- [x] Revisión de candidatos, uniones/separaciones reversibles y resolución histórica.
- [x] Auditoría local y adaptación inicial de los cuatro portales con evidencia DOM; validación real pendiente.
- [x] Filtros ejecutables y alta de nuevos anuncios dentro de una captura.
- [x] Recolección bajo demanda de primera página, con cierre parcial y diagnóstico.
- [x] Programación independiente y recuperación automática.
- [x] Paginación limitada, evidencia por página y condiciones verificables de detención/fin de navegación; validación real pendiente.
- [ ] Verificación operativa con PostgreSQL y fallos de portales.

Para el primer dashboard utilizable faltan **dos bloques grandes**: cerrar cobertura y validación de fase 2 y construir el dashboard/métricas de fase 3, incluyendo los contratos necesarios de fase 0. Eso no equivale a dos entregas pequeñas ni a una estimación de horas. El módulo completo todavía requiere fases 4–7; los pronósticos además dependen del tiempo de acumular historia, no sólo del desarrollo.


### Índice de avance solicitado

Regla estable desde el incremento 8: las ocho fases pesan igual en el índice completo. Para el primer corte utilizable sólo se promedian las fases 0–3. Dentro de fase 2 se cuentan los once bloques del listado anterior; no tienen necesariamente el mismo esfuerzo. Fase 0 se divide en cuatro grupos: contratos generales/datos, diseño y ejemplos, auditoría local de conectores, contratos especializados; los primeros tres están disponibles. Fase 1 mide el alcance manual aceptado. Las fases futuras empezarán a acumular avance al verificar sus entregables.

| Fase | Avance del índice | Base |
| --- | --- | --- |
| 0 | 75% | 3 de 4 grupos de contratos/diseño. |
| 1 | 100% | Registro manual e historial aceptados. |
| 2 | 91% | 10 de 11 bloques; 10/11 sin redondear para el cálculo. |
| 3–7 | 0% | Entregables todavía pendientes. |

- **Módulo completo: aproximadamente 33% realizado; 67% pendiente.** Cálculo: `(0.75 + 1 + 10/11) / 8`.
- **Primer corte utilizable, con dashboard: aproximadamente 67% realizado; 33% pendiente.** Cálculo: `(0.75 + 1 + 10/11) / 4`.
- **Fase 2: aproximadamente 91% realizado; 9% pendiente del índice de bloques.** El bloque restante agrupa validación real y puede requerir más esfuerzo que otros bloques.

Son índices de entregables, no porcentajes de tiempo, cobertura del mercado o confiabilidad estadística. No hay una fecha de terminación defendible a partir de ellos; la validación externa y la acumulación de historia condicionan los siguientes resultados.
