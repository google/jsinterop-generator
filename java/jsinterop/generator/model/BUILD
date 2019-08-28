# Description:
#  JsInterop version of the typescript compiler api

package(
    default_visibility = ["//visibility:public"],
    # Apache2
    licenses = ["notice"],
)

java_library(
    name = "model",
    srcs = glob(["*.java"]),
    deps = [
        "//third_party:auto_value",
        "//third_party:guava",
        "//third_party:j2cl_processors",
        "//third_party:j2cl_visitable",
        "//third_party:jsr305_annotations",
        "@com_google_errorprone_error_prone_annotations",
    ],
)
