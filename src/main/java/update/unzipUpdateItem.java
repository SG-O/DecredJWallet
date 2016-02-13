/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class unzipUpdateItem extends updateItem {
    private File sourceDir;
    private String relativeSourceDir;
    private String destDir;

    public unzipUpdateItem(String ID, File sourceDir, String relativeSourceDir, String destDir) {
        super(ID, updateConstants.UNZIP);
        this.sourceDir = sourceDir;
        this.relativeSourceDir = relativeSourceDir;
        this.destDir = destDir;
    }

    @Override
    public boolean execute() {
        Path source = Paths.get(sourceDir.getAbsolutePath(),relativeSourceDir, super.getID());
        if(!source.toFile().exists()) return false;
        byte[] buffer = new byte[1024];

        try{

            //create output directory is not exists
            File folder = Paths.get(source.getParent().toString(),destDir).toFile();
            if(!folder.exists()){
                folder.mkdirs();
            }
            System.out.println(source);

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(folder + File.separator + fileName);

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                if(ze.isDirectory()){
                    newFile.mkdir();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            return true;

        }catch(Exception e)
        {
            return false;
        }


    }
}
