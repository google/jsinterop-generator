# Description:
#   Tests conversion of closure enums.
#

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
)

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "enums",
    srcs = ["enums.js"],
    expected_output = [
        "Foo.java",
        "Foo__Constants.java",
        "Bar.java",
        "Bar__Constants.java",
        "Baz.java",
    ],
)
