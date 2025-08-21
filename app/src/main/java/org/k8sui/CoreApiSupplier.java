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

import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * Core API Supplier
 */
public class CoreApiSupplier {

  private static final CoreV1Api api = new CoreV1Api(ApiClientSupplier.client());
  private static final AppsV1Api apps = new AppsV1Api(ApiClientSupplier.client());

  private CoreApiSupplier() {
  }

  public static CoreV1Api api() {
    return api;
  }

  public static AppsV1Api app() {
    return apps;
  }
}
