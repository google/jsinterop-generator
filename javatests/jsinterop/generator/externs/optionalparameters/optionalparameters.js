/**
 * @fileoverview Test conversion of methods and constructors with optional
 * parameters.
 * @externs
 */

/**
 * @param {string=} opt_foo
 * @constructor
 */
function SimpleClass(opt_foo) {}

/**
 * @param {string} foo
 * @param {string=} opt_bar
 * @param {string=} opt_baz
 * @return {undefined}
 */
SimpleClass.prototype.foo = function(foo, opt_bar, opt_baz) {};

/**
 * @param {string=} opt_foo
 * @param {...*} bar
 * @return {undefined}
 */
SimpleClass.prototype.optionalParameterWithVarArgs = function(opt_foo, bar) {};

/**
 * @interface
 */
function SimpleInterface() {}

/**
 * @param {string=} opt_foo
 * @param {string=} opt_bar
 * @param {string=} opt_baz
 * @return {undefined}
 */
SimpleInterface.prototype.foo = function(opt_foo, opt_bar, opt_baz) {};
