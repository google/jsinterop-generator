# Description:
#   Tests conversion of a simple type
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
    name = "simpleclass",
    srcs = ["simpleclass.js"],
    expected_output = [
        "PrivateClass.java.txt",
        "SimpleClass.java.txt",
        "SimpleClass__Constants.java.txt",
        "SimpleInterface.java.txt",
        "SimpleInterface__Constants.java.txt",
        "SimpleStructuralInterface.java.txt",
        "DeprecatedInterface.java.txt",
    ],
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
