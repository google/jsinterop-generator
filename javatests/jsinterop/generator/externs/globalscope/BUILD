# Description:
#   Tests conversion property/functions defined on the global scope
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
    name = "globalscope",
    srcs = ["globalscope.js"],
    expected_output = glob(["*.java.txt"]),
    global_scope_class_name = "Global",
)
