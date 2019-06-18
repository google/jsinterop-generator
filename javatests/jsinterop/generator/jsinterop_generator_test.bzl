"""jsinterop_generator_test build macro.

Defines an integration test for the JsInteropGenerator


Example usage:

jsinterop_generator_test(
        name = "SimpleTest",
        srcs = ["test.d.ts"],
        expected_output = "test_output.txt",
)

"""

load("//:jsinterop_generator.bzl", "jsinterop_generator")
load("@bazel_tools//tools/build_defs/js:rules.bzl", "js_library")

def jsinterop_generator_test(
        name,
        srcs,
        expected_output,
        extension_type_prefix = None,
        global_scope_class_name = None,
        name_mapping_files = [],
        deps = [],
        conversion_mode = "closure",
        j2cl_test_externs_list = [],
        integer_entities_files = [],
        wildcard_types_files = [],
        generate_j2cl_build_test = None):
    jsinterop_generator_name = "%s__jsinterop_generator" % name
    generator_output = ":%s__internal_src_generated.srcjar" % jsinterop_generator_name

    j2cl_js_deps = None
    if j2cl_test_externs_list:
        externs_lib_name = "%s-externs" % name
        js_library(
            name = externs_lib_name,
            srcs = j2cl_test_externs_list,
            check_level = "OFF",
        )
        j2cl_js_deps = [":%s" % externs_lib_name]

    jsinterop_generator(
        name = jsinterop_generator_name,
        srcs = srcs,
        extension_type_prefix = extension_type_prefix,
        global_scope_class_name = global_scope_class_name,
        name_mapping_files = name_mapping_files,
        deps = deps,
        conversion_mode = conversion_mode,
        j2cl_js_deps = j2cl_js_deps,
        integer_entities_files = integer_entities_files,
        wildcard_types_files = wildcard_types_files,
        generate_j2cl_build_test = generate_j2cl_build_test,
    )

    native.sh_test(
        name = name,
        size = "small",
        srcs = [
            "//javatests/jsinterop/generator:jsinterop_generator_test.sh",
        ],
        data = [
            "//third_party:jar",
            "//third_party:google_java_format",
            generator_output,
        ] + expected_output,
        args = [
            "$(location %s)" % generator_output,
            native.package_name(),
            "$(location //third_party:jar)",
            "$(location //third_party:google_java_format)",
        ],
    )
