# Description:
#   Thirdparty dependencies indirection for bazel.
package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

# Direct dependencies for compiling java src code of the closure jsinterop generator.
# These targets don't need to be public.
java_plugin(
    name = "auto_annotation_plugin",
    processor_class = "com.google.auto.value.processor.AutoAnnotationProcessor",
    deps = ["@auto_value//jar"],
)

java_plugin(
    name = "auto_value_plugin",
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = ["@auto_value//jar"],
)

java_library(
    name = "auto_value",
    exported_plugins = [
        ":auto_annotation_plugin",
        ":auto_value_plugin",
    ],
    exports = ["@auto_value//jar"],
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
    exports = ["@jsr305//jar"],
)

java_library(
    name = "guava",
    exports = ["@guava//jar"],
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
    runtime_deps = ["@com_google_google_java_format//jar"],
    visibility = ["//visibility:public"],
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

java_library(
    name = "jsinterop-base",
    exports = ["@com_google_jsinterop_base//java/jsinterop/base:base"],
    visibility = ["//javatests:__subpackages__"],
)
