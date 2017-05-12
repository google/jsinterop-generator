/**
 * @fileoverview  JsCompiler requires that at least Symbol definitions are
 * present in the extern files otherwise the compilation fails.
 * @externs
 */

/**
 * @typedef {?}
 *
 * Need to add this annotation below because this symbol is already defined
 * for the j2cl compilation needed for the generated j2cl build tests.
 * @suppress {duplicate}
 */
var symbol;

/**
 * Need to add this annotation below because this symbol is already defined
 * for the j2cl compilation needed for the generated j2cl build tests.
 * @suppress {duplicate}
 *
 * @param {string} description
 * @return {symbol}
 */
function Symbol(description) {}
