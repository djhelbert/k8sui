package org.k8sui.ui.secret;

import org.k8sui.model.Secret;
import org.k8sui.ui.BaseTableModel;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Secret Table Model
 */
public class SecretModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Created"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<Secret> secrets;

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