/**
 * @fileoverview Simulate third_party extern file
 * @externs
 */

/**
 * @constructor
 */
function Foo() {}

/**
 * @return {bigint}
 */
Foo.prototype.asBigint = function() {};
/**
 * @param {bigint|IArrayLike<bigint>} bigintOrArrayLikeOfBigint
 * @param {function(number): bigint=} mapFn
 * @return {undefined}
 */
Foo.prototype.consumeBigint = function(bigintOrArrayLikeOfBigint, mapFn) {};

/**
 * @type {bigint}
 */
Foo.prototype.bigintField;
