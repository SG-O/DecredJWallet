/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 05.02.2016.
 */
public class downloadUpdateItem extends updateItem {
    private String url;
    private String hash;
    private File tempdir;


    public downloadUpdateItem(String ID, String url, File tempdir, String hash) {
        super(ID, updateConstants.DOWNLOAD);
        this.url = url;
        this.tempdir = tempdir;
        this.hash = hash;
    }

    public static String hash(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            BASE64Encoder encoder = new BASE64Encoder();
            System.out.println(encoder.encode(mdbytes));
            fis.close();
            return encoder.encode(mdbytes);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean execute() {
        if (tempdir == null) return false;
        if(!tempdir.exists()) return false;
        if (url == null) return false;
        try {
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(new File(tempdir, super.getID()));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean check() { //Check if the downloaded file is the file that has been specified in the update commands
        String hashResult = hash(new File(tempdir, super.getID()));
        if (hashResult == null) return false;
        if (!hashResult.equals(hash)) return false;
        return super.check();
    }
}
