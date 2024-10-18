/**
 * @fileoverview Test conversion of IObject and IArrayLike. IObject references
 * should be converted to jsinterop.base.JsPropertyMap and IArrayLike to
 * jsinterop.base.JsArrayLike.
 * <p>
 * Internally, JsCompiler represent the Object type as a parametrized type with
 * two optional type parameters: IObject#KEY1, IObject#VALUE.
 * The tests using Object references are needed for checking that conversion of
 * the two optional type parameters are correct in all cases.
 *
 * @externs
 */
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
 * @return {IArrayLike<string>}
 */
Bar.prototype.asIArrayLike = function() {};

/**
 * @return {IObject<string, string>}
 */
Bar.prototype.asIObject = function() {};

/**
 * @param {IObject<string, string>} object
 * @param {IArrayLike<string>} arrayLike
 * @param {IArrayLike<IArrayLike<string>>} doubleArrayLike
 * @return {undefined}
 */
Bar.prototype.consumeIObjectAndIArrayLike = function(
    object, arrayLike, doubleArrayLike) {};


/**
 * @param {Object} object
 * @param {IArrayLike<string>} arrayLike
 * @param {function(new:Bar, string)} ctor
 * @return {undefined}
 */
Bar.prototype.consumeObjectIArrayLikeAndCtorFn = function(
    object, arrayLike, ctor) {};

/**
 * @type {IObject<string, string>}
 */
Bar.prototype.iObjectField;

/**
 * @type {IArrayLike<string>}
 */
Bar.prototype.iArrayLikeField;

/**
 * @type {Object<string>}
 */
Bar.prototype.templatizedObject;

/**
 * @type {Object<string, number>}
 */
Bar.prototype.templatizedObjectWithTwoParameters;

/**
 * @type {Object<string|number, number>}
 */
Bar.prototype.templatizedObjectWithStringOrNumberKeys;

/**
 * @type {Object<string|symbol, number>}
 */
Bar.prototype.templatizedObjectWithStringOrSymbolKeys;

/**
 * @interface
 * @extends {IObject<number|string, T>}
 * @extends {IArrayLike<T>}
 * @template T
 */
function Baz() {}

/** @constructor */
function Varargs() {}

// Test a union type in a varargs position.
/**
 * @param {...Object} var_args
 * @return {void}
 */
Varargs.prototype.methodWithJsObjectVarargs = function(var_args) {};
