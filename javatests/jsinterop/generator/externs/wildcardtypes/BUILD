# Description:
#   Tests conversion with forced wildcard types.
#

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

jsinterop_generator_test(
    name = "wildcardtypes",
    srcs = ["wildcardtypes.js"],
    expected_output = glob(["*.java.txt"]),
    wildcard_types_files = ["wildcardtypes.txt"],
)
