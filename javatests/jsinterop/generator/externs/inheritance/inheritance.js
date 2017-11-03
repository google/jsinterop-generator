/**
 * @fileoverview Test conversion of type hierarchies.
 * @externs
 */

/**
 * @interface
 */
function GreatParentInterface() {}
/**
 * @type {string}
 */
GreatParentInterface.prototype.greatParentInterfaceProperty;

/**
 * @param {boolean} foo
 * @return {string}
 */
GreatParentInterface.prototype.greatParentInterfaceMethod = function(foo) {};

/**
 * test interface extension
 *
 * @interface
 * @extends {GreatParentInterface}
 */
function Parent1Interface() {}

/**
 * @type {boolean}
 */
Parent1Interface.prototype.parent1InterfaceProperty;

/**
 * @param {number} foo
 * @return {boolean}
 */
Parent1Interface.prototype.parent1InterfaceMethod = function(foo) {};


/**
 * @interface
 */
function Parent2Interface() {}

/**
 * @type {number}
 */
Parent2Interface.prototype.parent2InterfaceProperty;

/**
 * @return {number}
 */
Parent2Interface.prototype.parent2InterfaceMethod = function() {};

/**
 * @constructor
 * @extends {Array<number>}
 * @param {string} s
 * @param {boolean} b
 * @param {number} n
 */
function GreatParentClass(s, b, n) {}

/**
 * @type {number}
 */
GreatParentClass.prototype.greatParentClassProperty;

/**
 * @return {number}
 */
GreatParentClass.prototype.greatParentClassMethod = function() {};

/**
 * test class extension
 * @constructor
 * @extends {GreatParentClass}
 */
function ParentClass() {}

/**
 * @type {number}
 */
ParentClass.prototype.parentClassProperty;

/**
 * @return {number}
 */
ParentClass.prototype.parentClassMethod = function() {};

/**
 * test class extension and interfaces implementation
 * @constructor
 * @extends {ParentClass}
 * @implements {Parent1Interface}
 * @implements {Parent2Interface}
 */
function SimpleClass() {}

/**
 * @type {string}
 */
SimpleClass.prototype.classProperty;

/**
 * @param {string} foo
 * @return {boolean}
 */
SimpleClass.prototype.classMethod = function(foo) {};

/**
 * @type {boolean}
 */
SimpleClass.prototype.parent1InterfaceProperty;

/**
 * @param {number} foo
 * @return {boolean}
 */
SimpleClass.prototype.parent1InterfaceMethod = function(foo) {};

/**
 * @type {number}
 */
SimpleClass.prototype.parent2InterfaceProperty;

/**
 * @return {number}
 */
SimpleClass.prototype.parent2InterfaceMethod = function() {};

/**
 * @type {string}
 */
SimpleClass.prototype.greatParentInterfaceProperty;

/**
 * @param {boolean} foo
 * @return {string}
 */
SimpleClass.prototype.greatParentInterfaceMethod = function(foo) {};


/**
 * test inheritance with structural types.
 * @interface
 * @template U,V
 */
function InterfaceWithStructuralType() {}

/**
 * test with function types and field
 * @type {function(boolean):undefined}
 */
InterfaceWithStructuralType.prototype.bar;

/**
 * test parameter method, method return type and array reference
 * @param {Array<Array<{foo: string}>>}  foo
 * @return {{bar: number}}
 */
InterfaceWithStructuralType.prototype.foo = function(foo) {};

/**
 * test with generic used in anonymous type. This test ensure the type key
 * generation handles correctly generics.
 *
 * @param {{baz: U, baz2: V}} baz
 * @param {{baz: V, baz2: U}} baz2
 * @return {V}
 */
InterfaceWithStructuralType.prototype.baz = function(baz, baz2) {};

/**
 * test union type in inheritance
 * @param {(function(V):U|string)}  barCallback
 * @return {undefined}
 */
InterfaceWithStructuralType.prototype.bar2 = function(barCallback) {};

/**
 * @template U
 * @param {(U|string)} param1
 * @param {U} param2
 * @return {undefined}
 * @override
 */
InterfaceWithStructuralType.prototype.bar3 = function(param1, param2) {};

/**
 * we deliberately switch generics name in class definition.
 * @constructor
 * @implements InterfaceWithStructuralType<V,U>
 * @template V,U
 */
function InterfaceWithStructuralTypeImpl() {}

/**
 * Test that a static field with same name than instance field, don't reuse the
 * synthetic type of the instance field.
 * @type {function(boolean):undefined}
 */
InterfaceWithStructuralTypeImpl.bar;

/**
 * Test that a static method with same signature than instance method,
 * don't reuse the synthetic types of the instance method.
 * @param {Array<Array<{foo: string}>>}  foo
 * @return {{bar: number}}
 */
InterfaceWithStructuralTypeImpl.foo = function(foo) {};

/**
 * The implementation will extends getter and setter for the field and create
 * a field using the type defined on the parent interface.
 * @type {function(boolean):undefined}
 */
InterfaceWithStructuralTypeImpl.prototype.bar;

/**
* @param {Array<Array<{foo: string}>>}  foo
* @return {{bar: number}}
*/
InterfaceWithStructuralTypeImpl.prototype.foo = function(foo) {};

/**
 * @param {{baz: V, baz2: U}} baz
 * @param {{baz: U, baz2: V}} baz2
 * @return {U}
 * @override
 */
InterfaceWithStructuralTypeImpl.prototype.baz = function(baz, baz2) {};

/**
 * @param {(function(U):V|string)}  bar
 * @return {undefined}
 * @override
 */
InterfaceWithStructuralTypeImpl.prototype.bar2 = function(bar) {};


/**
 * @template U
 * @param {(U|string)} param1
 * @param {U} param2
 * @return {undefined}
 * @override
 */
InterfaceWithStructuralTypeImpl.prototype.bar3 = function(param1, param2) {};

/**
 * Test that a synthetic type is not reused across parameter and return type if
 * they are typed with a same structural type.
 * @param {(number|string)} param1
 * @param {(number|string)} param2
 * @return {(number|string)}
 * @override
 */
InterfaceWithStructuralTypeImpl.prototype.bar4 = function(param1, param2) {};
