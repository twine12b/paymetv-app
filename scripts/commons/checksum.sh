#!/bin/sh

# Run a command only when the checksum of a file or optional key changes. The
# command can perform follow-up actions such as building an image and rolling
# out a deployment. Chain dependent actions with && so a failure leaves the
# checksum unchanged and the next run retries them.
# Usage: checksum.sh <caller-script-directory> <file-to-check> <command> [args...]

set -e

if [ "$#" -lt 3 ]; then
    echo "Usage: $0 <caller-script-directory> <file-to-check> <command> [args...]" >&2
    exit 2
fi

CHECKSUM_DIR=$1
CHECKED_FILE=$2
shift 2
CHECKSUM_FILE="${CHECKSUM_DIR%/}/checksum"

if command -v sha256sum >/dev/null 2>&1; then
    CURRENT_CHECKSUM=$(
        { printf '%s\n' "${CHECKSUM_KEY:-}"; cat "$CHECKED_FILE"; } |
            sha256sum | awk '{print $1}'
    )
elif command -v shasum >/dev/null 2>&1; then
    CURRENT_CHECKSUM=$(
        { printf '%s\n' "${CHECKSUM_KEY:-}"; cat "$CHECKED_FILE"; } |
            shasum -a 256 | awk '{print $1}'
    )
else
    echo "A SHA-256 utility (sha256sum or shasum) is required." >&2
    exit 1
fi

if [ ! -f "$CHECKSUM_FILE" ]; then
    echo "No checksum found; running command and saving the checksum."
elif [ "$(cat "$CHECKSUM_FILE")" = "$CURRENT_CHECKSUM" ]; then
    echo "Checksum matches; skipping command."
    exit 0
else
    echo "Checksum changed; running command and updating the checksum."
fi

"$@"
printf '%s\n' "$CURRENT_CHECKSUM" > "$CHECKSUM_FILE"
