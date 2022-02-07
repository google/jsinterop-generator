# Description:
#  JsInterop version of the typescript compiler api

package(
    default_visibility = [
        "//:__subpackages__",
        "@com_google_elemental2//java/elemental2:__subpackages__",
    ],
    licenses = ["notice"],
)

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    deps = [
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:j2cl_visitor",
        "//third_party:jsr305_annotations",
        "@com_google_errorprone_error_prone_annotations",
    ],
)
