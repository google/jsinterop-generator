# Description:
#   Tests conversion integer entities.
#

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

jsinterop_generator_test(
    name = "integerentities",
    srcs = ["integerentities.js"],
    expected_output = glob(["*.java.txt"]),
    global_scope_class_name = "Global",
    integer_entities_files = ["integerentities.txt"],
    name_mapping_files = ["renaming.txt"],
)
