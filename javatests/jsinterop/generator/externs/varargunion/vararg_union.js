/**
 * @fileoverview Test externs for vararg union type.
 * @externs
 */

/**
 * @constructor
 */
function Foo() {}

/**
 * @param {...(string|number)} x
 */
Foo.prototype.method = function(x) {};
