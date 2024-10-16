# Description:
#   Tests conversion index signatures
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
    name = "iobjectiarraylike",
    srcs = ["iobjectiarraylike.js"],
    expected_output = glob(["*.java.txt"]),
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
