# Description:
#   Tests conversion of function types.
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
    name = "FunctionType",
    srcs = ["functiontype.js"],
    expected_output = [
        "AliasedFunctionType.java.txt",
        "SimpleClass.java.txt",
        "SimpleInterface.java.txt",
        "SimpleModule.java.txt",
        "Global.java.txt",
    ],
    global_scope_class_name = "Global",
    deps = [
        "//javatests/jsinterop/generator/externs/natives",
    ],
)
