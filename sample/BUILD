load("@jsinterop_generator//:jsinterop_generator.bzl", "jsinterop_generator")

package(
    default_applicable_licenses = ["@jsinterop_generator//:license"],
    licenses = ["notice"],
)

jsinterop_generator(
    name = "sample",
    srcs = ["simpleclass.js"],
    package_prefix = "com.google.jsinterop.generator.sample",
)
