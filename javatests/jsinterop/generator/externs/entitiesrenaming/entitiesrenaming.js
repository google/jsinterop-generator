/**
 * @fileoverview Test renaming of some entities.
 * @externs
 */

/**
 * @constructor
 */
function Foo() {}

/**
 * @param {function(string):undefined} parameterToRename will be renamed to foo
 * @return {undefined}
 */
Foo.prototype.foo = function(parameterToRename) {};

/**
 * Test renaming with record type
 * @param {{bar: string}} parameterToRename
 * @return {undefined}
 */
Foo.prototype.bar = function(parameterToRename) {};
/**
 * @interface
 */
function SimpleInterface() {}


/**
 * Test renaming in function type.
 * @param {function((function(string):undefined)):boolean} foo
 * @return {undefined}
 */
SimpleInterface.prototype.method = function(foo) {};


/**
 * Test renaming parameter of a method of the global Scope.
 * @param {(string|number)} bar
 */
function foo(bar) {}