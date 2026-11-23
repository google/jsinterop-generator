# Description:
#  Test behavior of the jsinterop generator with functional interface annotator
#

load("@rules_java//java:defs.bzl", "java_test")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_test(
    name = "FunctionalInterfaceAnnotatorTest",
    srcs = ["FunctionalInterfaceAnnotatorTest.java"],
    deps = [
        "//java/jsinterop/generator/model",
        "//java/jsinterop/generator/visitor",
        "//third_party:junit",
    ],
)
