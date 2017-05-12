# Description:
#  Helper classes

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:jscomp",
    ],
)
