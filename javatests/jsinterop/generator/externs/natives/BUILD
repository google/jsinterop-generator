# Description:
#   Contains helper lib used by tests
#

package(
    default_visibility = [
        "//javatests/jsinterop/generator/externs:__subpackages__",
    ],
    # Apache2
    licenses = ["notice"],
)

load(
    "//:jsinterop_generator_import.bzl",
    "jsinterop_generator_import",
)

# In our test, we use a reference to native js types like Object or Array.
# In order to avoid generating java types for those types, we put its definitions in a separate lib
# and use it as a dependency or our test.
# We import handwritten java types so we don;t use the jsinterop generator for building dependencies
# of our tests.
jsinterop_generator_import(
    name = "natives",
    srcs = [
        "JsArray.java",
        "JsIterable.java",
        "JsObject.java",
    ],
    externs_srcs = ["native.js"],
    types_mapping_files = ["natives.types"],
)
