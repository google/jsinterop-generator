# Description:
#   Tests dependency mechanism
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
    name = "dependency",
    srcs = ["dependency.js"],
    expected_output = glob(["*.java.txt"]),
    extension_type_prefix = "MyLib",
    deps = [
        "//javatests/jsinterop/generator/externs/dependency/parentthirdparty",
        "//javatests/jsinterop/generator/externs/dependency/thirdparty",
        "//javatests/jsinterop/generator/externs/dependency/thirdparty2",
    ],
)
