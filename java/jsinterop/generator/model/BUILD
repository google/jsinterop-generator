# Description:
#  JsInterop version of the typescript compiler api

package(default_visibility = [
    "//:__subpackages__",
])

# Apache2
licenses(["notice"])

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    deps = [
        "//java/com/google/common/collect:collect-gwt",
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:jsr305_annotations",
        "@com_google_errorprone_error_prone_annotations",
    ],
)
