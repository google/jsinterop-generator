load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

def jsinterop_generator_repositories():
    native.maven_jar(
        name = "args4j",
        artifact = "args4j:args4j:2.33",
    )

    native.maven_jar(
        name = "jscomp",
        artifact = "com.google.javascript:closure-compiler:1.0-SNAPSHOT",
        server = "sonatype_snapshot",
    )

    native.maven_jar(
        name = "jsinterop_annotations",
        artifact = "com.google.jsinterop:jsinterop-annotations:1.0.2",
    )

    http_archive(
        name = "com_google_closure_compiler",
        build_file = "//:BUILD.jscomp",
        strip_prefix = "closure-compiler-20170409",
        url = "https://github.com/google/closure-compiler/archive/v20170409.tar.gz",
    )

    native.maven_jar(
        name = "com_google_jsinterop_base",
        artifact = "com.google.jsinterop:base:1.0.0-RC1",
    )
