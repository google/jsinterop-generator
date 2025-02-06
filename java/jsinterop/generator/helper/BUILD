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
        # Do not use. Temporary visible to workaround https://github.com/bazelbuild/bazel/issues/25214.
        "//visibility:public",
    ],
    deps = [
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
