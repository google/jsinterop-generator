# Description:
#   Tests conversion of a simple type
#

package(default_visibility = ["//:__subpackages__"])

# Apache2
licenses(["notice"])

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)
load(
    "//:jsinterop_generator.bzl",
    "jsinterop_generator",
)

# In our test, we use a reference to native js Object type.
# In order to avoid generating type for Object, we put its definitions in a separate generated lib
# and use it as a dependency or our test.
jsinterop_generator(
    name = "native_types",
    srcs = ["native_types.js"],
    generate_j2cl_build_test = False,
)

jsinterop_generator_test(
    name = "SimpleClass",
    srcs = ["simpleclass.js"],
    expected_output = [
        "PrivateClass.java",
        "SimpleClass.java",
        "SimpleClass__Constants.java",
        "SimpleInterface.java",
        "SimpleInterface__Constants.java",
        "SimpleStructuralInterface.java",
        "DeprecatedInterface.java",
    ],
    deps = [":native_types"],
)
