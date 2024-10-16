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
    name = "inheritance",
    srcs = ["inheritance.js"],
    expected_output = glob(["*.java.txt"]),
)
