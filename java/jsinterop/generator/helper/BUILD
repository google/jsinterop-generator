# Description:
#  Generator helper

load("@rules_java//java:defs.bzl", "java_library")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    visibility = [
        "//:__subpackages__",
        "@com_google_elemental2//java/elemental2:__subpackages__",
    ],
    deps = [
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
