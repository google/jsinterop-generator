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
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility class for reporting problems during code generation */
public class Problems {
  private static final Logger logger = Logger.getLogger(Problems.class.getName());

  private final boolean strict;
  private final List<String> warnings = new ArrayList<>();
  private final List<String> infos = new ArrayList<>();
  private final List<ErrorMessage> errors = new ArrayList<>();

  public Problems(boolean strict) {
    this.strict = strict;
  }

  public void warning(String msg, Object... args) {
    if (strict) {
      error(msg, args);
    } else {
      warnings.add(formatMessage(msg, args));
    }
  }

  public void info(String msg, Object... args) {
    infos.add(formatMessage(msg, args));
  }

  public void error(String msg, Throwable t, Object... args) {
    errors.add(new ErrorMessage(formatMessage(msg, args), t));
  }

  public void error(String msg, Object... args) {
    error(msg, null, args);
  }

  public void fatal(String msg, Throwable t, Object... args) {
    error(msg, t, args);
    report();
  }

  private static String formatMessage(String msg, Object... args) {
    if (args.length == 0) {
      return msg;
    }

    return Strings.lenientFormat(msg, args);
  }

  public void report() {
    for (String info : infos) {
      logger.info(info);
    }

    for (String warning : warnings) {
      logger.warning(warning);
    }

    for (ErrorMessage error : errors) {
      logger.log(Level.SEVERE, error.message, error.throwable);
    }

    logger.info(
        formatMessage(
            "%s notice(s), %s warning(s), %s error(s)",
            infos.size(), warnings.size(), errors.size()));

    if (!errors.isEmpty()) {
      throw new AbortError();
    }
  }

  private static class ErrorMessage {
    private final String message;
    private final Throwable throwable;

    private ErrorMessage(String message, Throwable throwable) {
      this.message = message;
      this.throwable = throwable;
    }
  }
}
