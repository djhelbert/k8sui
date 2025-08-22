/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui;

public class KeyValidator {

  /**
   * Key Pattern
   */
  private static final String KEY_PATTERN = "^[a-zA-Z0-9-._]+$";

  /**
   * Private Constructor
   */
  private KeyValidator() {
  }

  /**
   * Valid Name
   * @param name Name
   * @return boolean
   */
  public static boolean validName(String name) {
    return name.length() <= 253 && name.matches(KEY_PATTERN);
  }
}
