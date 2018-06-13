#!/usr/bin/env bash
# Script taking as input the jar file containing the generated java classes and the directory
# containing the golden files and tests that for each generated java file, it exists one golden file
# with the same name and the contents are the same.
set -e

readonly WORKSPACE=$(pwd)

readonly GENERATED_JAR=${WORKSPACE}/$1
readonly ORIGINAL_GOLDEN_FILES_DIR=$2
readonly JAR=${WORKSPACE}/$3
readonly GOOGLE_JAVA_FORMAT=$4

readonly GENERATED_FILES_DIR=$(mktemp -d)
readonly GOLDEN_FILES_DIR=$(mktemp -d)

cleanup() {
  rm -rf ${GENERATED_FILES_DIR}
  rm -rf ${GOLDEN_FILES_DIR}
}

trap cleanup EXIT

format_java_code() {
  for f in $(list_java_files ${1}); do
    # ensure we can write the file
    chmod 664 $f
    ${GOOGLE_JAVA_FORMAT} -i $f
  done
}

# allows us to comment some part of java code when a feature is not implemented.
strip_java_comments() {
  # BSD version of sed requires a backup suffix when -i option is used.
  find "$1" -type f -name '*.java' -print -exec sed -i.bak '/\/\/.*$/d' '{}' \;
}

list_java_files() {
  echo -e "$(find "${1}" -name '*.java')"
}

setup_test() {
  # Extract java files from the jar
  cd $GENERATED_FILES_DIR
  "${JAR}" xf "${GENERATED_JAR}"
  cd -
  strip_java_comments ${GENERATED_FILES_DIR}

  # copy golden files and format them
  cp ${ORIGINAL_GOLDEN_FILES_DIR}/*.java ${GOLDEN_FILES_DIR}
  # TODO(dramaix): remove this when all feature are implemented.
  strip_java_comments ${GOLDEN_FILES_DIR}
  format_java_code ${GOLDEN_FILES_DIR}
}

log() {
  echo -e  >&2 $1
}

expect_file_content_eq() {
 local expected="$1"
 local actual="$2"

 if ! diff -u "${expected}" "${actual}"; then
    log "content of expected file \"${expected}\" and actual file \"${actual}\" differ"
    return 1
  fi
  return 0
}

run_test() {
  local nbr_generated_files=$(find "${GENERATED_FILES_DIR}" -name '*.java' | wc -l)
  local nbr_golden_files=$(find "${GOLDEN_FILES_DIR}" -name '*.java' | wc -l)

  if [ ${nbr_generated_files} -ne ${nbr_golden_files} ]; then
    log "The number of generated files [${nbr_generated_files}] doesn\'t match the number of golden files [${nbr_golden_files}].
>> Golden files:
$(list_java_files ${GOLDEN_FILES_DIR})
>> Generated files:
$(list_java_files ${GENERATED_FILES_DIR})"
    exit 1
  fi

  for file in $(list_java_files ${GENERATED_FILES_DIR}); do
    local golden_file="${GOLDEN_FILES_DIR}/$(basename ${file})"

    if [ -e ${golden_file} ]; then
      expect_file_content_eq ${file} ${golden_file}
    else
      log "Golden file $(basename ${file}) is missing"
      exit 1
     fi
  done
}

setup_test
run_test

