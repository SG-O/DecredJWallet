/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class updateTest {

    @Test
    public void testCheckUpdates() throws Exception {
        assertFalse(update.checkUpdates(9999999));
    }

    @Test
    public void testDoUpdate() throws Exception {
        assertTrue(update.doUpdate(update.DCRDWIN32_URL));
    }

    @Test
    public void testCeckSignature() throws Exception {
        assertTrue(update.checkSignature("Test", "Jxx69MVpEyCbn7c0IRmJeqTkaw9miaFT94dTOKdbhdHLsi3tvHndLR6sEwNm/mdNILPD+MdyaVF8y7DkPcWPzA2k1vvP/u/aZArHyL0PtUnZGsO0z/gYD9egIfOy4yv7jYbf9/SSbefrAqgmB4sCU+CsUtTK6mNnFXrx4FWBXi3yIQmjiKxHpp2I5+AN51RFKNbLuN4r6u1fzkK5yPZb4CZqLDiJnoZq5/VVqLMjya46FaRGEJE1pqwI7zEZTL2UNMlUnJUn0g4YE0607yh8RI3GHzFbFOdiyI7OXX3V4eM/yudKAuvVcC27/YuheYWdeaVOaxKyD3qXLJLSNFYdAw=="));
    }

    @Test
    public void buildToolsUpdate() throws Exception {
        String type = "w32";
        String URL;
        String Relative;
        if (type.equals("w64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.5/windows-amd64-20160226-01.zip";
            Relative = "windows-amd64";
        } else if (type.equals("w32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.5/windows-386-20160226-01.zip";
            Relative = "windows-386";
        } else return;
        File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime()));
        if(!(temp.delete()))
        {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }

        if(!(temp.mkdir()))
        {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }
        downloadUpdateItem tmpDow = new downloadUpdateItem("generatedown.tmp",URL, temp,"");
        System.out.println("Downloading...");
        tmpDow.execute();
        System.out.println("Hashing");
        String hashResult = downloadUpdateItem.hash(new File(temp, "generatedown.tmp"));
        temp.delete();
        String message = "[{\"ID\":\"dcrd.zip\",\"type\":1,\"url\":\""+ URL +"\",\"hash\":\"" + hashResult + "\"},{\"ID\":\"dcrd.zip\",\"type\":3},{\"relativeSource\":\"\",\"ID\":\""+Relative+"\",\"type\":2}]";
        String sig = update.signString(message, "D:\\key.der");
        JSONArray tmpMes = new JSONArray(message);
        JSONObject obj = new JSONObject();
        obj.put("Commands", tmpMes);
        obj.put("Signature", sig);
        System.out.println("Output:");
        System.out.println(obj);
        StringSelection selection = new StringSelection(obj.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        assertTrue(update.checkSignature(message,sig));
    }

    @Test
    public void buildMainUpdate() throws Exception {
        String URL = "http://sg-o.de/upd/V6/DecredJWallet.jar";
        String Updater = "https://github.com/SG-O/DcrdJGUIUpdater/releases/download/0.1/DcrdUpdater.jar";
        String RelativeURL = "DecredJWallet.jar.update";
        String RelativeUpdater = "DcrdUpdater.jar";
        File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime()));
        if(!(temp.delete()))
        {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }

        if(!(temp.mkdir()))
        {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }
        downloadUpdateItem tmpDow = new downloadUpdateItem("generatedown1.tmp",URL, temp,"");
        System.out.println("Downloading1...");
        tmpDow.execute();
        tmpDow = new downloadUpdateItem("generatedown2.tmp",Updater, temp,"");
        System.out.println("Downloading2...");
        tmpDow.execute();
        System.out.println("Hashing");
        String hashResult1 = downloadUpdateItem.hash(new File(temp, "generatedown1.tmp"));
        String hashResult2 = downloadUpdateItem.hash(new File(temp, "generatedown2.tmp"));
        temp.delete();
        String message = "[{\"ID\":\""+RelativeURL+"\",\"type\":1,\"url\":\""+ URL +"\",\"hash\":\"" + hashResult1 + "\"},{\"ID\":\""+RelativeUpdater+"\",\"type\":1,\"url\":\"" + Updater +"\",\"hash\":\""+hashResult2+"\"},{\"relativeSource\":\"\",\"ID\":\""+RelativeURL+"\",\"relativeDest\":\""+RelativeURL+"\",\"type\":2},{\"relativeSource\":\"\",\"ID\":\""+RelativeUpdater+"\",\"relativeDest\":\""+RelativeUpdater+"\",\"type\":2},{\"ID\":\""+RelativeUpdater+"\",\"type\":4}]";
        String sig = update.signString(message, "D:\\key.der");
        JSONArray tmpMes = new JSONArray(message);
        JSONObject obj = new JSONObject();
        obj.put("Commands", tmpMes);
        obj.put("Signature", sig);
        System.out.println("Output:");
        System.out.println(obj);
        StringSelection selection = new StringSelection(obj.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        assertTrue(update.checkSignature(message,sig));
    }

    @Test
    public void testHashString() throws Exception {
        System.out.println(downloadUpdateItem.hash(new File("D:\\Downloads\\windows-386-20160208-01.zip")));
        assertTrue(true);
    }
}