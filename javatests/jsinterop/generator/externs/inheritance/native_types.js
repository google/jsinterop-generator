/**
 * @fileoverview Extern files used as dependency of our test in order to provide
 * java class for native Object type without polluting our test.
 * @externs
 */

/**
 * @interface
 * @template T
 *
 * @suppress {duplicate}
 */
function Iterable(args) {}

/**
 * @constructor
 * @template T
 * @implements {Iterable<T>}
 *
 * @param {...*} args
 * @return {!Array<T>}
 *
 * @suppress {duplicate}
 */
function Array(args) {}

// JsInterop-generator expect to find unshift and concat methods on Array type
/**
 * @param {...*} var_args
 * @return {!Array<?>}
 * @this {*}
 */
Array.prototype.concat = function(var_args) {};

/**
 * @param {...*} var_args
 * @return {number} The new length of the array
 * @this {IArrayLike<?>}
 * @modifies {this}
 */
Array.prototype.unshift = function(var_args) {};
