# Description:
#   Tests conversion of a simple type
#

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
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
        "DeprecatedInterface.java",
    ],
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
