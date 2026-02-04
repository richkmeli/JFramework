## 2026-02-04 - Timing Attacks in Hash/Token Verification
**Vulnerability:** Use of `equalsIgnoreCase` or `equals` for comparing sensitive hashes and tokens (Passwords, session cookies, etc.) allows for timing attacks.
**Learning:** These side-channel vulnerabilities were widespread in the codebase's authentication and token management modules.
**Prevention:** Always use `java.security.MessageDigest.isEqual` for constant-time comparison of sensitive data.
