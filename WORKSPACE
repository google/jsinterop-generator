workspace(name = "com_google_jsinterop_generator")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "google_bazel_common",
    strip_prefix = "bazel-common-2782531da81d4002bce16e853953d9e8117a6fc1",
    url = "https://github.com/google/bazel-common/archive/2782531da81d4002bce16e853953d9e8117a6fc1.tar.gz",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()

maven_server(
    name = "sonatype_snapshot",
    url = "https://oss.sonatype.org/content/repositories/snapshots",
)

load("//:workspace_defs.bzl", "jsinterop_generator_repositories")

jsinterop_generator_repositories()
