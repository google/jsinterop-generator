# Description:
#  JsInterop version of the typescript compiler api

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "visitor",
    srcs = glob(["*.java"]),
    deps = [
        "//java/com/google/common/collect:collect-gwt",
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
