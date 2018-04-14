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
    javacopts = ["-Xep:BetaApi:OFF"],
    deps = [
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
