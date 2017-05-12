# Description:
#   Tests conversion of modules
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "Modules",
    srcs = ["modules.js"],
    expected_output = [
        "Bar.java",
        "BarInterface.java",
        "Baz.java",
        "Foo.java",
        "FooInterface.java",
    ],
)
