# Description:
#  Generator helper

package(licenses = ["notice"])

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    visibility = [
        "//:__subpackages__",
        "@com_google_elemental2//java/elemental2:__subpackages__",
    ],
    deps = [
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
