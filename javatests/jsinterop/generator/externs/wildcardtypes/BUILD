# Description:
#   Tests conversion with forced wildcard types.
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
    name = "wildcardtypes",
    srcs = ["wildcardtypes.js"],
    expected_output = [
        "Foo.java.txt",
        "Bar.java.txt",
    ],
    wildcard_types_files = ["wildcardtypes.txt"],
)
