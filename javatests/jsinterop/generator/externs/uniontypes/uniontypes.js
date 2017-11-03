/**
 * @fileoverview Test conversion of union type
 * @externs
 */

/**
 * @interface
 * @template T
 */
function ParentInterface() {}

/**
 * @param {(string| number)} foo
 */
ParentInterface.prototype.parentMethod = function(foo) {};

/**
 * @param {(string| number)} foo
 */
ParentInterface.prototype.parentMethod2 = function(foo) {};

/**
 * @constructor
 * @param {(string|number)} foo
 * @implements {ParentInterface<(string|number)>}
 */
function Child(foo) {}

/**
 * @param {(string| number)} foo
 * @override
 * Test that overridden methods with UnionTypes use helper types from the parent
 */
Child.prototype.parentMethod = function(foo) {};

/**
 * Augment parent method with optional parameter.
 * @param {(string| number)} foo
 * @param {(string|boolean)=} bar
 */
Child.prototype.parentMethod2 = function(foo, bar) {};

/**
 * @return {(string|number|Child)}
 * Tests UnionType used as return type
 */
Child.prototype.method = function() {};

/**
 * @param {(string|number|Child)} foo
 * @param {(string|number|boolean)} bar
 * @param {boolean} baz
 * Tests UnionType used as parameter
 */
Child.prototype.method1 = function(foo, bar, baz) {};

/**
 * @param {(string|number|Child)} foo
 * @return {(string|number|boolean)}
 * Tests method that use UnionType both in parameter and in return type
 */
Child.prototype.method2 = function(foo) {};

/**
 * @param {(string|Array<number|Child> | Foo<string|number, string>)} foo
 * @return {undefined}
 * Tests UnionType both in generics and array type.
 */
Child.prototype.method3 = function(foo) {};


/**
 * Test that we don't create conflicting methods overloads for union type where
 * raw generics are involved.
 * @param {(T|V)} foo
 * @param {function((T|V)):boolean} barCallback
 * @return {V}
 * @template T,V
 */
Child.prototype.method4 = function(foo, barCallback) {};

/**
 * @interface
 * @template T,V
 */
function Foo() {}

/**
 * Test union type of type parameters involved in parametrized type.
 * @type {ParentInterface<(T|V)>}
 */
Foo.prototype.foo;
