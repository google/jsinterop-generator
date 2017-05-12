/**
 * @fileoverview Test function type across different scopes.
 * @externs
 */


/**
 * Interface provided by the third party lib
 * @interface
 */
function ParentThirdPartyInterface() {}

/**
 * @param {{foo:string}} foo
 * @return {undefined}
 */
ParentThirdPartyInterface.prototype.parentThirdpartyMethod = function(foo) {};


/**
 * Class provided by the third party lib
 * class
 * @constructor
 * @template T
 */
function ParentThirdPartyClass() {}