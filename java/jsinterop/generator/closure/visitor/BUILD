# Description:
#  Closure visitor.

package(
    default_applicable_licenses = ["//:license"],
    default_visibility = [
        "//:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "visitor",
    srcs = glob(["*.java"]),
    deps = [
        "//java/jsinterop/generator/closure/helper",
        "//java/jsinterop/generator/helper",
        "//java/jsinterop/generator/model",
        "//third_party:guava",
        "//third_party:jscomp",
    ],
)
