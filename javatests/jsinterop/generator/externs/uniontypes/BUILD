# Description:
#   Tests conversion of union types
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
    name = "UnionTypes",
    srcs = ["uniontypes.js"],
    expected_output = [
        "ParentInterface.java.txt",
        "Child.java.txt",
        "Foo.java.txt",
    ],
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
