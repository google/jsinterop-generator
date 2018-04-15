"""jsinterop_generator_import macro.

Takes non standard input and repackages it with names that will allow the
jsinterop_generator_import() target to be directly depended upon from jsinterop_generator()
targets.
"""

load("//third_party:j2cl_library.bzl", "j2cl_library")
load(":jsinterop_generator.bzl", "JS_INTEROP_RULE_NAME_PATTERN", "JsInteropGeneratorInfo")

_is_bazel = not hasattr(native, "genmpm")

def _jsinterop_generator_import_impl(ctx):
  # expose files and properties used when the target is used as dependency
  return [
      JsInteropGeneratorInfo(
          transitive_sources=depset(ctx.files.externs_srcs),
          transitive_types_mappings=depset(ctx.files.types_mapping_files),
          transitive_names_mappings=depset(),
          gwt_module_names=[ctx.attr.gwt_module_name],
      )
  ]


_jsinterop_generator_import = rule(
    attrs = {
        "externs_srcs": attr.label_list(allow_files = True),
        "types_mapping_files": attr.label_list(allow_files = True),
        "gwt_module_name": attr.string(),
    },
    implementation = _jsinterop_generator_import_impl,
)

def jsinterop_generator_import(
    name,
    srcs,
    externs_srcs=[],
    types_mapping_files=[],
    gwt_module_name=None,
    gwt_xml = None,
    visibility=None,
    gwt_library_tags = [],
#    j2cl_library_tags = [],
    ):

  _jsinterop_generator_import(
      name = JS_INTEROP_RULE_NAME_PATTERN % name,
      externs_srcs=externs_srcs,
      types_mapping_files=types_mapping_files,
      gwt_module_name=gwt_module_name,
  )

  java_library_args = {
      "name" : name,
      "srcs" : srcs,
      "deps" : [
          "//third_party:gwt-jsinterop-annotations",
          "//third_party:jsinterop-base",
      ],
      "tags" : gwt_library_tags,
      "visibility" : visibility,
  }

  # bazel doesn't support constraint and gwtxml attributes
  if _is_bazel:
    java_library_args["resources"] = [gwt_xml]
  else:
    java_library_args["gwtxml"] = gwt_xml
    java_library_args["constraints"] = ["gwt","public"]

  native.java_library(**java_library_args)

  j2cl_library(
      name = "%s-j2cl" % name,
      srcs = srcs,
      visibility = visibility,
      deps = [
          "//third_party/java/gwt:gwt-jsinterop-annotations-j2cl",
          "//third_party/java/jsinterop:jsinterop-base-j2cl",
      ],
  )

  native.filegroup(
      name = "%s__deps_srcs_internal" % name,
      srcs = externs_srcs,
  )
