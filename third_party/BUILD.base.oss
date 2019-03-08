# Description:
#   Thirdparty dependencies indirection for bazel for jsinterop base.
package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_import")

alias(
    name = "gwt-jsinterop-annotations",
    actual = "@com_google_j2cl//:jsinterop-annotations",
)

alias(
    name = "gwt-jsinterop-annotations-j2cl",
    actual = "@com_google_j2cl//:jsinterop-annotations-j2cl",
)

java_library(
    name = "gwt-dev",
    exports = ["@org_gwtproject_gwt_dev//jar"],
)

java_library(
    name = "gwt-javaemul-internal-annotations",
    exports = ["@org_gwtproject_gwt//user:gwt-javaemul-internal-annotations"],
)

j2cl_import(
    name = "gwt-javaemul-internal-annotations-j2cl",
    jar = "@org_gwtproject_gwt//user:gwt-javaemul-internal-annotations",
)

alias(
    name = "zip",
    actual = "@bazel_tools//tools/zip:zipper",
)
