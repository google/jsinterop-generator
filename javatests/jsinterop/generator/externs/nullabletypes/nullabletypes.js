
// Copyright 2024 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview Test nested classes conversion.
 * @externs
 */

/**
 * @constructor
 */
function SimpleClass() {}

/**
 * @record
 */
function SimpleRecord() {};

/**
 * @interface
 */
function SimpleInterface() {}

/**
 * @enum {string}
 */
var SimpleEnum = {A: 'A', B: 'B'};

/**
 * @typedef {?function(string):boolean}
 */
var NullableFunctionType;

/**
 * @typedef {?string|number}
 */
var NullableUnionType;

/**
 * @typedef {null|string|number}
 */
var UnionTypeWithNull;

/**
 * @typedef {?{
 *      foo: number,
 *      bar: string
 *      }}
 */
var NullableTypeDefOfRecord;

/**
 * @constructor
 */
function ClassWithNullableRefs() {}

/**
@type {?SimpleClass}
 */
ClassWithNullableRefs.prototype.simpleClassRef;

/**
 * @type {?SimpleRecord}
 */
ClassWithNullableRefs.prototype.nullableRecordRef;

/**
 * @type {?SimpleInterface}
 */
ClassWithNullableRefs.prototype.nullableInterfaceRef;

/**
 * @type {?SimpleEnum}
 */
ClassWithNullableRefs.prototype.nullableEnumRef;

/**
 * @param {?SimpleInterface} foo
 * @return {?SimpleRecord}
 */
ClassWithNullableRefs.prototype.methodWithNullableParam = function(foo) {};

/**
 * @param {?Array<string>} foo
 */
ClassWithNullableRefs.prototype.methodWithNullableArrayParam = function(foo) {};


/**
 * @param {!Array<?string>} foo
 */
ClassWithNullableRefs.prototype.methodWithArrayOfNullableElementParam =
    function(foo) {};

/**
 * @param {?Array<?string>} foo
 */
ClassWithNullableRefs.prototype.methodWithNullableArrayOfNullableElementParam =
    function(foo) {};

/**
 * @type {?string|number}
 */
ClassWithNullableRefs.prototype.nullableAnonymousUnionTypeRef;

/**
 * @type {?(string|number)}
 */
ClassWithNullableRefs.prototype.nullableAnonymousUnionTypeRef2;

/**
 * @type {?function(string):boolean}
 */
ClassWithNullableRefs.prototype.nullableAnonymousFunctionTypeRef;

/**
 * @type {?{
 *      foo: number,
 *      bar: string
 *      }}
 */
ClassWithNullableRefs.prototype.nullableAnonymousRecordRef;

/**
 * @type {NullableUnionType}
 */
ClassWithNullableRefs.prototype.nullableUnionTypeRef;

/**
 * @type {NullableFunctionType}
 */
ClassWithNullableRefs.prototype.nullableFunctionTypeRef;

/**
 * @type {UnionTypeWithNull}
 */
ClassWithNullableRefs.prototype.unionTypeWithNullRef;

/**
 * @type {null|string|number}
 */
ClassWithNullableRefs.prototype.anonymousUnionTypeWithNull;

/**
 * @type {NullableTypeDefOfRecord}
 */
ClassWithNullableRefs.prototype.nullableTypeDefOfRecordRef;

/**
 * @type {?number}
 */
ClassWithNullableRefs.prototype.nullableNumber;

/**
 * @type {?string}
 */
ClassWithNullableRefs.prototype.nullableString;
