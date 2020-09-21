# Description:
#   Thirdparty dependencies indirection for bazel.
package(default_visibility = ["//:__subpackages__"])

licenses(["notice"])

# Direct dependencies for compiling java src code of the closure jsinterop generator.
# These targets don't need to be public.
java_plugin(
    name = "auto_annotation_plugin",
    processor_class = "com.google.auto.value.processor.AutoAnnotationProcessor",
    deps = ["@com_google_auto_value"],
)

java_plugin(
    name = "auto_value_plugin",
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = ["@com_google_auto_value"],
)

java_library(
    name = "auto_value",
    exported_plugins = [
        ":auto_annotation_plugin",
        ":auto_value_plugin",
    ],
    exports = ["@com_google_auto_value"],
)

java_library(
    name = "jscomp",
    exports = ["@com_google_javascript_closure_compiler"],
)

java_library(
    name = "args4j",
    exports = ["@args4j"],
)

java_library(
    name = "jsr305_annotations",
    exports = ["@com_google_code_findbugs_jsr305"],
)

java_library(
    name = "guava",
    exports = ["@com_google_guava"],
)

# Dependencies used in the jsinterop_generator skylark rules.
# These dependencies need to be publicly visible in order to allow any other repo to use the
# jsinterop generator rule.

java_binary(
    name = "google_java_format",
    main_class = "com.google.googlejavaformat.java.Main",
    visibility = ["//visibility:public"],
    runtime_deps = ["@com_google_google_java_format//jar"],
)

alias(
    name = "gwt-jsinterop-annotations",
    actual = "@com_google_j2cl//:jsinterop-annotations",
    visibility = ["//visibility:public"],
)

alias(
    name = "gwt-jsinterop-annotations-j2cl",
    actual = "@com_google_j2cl//:jsinterop-annotations-j2cl",
    visibility = ["//visibility:public"],
)

alias(
    name = "jsinterop-base",
    actual = "@com_google_jsinterop_base//:jsinterop-base",
    visibility = ["//visibility:public"],
)

alias(
    name = "jsinterop-base-j2cl",
    actual = "@com_google_jsinterop_base//:jsinterop-base-j2cl",
    visibility = ["//visibility:public"],
)

alias(
    name = "j2cl_visitable",
    actual = "@com_google_j2cl//transpiler/java/com/google/j2cl/ast/annotations",
)

alias(
    name = "j2cl_processors",
    actual = "@com_google_j2cl//transpiler/java/com/google/j2cl/ast/processors/common",
)

java_library(
    name = "junit",
    testonly = 1,
    exports = ["@org_junit//jar"],
)
