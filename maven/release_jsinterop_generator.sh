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

usage() {
    echo ""
    echo "$(basename $0): Build and deploy script for JsInterop Generator."
    echo ""
    echo "$(basename $0) --version <version> [--no-deploy]"
    echo "    --help"
    echo "        Print this help output and exit."
    echo "    --version <version>"
    echo "        Maven version to use for deploying to sonatype."
    echo "    --no-deploy"
    echo "        Skip the deployment part but build all artifacts."
    echo "    --no-git-tag"
    echo "        Skip the creation of git tag."
    echo ""
}

deploy_flag=""
git_tag=true

while [[ "$1" != "" ]]; do
  case $1 in
    --version )    if [ -z $2 ] || [[ $2 == "--no-deploy" ]]; then
                     echo "Error: Incorrect version value."
                     usage
                     exit 1
                   fi
                   shift
                   lib_version=$1
                   ;;
    --no-deploy )  deploy_flag="--no-deploy"
                   ;;
    --no-git-tag ) git_tag=false
                   ;;
    --help )       usage
                   exit 1
                   ;;
    * )            echo "Error: unexpected option $1"
                   usage
                   exit 1
                   ;;
  esac
  shift
done

if [[ -z "$lib_version" ]]; then
  echo "Error: --version flag is missing"
  usage
  exit 1
fi

if [ ! -f "WORKSPACE" ]; then
  echo "Error: should be run from the root of the Bazel repository"
  exit 1
fi

bazel_root=$(pwd)

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

deploy_target='@com_google_j2cl//maven:deploy'
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
bazel run --script_path="$runcmd"  ${deploy_target} -- ${deploy_flag}  \
    --artifact ${maven_artifact} \
    --jar-file ${tmp_artifact_dir}/generator.jar \
    --src-jar ${tmp_artifact_dir}/generator-src.jar \
    --pom-template ${pom_template} \
    --lib-version ${lib_version} \
    --group-id ${group_id}

"$runcmd"

rm "$runcmd"

if [[ ${git_tag} == true ]]; then
  git tag -a v${lib_version} -m "${lib_version} release"
  git push origin v${lib_version}
fi
