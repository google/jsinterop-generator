# Description:
#   Tests conversion of type literal.
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
    name = "structuraltypes",
    srcs = ["structuraltypes.js"],
    expected_output = [
        "SimpleClass.java.txt",
        "SimpleModule.java.txt",
        "Global.java.txt",
        "FooBar.java.txt",
        "FooBar2.java.txt",
        "InnerStructuralType.java.txt",
        "ClassInModule.java.txt",
    ],
    global_scope_class_name = "Global",
    deps = ["//javatests/jsinterop/generator/externs/natives"],
)
