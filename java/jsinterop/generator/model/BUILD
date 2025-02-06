# Description:
#  JsInterop version of the typescript compiler api

load("@rules_java//java:defs.bzl", "java_library")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    visibility = [
        # Do not use. Temporary visible to workaround https://github.com/bazelbuild/bazel/issues/25214.
        "//visibility:public",
    ],
    deps = [
        "//third_party:auto_value",
        "//third_party:error_prone_annotations",
        "//third_party:guava",
        "//third_party:jsr305_annotations",
        "@com_google_j2cl//transpiler/java/com/google/j2cl/common/visitor",
    ],
)
