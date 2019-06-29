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
 * @interface
 * @template T
 */
function InterfaceWithGeneric() {}

/**
 * @type {T}
 */
InterfaceWithGeneric.prototype.foo;

/**
 * @param {T} foo
 * @return {undefined}
 */
InterfaceWithGeneric.prototype.method = function(foo) {};

/**
 * @return {T}
 */
InterfaceWithGeneric.prototype.method2 = function() {};

/**
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
 * @type {InterfaceWithGeneric<number>}
 */
Bar.prototype.bar2;

/**
 * @template V
 * @param {{foo: V, bar: T}} param
 * @return {V}
 */
Bar.prototype.barMethod2 = function(param) {};

/**
 * @param {function(V):{foo:T, bar: Y, baz: Z}} fooCallback
 * @return {undefined}
 * @template Z,Y
 */
Bar.prototype.barMethod3 = function(fooCallback) {};

// Test wildcard type: TypeParameter used in return type and parameter should
// not use wildcardtype.
/**
 * @param {function(U):U} fooCallback
 * @return {undefined}
 */
Bar.prototype.barMethod4 = function(fooCallback) {};

// Test wildcard type: Array asd union type are considered as direct reference.
/**
 * @param {function(U, Array<U>, (T|string)):undefined} fooCallback
 * @return {undefined}
 */
Bar.prototype.barMethod5 = function(fooCallback) {};

// Test wildcard type: indirect reference don't create wildcard type.
/**
 * @param {function(U, InterfaceWithGeneric<U>):InterfaceWithGeneric<T>} fooCallback
 * @return {undefined}
 */
Bar.prototype.barMethod6 = function(fooCallback) {};

/**
 * @interface
 * @template U,T,V
 * @extends {Bar<U,T,V>}
 */
function BarChild() {}

/**
 * @param {U} u
 * @param {V} v
 * @return {T}
 * @this {Bar<U,T,V>|string}
 * @modifies {this}
 * @template U,T,V
 */
BarChild.prototype.methodWithThis = function(u, v) {};

/**
 * @constructor
 * @template T
 */
function AnonymousTypes() {}

/**
 * @param {function(this:T, string):?} callback
 * @return {undefined}
 * @template T
 */
AnonymousTypes.prototype.functionTypeRedefiningThis = function(callback) {};

/**
 * @template Z
 * @param {InterfaceWithGeneric<{bar: Z}>} foo
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
 * @template T
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
 * @interface
 * @extends {InterfaceWithGeneric<number>}
 */
function ExtendInterfaceWithGeneric() {}

// Type Array should be converted to classic java array when used in type
// parameter. param should be of type InterfaceWithGenerics<boolean[]>
/**
 * @param {InterfaceWithGeneric<Array<boolean>>} param
 * @return {undefined}
 */
ExtendInterfaceWithGeneric.prototype.bar = function(param) {};

/**
 * @constructor
 * @template T
 * @param {T} value
 */
function SimpleClass(value) {}

// The class has a static and instance methods with same name. The logic
// collecting type parameters is based on method name. We check here that the
// TypeParameter V is collected for both methods and not only the first one.
/**
 * @template V
 * @return {V}
 */
SimpleClass.prototype.foo = function() {};

/**
 * @param {THIS} param
 * @return {THIS}
 * @this {THIS}
 * @template THIS
 */
SimpleClass.prototype.chainableMethodWithThis = function(param) {};

/**
 * @template V
 * @param {SimpleClass} obj
 * @return {V}
 */
SimpleClass.foo = function(obj) {};

/**
 * @constructor
 * @extends {SimpleClass<string>}
 */
function SimpleClassChild() {}
