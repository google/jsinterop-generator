# Description:
#   Tests conversion of type literal.
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "structuraltypes",
    srcs = ["structuraltypes.js"],
    expected_output = [
        "SimpleClass.java",
        "SimpleModule.java",
        "Global.java",
        "FooBar.java",
        "FooBar2.java",
        "InnerStructuralType.java",
        "ClassInModule.java",
    ],
    global_scope_class_name = "Global",
)
