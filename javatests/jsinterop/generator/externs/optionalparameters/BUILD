# Description:
#   Tests conversion of optional parameters
#

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
)

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "optionalparameters",
    srcs = ["optionalparameters.js"],
    expected_output = [
        "SimpleClass.java",
        "SimpleInterface.java",
    ],
)
