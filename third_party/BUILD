# Description:
#   Thirdparty dependencies indirection for bazel.

load("@j2cl//build_defs:rules.bzl", "j2cl_import")
load("@rules_java//java:defs.bzl", "java_binary", "java_library")

package(default_visibility = ["//:__subpackages__"])

licenses(["notice"])

# Direct dependencies for compiling java src code of the closure jsinterop generator.
# These targets don't need to be public.

java_library(
    name = "auto_value",
    exports = ["@google_bazel_common//third_party/java/auto:value"],
)

java_library(
    name = "jscomp",
    exports = ["@maven//:com_google_javascript_closure_compiler"],
)

java_library(
    name = "args4j",
    exports = ["@maven//:args4j_args4j"],
)

java_library(
    name = "jsr305_annotations",
    exports = ["@google_bazel_common//third_party/java/jsr305_annotations"],
)

java_library(
    name = "guava",
    exports = ["@google_bazel_common//third_party/java/guava"],
)

# Dependencies used in the jsinterop_generator skylark rules.
# These dependencies need to be publicly visible in order to allow any other repo to use the
# jsinterop generator rule.

java_binary(
    name = "google_java_format",
    main_class = "com.google.googlejavaformat.java.Main",
    visibility = ["//visibility:public"],
    runtime_deps = ["@google_bazel_common//third_party/java/google_java_format"],
)

java_library(
    name = "junit",
    testonly = 1,
    exports = ["@google_bazel_common//third_party/java/junit"],
)

alias(
    name = "error_prone_annotations",
    actual = "@google_bazel_common//third_party/java/error_prone:annotations",
)

alias(
    name = "jspecify_annotations",
    actual = "@google_bazel_common//third_party/java/jspecify_annotations",
    visibility = [
        # Do not use. Temporary visible to workaround https://github.com/bazelbuild/bazel/issues/25214.
        "//visibility:public",
    ],
)

j2cl_import(
    name = "jspecify_annotations-j2cl",
    jar = "@google_bazel_common//third_party/java/jspecify_annotations",
    visibility = [
        # Do not use. Temporary visible to workaround https://github.com/bazelbuild/bazel/issues/25214.
        "//visibility:public",
    ],
)
