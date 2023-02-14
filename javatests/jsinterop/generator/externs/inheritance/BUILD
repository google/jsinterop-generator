# Description:
#   Tests conversion of type inehritance
#

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

jsinterop_generator_test(
    name = "Inheritance",
    srcs = ["inheritance.js"],
    expected_output = [
        "SimpleClass.java.txt",
        "ParentClass.java.txt",
        "Parent1Interface.java.txt",
        "Parent2Interface.java.txt",
        "GreatParentClass.java.txt",
        "GreatParentInterface.java.txt",
        "InterfaceWithStructuralType.java.txt",
        "InterfaceWithStructuralTypeImpl.java.txt",
    ],
)
