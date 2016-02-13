/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class executeUpdateItem extends updateItem {

    public executeUpdateItem(String ID) {
        super(ID, updateConstants.EXECUTE);
    }

    @Override
    public boolean execute() {
        Path dest = Paths.get(super.getID());
        if(dest.toFile().isAbsolute()) return false;
        if (!dest.toFile().isFile()) return false;
        if (!dest.toFile().canRead()) return false;
        try {
            Process runt = Runtime.getRuntime().exec("java -jar " + dest.toString());
        } catch (IOException e) {
            return false;
        }
        System.exit(0);
        return true;
    }
}
