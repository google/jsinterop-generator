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
 * @param {function(string):boolean} foo
 * @return {undefined}
 */
SimpleClass.prototype.method = function(foo) {};

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
 * @param {function(string):boolean} foo
 * @return {undefined}
 */
SimpleInterface.prototype.method = function(foo) {};

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
SimpleInterface.prototype.method1 = function(foo) {};

/**
 * @param {function((string|number)):boolean} foo
 * @return {undefined}
 */
SimpleInterface.prototype.withUnionType = function(foo) {};

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
 * @param {function(string):boolean} foo
 * @return {undefined}
 */
SimpleModule.method = function(foo) {};

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
 * @param {function(string):boolean} foo
 * @return {undefined}
 */
function method(foo) {}

/**
 * @param {string} foo
 * @return {function(string):boolean}
 */
function method1(foo) {}

/**
 * test FunctionType in generics and arraytype
 * @type {Array<function(string):boolean>}
 */
var baz;

/**
 * test FunctionType in union type
 * @param {function(string)|string} foo
 * @return {undefined}
 */
function method2(foo) {}

/**
 * test inner function type
 * @param {function(function():boolean):string} foo
 * @return {undefined}
 */
function method3(foo) {}

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
