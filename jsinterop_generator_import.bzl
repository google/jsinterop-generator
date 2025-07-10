"""jsinterop_generator_import macro.

Takes non standard input and repackages it with names that will allow the
jsinterop_generator_import() target to be directly depended upon from jsinterop_generator()
targets.
"""

load("@rules_java//java:defs.bzl", "java_library")
load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_library")
load(":jsinterop_generator.bzl", "JS_INTEROP_RULE_NAME_PATTERN", "JsInteropGeneratorInfo")

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
        enable_jspecify_support = False,
        visibility = None):
    _jsinterop_generator_import(
        name = JS_INTEROP_RULE_NAME_PATTERN % name,
        externs_srcs = externs_srcs,
        types_mapping_files = types_mapping_files,
        gwt_module_name = gwt_module_name,
    )

    java_library(
        name = name,
        srcs = srcs,
        deps = [
            Label("@com_google_j2cl//:jsinterop-annotations"),
            Label("@com_google_jsinterop_base//:jsinterop-base"),
            Label("//third_party:jspecify_annotations"),
        ],
        resources = [gwt_xml] if gwt_xml else [],
        visibility = visibility,
    )

    j2cl_library(
        name = "%s-j2cl" % name,
        srcs = srcs,
        visibility = visibility,
        deps = [
            Label("@com_google_j2cl//:jsinterop-annotations-j2cl"),
            Label("@com_google_jsinterop_base//:jsinterop-base-j2cl"),
            Label("//third_party:jspecify_annotations-j2cl"),
        ],
        experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = enable_jspecify_support,
    )
