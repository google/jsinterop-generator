/**
 * @fileoverview Test conversion of a type
 * @externs
 */

/**
 * @constructor
 * @param {string=} foo
 */
function SimpleClass(foo) {}

/**
 * @type {number}
 */
SimpleClass.staticProperty;

/**
 * @param {string} foo
 * @param {string} bar
 * @param {boolean=} opt_baz
 * @return {boolean}
 */
SimpleClass.staticMethod = function(foo, bar, opt_baz) {};

// Static methods conflicting with java.lang.Object methods need to be renamed.
/**
 * @return {string}
 */
SimpleClass.getClass = function() {};

/**
 * @return {number}
 */
SimpleClass.hashCode = function() {};

/**
 * @param {*} other
 * @return {boolean}
 */
SimpleClass.equals = function(other) {};

/**
 * @return {?}
 */
SimpleClass.clone = function() {};

/**
 * @return {string}
 */
SimpleClass.toString = function() {};

/**
 * @return {undefined}
 */
SimpleClass.notify = function() {};

/**
 * @return {undefined}
 */
SimpleClass.notifyAll = function() {};

/**
 * @return {undefined}
 */
SimpleClass.wait = function() {};

/**
 * @const {string}
 */
SimpleClass.staticReadonlyProperty

/**
 * @type {string}
 */
SimpleClass.prototype.fooProperty;

/**
 * @const {boolean}
 */
SimpleClass.prototype.readonlyProperty;

/**
 * @type {Array<Array<Array<string>>>}
 */
SimpleClass.prototype.fooProperty2;

/**
 * @type {SimpleClass}
 */
SimpleClass.prototype.thisType;

/**
 * @param {string} foo
 * @param {string} bar
 * @param {Object=} opt_baz
 * @return {boolean}
 */
SimpleClass.prototype.fooMethod = function(foo, bar, opt_baz) {};

// Instance methods conflicting with java.lang.Object methods need to be
// renamed.
/**
 * @return {string}
 */
SimpleClass.prototype.toString = function() {};

/**
 * @return {number}
 */
SimpleClass.prototype.hashCode = function() {};

/**
 * @param {*} other
 * @return {boolean}
 */
SimpleClass.prototype.equals = function(other) {};

/**
 * @return {string}
 */
SimpleClass.prototype.getClass = function() {};

/**
 * @return {?}
 */
SimpleClass.prototype.clone = function() {};

/**
 * @return {undefined}
 */
SimpleClass.prototype.notify = function() {};

/**
 * @return {undefined}
 */
SimpleClass.prototype.notifyAll = function() {};

/**
 * @return {undefined}
 */
SimpleClass.prototype.wait = function() {};

/**
 * @interface
 */
function SimpleInterface() {}

/**
 * @const {string}
 */
SimpleInterface.staticProperty;

/**
 * @type {string}
 */
SimpleInterface.prototype.fooProperty;

/**
 * @const {boolean}
 */
SimpleInterface.prototype.readonlyProperty;

/**
 * @param {string} foo
 * @param {string} bar
 * @param {boolean=} opt_baz
 * @return {boolean}
 */
SimpleInterface.prototype.fooMethod = function(foo, bar, opt_baz) {};


/**
 * @record
 */
function SimpleStructuralInterface() {}

/**
 * @param {string} foo
 * @param {string} bar
 * @param {boolean=} opt_baz
 * @return {boolean}
 */
SimpleStructuralInterface.prototype.fooMethod = function(foo, bar, opt_baz) {};

// TODO(b/34389745): Type alias with a constant is not supported.
/**
 * @const
 */
var SimpleClassAlias = SimpleClass;

/**
 * @constructor
 * @private
 */
function PrivateClass() {}
