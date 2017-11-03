/**
 * @fileoverview Test conversion of type parameters.
 * Note that unlike Typescript, closure doesn't support bounded generics.
 * @externs
 */

/**
 * @interface
 */
function SimpleInterface() {}

/**
 * test simple type parameter
 * @interface
 * @template T
 */
function InterfaceWithGeneric() {}

/**
 * test type parameter used in field
 * @type {T}
 */
InterfaceWithGeneric.prototype.foo;

/**
 * test type parameter used in parameter type
 * @param {T} foo
 * @return {undefined}
 */
InterfaceWithGeneric.prototype.method = function(foo) {};

/**
 * test type parameter used in return type
 * @return {T}
 */
InterfaceWithGeneric.prototype.method2 = function() {};

/**
 * test several type parameters
 * @interface
 * @template U,T,V
 */
function Bar() {}

/**
 * @type {InterfaceWithGeneric<{foo:{baz:T}}>}
 */
Bar.prototype.bar;

/**
 * @type {U}
 */
Bar.prototype.foo;

/**
 * @type {V}
 */
Bar.prototype.baz;

/**
 * test reference to a parametrized type
 * @type {InterfaceWithGeneric<number>}
 */
Bar.prototype.bar2;

/**
 * test type parameter scope.
 * @template V
 * @param {{foo: V, bar: T}} param
 * @return {V}
 */
Bar.prototype.barMethod2 = function(param) {};

/**
 * Test generics used in structural type enclosed in another structural type.
 * @param {function(V):{foo:T, bar: Y, baz: Z}} fooCallback
 * @return {undefined}
 * @template Z,Y
 */
Bar.prototype.barMethod3 = function(fooCallback) {};

/**
 * test generics used in anonymous types
 * @constructor
 * @template T
 */
function AnonymousTypes() {}

/**
 * @template T
 * @param {{bar: T}} foo
 * @return {undefined}
 */
AnonymousTypes.bar = function(foo) {};

/**
 * @param {{bar: T, foo: T}} foo
 * @return {undefined}
 */
AnonymousTypes.prototype.typeLiteral = function(foo) {};

/**
 * @param {function(T):boolean} fooCallback
 * @return {undefined}
 */
AnonymousTypes.prototype.functionTypeWithGenericInParameter = function(
    fooCallback) {};

/**
 * @param {(T|string)} foo
 * @return {undefined}
 */
AnonymousTypes.prototype.unionType = function(foo) {};

/**
 * @param {function(boolean):T} foo
 * @return {undefined}
 */
AnonymousTypes.prototype.functionTypeWithGenericInReturnType = function(foo) {};

/**
 * @param {{bar:U, baz:T, foo: V}} foo
 * @return {undefined}
 * @template V, U
 */
AnonymousTypes.prototype.foo = function(foo) {};


/**
 * test generics in extension clause
 * @interface
 * @extends {InterfaceWithGeneric<number>}
 */
function ExtendInterfaceWithGeneric() {}

/**
 * TypeArray should be converted to classic java array when used in type
 * parameter
 * @param {InterfaceWithGeneric<Array<boolean>>} param
 * @return {undefined}
 */
ExtendInterfaceWithGeneric.prototype.bar = function(param) {};

/**
 * @constructor
 */
function SimpleClass() {}

/**
 * Static and instance methods with same name, test that generics are collected
 * on the right method
 * @template V
 * @return {V}
 */
SimpleClass.prototype.foo = function() {};

/**
 * Static and instance methods with same name, test that generics are collected
 * on the right method
 * @template V
 * @param {SimpleClass} obj
 * @return {V}
 */
SimpleClass.foo = function(obj) {};
