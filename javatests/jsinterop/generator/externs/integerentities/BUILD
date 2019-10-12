# Description:
#   Tests conversion integer entities.
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
    name = "integerentities",
    srcs = ["integerentities.js"],
    expected_output = [
        "Foo.java.txt",
        "Foo__Constants.java.txt",
        "Global.java.txt",
    ],
    global_scope_class_name = "Global",
    integer_entities_files = ["integerentities.txt"],
    name_mapping_files = ["renaming.txt"],
)
