#!/usr/bin/env bash
# Builds the GitHub Release description of a tag from its CHANGELOG.md section.
# Usage: release-notes.sh vX.Y.Z [path/to/armadillo-android.apk.sha256]
# Fails if the tag has no section, so a release cannot be published without notes.
set -euo pipefail

tag="${1:?usage: release-notes.sh vX.Y.Z [sha256-file]}"
sha_file="${2:-}"
version="${tag#v}"
repo="${GITHUB_REPOSITORY:-Kazuryy/armadillo-android}"
changelog="$(cd "$(dirname "$0")/../.." && pwd)/CHANGELOG.md"

# Text between "## [version]" and the next "## [" heading, without surrounding blank lines
notes="$(awk -v header="## [${version}]" '
  index($0, header) == 1 { found = 1; next }
  found && index($0, "## [") == 1 { exit }
  found { print }
' "$changelog" | sed -e :a -e '/^[[:space:]]*$/{$d;N;ba' -e '}' | sed '/./,$!d')"

if [ -z "$notes" ]; then
  echo "error: no section '## [${version}]' in CHANGELOG.md, add release notes before tagging" >&2
  exit 1
fi

previous="$(git -C "$(dirname "$changelog")" describe --tags --abbrev=0 "${tag}^" 2>/dev/null || true)"

printf '%s\n\n---\n\n' "$notes"

cat <<'EOF_INSTALL'
### Install

Download `armadillo-android.apk` and install it on your Android TV or Google TV (Android 7.0 or later). You may need to allow "Install unknown apps" for the app you install it from. If Armadillo is already installed, it offers this update on its home screen.

EOF_INSTALL

if [ -n "$sha_file" ] && [ -f "$sha_file" ]; then
  printf '### Verify the download\n\nSHA-256 of `armadillo-android.apk`:\n\n```\n%s\n```\n\nCheck it with `shasum -a 256 armadillo-android.apk` (macOS) or `sha256sum armadillo-android.apk` (Linux).\n\n' "$(awk '{print $1}' "$sha_file")"
fi

if [ -n "$previous" ]; then
  printf '**Full changelog**: https://github.com/%s/compare/%s...%s\n' "$repo" "$previous" "$tag"
else
  printf '**Full changelog**: https://github.com/%s/commits/%s\n' "$repo" "$tag"
fi
