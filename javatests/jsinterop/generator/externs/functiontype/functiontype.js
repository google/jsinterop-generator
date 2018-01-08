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
 * @param {function(string)|string} fooCallback
 * @return {undefined}
 */
function method2(fooCallback) {}

/**
 * @param {function(function():boolean):string} fooCallback
 * @return {undefined}
 */
function method3(fooCallback) {}

/**
 * @param {function()} fooCallback
 * @return {undefined}
 */
function method4(fooCallback) {}

/**
 * @param {function({foo:string}):{foo:number}} fooCallback
 * @return {undefined}
 */
function method5(fooCallback) {}

/**
 * @param {function((function():boolean), (function():string)):(function():number)} fooCallback
 * @return {undefined}
 */
function method6(fooCallback) {}

// Both inner function types will be named P0Fn. This test checks that
// references to those classes don't conflict together.
/**
 * @param {function(function():boolean):undefined} fooCallback
 * @param {function(function():number):undefined} barCallback
 * @return {undefined}
 */
function method7(fooCallback, barCallback) {}
