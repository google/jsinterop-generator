# Description:
#   Tests conversion of type inehritance
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

# In our test, we use a reference to native js Array type.
# In order to avoid generating type for Array, we put its definitions in a separate generated lib
# and use it as a dependency or our test.
jsinterop_generator(
    name = "native_types",
    srcs = ["native_types.js"],
    generate_j2cl_build_test = False,
)

jsinterop_generator_test(
    name = "Inheritance",
    srcs = ["inheritance.js"],
    expected_output = [
        "SimpleClass.java",
        "ParentClass.java",
        "Parent1Interface.java",
        "Parent2Interface.java",
        "GreatParentClass.java",
        "GreatParentInterface.java",
        "InterfaceWithStructuralType.java",
        "InterfaceWithStructuralTypeImpl.java",
    ],
    deps = [":native_types"],
)
