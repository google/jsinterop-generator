# Description:
#   Tests dictionary types conversion
#

package(
    default_visibility = ["//:__subpackages__"],
    # Apache2
    licenses = ["notice"],
)

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

jsinterop_generator_test(
    name = "dictionarytype",
    srcs = ["dictionarytype.js"],
    expected_output = [
        "DictionaryType.java",
        "SimpleDictionaryType.java",
    ],
)
