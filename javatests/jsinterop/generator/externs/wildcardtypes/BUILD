# Description:
#   Tests conversion with forced wildcard types.
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
    name = "wildcardtypes",
    srcs = ["wildcardtypes.js"],
    expected_output = [
        "Foo.java",
        "Bar.java",
    ],
    wildcard_types_files = ["wildcardtypes.txt"],
)
