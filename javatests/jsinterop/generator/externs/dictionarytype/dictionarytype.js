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
 * Test dictionary type used in structural type
 * @type {{foo: string, bar: number}}
 */
SimpleDictionaryType.prototype.bar;

/**
 * Test dictionary type used in structural type
 * @type {function({foo: string, bar: number}):void}
 */
SimpleDictionaryType.prototype.baz;

/**
 * Test dictionary type used in a typedef
 * @typedef {{foo: string, bar: number}}
 */
var DictionaryType;
