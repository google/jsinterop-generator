# Description:
#   Tests conversion of function types.
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
    name = "entitiesrenaming",
    srcs = ["entitiesrenaming.js"],
    expected_output = [
        "SimpleInterface.java.txt",
        "Foo.java.txt",
        "Global.java.txt",
    ],
    global_scope_class_name = "Global",
    name_mapping_files = ["renaming.txt"],
)
