// Copyright 2019 Google Inc.
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
 * @fileoverview Extern files used as dependency of our test in order to provide
 * java class for native types without polluting our test.
 * @externs
 */

/**
 * @constructor
 * @param {*=} args
 * @suppress {duplicate}
 */
function Object(args) {}

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
 * @param {...*} args
 * @suppress {duplicate}
 */
function Array(args) {}

/**
 * @interface
 * @template KEY1, VALUE1
 * @suppress {duplicate}
 */
function IObject() {}

/**
 * @record
 * @extends {IObject<number, VALUE2>}
 * @template VALUE2
 * @suppress {duplicate}
 */
function IArrayLike() {}