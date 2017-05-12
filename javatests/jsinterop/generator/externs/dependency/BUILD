# Description:
#   Tests dependency mechanism
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "dependency",
    srcs = ["dependency.js"],
    expected_output = [
        "MyLibGlobal.java",
        "SimpleClass.java",
        "SimpleInterface.java",
        "MyLibThirdPartyClass.java",
    ],
    extension_type_prefix = "MyLib",
    deps = [
        "//javatests/jsinterop/generator/externs/dependency/parentthirdparty",
        "//javatests/jsinterop/generator/externs/dependency/thirdparty",
        "//javatests/jsinterop/generator/externs/dependency/thirdparty2",
    ],
)
