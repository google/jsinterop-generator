# Description:
#   Tests conversion of "overloaded" members, static and non-static
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
    name = "overloads",
    srcs = ["overloads.js"],
    expected_output = [
        "Foo.java.txt",
    ],
)
