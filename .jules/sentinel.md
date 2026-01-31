# Sentinel Security Journal 🛡️

## 2026-01-31 - Timing Attack in Password Verification
**Vulnerability:** Password verification used `equalsIgnoreCase`, which is vulnerable to timing attacks.
**Learning:** Even if the input is hashed, comparing hex strings with non-constant-time functions can leak information about the correct hash.
**Prevention:** Always use `MessageDigest.isEqual` for comparing security-sensitive data like hashes or tokens.

## 2026-01-31 - Platform-Dependent Charset in Cryptography
**Vulnerability:** Usage of `Charset.defaultCharset()` and implicit default charsets in `new String(byte[])` and `String.getBytes()`.
**Learning:** This leads to inconsistent hashes and encodings across different environments, which can be exploited or cause authentication failures.
**Prevention:** Always specify `StandardCharsets.UTF_8` for all cryptographic and data transmission operations.
