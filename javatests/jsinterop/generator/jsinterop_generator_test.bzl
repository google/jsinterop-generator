"""jsinterop_generator_test build macro.

Defines an integration test for the JsInteropGenerator


Example usage:

jsinterop_generator_test(
        name = "SimpleTest",
        srcs = ["test.d.ts"],
        expected_output = "test_output.txt",
)

"""

load(
    "//:jsinterop_generator.bzl",
    "jsinterop_generator",
)

def jsinterop_generator_test(
    name,
    srcs,
    expected_output,
    extension_type_prefix = None,
    name_mapping_files = [],
    deps = [],
    conversion_mode = "closure",
    j2cl_test_externs_list = [],
    integer_entities_files = [],
    wildcard_types_files = [],
    generate_j2cl_build_test = None,
    ):

  jsinterop_generator_name = "%s__jsinterop_generator" % name
  generator_output = ":%s__internal_src_generated.srcjar" % jsinterop_generator_name

  if not j2cl_test_externs_list:
    j2cl_test_externs_list = ["//third_party:common_externs"]

  jsinterop_generator(
      name = jsinterop_generator_name,
      srcs = srcs,
      extension_type_prefix = extension_type_prefix,
      name_mapping_files = name_mapping_files,
      deps = deps,
      conversion_mode = conversion_mode,
      j2cl_test_externs_list = j2cl_test_externs_list,
      integer_entities_files = integer_entities_files,
      wildcard_types_files = wildcard_types_files,
      generate_j2cl_build_test = generate_j2cl_build_test,
  )

  native.sh_test(
      name = name,
      size = "small",
      srcs = [
          "//javatests/jsinterop/generator:jsinterop_generator_test.sh"
      ],
      data = [
          "//third_party:jar",
          "//third_party:google_java_format",
          generator_output
          ] + expected_output,
      args = [
          "$(location %s)" % generator_output,
          PACKAGE_NAME,
          "$(location //third_party:jar)",
          "$(location //third_party:google_java_format)",
     ],
  )
