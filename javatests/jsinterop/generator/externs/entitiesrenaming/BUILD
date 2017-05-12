# Description:
#   Tests conversion of function types.
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "entitiesrenaming",
    srcs = ["entitiesrenaming.js"],
    expected_output = [
        "SimpleInterface.java",
        "Foo.java",
        "Global.java",
    ],
    name_mapping_files = ["renaming.txt"],
)
