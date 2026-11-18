#!/usr/bin/env sh
# Lightweight Gradle launcher: uses system Gradle when present, otherwise bootstraps
# the pinned official Gradle distribution into the user's Gradle cache.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
VERSION=8.9
if command -v gradle >/dev/null 2>&1; then exec gradle -p "$APP_HOME" "$@"; fi
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
export GRADLE_USER_HOME
DIST="$GRADLE_USER_HOME/wrapper/dists/gradle-$VERSION-bin/esp32academy"
BIN="$DIST/gradle-$VERSION/bin/gradle"
if [ ! -x "$BIN" ]; then
  mkdir -p "$DIST"
  TMP="$DIST/gradle-$VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then curl -fL --retry 3 "$URL" -o "$TMP"
  elif command -v wget >/dev/null 2>&1; then wget -O "$TMP" "$URL"
  else echo "curl or wget is required to download Gradle $VERSION" >&2; exit 1; fi
  unzip -oq "$TMP" -d "$DIST"
  rm -f "$TMP"
fi
exec "$BIN" -p "$APP_HOME" "$@"
