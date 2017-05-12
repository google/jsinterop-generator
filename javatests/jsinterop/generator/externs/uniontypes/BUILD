# Description:
#   Tests conversion of union types
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "UnionTypes",
    srcs = ["uniontypes.js"],
    expected_output = [
        "ParentInterface.java",
        "Child.java",
        "Foo.java",
    ],
)
