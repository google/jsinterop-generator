# Description:
#  Test behavior of the jsinterop generator with type that are not defined
#

load("@rules_java//java:defs.bzl", "java_test")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_test(
    name = "UndefinedTypesTest",
    srcs = ["UndefinedTypesTest.java"],
    deps = [
        "//java/jsinterop/generator/helper",
        "//javatests/jsinterop/generator/closure:helpers",
        "//third_party:guava",
        "//third_party:jscomp",
        "//third_party:junit",
    ],
)
