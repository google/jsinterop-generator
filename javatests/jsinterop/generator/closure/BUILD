load("@rules_java//java:defs.bzl", "java_library")

# Helper classes for test.
package(
    default_applicable_licenses = ["//:license"],
    default_visibility = ["//javatests:__subpackages__"],
    licenses = ["notice"],
)

java_library(
    name = "helpers",
    srcs = ["TestUtil.java"],
    deps = [
        "//java/jsinterop/generator/closure",
        "//third_party:guava",
        "//third_party:jscomp",
    ],
)
