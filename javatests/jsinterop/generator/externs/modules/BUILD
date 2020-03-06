# Description:
#   Tests conversion of modules
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
    name = "Modules",
    srcs = ["modules.js"],
    expected_output = [
        "Bar.java.txt",
        "BarInterface.java.txt",
        "Baz.java.txt",
        "Foo.java.txt",
        "FooInterface.java.txt",
    ],
)
