# Description:
#  Test behavior of the jsinterop generator with type that are not defined
#

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
)

java_test(
    name = "UndefinedTypesTest",
    srcs = ["UndefinedTypesTest.java"],
    deps = [
        "//java/jsinterop/generator/helper",
        "//javatests/jsinterop/generator/closure:helpers",
        "//third_party:guava",
        "//third_party:jscomp",
        "//third_party:junit",
    ],
)
