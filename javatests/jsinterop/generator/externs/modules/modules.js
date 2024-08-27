/**
 * @fileoverview Test conversion of modules and namespaces.
 * @externs
 */

/**
 * @const
 */
var namespace = {};

/**
 * @const
 */
namespace.nestednamespace = {};

/**
 * Property referring the type of the enclosing namespace.
 *
 * @type {namespace.NamespacedInterface}
 */
namespace.nestednamespace.staticProperty;

/**
 * @return {undefined}
 */
namespace.nestednamespace.staticFunction = function() {};

/**
 * @interface
 */
namespace.nestednamespace.InterfaceFromNestedNamespace = function() {};

/**
 * Property referring the type of the nested namespace.
 *
 * @type {namespace.nestednamespace.InterfaceFromNestedNamespace}
 */
namespace.staticProperty;

/**
 * @interface
 */
namespace.NamespacedInterface = function() {};

/**
 * @const
 */
var othernamespace = {};

/**
 * Property referring the type of another namespace.
 *
 * @type {namespace.nestednamespace.InterfaceFromNestedNamespace}
 */
othernamespace.property;

/**
 * This namespace defines types only. No Java class will be generated.
 *
 * @const
 */
var namespacewithtypeonly = {};

/**
 * @interface
 */
namespacewithtypeonly.Interface = function() {};
