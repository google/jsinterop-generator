# Description:
#  JsInterop version of the typescript compiler api

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "writer",
    srcs = glob(["*.java"]),
    javacopts = ["-Xep:BetaApi:OFF"],
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//java/jsinterop/generator/option",
        "//third_party:guava",
    ],
)
