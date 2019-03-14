"""Bazel definitions for jsinterop generator.

See corresponding bzl files for the documentation.
"""

load(
    "//build_defs/internal_do_not_use:jsinterop_generator_workspace.bzl",
    _setup_jsinterop_generator_workspace = "setup_jsinterop_generator_workspace",
)

setup_jsinterop_generator_workspace = _setup_jsinterop_generator_workspace
