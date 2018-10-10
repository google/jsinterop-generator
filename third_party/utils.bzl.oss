"""Utility methods used by jsinterop_generator rule.
"""
def parse_label(label):
  """Parse a label into (package, name).

  Args:
    label: string in relative or absolute form.

  Returns:
    Pair of strings: package, relative_name

  Raises:
    ValueError for malformed label (does not do an exhaustive validation)
  """
  if is_absolute_label(label):
    label = label[2:]  # drop the leading //
    colon_split = label.split(":")
    if len(colon_split) == 1:  # no ":" in label
      pkg = label
      _, _, target = label.rpartition("/")
    else:
      pkg, target = colon_split  # fails if len(colon_split) != 2
  else:
    colon_split = label.split(":")
    if len(colon_split) == 1:  # no ":" in label
      pkg, target = PACKAGE_NAME, label
    else:
      pkg2, target = colon_split  # fails if len(colon_split) != 2
      pkg = PACKAGE_NAME + ("/" + pkg2 if pkg2 else "")
  return pkg, target


def is_absolute_label(label):
  """Test whether a label is absolute or relative."""
  return label.startswith("//")

def absolute_label(label):
  """Expand a label to be of the full form //package:foo.

  absolute_label("//gws:foo") = "//gws:foo"
  absolute_label("//gws:gws") = "//gws:gws"
  absolute_label("//gws")     = "//gws:gws"
  absolute_label(":foo")      = "//current_package:foo"
  absolute_label("foo")       = "//current_package:foo"

  The form is "canonical" - that is, every label with the same meaning will
  generate a single absolute label form.

  Args:
    label: string in absolute or relative form.

  Returns:
    Absolute form of the label as a string.

  Raises:
    ValueError for malformed label (does not do an exhaustive validation)
  """
  return "//%s:%s" % parse_label(label)


def _get_java_root_index(pkg_name):
  """Returns the index of the java_root within a build package"""
  # Find the java folder in the beginning, middle or end of a path.
  java_index = _get_last_dir_occurrence_of(pkg_name, "java")

  # Find the javatests folder in the beginning, middle or end of a path.
  javatests_index = _get_last_dir_occurrence_of(pkg_name, "javatests")

  if java_index == -1 and javatests_index == -1:
    fail("can not find java root: " + pkg_name)

  if java_index > javatests_index:
    index = java_index + len("java/")
  else:
    index = javatests_index + len("javatests/")
  return index

def get_java_root(pkg_name):
  """Extract the path to java root from the build package"""
  return pkg_name[:_get_java_root_index(pkg_name)]

def get_java_path(pkg_name):
  """Extract the java path from the build package"""
  return pkg_name[len(get_java_root(pkg_name)):]

def get_java_package(pkg_name):
  """Extract the java package from the build package"""
  return get_java_path(pkg_name).replace("/", ".")

def _get_last_dir_occurrence_of(path, name):
  """Returns the index of the last occurrence of directory name in path."""
  if path == name:
    return 0
  if path.endswith("/" + name):
    return path.rfind(name)
  index = path.rfind("/" + name + "/")
  if index != -1:
    return index + 1
  if path.startswith(name + "/"):
    return 0
  else:
    return -1
