workspace(name = "com_google_jsinterop_generator")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

maven_server(
    name = "sonatype_snapshot",
    url = "https://oss.sonatype.org/content/repositories/snapshots",
)

http_archive(
    name = "google_bazel_common",
    strip_prefix = "bazel-common-2782531da81d4002bce16e853953d9e8117a6fc1",
    url = "https://github.com/google/bazel-common/archive/2782531da81d4002bce16e853953d9e8117a6fc1.tar.gz",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()

maven_jar(
    name = "args4j",
    artifact = "args4j:args4j:2.33",
)

maven_jar(
    name = "jscomp",
    artifact = "com.google.javascript:closure-compiler:1.0-SNAPSHOT",
    server = "sonatype_snapshot",
)

maven_jar(
    name = "jsinterop_annotations",
    artifact = "com.google.jsinterop:jsinterop-annotations:1.0.2",
)

http_archive(
    name = "com_google_closure_compiler",
    build_file = "//:BUILD.jscomp",
    strip_prefix = "closure-compiler-20170409",
    url = "https://github.com/google/closure-compiler/archive/v20170409.tar.gz",
)

maven_jar(
    name = "com_google_jsinterop_base",
    artifact = "com.google.jsinterop:base:1.0.0-RC1",
)
