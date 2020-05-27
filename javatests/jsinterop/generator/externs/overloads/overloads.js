
/**
 * @fileoverview Test "overloaded" method conventions
 * @externs
 */

/**
 * @constructor
 */
function Foo() {}

Foo.noArgMethod = function() {};
Foo.prototype.noArgMethod = function() {};

/**
 * @param {string|number} arg
 */
Foo.unionArgMethod = function(arg) {};
/**
 * @param {string|number} arg
 */
Foo.prototype.unionArgMethod = function(arg) {};

/**
 * @type {string|number}
 */
Foo.unionProperty;
/**
 * @type {string|number}
 */
Foo.prototype.unionProperty;