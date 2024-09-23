# Description:
#  Helper classes

load("@rules_java//java:defs.bzl", "java_library")

package(
    default_applicable_licenses = ["//:license"],
    default_visibility = [
        "//:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:jscomp",
    ],
)
