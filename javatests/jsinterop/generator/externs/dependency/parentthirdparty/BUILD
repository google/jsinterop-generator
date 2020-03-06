# Description:
#   Tests dependency mechanism
#

load(
    "//:jsinterop_generator.bzl",
    "jsinterop_generator",
)

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
)

jsinterop_generator(
    name = "parentthirdparty",
    srcs = ["parentthirdparty.js"],
    generate_j2cl_build_test = False,
)
