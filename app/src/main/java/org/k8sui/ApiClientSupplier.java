/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import java.io.IOException;

/**
 * API Client Supplier
 */
public class ApiClientSupplier {

  private static final ApiClient client;

  static {
    try {
      client = Config.defaultClient();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ApiClientSupplier() {
  }

  public static ApiClient client() {
    return client;
  }
}
