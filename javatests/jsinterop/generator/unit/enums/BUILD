# Description:
#  Test behavior of the jsinterop generator with unsupported Closure enum types
#

load("@rules_java//java:defs.bzl", "java_test")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_test(
    name = "EnumsTest",
    srcs = ["EnumsTest.java"],
    deps = [
        "//java/jsinterop/generator/helper",
        "//javatests/jsinterop/generator/closure:helpers",
        "//third_party:jscomp",
        "//third_party:junit",
    ],
)
