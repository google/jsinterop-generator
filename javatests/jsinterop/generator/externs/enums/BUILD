# Description:
#   Tests conversion of closure enums.
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
    name = "enums",
    srcs = ["enums.js"],
    expected_output = [
        "Foo.java.txt",
        "Bar.java.txt",
        "Baz.java.txt",
    ],
)
