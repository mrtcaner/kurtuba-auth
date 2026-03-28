# PostgreSQL Setup

## Overview

`kurtuba-auth` can run in two PostgreSQL-oriented modes:

- a simple developer-friendly mode where one PostgreSQL user is used for both Flyway and the application
- a more production-oriented mode where schema migration and runtime access are split across separate roles

The repository defaults to H2 for demo and showcase use, but PostgreSQL is the intended relational database for persistent environments.

## What Has Been Validated

The current public repository has been smoke-tested against a real PostgreSQL instance with:

- a separate throwaway database
- `init_db.sql`-style database setup
- Flyway enabled
- Hibernate running in `validate` mode

That validation confirmed:

- the public `init_db.sql` approach works for a new PostgreSQL database
- `V1__baseline.sql` applies successfully through Flyway
- the application can boot successfully against PostgreSQL after migration

## Two Supported Setup Styles

### 1. Simple developer setup

This is the easiest path for local integration work.

Characteristics:

- one PostgreSQL database user
- one JDBC datasource credential
- Flyway and the application both use the same account
- fewer moving parts
- suitable for local development, CI, and quick evaluation

Use this when you want to:

- move off H2
- test against a real PostgreSQL engine quickly
- keep configuration simple

### 2. Production-oriented role split

This is the more structured database model represented by `src/main/resources/db/sql/init_db.sql`.

Characteristics:

- separate migrator role and runtime role
- explicit grants on schema, tables, sequences, and functions
- tighter privilege boundaries
- more operational discipline

Use this when you want to:

- keep schema migration privileges separate from app runtime privileges
- align the auth server with a more locked-down production database model
- make ownership and grants explicit

## Developer-Friendly PostgreSQL Setup

A simple setup can use a single PostgreSQL account for both Flyway and application runtime.

Example config:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kurtuba_auth
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

Recommended notes for this mode:

- keep `ddl-auto` as `validate` when Flyway is enabled
- do not use `create-drop` on a persistent PostgreSQL database
- let Flyway own schema creation and migration
- keep this setup in a profile-specific file rather than changing the default demo `application.yml`

## Production-Oriented PostgreSQL Setup

The repository includes `src/main/resources/db/sql/init_db.sql` for a more structured database bootstrap.

That script is designed to:

- create the database
- create a migration role
- create a runtime role
- restrict broad schema privileges
- grant only the permissions needed by the runtime role
- set default privileges for future objects created by the migrator

### Important behavior of `init_db.sql`

The checked-in script is intentionally more production-oriented than the default H2 demo setup.

It assumes:

- PostgreSQL is already available
- the script is run by a sufficiently privileged account
- the target database does not already exist
- the role names `kurtuba_auth_migrator` and `kurtuba_auth_user` are acceptable for your environment

If your environment already has its own role conventions, adapt the script rather than forcing your database to match the demo names.

## Suggested Configuration for Role Split

A production-oriented deployment usually ends up with one of these patterns:

### Pattern A: Flyway and app started separately

- Flyway runs with the migrator account
- application runs with the runtime account
- most controlled option

### Pattern B: same process, shared credential

- simplest operationally
- acceptable for lower-risk or smaller deployments
- less strict privilege separation

The current app supports the database side of Pattern A, but whether you run it that way depends on your deployment tooling and secret-management model.

## Example Runtime Config

If you use the runtime account for the application:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kurtuba_auth
    driver-class-name: org.postgresql.Driver
    username: kurtuba_auth_user
    password: change-me
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

If you run Flyway externally with a stronger migration credential, the application should still use `ddl-auto: validate` so entity/schema mismatches are caught at startup.

## H2 vs PostgreSQL Guidance

Use H2 when:

- you want the repo to run immediately
- you are evaluating capabilities
- you are exercising the API surface casually
- you do not need persistence across restarts

Use PostgreSQL when:

- you want realistic relational behavior
- you want to validate Flyway migrations
- you want to test role/grant behavior
- you want a deployment-like environment
- you want to verify that schema validation passes outside H2

## Recommended Dev Workflow

For most developers, the easiest progression is:

1. Start with the default H2-backed demo config.
2. Move to a separate PostgreSQL profile once you need realistic persistence.
3. Enable Flyway and set Hibernate to `validate`.
4. Use the split-role `init_db.sql` only when you are ready for a stricter database model.

## Operational Notes

Keep these points in mind:

- `V1__baseline.sql` includes ownership and grant assumptions tied to the production-oriented role model.
- If your target database already contains objects or uses different owners, review the migration before applying it blindly.
- The public repo is intentionally documented to make these requirements visible, but it does not force a single operational model.

## Related Documents

- `docs/configuration.md`
- `docs/demo-vs-production.md`
- `docs/overview.md`
