# Description:
#  Generates JsTypes classes from closure extern files

package(default_visibility = [
    "//third_party/java/jsinterop:__subpackages__",
])

# Apache2
licenses(["notice"])

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
