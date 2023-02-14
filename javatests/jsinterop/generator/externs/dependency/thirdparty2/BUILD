# Description:
#   Tests dependency mechanism
#

load(
    "//:jsinterop_generator.bzl",
    "jsinterop_generator",
)

package(
    default_applicable_licenses = ["//:license"],
    default_visibility = ["//:__subpackages__"],
    licenses = ["notice"],
)

jsinterop_generator(
    name = "thirdparty2",
    srcs = ["thirdparty2.js"],
    extension_type_prefix = "Foo",
    generate_j2cl_build_test = False,
    deps = [
        "//javatests/jsinterop/generator/externs/dependency/thirdparty",
    ],
)
