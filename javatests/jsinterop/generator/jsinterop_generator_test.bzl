"""jsinterop_generator_test build macro.

Defines an integration test for the JsInteropGenerator


Example usage:

jsinterop_generator_test(
        name = "SimpleTest",
        srcs = ["test.d.ts"],
        expected_output = "test_output.txt",
)

"""

load("@rules_shell//shell:sh_test.bzl", "sh_test")
load("//:jsinterop_generator.bzl", "jsinterop_generator")

def jsinterop_generator_test(
        name,
        srcs,
        expected_output,
        extension_type_prefix = None,
        global_scope_class_name = None,
        name_mapping_files = [],
        deps = [],
        j2cl_test_externs_list = None,
        integer_entities_files = [],
        wildcard_types_files = [],
        generate_j2cl_build_test = None):
    """Defines an integration test for jsinterop_generator.

    Args:
      name: Name of the test target.
      srcs: Closure extern files passed to `jsinterop_generator`.
      expected_output: Expected `.java.txt` golden files for comparison.
      extension_type_prefix: Prefix used for generated extension types.
      global_scope_class_name: Java class name for the global scope.
      name_mapping_files: Files mapping symbol FQNs to renamed parameter names.
      deps: Dependency `jsinterop_generator` targets.
      j2cl_test_externs_list: Externs file list passed as `externs_deps` to
        `jsinterop_generator`.
      integer_entities_files: Files listing numeric entities typed as integers.
      wildcard_types_files: Files specifying wildcard bounds for type parameters.
      generate_j2cl_build_test: Whether to generate a J2CL build test.
    """
    jsinterop_generator_name = "%s__jsinterop_generator" % name
    generator_output = ":%s__internal_src_generated.srcjar" % jsinterop_generator_name
    jsinterop_generator(
        name = jsinterop_generator_name,
        srcs = srcs,
        extension_type_prefix = extension_type_prefix,
        global_scope_class_name = global_scope_class_name,
        name_mapping_files = name_mapping_files,
        deps = deps,
        externs_deps = j2cl_test_externs_list,
        integer_entities_files = integer_entities_files,
        wildcard_types_files = wildcard_types_files,
        generate_j2cl_build_test = generate_j2cl_build_test,
    )

    zip_tool = "@bazel_tools//tools/zip:zipper"
    java_format_tool = "//third_party:google_java_format-testonly"
    sh_test(
        name = name,
        size = "small",
        srcs = [
            "//javatests/jsinterop/generator:jsinterop_generator_test.sh",
        ],
        data = [
            zip_tool,
            java_format_tool,
            generator_output,
        ] + expected_output,
        args = [
            "$(rootpath %s)" % generator_output,
            native.package_name(),
            "$(rootpath %s)" % zip_tool,
            "$(rootpath %s)" % java_format_tool,
        ],
    )
