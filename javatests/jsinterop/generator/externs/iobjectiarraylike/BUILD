# Description:
#   Tests conversion index signatures
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
    name = "iobjectiarraylike",
    srcs = ["iobjectiarraylike.js"],
    expected_output = [
        "Foo.java.txt",
        "Bar.java.txt",
        "Baz.java.txt",
    ],
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
