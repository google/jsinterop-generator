# Description:
#  Generates JsTypes classes from closure extern files

load("@rules_java//java:defs.bzl", "java_binary", "java_library")

package(
    default_applicable_licenses = ["//:license"],
    default_visibility = [
        "//:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "closure",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/closure/helper",
        "//java/jsinterop/generator/closure/visitor",
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//java/jsinterop/generator/visitor",
        "//java/jsinterop/generator/writer",
        "//third_party:args4j",
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:jscomp",
        "//third_party:jsr305_annotations",
    ],
)

java_binary(
    name = "ClosureJsinteropGenerator",
    main_class = "jsinterop.generator.closure.Runner",
    visibility = ["//visibility:public"],
    runtime_deps = [":closure"],
)
