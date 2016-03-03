/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import java.util.Properties;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class softwareInfo {
    public static long getVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(softwareInfo.class.getResourceAsStream("project.properties"));
            String version = properties.getProperty("version");
            version = version.replaceAll("\\D+", "");
            long vers = Long.parseLong(version);
            return vers;
        } catch (Exception e) {
            return 0;
        }
    }
}
