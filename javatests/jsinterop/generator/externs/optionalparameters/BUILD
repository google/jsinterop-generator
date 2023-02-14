# Description:
#   Tests conversion of optional parameters
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
    name = "optionalparameters",
    srcs = ["optionalparameters.js"],
    expected_output = [
        "SimpleClass.java.txt",
        "SimpleInterface.java.txt",
    ],
)
