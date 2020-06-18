"""Macro to use for loading the jsinterop generator repository"""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")
load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

def setup_jsinterop_generator_workspace():
    """Load all dependencies needed for jsinterop generator."""

    http_jar(
        name = "com_google_google_java_format",
        url = "https://github.com/google/google-java-format/releases/download/google-java-format-1.6/google-java-format-1.6-all-deps.jar",
        sha256 = "73faf7c9b95bffd72933fa24f23760a6b1d18499151cb39a81cda591ceb7a5f4",
    )
