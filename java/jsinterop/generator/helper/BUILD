# Description:
#  Generator helper

package(
    default_visibility = [
        "//:__subpackages__",
        "@com_google_elemental2//java/elemental2:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "helper",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/model",
        "//third_party:guava",
    ],
)
