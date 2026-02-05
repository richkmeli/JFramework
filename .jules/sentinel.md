## 2026-02-05 - Session ID Predictability and Timing Attacks
**Vulnerability:** ServletManager generated session cookies using only predictable client information (IP, User-Agent, JSESSIONID) without a server-side secret. Also, multiple classes used non-constant-time string comparisons for sensitive tokens and hashes.
**Learning:** Security mechanisms like session hijacking protection can be bypassed if the generation logic depends only on publicly available or predictable information. Timing attacks are often overlooked in standard string comparisons.
**Prevention:** Always incorporate a server-side secret (salt) when generating session identifiers or security tokens. Use MessageDigest.isEqual for all cryptographic comparisons to ensure constant-time execution.
