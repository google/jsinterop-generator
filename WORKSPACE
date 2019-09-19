workspace(name = "com_google_jsinterop_generator")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# Load J2CL separately

http_archive(
    name = "com_google_j2cl",
    sha256 = "83c5ed81aa5036535f84d85ab2fd1faa6ef724f5f272ac489495568afac1c562",
    strip_prefix = "j2cl-20190918",
    url = "https://github.com/google/j2cl/archive/v20190918.zip",
)

load("@com_google_j2cl//build_defs:repository.bzl", "load_j2cl_repo_deps")

load_j2cl_repo_deps()

load("@com_google_j2cl//build_defs:rules.bzl", "setup_j2cl_workspace")

setup_j2cl_workspace()

# Load the other dependencies
load("//build_defs:repository.bzl", "load_jsinterop_generator_repo_deps")

load_jsinterop_generator_repo_deps()

load("//build_defs:rules.bzl", "setup_jsinterop_generator_workspace")

setup_jsinterop_generator_workspace()
