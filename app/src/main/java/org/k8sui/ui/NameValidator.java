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

/**
 * A general set of rules applies to most resource names.
 * Length        : Typically, names must be no more than 253 characters long.
 * Characters    : Names generally consist of lowercase alphanumeric characters, hyphens (-), and dots (.).
 * Start and End : Names must begin and end with an alphanumeric character.
 */
public class NameValidator {

  /**
   * Pattern
   */
  private static final String PATTERN = "^[a-z0-9][a-z0-9-.]*[a-z0-9]$";

  /**
   * Constructor
   */
  private NameValidator() {
  }

  /**
   * Valid Name
   *
   * @param name Name
   * @return boolean
   */
  public static boolean validName(String name) {
    return name.length() <= 253 && name.matches(PATTERN);
  }
}
