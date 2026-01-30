## 2026-01-30 - Unsalted Password Hash Pattern
**Vulnerability:** The `PasswordManager` and `AuthDatabaseJframeworkManager` use a default unsalted password hashing scheme where a hardcoded salt `000000000` is prepended to a SHA-256 hash.
**Learning:** This pattern was likely chosen to allow client-side hashing without a challenge-response protocol, but it leaves the database vulnerable to offline brute-force and rainbow table attacks.
**Prevention:** Always use slow, salted hashing algorithms like Argon2 or BCrypt. If client-side hashing is required, use a proper challenge-response protocol where the server provide a per-user salt.
