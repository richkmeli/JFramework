## 2026-02-03 - Timing Attacks and Encoding Consistency
**Vulnerability:** Cryptographic comparisons (passwords, tokens) were using `equalsIgnoreCase`, which is not constant-time and vulnerable to timing attacks. String encoding was platform-dependent.
**Learning:** `java.security.MessageDigest.isEqual` is essential for comparing sensitive cryptographic values. `StandardCharsets.UTF_8` should always be explicitly specified.
**Prevention:** Always use `MessageDigest.isEqual` for secrets and specify `StandardCharsets.UTF_8` for all string-to-byte conversions in crypto logic.
