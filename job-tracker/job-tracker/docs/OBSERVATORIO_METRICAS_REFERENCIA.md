# Contratos y ejemplos de referencia de métricas

Estado: especificación para fase 3; no implica que exista ya un dashboard ni un motor de cálculo. Datos ficticios en [observatorio_metricas.csv](fixtures/observatorio_metricas.csv). Estos ejemplos no describen el mercado real.

## Cohorte, tiempo y revisión

P1 y P2 representan dos semanas completas consecutivas, con igual segmento y protocolo de captura. Cada fila es un anuncio canónico diferente cuya primera observación ocurre en su periodo. La fecha de publicación se desconoce: son nuevos anuncios observados, no nuevas publicaciones.

Contar 4 anuncios en cada periodo y crecimiento de 0 %. En datos reales usar intervalos temporales semiabiertos `[inicio, fin)`, zona de análisis explícita y fecha de corte. Elegir la última versión disponible antes del corte; nunca usar una corrección posterior para simular conocimiento pasado. Descontar duplicados sólo después de resolver su identidad y versionar esa decisión. Capturas fallidas no crean filas artificiales con demanda cero.

## Presencia de habilidades

Unidad: anuncio revisado, una vez por habilidad. Denominador: anuncios con `requirementsReviewed=true`. Una lista vacía revisada entra en el denominador; una sin revisar no. No significa ausencia de habilidades en el puesto real: únicamente que no se identificaron menciones bajo la revisión indicada.

| Resultado | P1 | P2 |
| --- | --- | --- |
| Anuncios revisados / total | 3/4 = 75 % | 4/4 = 100 % |
| Presencia Java | 2/3 = 66.6667 % | 2/4 = 50 % |
| Presencia SQL | 2/3 = 66.6667 % | 2/4 = 50 % |
| Presencia Docker | 0/3 = 0 % | 2/4 = 50 % |

Cambio de Java: `100 × (2/4 - 2/3) = -16.6667` puntos porcentuales. Crecimiento relativo: `(0.5 / (2/3) - 1) × 100 = -25 %`. Cambio de Docker: +50 puntos porcentuales; crecimiento relativo indefinido por base cero, nunca infinito ni 100 %.

Mostrar cobertura de revisión junto al cambio: la diferencia de cobertura entre P1 y P2 limita la conclusión. No emitir automáticamente una señal de caída de Java con este conjunto mínimo.

Para segmentar obligatoriedad, usar REQUIRED, DESIRED y UNKNOWN por separado. Una mención con obligatoriedad desconocida cuenta como mención, pero no como requisito obligatorio. No hacer inferencias por un alias sin resolver su habilidad y versión. La comparación entre versiones de taxonomía exige una correspondencia explícita; no actualizar el pasado silenciosamente.

## Coocurrencia y concentración

En P2: P(Java)=P(SQL)=P(Docker)=0.5. Java y Docker aparecen juntos en 2 anuncios: soporte 2, P(Java y Docker)=0.5, P(Docker dado Java)=1 y lift=2. Java y SQL aparecen juntos en 1 anuncio: soporte 1, probabilidad conjunta=0.25 y lift=1. Si alguna frecuencia marginal es cero, lift queda sin definir. Este ejemplo verifica aritmética, no supera un umbral de soporte para recomendaciones.

Empresas identificadas: P1 tiene 2 distintas sobre 3 anuncios con empresa conocida (75 % de cobertura); P2 tiene 3 distintas sobre 4 anuncios conocidos (100 %). No contar todos los desconocidos como una empresa.

HHI sobre anuncios con empresa identificada: P1 `(2/3)^2 + (1/3)^2 = 5/9 = 0.555556`; P2 `(1/4)^2 + (1/4)^2 + (2/4)^2 = 0.375`. Escala 0–1. Informar 1 anuncio excluido por empresa desconocida en P1. La participación de E1 sobre anuncios identificados baja de 66.6667 % a 25 %, sin que esto demuestre cambios en empleo o contrataciones.

## Salarios publicados

Cohorte del ejemplo: moneda MXN, periodicidad mensual y base bruta; asumir sólo para el ejercicio que ubicación, jornada y contratación coinciden. En la aplicación esos campos deben comprobarse antes de declarar comparabilidad completa. No mezclar el salario USD de d ni convertirlo sin serie de referencia.

P2: 3 de 4 anuncios tienen al menos un importe y metadatos básicos compatibles (75 %). Para mínimos hay 3 valores: 30000, 35000 y 40000. Para máximos hay 2: 40000 y 45000. Medianas: 35000 para mínimos y 42500 para máximos. No son extremos de una vacante típica ni un intervalo de confianza del salario.

Convención de cuantiles: interpolación lineal sobre valores ordenados, índice de base cero `(n-1)×p`. Si el índice no es entero, interpolar entre vecinos. Para mínimos, P25=32500 y P75=37500. Para máximos, P25=41250 y P75=43750. Muestra vacía produce desconocido; un solo dato produce ese valor para cualquier cuantil. Conservar precisión interna y redondear sólo al presentar.

Distribución de amplitud de rangos: usar únicamente e y f (ambos extremos conocidos): 10000 en cada caso, mediana 10000. g tiene límite abierto, no amplitud cero ni un máximo inventado. No sustituir rangos por puntos medios para estimar salarios pagados.

## Métricas aún sin datos suficientes en este fixture

Experiencia, inglés, estudios, modalidad y restricciones geográficas requieren columnas estructuradas con evidencia y desconocidos explícitos antes de calcular sus distribuciones. No interpretar campos ausentes como cero años, presencial o elegibilidad remota. Pronósticos, intervalos inferenciales y asociaciones ajustadas no se validan con estas ocho filas; requieren conjuntos específicos y evaluación definida en el plan.
