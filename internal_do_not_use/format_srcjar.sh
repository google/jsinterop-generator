#!/usr/bin/env bash
# Script taking as input the jar file containing the generated java classes and
# formatting each java file with the google java formatter
set -e

generated_jar=$(pwd)/$1
output=$(pwd)/$2
formatter=$(pwd)/$3
zip_tool=$4

# if a relative path is passed, use the absolute path
if [ "${zip_tool}" != "jar" ]; then
  zip_tool=$(pwd)/${zip_tool}
fi

GENERATED_FILES_DIR=$(mktemp -d)

function format_java_code () {
  java_files=$(find ${1} -name '*.java')
  chmod -R 664 ${java_files}
  ${formatter} -i ${java_files}
}


cd $GENERATED_FILES_DIR
${zip_tool} x "${generated_jar}"
format_java_code .
${zip_tool} c "${output}" $(find . -print)
cd -

rm -rf ${GENERATED_FILES_DIR}
