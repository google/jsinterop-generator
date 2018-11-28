"""Bazel implementation of js_library rule.
"""

load("@io_bazel_rules_closure//closure:defs.bzl", _closure_js_library = "closure_js_library")

js_library = _closure_js_library
