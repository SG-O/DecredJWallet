/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;


/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class moveUpdateItem extends updateItem{
    private File sourceDir;
    private String relativeSourceDir;
    private String destDir;

    public moveUpdateItem(String ID, File sourceDir, String relativeSourceDir, String destDir) {
        super(ID, updateConstants.MOVE);
        this.sourceDir = sourceDir;
        this.relativeSourceDir = relativeSourceDir;
        this.destDir = destDir;
    }

    @Override
    public boolean execute() {
        Path source = Paths.get(sourceDir.getAbsolutePath(),relativeSourceDir, super.getID());
        Path dest = Paths.get(System.getProperty("user.dir"), destDir);
        System.out.println(source);
        System.out.println(dest.getParent());

        if (!source.toFile().exists()) {
            System.err.println("NO file to move!");
            return false;
        }
        if (!dest.getParent().toFile().exists()) {
            if(!dest.getParent().toFile().mkdirs()) {
                System.err.println("NO dest directory");
                return false;
            }
        }

        if (source.toFile().isDirectory()){
            try {
                FileUtils.copyDirectory(source.toFile(),dest.toFile(),true);
                return true;
            } catch (IOException e) {
                System.err.println(e);
                return false;
            }
        }
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e);
            return false;
        }
        return true;
    }
}
