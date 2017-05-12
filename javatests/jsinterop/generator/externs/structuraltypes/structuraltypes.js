/**
 * @fileoverview Test conversion of closure structural type
 * @externs
 */

/**
 * Test typedef used as an alias
 * @typedef {(string|number)}
 */
var NumberLike;

/**
 * Test typedef used as an structural type
 * @typedef {{
 *      foo: number,
 *      bar: string
 *      }}
 */
var FooBar;

/**
 * As Closure inlines typedefs, we test that when several typedefs have the same
 * structure, we still refer to the right named typedef.
 * @typedef {{
 *      foo: number,
 *      bar: string,
 *      }}
 */
var FooBar2;

/**
 * Test structural type defining inner structural type.
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
 * Record type in generics
 * @type {Array<{foo: number}>}
 */
SimpleClass.prototype.recordTypeArray;

/**
 * As Closure inlines typedefs, we test that when several typedefs have the same
 * we still refer to the right named typedef.
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
 * Test RecordType in UnionType
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
 * test naming of function type inside structural type
 * @param {{foo:function({bar:string}):undefined}} foo
 * @return {undefined}
 */
function method1(foo) {}

/**
 * Test naming of inner structural type
 * @param {{bar: {foo:string}, foo: {baz: {insane:string}}}} foo
 * @return {undefined}
 */
function method2(foo) {}

/**
 * Test symbol conflict
 * @param {{bar: {foo:string}, foo: string}} foo
 * @param {{bar: {foo:string}}} bar
 * @return {undefined}
 */
function method3(foo, bar) {}
