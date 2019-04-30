# Description:
#   Tests conversion of generics

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "Generics",
    srcs = ["generics.js"],
    expected_output = [
        "Bar.java",
        "InterfaceWithGeneric.java",
        "SimpleInterface.java",
        "AnonymousTypes.java",
        "ExtendInterfaceWithGeneric.java",
        "SimpleClass.java",
        "BarChild.java",
    ],
)
