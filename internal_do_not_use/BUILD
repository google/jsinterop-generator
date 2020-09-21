# Description:
#   Build rules in this directory are used by our tooling and build rules

package(
    default_visibility = ["//visibility:public"],
    licenses = ["notice"],
)

##########################################################
#                                                        #
#  __          __     _____  _   _ _____ _   _  _____    #
#  \ \        / /\   |  __ \| \ | |_   _| \ | |/ ____|   #
#   \ \  /\  / /  \  | |__) |  \| | | | |  \| | |  __    #
#    \ \/  \/ / /\ \ |  _  /| . ` | | | | . ` | | |_ |   #
#     \  /\  / ____ \| | \ \| |\  |_| |_| |\  | |__| |   #
#      \/  \/_/    \_\_|  \_\_| \_|_____|_| \_|\_____|   #
#                                                        #
#                                                        #
##########################################################
# Never depend on any of the targets in this BUILD file  #
# manually. They are used within tools/build rules and   #
# and should actually be private, but Bazel does not     #
# support this yet, b/34359566.                          #
##########################################################

# An implicit dependency of all "jsinterop_generator" skylark rules.
sh_binary(
    name = "format_srcjar",
    srcs = ["format_srcjar.sh"],
)
