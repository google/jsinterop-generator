/**
 * @fileoverview Simulate third_party extern file
 * @externs
 */

/**
 * @constructor
 * @extends {ThirdPartyClass}
 * @implements {ThirdPartyInterface}
 */
function SimpleClass() {}

/**
 * @type {ThirdPartyClass}
 */
SimpleClass.prototype.field;


/**
 * @param {ThirdPartyInterface} foo
 * @return {ThirdPartyClass}
 */
SimpleClass.prototype.method = function(foo) {};

/**
 * @param {{foo:string}} foo
 * @return {undefined}
 */
SimpleClass.prototype.parentThirdpartyMethod = function(foo) {};

/**
 * @interface
 * @extends {ThirdPartyInterface}
 */
function SimpleInterface() {}

/**
 * @type {ThirdParty2Class}
 */
ThirdPartyClass.prototype.extraField;


// TODO(b/35681242): reenable the test with local parameter when the bug is
// fixed
//@param {function(T):U} baz
//@return {U}
//@template U
/**
 * @param {T} foo
 * @param {(T |{bar:T})} bar
 * @return {undefined}
 */
ThirdPartyClass.prototype.extraMethod = function(foo, bar) {};

// Test global scope extension.
/**
 * @return {string}
 */
function foo() {}
