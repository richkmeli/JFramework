## 2026-02-02 - Constant-time comparison for authentication
**Vulnerability:** Timing attacks in password and token verification.
**Learning:** Using `String.equals()` or `String.equalsIgnoreCase()` for comparing hashes allows an attacker to guess the hash byte-by-byte by measuring the time taken for the comparison.
**Prevention:** Always use `java.security.MessageDigest.isEqual()` for constant-time comparison of sensitive data like hashes or tokens.

## 2026-02-02 - Charset standardization for cryptographic operations
**Vulnerability:** Platform-dependent hashing and encoding.
**Learning:** Using platform default charsets (e.g. `String.getBytes()`) can lead to inconsistent hashes across different environments, potentially locking out users or creating security gaps if different charsets handle certain characters differently.
**Prevention:** Explicitly specify `StandardCharsets.UTF_8` for all cryptographic operations involving string-to-byte or byte-to-string conversions.
