/**
 * @fileoverview Test function type across different scopes.
 * @externs
 */

/**
 * Test typedef used as an structural type
 * @typedef {function(string):boolean}
 */
var AliasedFunctionType;


/**
 * test function type used in field, method(return type + parameters) of an
 * class
 * @constructor
 */
function SimpleClass() {}

/** @type {AliasedFunctionType} */
SimpleClass.prototype.bar;

/** @type {function(string):boolean} */
SimpleClass.prototype.foo;

/**
 * @param {function(string):boolean} fooCallback
 * @return {undefined}
 */
SimpleClass.prototype.method = function(fooCallback) {};

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
SimpleClass.prototype.method1 = function(foo) {};


/**
 * test function type used in field, method(return type + parameters) of an
 * interface
 * @interface
 */
function SimpleInterface() {}

/** @type {AliasedFunctionType} */
SimpleInterface.prototype.bar;

/** @type {function(string):boolean} */
SimpleInterface.prototype.foo;

/**
 * @param {function(string):boolean} fooCallback
 * @return {undefined}
 */
SimpleInterface.prototype.method = function(fooCallback) {};

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
SimpleInterface.prototype.method1 = function(foo) {};

/**
 * @param {function((string|number)):boolean} fooCallback
 * @return {undefined}
 */
SimpleInterface.prototype.withUnionType = function(fooCallback) {};

/**
 * test FunctionType used in variable, function(return type + parameters) of a
 * module
 * @const
 */
var SimpleModule = {};

/** @type {AliasedFunctionType} */
SimpleModule.bar;
/** @type {function(string):boolean} */
SimpleModule.foo;

/**
 * @param {function(string):boolean} fooCallback
 * @return {undefined}
 */
SimpleModule.method = function(fooCallback) {};

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
SimpleModule.method1 = function(foo) {};

// test FunctionType used in variable, function(return type + parameters) of the
// global scope
/** @type {AliasedFunctionType} */
var bar;

/** @type {function(string):boolean} */
var foo;

/**
 * test FunctionType in generics and arraytype
 * @type {Array<function(string):boolean>}
 */
var baz;

/** @type {function(new:SimpleClass)} */
var simpleClassCtor;

/**
 * @param {function(string):boolean} fooCallback
 * @param {function(new:SimpleClass)} ctor
 * @return {undefined}
 */
function method(fooCallback, ctor) {}

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
function method1(foo) {}

/**
 * test FunctionType in union type
 * @param {function(string)|string} fooCallback
 * @return {undefined}
 */
function method2(fooCallback) {}

/**
 * test inner function type
 * @param {function(function():boolean):string} fooCallback
 * @return {undefined}
 */
function method3(fooCallback) {}

/**
 * test naming of function type when the name of parameter contains callback
 * @param {function()} fooCallback
 * @return {undefined}
 */
function method4(fooCallback) {}

/**
 * test naming of synthetic type with structural type inside function type
 * @param {function({foo:string}):{foo:number}} fooCallback
 * @return {undefined}
 */
function method5(fooCallback) {}

/**
 * Test naming of function type inside function type
 * @param {function((function():boolean), (function():string)):(function():number)} fooCallback
 * @return {undefined}
 */
function method6(fooCallback) {}

/**
 * Test symbol conflict
 * @param {function(function():boolean):undefined} fooCallback
 * @param {function(function():number):undefined} barCallback
 * @return {undefined}
 */
function method7(fooCallback, barCallback) {}
