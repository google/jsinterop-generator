/**
 * @fileoverview Simulate third_party extern file
 * @externs
 */

/**
 * Class using third party types.
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
 * Interface extending third party types.
 * @interface
 * @extends {ThirdPartyInterface}
 */
function SimpleInterface() {}

/**
 * Test api extension of a provided type.
 * @type {ThirdParty2Class}
 */
ThirdPartyClass.prototype.extraField;


/**
 * Test api extension of a provided type.
 * @param {T} foo
 * @param {(T |{bar:T})} bar
 * @return {undefined}
 * TODO(b/35681242): reenable the test with local parameter when the bug is
 * fixed
 * //@param {function(T):U} baz
 * //@return {U}
 * //@template U
 */
ThirdPartyClass.prototype.extraMethod = function(foo, bar) {};

/**
 * Test global scope extension
 * @return {string}
 */
function foo() {}