# VULNERABLE Alpine 3.20 (PRE-CVE-2025-15467 fix)
# For testing vulnerability scanning/fixing tools
FROM alpine:3.24

# Install vulnerable OpenSSL (pre-Jan 2026 patches)
RUN apk add --no-cache openssl

# Verify vulnerable version
RUN openssl version  # → 3.0.x or 3.3.x < fixed patches

# Sleep for tool testing
CMD ["sleep", "1000"]
