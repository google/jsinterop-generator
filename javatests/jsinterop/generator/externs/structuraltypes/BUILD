# Description:
#   Tests conversion of type literal.
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
    name = "structuraltypes",
    srcs = ["structuraltypes.js"],
    expected_output = glob(["*.java.txt"]),
    global_scope_class_name = "Global",
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
