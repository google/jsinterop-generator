#!/usr/bin/env bash
# Script validating that everything is correctly installed.
set -e

command -v jar >/dev/null 2>&1 || { echo "jar command is required but it's not installed.  Aborting." >&2; exit 1; }

if [ -z "$1" ]; then
  command -v d8 >/dev/null 2>&1 || { echo "d8 command is required but it's not installed.  Aborting." >&2; exit 1; }
fi

if [ -z "${GOOGLE_JAVA_FORMATTER}" ]; then
   echo "Google java formatter is not installed.  Aborting." >&2
   exit 1
fi