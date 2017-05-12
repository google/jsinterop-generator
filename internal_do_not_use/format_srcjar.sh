#!/usr/bin/env bash
# Script taking as input the jar file containing the generated java classes and 
# formatting each java file with the google java formatter
set -e

generated_jar=$(pwd)/$1
output=$(pwd)/$2
formatter=$3
jar_tool=$4

# if a relative path is passed, use the absolute path
if [ "${jar_tool}" != "jar" ]; then
  jar_tool=$(pwd)/${jar_tool}
fi

GENERATED_FILES_DIR=$(mktemp -d)

function format_java_code () {
  java_files=$(find ${1} -name '*.java')
  chmod -R 664 ${java_files}
  ${formatter} -i ${java_files}
}


cd $GENERATED_FILES_DIR
${jar_tool} xf "${generated_jar}"
cd -

format_java_code ${GENERATED_FILES_DIR}

${jar_tool} -cf "${output}" -C ${GENERATED_FILES_DIR} .

rm -rf ${GENERATED_FILES_DIR}
