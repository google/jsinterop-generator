"""Bazel rule for loading external repository deps for jsinterop generator."""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

_JSINTEROP_BASE_VERSION = "1.0.0"

def load_jsinterop_generator_repo_deps():
    http_archive(
        name = "com_google_jsinterop_base",
        strip_prefix = "jsinterop-base-%s" % _JSINTEROP_BASE_VERSION,
        url = "https://github.com/google/jsinterop-base/archive/%s.zip" % _JSINTEROP_BASE_VERSION,
        sha256 = "56ded177123efca2ce68e433e5b496898a80986cf48f489572ca7b5bf17db421",
    )
