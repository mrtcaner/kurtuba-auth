# Authentication Model

## Overview

`kurtuba-auth` uses a client-aware authentication model built around signed JWT access tokens, persisted user-token state, refresh-token rotation, and server-side token blocking.

Authentication behavior depends on both:

- the authenticated principal
- the registered client through which authentication is performed

That means token shape, refresh support, cookie behavior, scopes, and TTL values are controlled by client configuration rather than by one global auth policy.

## Principal Types

The service works with two main principal categories:

- end users
- service clients

### End users

End users authenticate with `emailMobile` plus password, or arrive through the external-provider registration/login path.

### Service clients

Service clients authenticate with client credentials and receive short-lived access tokens intended for machine-to-machine communication.

## Registered Clients

A registered client defines how tokens are issued and handled.

Client definitions influence:

- whether refresh tokens are enabled
- access-token TTL
- refresh-token TTL
- whether scopes are included
- whether access tokens are returned in cookies
- cookie max age
- intended audiences
- client type

If a user authenticates through different registered clients, the resulting token behavior can differ.

## Client Types

The service recognizes these `RegisteredClientType` values:

- `DEFAULT`
- `WEB`
- `MOBILE`
- `SERVICE`
- `GENERIC`

### `DEFAULT`

Used as a fallback when some flows omit the client explicitly. This happens in normal login and in the external-provider flow if `registeredClientId` is not supplied.

### `WEB`

Web-oriented client type. These clients may use cookie-based access-token transport and the dedicated web refresh flow.

### `MOBILE`

Mobile-oriented client type. These clients typically use JSON token responses and refresh-token-based continuation.

### `SERVICE`

Machine-to-machine client type. These clients authenticate with client credentials and receive short-lived access tokens only.

### `GENERIC`

General-purpose client type for integrations that do not fit the other categories.

## User Authentication Flow

For normal credential login, authentication includes:

- looking up the user by email or mobile
- checking lock-window state
- validating the BCrypt password hash
- incrementing failure state on incorrect password
- checking activation state after password verification
- resetting failure state on success
- passing the user into client-aware token issuance

This means password verification alone is not enough. The user must also be in a valid account state.

## Failed Login and Lockout Behavior

The current lockout behavior is stateful and progressive.

Current code behavior includes:

- incrementing `failedLoginCount` on incorrect password
- enabling `showCaptcha` at 5 failed attempts
- locking the account at 10 failed attempts
- applying an exponential backoff lock window derived from failed-attempt count

The blocked-until value is returned in the lock exception message.

## Activation Requirement

For normal end-user credential login, authentication succeeds only if the account has already been activated.

## External-Provider Login Model

The external-provider path is implemented as registration-plus-token-issuance, not as a separate generic OAuth login subsystem.

Current behavior:

- supported providers are `GOOGLE` and `FACEBOOK`
- the request always includes a `providerClientId`
- the request may include either a direct provider token or an authorization code
- when an authorization code is used, the server exchanges it with the provider using its configured server-side client credentials
- when the local user already exists by email, that existing account is reused
- after provider identity is accepted, the service issues normal local application tokens through a registered client

### Google path

Google supports:

- direct ID token verification
- authorization-code exchange to retrieve an ID token

The exchanged or supplied ID token is then verified and decoded into local registration data.

### Facebook path

Facebook supports:

- direct access-token use
- authorization-code exchange to retrieve an access token

The access token is then used to fetch user profile data from Facebook, and the email is treated as the local account identity key.

## Access Tokens

The service issues signed JWT access tokens.

Access tokens include claims such as:

- `jti`
- `sub`
- `iss`
- `aud`
- optional `scope`
- timing claims such as `iat`, `nbf`, and `exp`

New access tokens are signed with the currently active signing key.

## JWT Verification Model

Within the service, access-token verification is not purely stateless.

The refresh path and revocation-sensitive paths combine:

- persisted token lookup by `jti`
- client matching
- blocked-token checks
- signature verification using the loaded signing keys

A token can therefore be cryptographically valid but still rejected because its persisted server-side state is invalid.

## Persisted Token State

When the service issues tokens for a user, it also saves a `UserToken` record.

That record includes fields such as:

- `jti`
- user id
- client id
- audiences
- scopes
- access-token expiration
- BCrypt-hashed refresh token
- refresh-token expiration
- blocked state
- refresh-token-used state

This persisted state enables:

- revocation
- refresh-token rotation
- refresh-token reuse detection
- client matching during refresh

## Refresh Tokens

Refresh tokens are supported only for clients that have refresh enabled.

The raw refresh token returned to the client is a base64 string. Before storage, the service:

- base64-decodes the raw refresh token
- BCrypt-hashes the decoded value
- stores only the BCrypt hash

During refresh, the supplied token is base64-decoded again and checked against the stored BCrypt hash.

## Refresh Validation Rules

Refresh is based on both token state and client state.

The refresh process validates:

- persisted access-token record lookup by `jti`
- refresh-token expiration
- refresh-token-used flag
- blocked-token state
- requesting client id matches original token client id
- client credentials when the client has a stored secret
- JWT signature and claims using the original token client’s refresh TTL as clock-skew allowance

## Refresh Token Rotation

Refresh tokens are single-use.

When a refresh request succeeds, the service marks the old refresh token as used through an atomic repository update and then issues a new token pair.

If the same refresh token is reused, the request is rejected.

## Web Refresh Model

For `WEB` clients, refresh follows a cookie-oriented path.

Current behavior:

- the access token is read from the `jwt` cookie
- the registered client must be of type `WEB`
- the client must also support refresh tokens
- a new access token is issued and written back into the `jwt` cookie

## Cookie-Based Authentication

Some registered clients can be configured to receive access tokens in an HTTP-only cookie instead of the JSON response body.

Current implementation details:

- cookie name is `jwt`
- cookie path is `/`
- cookie max age comes from the registered client
- cookies are currently created with `secure(false)` in code, which is acceptable for local HTTP testing but not a production-safe default

## Scope and Authority Model

The service supports scope-based authorization through token claims and Spring Security method protection.

Current behavior:

- when a client has `scopeEnabled=true`, user scopes are derived from the user’s role names
- service tokens carry the client’s configured scopes
- admin/service/user restrictions are enforced through Spring Security authorities such as `SCOPE_ADMIN` and `SCOPE_SERVICE`

## Client Credential Validation

The registered client is part of the security boundary.

When tokens are issued or refreshed, the service:

- resolves the registered client by `clientId`
- checks the client secret when that client has a stored secret
- rejects refresh if the token’s original client id and the requested client id differ

## Token Blocking and Logout

The service supports server-side token revocation by marking tokens as blocked in persistent storage.

Logout therefore has server-side effect rather than being only a client-side token deletion step.

## JWKS and Signing-Key Loading

The service loads signing keys at startup from two coordinated inputs:

- `kurtuba.jwk.file`
- `kurtuba.jwk.keys`

### `kurtuba.jwk.file`

This is loaded as a Spring `Resource`, so it can be provided as values such as:

- `classpath:jwk.json`
- `file:/absolute/path/to/jwk.json`

### `kurtuba.jwk.keys`

This is a JSON object mapping entry id to base64 decryption secret.

### Startup loading behavior

At startup, the service:

1. reads the encrypted key entries from the JWK resource
2. parses the `kurtuba.jwk.keys` JSON map
3. matches every encrypted entry by `id`
4. decrypts each `encryptedKey`
5. sorts all loaded keys by `order` descending
6. uses the highest-ordered key as the active signing key

If the supplied key map does not contain a matching secret for an entry id, startup fails.

## JWKS Publication Behavior

The JWKS endpoint publishes the public portion of all loaded signing keys, not only the active one.

Current public endpoint:
- `/auth/oauth2/jwks`

That enables overlap windows during key rotation, where new tokens are signed with the newest key while older tokens remain verifiable.

## Supported Signature Algorithms

The security configuration accepts verification of:

- `RS256`
- `ES256`

The bundled helper path currently defaults to RSA key generation, so the practical default deployment model is still RSA signing unless operators deliberately generate and deploy EC keys.

## Relationship to Other Documents

This document should be read together with:

- `docs/capabilities.md`
- `docs/api.md`
- `docs/configuration.md`
- `docs/key-management.md`
