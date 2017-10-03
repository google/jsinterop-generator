/**
 * @fileoverview Extern files used as dependency of our test in order to provide
 * java class for native Object type without polluting our test.
 * @externs
 */


/**
 * Test that conversion of Object type doesn't contain the two type parameters
 * from IObject : class NativeObject<IObject#KEY1, IObject#VALUE> {}
 * @constructor
 * @param {*=} args
 * @suppress {duplicate}
 */
function Object(args) {}