/**
 * @fileoverview Test conversion of modules and namespaces.
 * @externs
 */

/**
 * @const
 */
var foo = {};

/**
 * @const
 */
foo.bar = {};

/**
 * @type {foo.FooInterface}
 */
foo.bar.fooProperty;

/**
 * @return {undefined}
 */
foo.bar.bar = function() {};

/**
 * @interface
 */
foo.bar.BarInterface = function() {};

/**
* @type {foo.bar.BarInterface}
*/
foo.fooProperty;

/**
 * @return {undefined}
 */
foo.foo = function() {};

/**
 * @interface
 */
foo.FooInterface = function() {};

/**
 * @const
 */
var baz = {};

/**
 * @type {foo.bar.BarInterface}
 */
baz.barProperty;

/**
 * @type {foo.FooInterface}
 */
baz.fooProperty;

/**
 * @return {undefined}
 */
baz.foo = function() {};
