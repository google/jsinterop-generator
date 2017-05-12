/**
 * @fileoverview Test function type across different scopes.
 * @externs
 */


/**
 * Interface provided by the third party lib
 * @interface
 */
function ThirdParty2Interface() {}


/**
 * Class provided by the third party lib
 * class
 * @constructor
 * @template T
 */
function ThirdParty2Class() {}

/**
 * Test api extension of a provided type.
 * @return {string}
 */
ThirdPartyClass.prototype.extraMethod2 = function() {};

/**
 * Test global scope extension
 * @return {string}
 */
function thirdparty2() {}