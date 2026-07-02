#!/usr/bin/env bash
set -euo pipefail

for forbidden in local.properties signing.properties .env secrets.properties; do
  if find . -path './.git' -prune -o -name "$forbidden" -print | grep -q .; then
    echo "Forbidden private file found: $forbidden"
    exit 1
  fi
done

if find . -path './.git' -prune -o \
  \( -name '*.jks' -o -name '*.keystore' -o -name '*.session' -o -name '*.session-journal' -o -name '*.p12' -o -name '*.pfx' \) \
  -print | grep -q .; then
  echo "Private key/session file found."
  exit 1
fi

if grep -RIl --exclude-dir=.git --exclude='*.png' --exclude='*.jpg' --exclude='*.jar' --exclude='*.dat' \
  -E -- '-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----' . | grep -q .; then
  echo "Private key material found."
  exit 1
fi

# Catch accidentally committed literal Bearer values. Source code that constructs
# "Bearer $token" is allowed; a long literal value is not.
if grep -RIn --exclude-dir=.git --exclude='*.md' --exclude='*.example' --exclude='*.yml' --exclude='*.yaml' \
  -E 'Bearer[[:space:]]+[A-Za-z0-9._-]{24,}' .; then
  echo "Possible literal bearer token found."
  exit 1
fi

# Catch common secret assignments in source/config. Placeholder/example files are excluded.
if grep -RIn --exclude-dir=.git --exclude='*.md' --exclude='*.example' --exclude='check-repository.sh' \
  -E '(PLUSX_BACKEND_TOKEN|storePassword|keyPassword|api[_-]?key|api[_-]?hash|secret)[[:space:]]*=[[:space:]]*[\"'\''][^\"'\'']{20,}[\"'\'']' .; then
  echo "Possible committed secret value found."
  exit 1
fi

echo "Repository check passed."
