# Description:
#   Tests conversion property/functions defined on the global scope
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
    name = "GlobalScope",
    srcs = ["globalscope.js"],
    expected_output = [
        "Global.java.txt",
        "Global__Constants.java.txt",
    ],
    global_scope_class_name = "Global",
)
