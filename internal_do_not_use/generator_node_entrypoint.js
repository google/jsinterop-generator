/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

const generator =
    require('jsinterop_generator_module/TypescriptJsInteropGenerator.js');

// arguments passed on command line. The two first item of process.argv are
// respectively the path to node and the path to this script.
const args = process.argv.slice(2);

// generator config
const config = {
  'copyright': args[0],
  'output': args[1],
  'packagePrefix': args[2],
  'debugEnabled': __to_boolean(args[3]),
  'withTypescriptLib': __to_boolean(args[4]),
  'useBeanConvention': __to_boolean(args[5]),
  'depsTypesMapping': __to_array(args[6]),
  'integerEntities': __to_array(args[7]),
};

// arrays containing paths to d.ts files.
const dts_files = __to_array(args[8]);

generator.runGenerator(dts_files, config);

/**
 * Convert comma separated string argument passed to this script to javascript
 * array.
 * @param {string} str
 * @return {Array<string>}
 */
function __to_array(str) {
  if (!str || str === '\'\'') {
    return [];
  }
  return str.split(',');
}

/**
 * Convert boolean string argument passed to this script to javascript boolean
 * @param {string} str
 * @return {boolean}
 */
function __to_boolean(str) {
  return str === 'true';
}
