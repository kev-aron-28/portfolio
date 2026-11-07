# AI Inventory Assistant

Proyecto para aprender LLM Function Calling con un caso de inventario. La primera función prevista, `get_stock`, recibirá un SKU, consultará PostgreSQL y devolverá el resultado al modelo para que responda al usuario.

## Arranque local

Requiere Java 21, Maven y Docker Compose. Maven ejecuta la aplicación en el host y Compose inicia PostgreSQL con un volumen persistente.

```sh
docker compose up -d --wait
mvn spring-boot:run
```

En otra terminal, verifica la aplicación y su conexión a PostgreSQL.

```sh
curl -fsS http://localhost:8080/actuator/health
```

Los valores locales se pueden ajustar exportando `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER` y `POSTGRES_PASSWORD` antes de ejecutar ambos comandos.

## Pruebas

`mvn test` ejecuta las pruebas que se añadan en `src/test/java`. El proyecto incluye JUnit 5 y Mockito a través de `spring-boot-starter-test`.

## OpenAI

La integración real usa la Responses API. Configura la clave y el modelo antes de iniciar la aplicación.

```sh
export OPENAI_API_KEY=...
export OPENAI_MODEL=gpt-5.4-mini
```

La primera ejecución expone únicamente herramientas de consulta. Cuando esas consultas funcionen, habilita la propuesta de movimientos con `ASSISTANT_WRITE_TOOLS_ENABLED=true`. La confirmación sigue siendo un endpoint explícito y no se expone como herramienta del modelo.

## Primera función de inventario

1. Crear el esquema de inventario, la entidad JPA y la consulta por SKU.
2. Registrar `get_stock` en una solicitud a la Responses API. La configuración reserva `OPENAI_API_KEY` y `OPENAI_MODEL` para esa integración. El arranque local funciona sin esas variables.
3. Validar los argumentos recibidos, ejecutar la consulta y devolver un `function_call_output` asociado al `call_id` para que el modelo genere su respuesta.

[Guía oficial de Function Calling de OpenAI](https://developers.openai.com/api/docs/guides/function-calling)

## Arquitectura de inventario

El dominio vive en `inventory/domain` y contiene las reglas de movimientos sin dependencias de Spring o JPA. `InventoryService` expone los casos de uso y depende del puerto `InventoryPort`. Los adaptadores REST y PostgreSQL están en `inventory/adapter`. Flyway crea las tablas al arrancar.

El catálogo comienza vacío. Para probar localmente, insertar un producto y un almacén en PostgreSQL.

```sql
INSERT INTO product(sku, name) VALUES ('A12', 'Producto de prueba');
INSERT INTO warehouse(code, name) VALUES ('NORTE', 'Almacén norte');
```

Crear una propuesta con `POST /api/stock-movements` y un cuerpo como el siguiente.

```json
{"sku":"A12","warehouse":"NORTE","quantity":5,"type":"ENTRY"}
```

Confirmar explícitamente mediante `POST /api/stock-movements/{id}/confirm`. Repetir la confirmación devuelve el movimiento aplicado. Las consultas disponibles son `GET /api/products?q=A12`, `GET /api/products/A12/stock` y `GET /api/warehouses/NORTE/stock`.

Esta implementación inicial requiere completar autenticación y autorización para asociar las propuestas a usuarios. También quedan pendientes las fechas de auditoría y las pruebas de integración de concurrencia contra PostgreSQL. La integración de IA consumirá los servicios de aplicación.
