# time

Time management backend.

## Deploy

Run the `deploy.sh` script to deploy.

## Develop

- Connect to the database with `flytctl postgres connect -a time-db`
- Set environment variables with `fly secrets set SECRET=<secret>`
- Remove environment variables with `fly secrets unset SECRET`

## Environment variables

| Name                | Description                             |
|---------------------|-----------------------------------------|
| `USERNAME`          | Username used for basic authentication. |
| `PASSWORD`          | Password used for basic authentication. |
| `POSTGRES_PASSWORD` | Password for the postgres user.         |
