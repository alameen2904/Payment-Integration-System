# Payment Integration System
![CI](https://github.com/alameen2904/Payment-Integration-System/actions/workflows/ci.yml/badge.svg)

A Spring Boot microservices system for processing card payments through Stripe, built around **transaction idempotency**, **configurable fraud/abuse rules**, and **request-signature authentication** — the core reliability and security concerns of any real payment flow.

This isn't a "call the Stripe SDK and return the response" demo. It separates payment *validation* (business rules, security, duplicate detection) from payment *execution* (the actual Stripe provider integration), coordinated through a config server and service registry — the same pattern used in production payment platforms.

## Architecture

```
                     ┌────────────────────────┐
                     │  Eureka Service Registry │
                     │      (service discovery)  │
                     └────────────┬────────────┘
                                  │
                     ┌────────────┴────────────┐
                     │   Payment Config Server   │
                     │ (Git-backed, per-env      │
                     │  properties + AWS Secrets)│
                     └────────────┬────────────┘
                                  │
        ┌─────────────────────────┴─────────────────────────┐
        │                                                     │
┌───────┴────────────┐                             ┌─────────┴──────────┐
│ Payment Validation   │  ── validated request ──▶  │ Stripe Provider     │
│ Service              │                             │ Service             │
│                       │                             │                     │
│ • HMAC-SHA256 auth    │                             │ • Stripe checkout   │
│ • Duplicate txn check │                             │   session creation  │
│ • Attempt-threshold   │                             │ • Stripe webhook    │
│   rule engine         │                             │   notifications     │
└───────────────────────┘                             └─────────────────────┘
```

Each service registers with Eureka and pulls its configuration (including validation rule parameters) from the Config Server at startup and on refresh — rule thresholds can change without redeploying the service (`@RefreshScope`).

## Services

| Service | Responsibility |
|---|---|
| `eureka-service-registry` | Service discovery so the other services can find each other by name instead of hardcoded URLs |
| `payment-config-servers` | Centralized, Git-backed configuration per environment (dev/qa/uat/prod/local), with AWS Secrets Manager integration for sensitive values |
| `payment-validation-services` | Authenticates and validates incoming payment requests before they reach Stripe |
| `stripe-provider-services` | Talks to Stripe directly — creates checkout sessions and handles Stripe webhook notifications |

## What the validation service actually does

**Request authentication** — every merchant request is signed and verified via a custom HMAC-SHA256 Spring Security filter (`HmacSha256Filter` + `HmacSha256Service`), not just an API key check.

**Duplicate transaction prevention** — `DuplicateTxnValidator` persists each merchant transaction reference and relies on a database-level uniqueness constraint to reject replays, rather than an in-memory check that wouldn't survive multiple instances.

**Configurable abuse prevention** — `PaymentAttemptThresholdValidator` rate-limits payment attempts per end user within a rolling time window (e.g. max N attempts per M minutes). Both the window and the threshold are pulled from the validation rule cache at request time, so they're tunable via config rather than baked into code.

**Resilience** — Resilience4j circuit breaker on outbound calls, Redis for caching, and Spring Cloud Config with `@RefreshScope` for live config updates.

## Tech stack

Java 17 · Spring Boot · Spring Cloud (Config, Eureka, Netflix) · Spring Security · Resilience4j · Redis · MySQL · Stripe API · Maven

## Running locally

Each service is an independent Maven/Spring Boot module. Start them in this order so service discovery and config are available before the dependent services boot:

```bash
# 1. Service registry
cd eureka-service-registry && ./mvnw spring-boot:run

# 2. Config server
cd payment-config-servers && ./mvnw spring-boot:run

# 3. Validation service
cd payment-validation-services && ./mvnw spring-boot:run

# 4. Stripe provider service
cd stripe-provider-services && ./mvnw spring-boot:run
```

Local configuration is supplied via `application-local.properties` in each module — copy and fill in your own Stripe test keys and database credentials before running.

## Example: create a payment

```
POST /v1/payments
Content-Type: application/json
Hmac-Signature: <computed-signature>

{
  "user": { "endUserID": "user-123" },
  "payment": {
    "merchantTxnRef": "txn-98765",
    "amount": 1999,
    "currency": "usd"
  },
  "lineItems": [
    { "name": "Widget", "quantity": 1, "unitPrice": 1999 }
  ]
}
```

If the same `merchantTxnRef` is submitted twice, or the calling user exceeds the configured attempt threshold, the request is rejected before it ever reaches Stripe.

## Roadmap / known gaps

- Unit tests for the business validators (duplicate-transaction and attempt-threshold logic) — currently only the HMAC service is covered.
- CI pipeline (build + test on push).
- Remove committed build artifacts and add a proper `.gitignore`.
