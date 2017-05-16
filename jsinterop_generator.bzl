"""jsinterop_generator build rule.

Takes closure extern files and generates java file annotated with JsInterop annotation for the types
defined in the extern files.

This rule produces by default a java_library with the same name usable with gwt.  This default
behavior can be disabled by setting the parameter generate_gwt_library to False.

This rule produces by default a j2cl_library with the same name suffixed by '-j2cl'. This default
behavior can be disabled by setting the parameter generate_j2cl_library to False.


Examples:

    jsinterop_generator(
      name = "foo",
      srcs = ["foo_extern.js"],
    )

    Generates:
    java_library(
       name = "foo"
       # contains generated sources and related gwt module
    }

    j2cl_library(
       name = "foo-j2cl",
       # contains generated sources
    )
"""
load("//third_party:j2cl_library.bzl", "j2cl_library")
load("//third_party:utils.bzl", "absolute_label", "get_java_package")


_is_bazel = not hasattr(native, "genmpm")


def _closure_impl(srcs, deps_srcs, types_mapping_files, ctx):
  arguments = [
      "--output=%s" % ctx.outputs._generated_jar.path,
      "--output_dependency_file=%s" % ctx.outputs._dependency_file.path,
      "--package_prefix=%s" % ctx.attr.package_prefix,
      "--copyright=%s" % ctx.attr.copyright_attr,
      "--extension_type_prefix=%s" % ctx.attr.extension_type_prefix,
      ]
  arguments += ["--dependency=%s" % f.path for f in deps_srcs]

  arguments += ["--dependency_mapping_file=%s" % f.path for f in types_mapping_files]

  arguments += ["--name_mapping_file=%s" % f.path for f in  ctx.files.name_mapping_files]

  if ctx.attr.debug:
    arguments += ["--debug_mode"]

  if ctx.attr.use_bean_convention:
    arguments += ["--bean_convention"]

  arguments += ["%s" % f.path for f in srcs]

  ctx.action(
      inputs = srcs + deps_srcs + types_mapping_files + ctx.files.name_mapping_files,
      outputs=[ctx.outputs._generated_jar, ctx.outputs._dependency_file],
      executable = ctx.executable._closure_generator,
      progress_message = "Generating jsinterop classes from extern closure files",
      arguments = arguments,
  )

def _impl(ctx):
  srcs = ctx.files.srcs
  deps_srcs = ctx.files.deps_srcs
  types_mapping_files = ctx.files.types_mapping_files

  if ctx.attr.conversion_mode == "closure":
    _closure_impl(srcs, deps_srcs, types_mapping_files, ctx)
  else:
    fail("Unknown conversion mode")

  # generate the gwt.xml file by concatenating dependencies gwt inherits files
  gwt_xml_file = ctx.outputs._gwt_xml_file
  inherits_files = ctx.files.gwt_inherits_files

  action_commands = ["printf '%%s\n' '<module>' '<source path=\"\"/>' '<inherits name=\"jsinterop.base.Base\" />' > %s" % gwt_xml_file.path]
  if inherits_files:
    action_commands += ["cat %s >> %s" % (" ".join([f.path for f in inherits_files]), gwt_xml_file.path)]
  action_commands += ["echo '</module>' >> %s" % gwt_xml_file.path]

  ctx.action(
      inputs = inherits_files,
      outputs = [gwt_xml_file],
      command= ";".join(action_commands),
  )

  # generate the gwt inherits file for dependency purpose
  if not ctx.attr.package_prefix:
    inherits_content = '<inherits name="%s" />' % ctx.attr.gwt_module_name
  else:
    inherits_content = '<inherits name="%s.%s" />' % (ctx.attr.package_prefix, ctx.attr.gwt_module_name)
  ctx.file_action(output=ctx.outputs._gwt_inherits_file, content=inherits_content)

  # format output
  arguments = [
      ctx.outputs._generated_jar.path,
      ctx.outputs._formatted_jar.path,
      ctx.executable._google_java_formatter.path,
      ctx.executable._jar.path,
  ]
  inputs = [
      ctx.outputs._generated_jar,
      ctx.executable._google_java_formatter,
      ctx.executable._jar,
  ] + ctx.files._jdk
  ctx.action(
      inputs = inputs,
      outputs = [ctx.outputs._formatted_jar],
      executable = ctx.executable._format_jar_script,
      progress_message = "Formatting java classes",
      arguments = arguments,
  )

  return struct(
      files=set([
          ctx.outputs._formatted_jar,
          ctx.outputs._generated_jar,
          ctx.outputs._dependency_file,
          ctx.outputs._gwt_xml_file,
          ctx.outputs._gwt_inherits_file,
          ]),
  )

_jsinterop_generator = rule(
    attrs = {
        "srcs": attr.label_list(
            mandatory = True,
            allow_files = [
                ".d.ts",
                ".js",
            ],
        ),
        "deps_srcs": attr.label_list(allow_files = True),
        "types_mapping_files": attr.label_list(allow_files = True),
        "copyright_attr": attr.string(),
        "output_directory": attr.string(),
        "package_prefix": attr.string(),
        "extension_type_prefix": attr.string(),
        "name_mapping_files": attr.label_list(allow_files = True),
        "use_bean_convention": attr.bool(),
        "debug": attr.bool(),
        "package_name": attr.string(),
        "conversion_mode": attr.string(),
        "gwt_inherits_files": attr.label_list(allow_files = True),
        "gwt_module_name": attr.string(),


        "_jar": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//third_party:jar")
        ),
        "_jdk": attr.label(
            cfg = "host",
            default = Label("//third_party:jdk")
        ),
        "_google_java_formatter": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//third_party:google_java_format")
        ),
        "_closure_generator": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//java/jsinterop/generator/closure:ClosureJsinteropGenerator"),
        ),
        "_srcjar_script": attr.label(
            cfg = "host",
            executable = True,
            default = Label(
                "//internal_do_not_use:create_generated_srcjar",
            ),
        ),
        "_format_jar_script": attr.label(
            cfg = "host",
            executable = True,
            allow_files = True,
            default = Label(
                "//internal_do_not_use:format_srcjar",
            ),
        ),
    },
    outputs = {
        "_formatted_jar": "%{name}.srcjar",
        "_generated_jar": "%{name}_non_formatted.jar",
        "_dependency_file": "%{name}.types",
        "_gwt_xml_file": "%{gwt_module_name}.gwt.xml",
        "_gwt_inherits_file" : "%{name}.gwt.inherits",
    },
    implementation = _impl,
)

# Macro invoking the skylark rule
def jsinterop_generator(
    name,
    srcs = [],
    exports = [],
    deps = [],
    copyright_attr = "",
    extension_type_prefix = None,
    name_mapping_files = [],
    use_bean_convention = True,
    package_prefix = None,
    generate_j2cl_library = True,
    generate_gwt_library = True,
    conversion_mode = "closure",
    generate_j2cl_build_test = None,
    visibility = None,
    testonly = None,
    j2cl_test_externs_list = [],
    ):

  if not srcs and not exports:
    fail("Empty rule. Nothing to generate or import.")

  if not srcs and deps:
    fail("deps cannot be used without srcs.")

  if not generate_j2cl_library and not generate_gwt_library:
    fail("either generate_j2cl_library or generate_gwt_library should be set to True")

  if not package_prefix:
    package_prefix = get_java_package(PACKAGE_NAME)

  if conversion_mode == "closure":
    externs_list = srcs
  else:
    externs_list = []

  if not extension_type_prefix:
    extension_type_prefix = name[0].upper() + name[1:]
  gwt_module_name = extension_type_prefix

  exports_java = [absolute_label(export) for export in exports]
  exports_j2cl = ["%s-j2cl" % export for export in exports_java]
  exports_srcs = ["%s__deps_srcs_internal" % export for export in exports_java]
  exports_type_mapping_files = ["%s__dep_type_mappings_internal" % export for export in exports_java]
  exports_name_mapping_files = ["%s__deps_name_mapping_file_internal" % export for export in exports_java]
  exports_gwt_inherits_files = ["%s__deps_gwt_inherit_internal" % export for export in exports_java]

  deps_java = [absolute_label(dep) for dep in deps]
  # deps_j2cl is computed later.
  deps_srcs = ["%s__deps_srcs_internal" % dep for dep in deps_java]
  deps_types_mapping_files = ["%s__dep_type_mappings_internal" % dep for dep in deps_java]
  deps_name_mapping_files= ["%s__deps_name_mapping_file_internal" % dep for dep in deps_java]
  deps_gwt_inherits_files = ["%s__deps_gwt_inherit_internal" % dep for dep in deps_java]

  types_mapping_files = []
  generated_jars = []
  gwt_xml_file = None
  gwt_inherits_files = []

  if srcs:
    generator_srcs = srcs


    j2cl_test_externs_list += deps_srcs
    jsinterop_generator_rule_name = "%s__internal_src_generated" % name

    _jsinterop_generator(
        name = jsinterop_generator_rule_name,
        srcs = generator_srcs,
        deps_srcs = deps_srcs,
        types_mapping_files = deps_types_mapping_files,
        copyright_attr = copyright_attr,
        output_directory = ".",
        package_prefix = package_prefix,
        extension_type_prefix = extension_type_prefix,
        name_mapping_files = deps_name_mapping_files + name_mapping_files,
        use_bean_convention = use_bean_convention,
        # TODO replace it by a blaze flag
        debug = False,
        package_name = PACKAGE_NAME,
        conversion_mode = conversion_mode,
        gwt_inherits_files = deps_gwt_inherits_files,
        gwt_module_name = gwt_module_name,
        testonly = testonly,
        visibility = ["//visibility:private"],
    )

    types_mapping_files = [":%s__internal_src_generated.types" % name]
    generated_jars = [":%s" % jsinterop_generator_rule_name]
    gwt_xml_file = ":%s.gwt.xml" % gwt_module_name
    gwt_inherits_files = [":%s.gwt.inherits" % jsinterop_generator_rule_name]

    deps_java += [
        "//third_party:gwt-jsinterop-annotations",
        "//third_party:jsinterop-base",
    ]

  # Because the _jsinterop_generator rule is private, we cannot refer the outputs of the rule
  # We create a filegroup to expose the mapping file and the mapping file of its dependencies
  # to other jsinterop_generator rules
  native.filegroup(
      name = "%s__dep_type_mappings_internal" % name,
      srcs = exports_type_mapping_files + deps_types_mapping_files + types_mapping_files
  )

  # create filegroup with provided extern/d.ts and deps files so that we can depend on later.
  native.filegroup(
      name = "%s__deps_srcs_internal" % name,
      srcs = exports_srcs + deps_srcs + srcs,
  )

  # expose the name mapping file so that we can depend on later
  native.filegroup(
      name = "%s__deps_name_mapping_file_internal" % name,
      srcs = exports_name_mapping_files + name_mapping_files,
  )

  # expose the file containing the gwt module to inherit
  native.filegroup(
      name = "%s__deps_gwt_inherit_internal" % name,
      srcs = exports_gwt_inherits_files + gwt_inherits_files,
  )

  if not deps_java:
    # Passing an empty array to java_library fails when we define an exports attribute
    deps_java = None
    deps_j2cl = None
  else:
    deps_j2cl = ["%s-j2cl" % dep for dep in deps_java]

  if generate_j2cl_library:
    j2cl_library(
        name = "%s-j2cl" % name,
        srcs = generated_jars,
        generate_build_test = generate_j2cl_build_test,
        deps = deps_j2cl,
        exports = exports_j2cl,
        testonly = testonly,
        visibility = visibility,
        _test_externs_list = externs_list + j2cl_test_externs_list,
    )

  if generate_gwt_library:
    java_library_args = {
        "name" : name,
        "srcs" : generated_jars,
        "deps" : deps_java,
        "exports" : exports_java,
        "visibility" : visibility,
        "testonly" : testonly,
    }

    # bazel doesn't support constraint and gwtxml attributes
    if _is_bazel:
      java_library_args["resources"] = [gwt_xml_file]
    else:
      java_library_args["gwtxml"] = gwt_xml_file
      java_library_args["constraints"] = ["gwt","public"]

    native.java_library(**java_library_args)
