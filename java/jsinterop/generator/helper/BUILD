# Description:
#  Generator helper

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    deps = [
        "//java/com/google/common/collect:collect-gwt",
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
