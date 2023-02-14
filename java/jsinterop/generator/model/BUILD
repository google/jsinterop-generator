# Description:
#  JsInterop version of the typescript compiler api

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    visibility = [
        "//:__subpackages__",
        "@com_google_elemental2//java/elemental2:__subpackages__",
    ],
    deps = [
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:jsr305_annotations",
        "@com_google_j2cl//transpiler/java/com/google/j2cl/common/visitor",
        "@com_google_errorprone_error_prone_annotations",
    ],
)
