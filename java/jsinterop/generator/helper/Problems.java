/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsinterop.generator.helper;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/** Utility class for reporting problems during code generation */
public class Problems {
  private static final Logger logger = Logger.getLogger(Problems.class.getName());

  private final boolean strict;
  private final List<String> warnings = new ArrayList<>();
  private final List<String> infos = new ArrayList<>();

  public Problems(boolean strict) {
    this.strict = strict;
  }

  public void reportWarning(String msg, Object... args) {
    warnings.add(formatMessage(msg, args));
  }

  public void reportInfo(String msg, Object... args) {
    infos.add(formatMessage(msg, args));
  }

  private static String formatMessage(String msg, Object... args) {
    if (args.length == 0) {
      return msg;
    }

    return Strings.lenientFormat(msg, args);
  }

  public void report() {
    if (!infos.isEmpty()) {
      logger.warning(formatMessage("%s warning(s):", infos.size()));
      for (String warning : infos) {
        logger.warning(warning);
      }
    }

    if (!warnings.isEmpty()) {
      logger.severe(formatMessage("%s errors(s):", warnings.size()));
      for (String error : warnings) {
        logger.severe(error);
      }
    }
  }

  public boolean hasErrors() {
    return strict && !warnings.isEmpty();
  }
}
