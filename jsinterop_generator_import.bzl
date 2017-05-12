"""jsinterop_generator_import macro.

Takes non standard input and repackages it with names that will allow the
jsinterop_generator_import() target to be directly depended upon from jsinterop_generator()
targets.
"""

load("//third_party:j2cl_library.bzl", "j2cl_library")

_is_bazel = not hasattr(native, "genmpm")

def jsinterop_generator_import(
    name,
    srcs,
    externs_srcs=[],
    types_mapping_files=[],
    gwt_inherit_files=[],
    gwt_xml = None,
    visibility=None):

  java_library_args = {
      "name" : name,
      "srcs" : srcs,
      "deps" : [
          "//third_party:gwt-jsinterop-annotations",
          "//third_party:jsinterop-base",
      ],
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

  native.filegroup(
      name = "%s__dep_type_mappings_internal" % name,
      srcs = types_mapping_files,
  )

  native.filegroup(
      name = "%s__deps_gwt_inherit_internal" % name,
      srcs = gwt_inherit_files,
  )

  # intentionaly empty.
  native.filegroup(
      name = "%s__deps_name_mapping_file_internal" % name,
      srcs = [],
  )

