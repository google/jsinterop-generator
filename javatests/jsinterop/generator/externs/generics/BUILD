# Description:
#   Tests conversion of generics

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

jsinterop_generator_test(
    name = "Generics",
    srcs = ["generics.js"],
    expected_output = [
        "Bar.java.txt",
        "InterfaceWithGeneric.java.txt",
        "SimpleInterface.java.txt",
        "AnonymousTypes.java.txt",
        "ExtendInterfaceWithGeneric.java.txt",
        "SimpleClass.java.txt",
        "SimpleClassChild.java.txt",
        "BarChild.java.txt",
    ],
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
