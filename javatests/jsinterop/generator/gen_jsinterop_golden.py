# Copyright 2024 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Regenerates all golden files.

Builds all extern tests, unzips the generated
corresponding files into the directories.
"""

import os
import subprocess
import sys


_TARGETS = "//javatests/jsinterop/generator/externs/%s:%s__jsinterop_generator__internal_src_generated.srcjar"
_GOLDEN_FILES_BASE_DIR = "javatests/jsinterop/generator/externs/"
_OUTPUT_BASE_PATH = "blaze-bin/javatests/jsinterop/generator/externs/%s/%s__jsinterop_generator__internal_src_generated.srcjar"


def _build_extern_test(test_name):
  code = subprocess.call(
      ["blaze", "--blazerc=/dev/null", "build"]
      + [_TARGETS % (test_name, test_name)]
  )
  if code != 0:
    print("Blaze build of extern test failed")
    sys.exit(-1)


def _unzip_jar(test_name, golden_dir):
  code = subprocess.call(
      ["unzip", "-o", "-j", "-q"]
      + [_OUTPUT_BASE_PATH % (test_name, test_name)]
      + ["-x", "META-INF/MANIFEST.MF"]
      + ["-d"]
      + [golden_dir]
  )
  if code != 0:
    print("unzip command failed")
    sys.exit(-1)


def _rename_golden_files(golden_dir):
  for root, _, files in os.walk(golden_dir):
    for f in files:
      if f.endswith(".java"):
        path = os.path.join(root, f)
        os.rename(path, path + ".txt")


def _delete_files(directory):
  file_path = os.path.join(directory, "*.java.txt")
  print("deleting %s" % file_path)
  subprocess.call("rm -rf " + file_path, shell=True)


def _g4(op, files):
  if os.path.exists("../.hg"):
    # Don't attempt g4 operations in fig clients
    return
  code = subprocess.call(["g4", op] + files)
  if code != 0:
    print("Cannot g4 %s" % op)
    sys.exit(-1)


def _update_golden(test_name):
  """Regenerates all golden files of the given test name."""
  _build_extern_test(test_name)
  golden_base = _GOLDEN_FILES_BASE_DIR + test_name
  _unzip_jar(test_name, golden_base)
  _delete_files(golden_base)
  _rename_golden_files(golden_base)
  _g4("revert", ["-a"])


def main():
  test_name = sys.argv[1]
  # `natives` directory does not contain test but a shared library used
  # by all tests.
  all_tests = sorted(
      [test for test in os.listdir(_GOLDEN_FILES_BASE_DIR) if test != "natives"]
  )

  if test_name == "all":
    tests = all_tests
  else:
    if test_name not in all_tests:
      print("invalid test name %s" % test_name)
      sys.exit(-1)
    tests = [test_name]

  for test in tests:
    print("Updating golden for %s" % test)
    _update_golden(test)


if __name__ == "__main__":
  main()
