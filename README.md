# Delivery Service

REST-сервис для управления доставками на Spring Boot.

## Технологии

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 (in-memory)
- Flyway
- Resilience4j (Circuit Breaker)
- Swagger/OpenAPI (springdoc)

## Запуск локально

```bash
./gradlew bootRun
```

Сервис стартует на `http://localhost:8083`.

## Полезные URL

- Swagger UI: `http://localhost:8083/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`
- H2 Console: `http://localhost:8083/h2-console`
  - JDBC URL: `jdbc:h2:mem:deliverydb`
  - User: `sa`
  - Password: *(пусто)*

## Flyway миграции

Миграции лежат в `src/main/resources/db/migration`.
При старте приложения выполняется `V1__create_deliveries_table.sql`.

## API

Базовый путь: `/api/deliveries`

### Создать доставку

```bash
curl -X POST "http://localhost:8083/api/deliveries" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1001,
    "status": "CREATED",
    "deliveryAddress": {
      "street": "Lenina 1",
      "city": "Moscow",
      "postalCode": "101000",
      "country": "Russia"
    },
    "deliveryDate": "2026-04-20",
    "timeWindow": {
      "startTime": "10:00:00",
      "endTime": "14:00:00"
    },
    "trackingNumber": "TRK-1001"
  }'
```

### Получить все доставки

```bash
curl "http://localhost:8083/api/deliveries"
```

### Получить доставку по ID

```bash
curl "http://localhost:8083/api/deliveries/1"
```

### Обновить доставку

```bash
curl -X PUT "http://localhost:8083/api/deliveries/1" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1001,
    "status": "IN_TRANSIT",
    "deliveryAddress": {
      "street": "Lenina 1",
      "city": "Moscow",
      "postalCode": "101000",
      "country": "Russia"
    },
    "deliveryDate": "2026-04-21",
    "timeWindow": {
      "startTime": "12:00:00",
      "endTime": "18:00:00"
    },
    "trackingNumber": "TRK-1001-UPD"
  }'
```

### Удалить доставку

```bash
curl -X DELETE "http://localhost:8083/api/deliveries/1"
```

## Docker

### Сборка и запуск контейнера

```bash
./gradlew clean bootJar
docker build -t delivery-service .
docker run --rm -p 8083:8083 delivery-service
```

### Запуск через docker-compose

```bash
./gradlew clean bootJar
docker compose up --build
```
