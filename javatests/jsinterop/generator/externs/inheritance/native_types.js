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
