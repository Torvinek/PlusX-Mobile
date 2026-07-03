#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
. "$ROOT_DIR/version.properties"
TAG="${1:-}"
PREVIOUS_CODE=$((VERSION_CODE - 1))
if [ "$VERSION_NAME" = "1.5.4" ]; then
  PREVIOUS_CODE=9
fi

if [ "$VERSION_CODE" -le "$PREVIOUS_CODE" ]; then
  echo "ERROR: VERSION_CODE $VERSION_CODE is not greater than previous VERSION_CODE $PREVIOUS_CODE." >&2
  exit 1
fi

TAG_NO_V="${TAG#v}"
if [ -n "$TAG" ] && [ "$TAG_NO_V" != "$VERSION_NAME" ]; then
  echo "ERROR: Git tag $TAG does not match VERSION_NAME $VERSION_NAME." >&2
  exit 1
fi

echo "VERSION OK: $VERSION_NAME ($VERSION_CODE)"
