/**
 * @fileoverview Test conversion of dictionary types.
 * @externs
 */

/**
 * @record
 */
function SimpleDictionaryType() {}

/**
 * @type {number}
 */
SimpleDictionaryType.prototype.foo;

/**
 * @type {{foo: string, bar: number}}
 */
SimpleDictionaryType.prototype.bar;

/**
 * @type {function({foo: string, bar: number}):void}
 */
SimpleDictionaryType.prototype.baz;

/**
 * @typedef {{foo: string, bar: number}}
 */
var DictionaryType;
