# Description:
#   Tests conversion of a simple type
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "SimpleClass",
    srcs = ["simpleclass.js"],
    expected_output = [
        "PrivateClass.java",
        "SimpleClass.java",
        "SimpleClass__Constants.java",
        "SimpleInterface.java",
        "SimpleInterface__Constants.java",
        "SimpleStructuralInterface.java",
    ],
)
