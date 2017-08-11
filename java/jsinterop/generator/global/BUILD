# Description:
#  JsInterop version of the typescript compiler api

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "global",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/nodejs",
        "//third_party:gwt-jsinterop-annotations",
    ],
)
