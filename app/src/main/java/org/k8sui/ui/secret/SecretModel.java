/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.secret;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.k8sui.model.Secret;
import org.k8sui.ui.BaseTableModel;

/**
 * Secret Table Model
 */
public class SecretModel extends BaseTableModel {

  private static final String[] headers = {"UID", "Name", "Namespace", "Created"};
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");
  private List<Secret> secrets;

  /**
   * Constructor
   * @param secrets Secrets List
   */
  public SecretModel(List<Secret> secrets) {
    super(headers);
    setSecrets(secrets);
  }

  public void setSecrets(List<Secret> secrets) {
    this.secrets = secrets;
    Collections.sort(secrets);
  }

  public Secret getSecret(int index) {
    return secrets.get(index);
  }

  @Override
  public int getRowCount() {
    return secrets.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return secrets.get(row).getUid();
    } else if (col == 1) {
      return secrets.get(row).getName();
    } else if (col == 2) {
      return secrets.get(row).getNameSpace();
    } else {
      if (secrets.get(row).getCreationDate() == null) {
        return "";
      }
      return formatter.format(secrets.get(row).getCreationDate());
    }
  }
}