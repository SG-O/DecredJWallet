package internal;

import java.io.File;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 11.03.2016.
 */
public class storageTools {
    private static File setDir;

    public static File getSettingsDirectory() {
        if (setDir != null) return setDir;
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            throw new IllegalStateException("user.home==null");
        }
        File home = new File(userHome);
        File settingsDirectory = new File(home, ".decredjwallet");
        if (!settingsDirectory.exists()) {
            if (!settingsDirectory.mkdir()) {
                throw new IllegalStateException(settingsDirectory.toString());
            }
        }
        setDir = settingsDirectory;
        return settingsDirectory;
    }
}
