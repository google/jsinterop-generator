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
        "Array.java",
    ],
)
