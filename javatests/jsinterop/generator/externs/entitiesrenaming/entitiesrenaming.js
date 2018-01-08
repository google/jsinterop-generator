/**
 * @fileoverview Test renaming of some entities.
 * @externs
 */

/**
 * @constructor
 */
function Foo() {}

/**
 * @param {function(string):undefined} parameterToRename
 * @return {undefined}
 */
Foo.prototype.foo = function(parameterToRename) {};

/**
 * @param {{bar: string}} parameterToRename
 * @return {undefined}
 */
Foo.prototype.bar = function(parameterToRename) {};

/**
 * @interface
 */
function SimpleInterface() {}

/**
 * @param {function((function(string):undefined)):boolean} fooCallback
 * @return {undefined}
 */
SimpleInterface.prototype.method = function(fooCallback) {};


/**
 * @param {(string|number)} bar
 */
function foo(bar) {}
