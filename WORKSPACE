workspace(name = "com_google_jsinterop_generator")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# Load j2cl repository for j2cl_library rule
http_archive(
    name = "com_google_j2cl",
    strip_prefix = "j2cl-master",
    url = "https://github.com/google/j2cl/archive/master.zip",
)

load("@com_google_j2cl//build_defs:repository.bzl", "load_j2cl_repo_deps")

load_j2cl_repo_deps()

load("@com_google_j2cl//build_defs:rules.bzl", "setup_j2cl_workspace")

setup_j2cl_workspace()

maven_server(
    name = "sonatype_snapshot",
    url = "https://oss.sonatype.org/content/repositories/snapshots",
)



http_archive(
    name = "com_google_jsinterop_base",
    url = "https://github.com/google/jsinterop-base/archive/master.zip",
    strip_prefix="jsinterop-base-master",
)

http_jar(
    name = "com_google_google_java_format",
    url = "https://github.com/google/google-java-format/releases/download/google-java-format-1.6/google-java-format-1.6-all-deps.jar",
)

# third_party libs used by jsinterop-base
# TODO(dramaix): add a macro for loading JsInterop-base repo
maven_jar(
    name = "org_gwtproject_gwt_dev",
    artifact = "com.google.gwt:gwt-dev:2.8.1",
)
