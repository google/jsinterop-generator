# Description:
#   Tests conversion of modules
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
    name = "modules",
    srcs = ["modules.js"],
    expected_output = glob([
        "*.java.txt",
    ]),
)
