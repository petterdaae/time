# time

Time management backend.

## Deploy

Run the `deploy.sh` script to deploy.

## Environment variables

Set environment variables with `fly secrets set SECRET=<secret>`, remove environment variables with `fly secrets unset SECRET`.

| Name                         | Description                                                                                                     |
|------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `USERNAME`                   | Username used for basic authentication.                                                                         |
| `PASSWORD`                   | Password used for basic authentication.                                                                         |
| `POSTGRES_PASSWORD`          | Password for the postgres user.                                                                                 |
| `OTEL_EXPORTER_OTLP_HEADERS` | Authentication for sending observability data to Grafana Cloud (format: `Authorization=Basic <grafana token>`). |

