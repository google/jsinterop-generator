# Description:
#   Tests for Array.
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
    name = "array",
    srcs = ["array.js"],
    expected_output = glob(["*.java.txt"]),
    extension_type_prefix = "Test",
    generate_j2cl_build_test = False,
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
