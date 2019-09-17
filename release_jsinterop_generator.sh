#!/bin/bash -i
# Copyright 2019 Google Inc. All Rights Reserved
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS-IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# The script generates the open source artifacts for jsinterop generator (closure only) and
# upload them to sonatype.
set -e

lib_version=$1

if [ -z ${lib_version} ]; then
  echo "Please specify the lib version."
  exit 1;
fi

bazel_root="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

merge_jars() {
  tmp_directory=$(mktemp -d)
  jars=$1

  cd ${tmp_directory}
  for jar in ${jars[@]}; do
    jar xf ${bazel_root}/bazel-bin/java/jsinterop/generator/${jar}
  done

  jar cf $2 .
  mv $2 $3
  cd -
  rm -rf ${tmp_directory}
}

deploy_target='@com_google_j2cl//:deploy'
license_header="--no-license"
group_id="com.google.jsinterop"
maven_artifact="closure-generator"

bazel_rules=("closure:libclosure-src.jar" "closure/helper:libhelper-src.jar" \
   "closure/visitor:libvisitor-src.jar" "helper:libhelper-src.jar" \
   "model:libmodel-src.jar" "visitor:libvisitor-src.jar" \
   "writer:libwriter-src.jar" "closure:libclosure.jar" \
   "closure/helper:libhelper.jar" "closure/visitor:libvisitor.jar" \
   "helper:libhelper.jar" "model:libmodel.jar" \
   "visitor:libvisitor.jar" "writer:libwriter.jar")

jars=("closure/libclosure.jar" "closure/helper/libhelper.jar" \
   "closure/visitor/libvisitor.jar" "helper/libhelper.jar" \
   "model/libmodel.jar" "visitor/libvisitor.jar" \
   "writer/libwriter.jar")

src_jars=("closure/libclosure-src.jar" "closure/helper/libhelper-src.jar" \
   "closure/visitor/libvisitor-src.jar" "helper/libhelper-src.jar" \
    "model/libmodel-src.jar" "visitor/libvisitor-src.jar" \
    "writer/libwriter-src.jar")

cd ${bazel_root}
for rule in ${bazel_rules[@]}; do
  bazel build //java/jsinterop/generator/${rule}
done

tmp_artifact_dir=$(mktemp -d)

merge_jars ${jars} generator.jar ${tmp_artifact_dir}
merge_jars ${src_jars} generator-src.jar ${tmp_artifact_dir}


pom_template=${bazel_root}/maven/pom-closure-generator.xml

# we cannot run the script directly from Bazel as bazel doesn't allow interactive script
runcmd="$(mktemp /tmp/bazel-run.XXXXXX)"
bazel run --script_path="$runcmd"  ${deploy_target} -- ${maven_artifact} \
    ${tmp_artifact_dir}/generator.jar \
    ${tmp_artifact_dir}/generator-src.jar \
    ${license_header} \
    ${pom_template} \
    ${lib_version} \
    ${group_id}

"$runcmd"

rm "$runcmd"
