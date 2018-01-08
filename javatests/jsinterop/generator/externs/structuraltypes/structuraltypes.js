/**
 * @fileoverview Test conversion of closure structural type
 * @externs
 */

/**
 * @typedef {(string|number)}
 */
var NumberLike;

/**
 * @typedef {{
 *      foo: number,
 *      bar: string
 *      }}
 */
var FooBar;

// As Closure inlines typedefs, we test that when several typedefs have the same
// structure, we still refer to the right named typedef.
/**
 * @typedef {{
 *      foo: number,
 *      bar: string,
 *      }}
 */
var FooBar2;

/**
 * @typedef {{
 *      foo: number,
 *      bar: {baz:string}
 *      }}
 */
var InnerStructuralType;

/**
 * @constructor
 */
function SimpleClass() {}

/**
 * @type {Array<{foo: number}>}
 */
SimpleClass.prototype.recordTypeArray;

// As Closure inlines typedefs, we test that when several typedefs have the same
// we still refer to the right named typedef.
/**
 * @param {FooBar2} fooBar
 * @return {FooBar}
 */
SimpleClass.prototype.consumeFooBar2 = function(fooBar) {};

/**
 * @param {NumberLike} number
 * @return {undefined}
 */
SimpleClass.prototype.consumeAliasedType = function(number) {};

/**
 * @param {{foo: number}=} opt_foo
 * @return {{bar:string}}
 */
SimpleClass.prototype.consumeAndReturnAnonymousType = function(opt_foo) {};

/**
 * @param {InnerStructuralType} foo
 * @return {undefined}
 */
SimpleClass.prototype.consumeInnerStructuralType = function(foo) {};

/**
 * @param {(string| {foo: string})} stringOrFoo
 * @return {undefined}
 */
SimpleClass.prototype.consumeUnionTypeWithRecordType = function(stringOrFoo) {};

// Test naming of structural type inside module
/** @const */
var SimpleModule = {};

/**
 * @param {{bar: string}} bar
 * @return {undefined}
 */
SimpleModule.foo = function(bar) {};

/**
 * @constructor
 */
SimpleModule.ClassInModule = function() {};

/**
 * @param {{baz: number}} baz
 * @return {undefined}
 */
SimpleModule.ClassInModule.prototype.foo = function(baz) {};

// Test structural type in global scope
/**
 * @param {{bar: string}} bar
 * @return {undefined}
 */
var foo = function(bar) {};

/**
 * @param {{foo:function({bar:string}):undefined}} foo
 * @return {undefined}
 */
function method1(foo) {}

/**
 * @param {{bar: {foo:string}, foo: {baz: {insane:string}}}} foo
 * @return {undefined}
 */
function method2(foo) {}

// Both inner record types will be named BarFieldType on java side. This test
// checks that references to those classes don't conflict together.
/**
 * @param {{bar: {foo:string}, foo: string}} foo
 * @param {{bar: {foo:string}}} bar
 * @return {undefined}
 */
function method3(foo, bar) {}
