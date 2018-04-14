# Description:
#   Tests dependency mechanism
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//:jsinterop_generator.bzl",
    "jsinterop_generator",
)

jsinterop_generator(
    name = "thirdparty",
    srcs = ["thirdparty.js"],
    generate_j2cl_build_test = False,
    deps = [
        "//javatests/jsinterop/generator/externs/dependency/parentthirdparty",
    ],
)
