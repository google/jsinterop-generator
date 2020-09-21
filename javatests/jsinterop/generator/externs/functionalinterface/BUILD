# Description:
#   Tests FunctionalInterface annotation logic.
#
load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_visibility = ["//:__subpackages__"],
    licenses = ["notice"],
)

jsinterop_generator_test(
    name = "functionalinterface",
    srcs = ["functionalinterface.js"],
    expected_output = [
        "InterfaceWithOneMethod.java.txt",
        "InterfaceWithTwoMethods.java.txt",
        "StructuralInterface.java.txt",
    ],
)
