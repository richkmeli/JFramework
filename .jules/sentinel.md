# Sentinel Security Journal

## 2026-02-01 - [Unsalted Password Storage in Authentication Protocol]
**Vulnerability:** Passwords are stored in the database as unsalted SHA256 hashes (with a dummy '000000000' salt prefix).
**Learning:** The authentication protocol requires the client to compute a salted hash of the stored hash. Since the client does not have access to a per-user salt before authentication, the stored hash must be computable by the client using only the password. This architectural constraint prevents the use of per-user salts in the database without changing the authentication protocol (e.g., by sending the salt to the client first).
**Prevention:** When designing authentication protocols, ensure that the server-side storage can use modern, salted hashing (like Argon2 or bcrypt) and that the protocol supports challenge-response mechanisms that don't depend on the client knowing the exact stored representation.
