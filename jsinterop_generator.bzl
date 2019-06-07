"""jsinterop_generator build rule.

Takes closure extern files and generates java files annotated with JsInterop annotations for the types
defined in the extern files.

By default, this rule produces a java_library with the same name usable with gwt. This behavior
can be disabled by setting the parameter generate_gwt_library to False.

By default, this rule produces a j2cl_library with the same name suffixed by '-j2cl'. This
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

load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_library")
load("//third_party:js_library.bzl", "js_library")

_is_bazel = not hasattr(native, "genmpm")

JS_INTEROP_RULE_NAME_PATTERN = "%s__internal_src_generated"

JsInteropGeneratorInfo = provider()

def _get_generator_files(deps):
    transitive_srcs = [dep[JsInteropGeneratorInfo].transitive_sources for dep in deps]
    transitive_types_mappings = [dep[JsInteropGeneratorInfo].transitive_types_mappings for dep in deps]
    gwt_module_names = [name for dep in deps for name in dep[JsInteropGeneratorInfo].gwt_module_names]

    return struct(
        sources = depset(transitive = transitive_srcs),
        types_mappings = depset(transitive = transitive_types_mappings),
        gwt_module_names = gwt_module_names,
    )

def _jsinterop_generator_export_impl(ctx):
    """ Implementation of the _jsinterop_generator_export skylark rule.

    This rule is used to export existing jsinterop_generator targets.
    It collects the infos of the JsInteropGeneratorInfo provider of each target
    of the exports attribute and reexpose everything in one provider.
    """
    jsinterop_files = _get_generator_files(ctx.attr.exports)

    # reexpose files and properties collected from all exported targets.
    return [
        JsInteropGeneratorInfo(
            transitive_sources = jsinterop_files.sources,
            transitive_types_mappings = jsinterop_files.types_mappings,
            gwt_module_names = jsinterop_files.gwt_module_names,
        ),
    ]

_jsinterop_generator_export = rule(
    attrs = {
        "exports": attr.label_list(allow_files = True),
    },
    implementation = _jsinterop_generator_export_impl,
)


def _closure_impl(srcs, deps_files, types_mapping_file, ctx):
    deps_srcs = deps_files.sources.to_list()
    dep_types_mapping_files = deps_files.types_mappings.to_list()
    names_mapping_files = ctx.files.name_mapping_files

    arguments = [
        "--output=%s" % ctx.outputs._generated_jar.path,
        "--output_dependency_file=%s" % types_mapping_file.path,
        "--package_prefix=%s" % ctx.attr.package_prefix,
        "--extension_type_prefix=%s" % ctx.attr.extension_type_prefix,
        "--global_scope_class_name=%s" % ctx.attr.global_scope_class_name,
    ]
    arguments += ["--dependency=%s" % f.path for f in deps_srcs]
    arguments += ["--dependency_mapping_file=%s" % f.path for f in dep_types_mapping_files]
    arguments += ["--name_mapping_file=%s" % f.path for f in names_mapping_files]
    arguments += ["--integer_entities_file=%s" % f.path for f in ctx.files.integer_entities_files]
    arguments += ["--wildcard_types_file=%s" % f.path for f in ctx.files.wildcard_types_files]

    if ctx.attr.debug:
        arguments += ["--debug_mode"]

    if ctx.attr.use_bean_convention:
        arguments += ["--bean_convention"]


    arguments += ["%s" % f.path for f in srcs]

    inputs = srcs + deps_srcs + dep_types_mapping_files + names_mapping_files
    inputs += ctx.files.integer_entities_files + ctx.files.wildcard_types_files

    ctx.actions.run(
        inputs = inputs,
        outputs = [ctx.outputs._generated_jar, types_mapping_file],
        executable = ctx.executable._closure_generator,
        progress_message = "Generating JsInterop classes from externs",
        arguments = arguments,
    )

def _jsinterop_generator_impl(ctx):
    srcs = ctx.files.srcs
    deps_files = _get_generator_files(ctx.attr.deps)
    types_mapping_file = ctx.actions.declare_file("%s_types" % ctx.attr.name)

    if ctx.attr.conversion_mode == "closure":
        _closure_impl(srcs, deps_files, types_mapping_file, ctx)

    # generate the gwt.xml file by concatenating dependencies gwt inherits files
    gwt_xml_file = ctx.outputs._gwt_xml_file
    dep_gwt_module_names = deps_files.gwt_module_names

    gwt_xml_content = [
        "<module>",
        "<source path=\"\"/>",
        "<inherits name=\"jsinterop.base.Base\" />",
    ]
    gwt_xml_content += ["<inherits name=\"%s\" />" % dep_module for dep_module in dep_gwt_module_names]
    gwt_xml_content += ["</module>"]

    ctx.actions.write(
        output = gwt_xml_file,
        content = "\n".join(gwt_xml_content),
    )

    # generate the gwt module name for dependency purpose
    if not ctx.attr.package_prefix:
        gwt_module_name = "%s" % ctx.attr.gwt_module_name
    else:
        gwt_module_name = "%s.%s" % (ctx.attr.package_prefix, ctx.attr.gwt_module_name)

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

    ctx.actions.run(
        inputs = inputs,
        outputs = [ctx.outputs._formatted_jar],
        executable = ctx.executable._format_jar_script,
        progress_message = "Formatting java classes",
        arguments = arguments,
    )

    return [
        JsInteropGeneratorInfo(
            transitive_sources = depset(srcs, transitive = [deps_files.sources]),
            transitive_types_mappings = depset([types_mapping_file], transitive = [deps_files.types_mappings]),
            gwt_module_names = [gwt_module_name],
        ),
    ]

_jsinterop_generator = rule(
    attrs = {
        "srcs": attr.label_list(
            mandatory = True,
            allow_files = [
                ".d.ts",
                ".js",
            ],
        ),
        "deps": attr.label_list(allow_files = True),
        "package_prefix": attr.string(),
        "extension_type_prefix": attr.string(),
        "global_scope_class_name": attr.string(),
        "name_mapping_files": attr.label_list(allow_files = True),
        "integer_entities_files": attr.label_list(allow_files = True),
        "wildcard_types_files": attr.label_list(allow_files = True),
        "use_bean_convention": attr.bool(),
        "debug": attr.bool(),
        "conversion_mode": attr.string(),
        "gwt_module_name": attr.string(),

        "_jar": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//third_party:jar"),
        ),
        "_jdk": attr.label(
            cfg = "host",
            default = Label("//third_party:jdk"),
        ),
        "_google_java_formatter": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//third_party:google_java_format"),
        ),
        "_closure_generator": attr.label(
            cfg = "host",
            executable = True,
            default = Label("//java/jsinterop/generator/closure:ClosureJsinteropGenerator"),
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
        "_gwt_xml_file": "%{gwt_module_name}.gwt.xml",
    },
    implementation = _jsinterop_generator_impl,
)

# Macro invoking the skylark rule
def jsinterop_generator(
        name,
        srcs = [],
        exports = [],
        deps = [],
        extension_type_prefix = None,
        global_scope_class_name = None,
        name_mapping_files = [],
        integer_entities_files = [],
        wildcard_types_files = [],
        use_bean_convention = True,
        package_prefix = None,
        generate_j2cl_library = True,
        generate_gwt_library = True,
        conversion_mode = "closure",
        generate_j2cl_build_test = None,
        j2cl_js_deps = None,
        visibility = None,
        testonly = None):
    if not srcs and not exports:
        fail("Empty rule. Nothing to generate or import.")

    if not srcs and deps:
        fail("deps cannot be used without srcs.")

    if not generate_j2cl_library and not generate_gwt_library:
        fail("either generate_j2cl_library or generate_gwt_library should be set to True")

    exports_java = [_absolute_label(export) for export in exports]
    exports_j2cl = ["%s-j2cl" % export for export in exports_java]
    exports_js_interop_generator = [JS_INTEROP_RULE_NAME_PATTERN % export for export in exports_java]

    deps_java = [_absolute_label(dep) for dep in deps]
    deps_j2cl = ["%s-j2cl" % dep for dep in deps_java]
    deps_js_interop_generator = [JS_INTEROP_RULE_NAME_PATTERN % dep for dep in deps_java]

    jsinterop_generator_rule_name = JS_INTEROP_RULE_NAME_PATTERN % name

    if srcs:
        generator_srcs = srcs[:]

        if not package_prefix:
            package_prefix = _get_java_package(native.package_name())

        if conversion_mode == "closure":
            if j2cl_js_deps == None:
                externs_lib_name = "%s-externs" % name
                js_library(
                    name = externs_lib_name,
                    srcs = srcs,
                )
                deps_j2cl += [":%s" % externs_lib_name]


        else:
            fail("Unknown conversion mode")

        if not extension_type_prefix:
            extension_type_prefix = name[0].upper() + name[1:]

        if not global_scope_class_name:
            global_scope_class_name = "%sGlobal" % extension_type_prefix

        gwt_module_name = extension_type_prefix

        _jsinterop_generator(
            name = jsinterop_generator_rule_name,
            srcs = generator_srcs,
            deps = deps_js_interop_generator,
            package_prefix = package_prefix,
            extension_type_prefix = extension_type_prefix,
            global_scope_class_name = global_scope_class_name,
            name_mapping_files = name_mapping_files,
            integer_entities_files = integer_entities_files,
            wildcard_types_files = wildcard_types_files,
            use_bean_convention = use_bean_convention,
            # TODO(dramaix): replace it by a blaze flag
            debug = False,
            conversion_mode = conversion_mode,
            gwt_module_name = gwt_module_name,
            testonly = testonly,
            visibility = ["//visibility:public"],
        )

        generated_jars = [":%s" % jsinterop_generator_rule_name]
        gwt_xml_file = ":%s.gwt.xml" % gwt_module_name

        deps_java += [
            Label(
                "//third_party:gwt-jsinterop-annotations",
                relative_to_caller_repository = False,
            ),
            Label(
                "//third_party:jsinterop-base",
                relative_to_caller_repository = False,
            ),
        ]
        deps_j2cl += [
            Label(
                "//third_party:gwt-jsinterop-annotations-j2cl",
                relative_to_caller_repository = False,
            ),
            Label(
                "//third_party:jsinterop-base-j2cl",
                relative_to_caller_repository = False,
            ),
        ]

    else:
        # exporting existing generated libraries.
        _jsinterop_generator_export(
            name = jsinterop_generator_rule_name,
            exports = exports_js_interop_generator,
        )

        generated_jars = None
        gwt_xml_file = None

    if not deps_java:
        # Passing an empty array to java_library fails when we define an exports attribute
        deps_java = None
        deps_j2cl = None

    if generate_j2cl_library:
        j2cl_library(
            name = "%s-j2cl" % name,
            srcs = generated_jars,
            generate_build_test = generate_j2cl_build_test,
            deps = deps_j2cl,
            exports = exports_j2cl,
            testonly = testonly,
            visibility = visibility,
        )

    if generate_gwt_library:
        java_library_args = {
            "name": name,
            "srcs": generated_jars,
            "deps": deps_java,
            "exports": exports_java,
            "visibility": visibility,
            "testonly": testonly,
        }

        # bazel doesn't support constraint and gwtxml attributes
        if _is_bazel:
            if gwt_xml_file:
                java_library_args["resources"] = [gwt_xml_file]
        else:
            java_library_args["gwtxml"] = gwt_xml_file
            java_library_args["constraints"] = ["gwt", "public"]

        native.java_library(**java_library_args)

def _absolute_label(label):
    """Expand a label to be of the full form //package:foo.

    Args:
      label: string in relative or absolute form.

    Returns:
      Absolute form of the label as a string.
    """
    if label.startswith("//"):
        label = label[2:]  # drop the leading //
        colon_split = label.split(":")
        if len(colon_split) == 1:  # no ":" in label
            pkg = label
            _, _, target = label.rpartition("/")
        else:
            pkg, target = colon_split  # fails if len(colon_split) != 2
    else:
        colon_split = label.split(":")
        if len(colon_split) == 1:  # no ":" in label
            pkg, target = native.package_name(), label
        else:
            pkg2, target = colon_split  # fails if len(colon_split) != 2
            pkg = native.package_name() + ("/" + pkg2 if pkg2 else "")

    return "//%s:%s" % (pkg, target)

def _get_java_package(path):
    """Extract the java package from path"""

    segments = path.split("/")

    # Find different root start indecies based on potential java roots
    java_root_start_indecies = [_find(segments, root) for root in ["java", "javatests"]]

    # Choose the root that starts earliest
    start_index = min(java_root_start_indecies)

    if start_index == len(segments):
        fail("Cannot find java root: " + path)

    return ".".join(segments[start_index + 1:])

def _find(segments, s):
    return segments.index(s) if s in segments else len(segments)
