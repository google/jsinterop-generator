# Description:
#   Tests dictionary types conversion
#

load(
    "//javatests/jsinterop/generator:jsinterop_generator_test.bzl",
    "jsinterop_generator_test",
)

package(licenses = ["notice"])

jsinterop_generator_test(
    name = "dictionarytype",
    srcs = ["dictionarytype.js"],
    expected_output = [
        "DictionaryType.java.txt",
        "SimpleDictionaryType.java.txt",
    ],
)
