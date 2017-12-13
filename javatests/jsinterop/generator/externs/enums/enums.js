/**
 * @fileoverview Test conversion of closure enums
 * @externs
 */

/**
 * @enum {string}
 */
var Foo = {FOO1: 'foo1', FOO2: 'foo2'};

/**
 * @enum {number}
 */
var Bar = {BAR1: 'bar1', BAR2: 'bar2'};

/**
 * @interface
 */
function Baz() {}

/**
 * @param {Foo} foo
 * @return {Bar}
 */
Baz.prototype.toBar = function(foo) {};
