/**
 * @fileoverview Test function type across different scopes.
 * @externs
 */


/**
 * Interface provided by the third party lib
 * @interface
 * @extends ParentThirdPartyInterface
 */
function ThirdPartyInterface() {}


/**
 * Class provided by the third party lib
 * class
 * @constructor
 * @param {(string|number)} foo
 * @template T
 * @extends ParentThirdPartyClass
 */
function ThirdPartyClass(foo) {}

/**
 * Test global scope extension
 * @param {function(number)}  callback
 * @return {string}
 */
function thirdparty(callback) {}

/**
 * @const
 */
var namespace1 = {};

/**
 * @interface
 */
namespace1.InterfaceWithConflictingName = function() {};
