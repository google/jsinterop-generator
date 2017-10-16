/**
 * @fileoverview Test conversion of IObject and IArrayLike
 * @externs
 */

// TODO(dramaix): move to an extern dependency
/**
 * @interface
 * @template KEY1, VALUE1
 * @suppress {duplicate}
 */
function IObject() {}

/**
 * @record
 * @extends {IObject<number, VALUE2>}
 * @template VALUE2
 * @suppress {duplicate}
 */
function IArrayLike() {}

/**
 * @constructor
 * @implements {IObject<string, string>}
 */
function Foo() {}

/**
 * @constructor
 * @implements {IArrayLike<string>}
 */
function Bar() {}

/**
 * Test that type reference to IArrayLike in return type of a method is converted
 * to JsArrayLike
 * @return {IArrayLike<string>}
 */
Bar.prototype.asIArrayLike = function() {};

/**
 * Test that type reference to IObject in return type of a method is converted
 * to JsPropertyMap
 * @return {IObject<string, string>}
 */
Bar.prototype.asIObject = function() {};

/**
 * Test that type reference to IObject and IArrayLike in parameters of a method
 * is converted to JsPropertyMap and JsArrayLike
 * @param {IObject<string, string>} object
 * @param {IArrayLike<string>} arrayLike
 * @param {IArrayLike<IArrayLike<string>>} doubleArrayLike
 * @return {undefined}
 */
Bar.prototype.consumeIObjectAndIArrayLike = function(
    object, arrayLike, doubleArrayLike) {};


/**
 * Test that type reference to IObject and IArrayLike in parameters of a method
 * is converted to JsPropertyMap and JsArrayLike
 * @param {Object} object
 * @param {IArrayLike<string>} arrayLike
 * @param {function(new:Bar, string)} ctor
 * @return {undefined}
 */
Bar.prototype.consumeObjectIArrayLikeAndCtorFn = function(
    object, arrayLike, ctor) {};

/**
 * Test that type reference to IObject in field is converted to JsPropertyMap.
 * @type {IObject<string, string>}
 */
Bar.prototype.iObjectField;

/**
 * Test that type reference to IArrayLike in field is converted to JsArrayLike.
 * @type {IArrayLike<string>}
 */
Bar.prototype.iArrayLikeField;

/**
 * Object can be templatized.
 * @type {Object<string>}
 */
Bar.prototype.templatizedObject;

/**
 * Object can be templatized with two parameters.
 * @type {Object<string, number>}
 */
Bar.prototype.templatizedObjectWithTwoParameters;
/**
 * test type parameters used with index signature.
 * test also type with string and number index signature.
 * @interface
 * @extends {IObject<number|string, T>}
 * @extends {IArrayLike<T>}
 * @template T
 */
function Baz() {}

/**
 * Test that conversion of Object type doesnt contain the two type parameters
 * from IObject : class NativeObject<IObject#KEY1, IObject#VALUE> {}
 * @constructor
 * @param {*=} args
 * @suppress {duplicate}
 */
function Object(args) {}
