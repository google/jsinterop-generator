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
    javacopts = ["-Xep:BetaApi:OFF"],
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
