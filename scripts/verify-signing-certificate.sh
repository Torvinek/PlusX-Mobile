#!/usr/bin/env sh
set -eu

APK_PATH="${1:-}"
if [ -z "$APK_PATH" ]; then
  echo "Usage: scripts/verify-signing-certificate.sh path/to.apk" >&2
  exit 2
fi

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
EXPECTED="$(grep -v '^[[:space:]]*#' "$ROOT_DIR/config/official-signing-cert.sha256" | grep -v '^[[:space:]]*$' | head -n 1 | tr 'A-Z' 'a-z')"
APKSIGNER="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/build-tools/35.0.0/apksigner"
if [ ! -x "$APKSIGNER" ]; then
  APKSIGNER="$(command -v apksigner || true)"
fi
if [ -z "$APKSIGNER" ]; then
  echo "apksigner not found." >&2
  exit 2
fi

ACTUAL="$("$APKSIGNER" verify --verbose --print-certs "$APK_PATH" | sed -n 's/^Signer #1 certificate SHA-256 digest: //p' | head -n 1 | tr 'A-Z' 'a-z')"
if [ "$ACTUAL" != "$EXPECTED" ]; then
  echo "ERROR: APK uses a different signing certificate than official PlusX Mobile releases." >&2
  echo "Publishing and installation have been blocked." >&2
  exit 1
fi

echo "SIGNATURE OK"
echo "APK uses the official PlusX Mobile signing certificate."
