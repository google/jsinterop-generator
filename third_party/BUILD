# Description:
#   Thirdparty dependencies indirection for bazel.
package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

# Direct dependencies for compiling java src code of the closure jsinterop generator.
# These targets don't need to be public.
java_library(
    name = "auto_value",
    exports = ["@google_bazel_common//third_party/java/auto:value"],
)

java_library(
    name = "jscomp",
    exports = ["@jscomp//jar"],
)

java_library(
    name = "args4j",
    exports = ["@args4j//jar"],
)

java_library(
    name = "jsr305_annotations",
    exports = ["@google_bazel_common//third_party/java/jsr305_annotations"],
)

java_library(
    name = "guava",
    exports = ["@google_bazel_common//third_party/java/guava"],
)

java_library(
    name = "gwt-jsinterop-annotations",
    exports = ["@jsinterop_annotations//jar"],
)

# Dependencies used in the jsinterop_generator skylark rules.
# These dependencies need to be publicly visible in order to allow any other repo to use the
# jsinterop generator rule.
filegroup(
    name = "typescript_compiler_js",
    # This part is not opensourced yet.
    srcs = ["empty.js"],
    visibility = ["//visibility:public"],
)

java_binary(
    name = "google_java_format",
    main_class = "com.google.googlejavaformat.java.Main",
    visibility = ["//visibility:public"],
    runtime_deps = ["@google_bazel_common//third_party/java/google_java_format"],
)

alias(
    name = "jar",
    actual = "@local_jdk//:jar",
    visibility = ["//visibility:public"],
)

alias(
    name = "jdk",
    actual = "@local_jdk//:jdk-default",
    visibility = ["//visibility:public"],
)

# Tests dependencies
alias(
    name = "common_externs",
    actual = "@com_google_closure_compiler//:common_externs",
    visibility = ["//javatests:__subpackages__"],
)

java_library(
    name = "jsinterop-base",
    visibility = ["//javatests:__subpackages__"],
    exports = ["@com_google_jsinterop_base//jar"],
)
