load("//:jsinterop_generator.bzl", "jsinterop_generator")

package(
    default_applicable_licenses = ["//:license"],
    licenses = ["notice"],
)

jsinterop_generator(
    name = "sample",
    srcs = ["simpleclass.js"],
    package_prefix = "com.google.jsinterop.generator.sample",
)
