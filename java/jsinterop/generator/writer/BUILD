# Description:
#  JsInterop version of the typescript compiler api

package(
    default_visibility = [
        "//:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "writer",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
