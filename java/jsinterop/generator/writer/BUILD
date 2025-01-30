# Description:
#  JsInterop version of the typescript compiler api

load("@rules_java//java:defs.bzl", "java_library")

package(
    default_applicable_licenses = ["//:license"],
    default_visibility = [
        "//:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "writer",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:error_prone_annotations",
        "//third_party:guava",
    ],
)
