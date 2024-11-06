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

def jsinterop_generator_test(
        name,
        srcs,
        expected_output,
        extension_type_prefix = None,
        global_scope_class_name = None,
        name_mapping_files = [],
        deps = [],
        conversion_mode = "closure",
        j2cl_test_externs_list = None,
        integer_entities_files = [],
        wildcard_types_files = [],
        generate_j2cl_build_test = None):
    if conversion_mode != "closure":
        # TODO(b/121201145): Migrate the typescript generator app to J2CL
        return

    jsinterop_generator_name = "%s__jsinterop_generator" % name
    generator_output = ":%s__internal_src_generated.srcjar" % jsinterop_generator_name
    jsinterop_generator(
        name = jsinterop_generator_name,
        srcs = srcs,
        extension_type_prefix = extension_type_prefix,
        global_scope_class_name = global_scope_class_name,
        name_mapping_files = name_mapping_files,
        deps = deps,
        conversion_mode = conversion_mode,
        externs_deps = j2cl_test_externs_list,
        integer_entities_files = integer_entities_files,
        wildcard_types_files = wildcard_types_files,
        generate_j2cl_build_test = generate_j2cl_build_test,
    )

    jar_tool = "@bazel_tools//tools/jdk:jar"
    java_format_tool = "//third_party:google_java_format"

    native.sh_test(
        name = name,
        size = "small",
        srcs = [
            "//javatests/jsinterop/generator:jsinterop_generator_test.sh",
        ],
        data = [
            jar_tool,
            java_format_tool,
            generator_output,
        ] + expected_output,
        args = [
            "$(location %s)" % generator_output,
            native.package_name(),
            "$(location %s)" % jar_tool,
            "$(location %s)" % java_format_tool,
        ],
    )
