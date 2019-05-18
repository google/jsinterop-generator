"""jsinterop_generator_import macro.

Takes non standard input and repackages it with names that will allow the
jsinterop_generator_import() target to be directly depended upon from jsinterop_generator()
targets.
"""

load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_library")
load(":jsinterop_generator.bzl", "JS_INTEROP_RULE_NAME_PATTERN", "JsInteropGeneratorInfo")

_is_bazel = not hasattr(native, "genmpm")

def _jsinterop_generator_import_impl(ctx):
    # expose files and properties used when the target is used as dependency
    return [
        JsInteropGeneratorInfo(
            transitive_sources = depset(ctx.files.externs_srcs),
            transitive_types_mappings = depset(ctx.files.types_mapping_files),
            transitive_names_mappings = depset(),
            gwt_module_names = [ctx.attr.gwt_module_name],
        ),
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
        externs_srcs = [],
        types_mapping_files = [],
        gwt_module_name = None,
        gwt_xml = None,
        visibility = None):
    _jsinterop_generator_import(
        name = JS_INTEROP_RULE_NAME_PATTERN % name,
        externs_srcs = externs_srcs,
        types_mapping_files = types_mapping_files,
        gwt_module_name = gwt_module_name,
    )

    java_library_args = {
        "name": name,
        "srcs": srcs,
        "deps": [
            Label(
                "//third_party:gwt-jsinterop-annotations",
                relative_to_caller_repository = False,
            ),
            Label(
                "//third_party:jsinterop-base",
                relative_to_caller_repository = False,
            ),
        ],
        "visibility": visibility,
    }

    # bazel doesn't support constraint and gwtxml attributes
    if _is_bazel:
        java_library_args["resources"] = [gwt_xml]
    else:
        java_library_args["gwtxml"] = gwt_xml
        java_library_args["constraints"] = ["gwt", "public"]

    native.java_library(**java_library_args)

    j2cl_library(
        name = "%s-j2cl" % name,
        srcs = srcs,
        visibility = visibility,
        deps = [
            Label(
                "//third_party:gwt-jsinterop-annotations-j2cl",
                relative_to_caller_repository = False,
            ),
            Label(
                "//third_party:jsinterop-base-j2cl",
                relative_to_caller_repository = False,
            ),
        ],
    )
