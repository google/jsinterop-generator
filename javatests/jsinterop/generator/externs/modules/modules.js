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
 * @typedef {{
 *      foo: number,
 *      bar: string
 *      }}
 */
namespace.NamespacedTypeDefOfRecord;

/**
 * @type {namespace.NamespacedTypeDefOfRecord}
 */
namespace.namespacedTypeDefOfRecordRef;

/**
 * @record
 */
namespace.NamespacedRecord = function() {};

/**
 * @constructor
 */
namespace.NamespacedClass = function() {};

/**
 * @typedef {function(string):boolean}
 */
namespace.NamespacedFunctionType;

/** @type {namespace.NamespacedFunctionType} */
namespace.namespacedFunctionTypeRef;

/**
 * @typedef {string|number}
 */
namespace.NamespacedUnionType;

/** @type {namespace.NamespacedUnionType} */
namespace.namespacedUnionTypeRef;

/**
 * @enum {string}
 */
namespace.NamespacedEnum = {A: 'A', B: 'B'};

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
 * Property referring an namespaced typedef of record defined in another
 * namespace.
 *
 * @type {namespace.NamespacedTypeDefOfRecord}
 */
othernamespace.namespacedTypeDefOfRecordRef;

/**
 * Property referring an namespaced union type defined in another namespace.
 *
 * @type {namespace.NamespacedUnionType}
 */
othernamespace.namespacedUnionTypeRef;

/**
 * Property referring namespaced function type defined in another namespace.
 *
 * @type {namespace.NamespacedFunctionType}
 */
othernamespace.namespacedFunctionTypeRef;

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
