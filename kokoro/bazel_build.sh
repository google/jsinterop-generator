#!/bin/bash
set -e

chmod +x ${KOKORO_GFILE_DIR}/setup_build_environment.sh
${KOKORO_GFILE_DIR}/setup_build_environment.sh

# the repo is cloned under git/jsinterop-base
cd git/jsinterop-generator

bazel build ...
bazel test ...
