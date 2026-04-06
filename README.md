# grpc-test

A Spring Boot multi-module project demonstrating inter-service communication via **gRPC**. The system exposes a single REST endpoint through a gateway, which internally fans out to multiple gRPC backend services to compose a response. This project is purely educational to get familiar with **gRPC** and serves no other purpose.

## Architecture

```
 Client (HTTP)
      │
      ▼
┌─────────────┐
│   Gateway   │  :8080  (REST → gRPC)
└──────┬──────┘
       │ gRPC
       ▼
┌──────────────────────┐
│ Registration Service │  :9090
└──────┬───────────────┘
       │ gRPC (fan-out)
       ├──────────────────────────┐
       ▼                          ▼
┌───────────────┐        ┌──────────────────┐
│ Person Service│ :9091  │  Address Service │ :9092
└───────────────┘        └──────────────────┘
```

### Modules

| Module | Description                                                                                                                                                                                                                                       |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `proto-lib` | Shared Protobuf definitions and generated gRPC stubs                                                                                                                                                                                              |
| `gateway` | REST API gateway: exposes `GET /api/v1/registrations/{id}` and calls Registration Service via gRPC. Caches previously sent responses in own memory. |
| `registration-service` | Aggregates Person + Address data, generates a fake event name and newsletter preference                                                                                                                                                           |
| `person-service` | Returns a randomly generated person (name, gender, date/place of birth, hobbies)                                                                                                                                                                  |
| `address-service` | Returns a randomly generated address (street, zip, city, country)                                                                                                                                                                                 |

---

## Error Handling

In order to propagate gRPC errors through the whole stream, the following errors are simulated at the furthest point downstream: 
- Person Service has a 10% chance to throw a `NOT_FOUND` response; 
- Address Service has a 10% chance to respond slowly, causing a `DEADLINE_EXCEEDED` response;
- Turning off a service downstream will result in an `UNAVAILABLE` response.

gRPC StatusRuntimeExceptions are mapped to relevant HTTP status codes, see `ControllerAdvice` for more information.

---

## Usage

Once all services are running, call the gateway's REST endpoint:

```
http://localhost:8080/api/v1/registrations/1
```

Example response (JSON):

```json
{
  "id": "1",
  "eventName": "Exciting Rock Climbing Rapidly",
  "wantsToReceiveNewsletter": true,
  "person": {
    "name": "James Miller",
    "gender": "Male",
    "dob": "15-03-1990",
    "pob": "Springfield",
    "hobbies": ["Cycling", "Photography", "Cooking"]
  },
  "address": {
    "streetAndNumber": "Maple Street 42",
    "zip": "12345",
    "city": "Portland",
    "country": "United States"
  }
}
```

> Responses for the same `id` are cached in-memory in the gateway. Subsequent requests for the same id return the cached result without hitting the downstream services again.

---

## Build

From the project root, build all modules:

```bash
mvn clean install
```

This compiles the `.proto` files in `proto-lib` and packages all services.

## How to start the project

Open the project and use the included **`everything`** compound run configuration (`!deployment/local/everything.run.xml`). It starts all four services in one click.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| (Optional) IntelliJ IDEA | For the included run configuration |

---

## What is to come in future builds
- virtual threads;
- webflux / streaming.