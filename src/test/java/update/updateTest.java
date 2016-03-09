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

import static org.junit.Assert.assertTrue;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class updateTest {

    @Test
    public void buildOtherToolsUpdate() throws Exception {
        String type = "larm";
        String URL;
        String Relative;
        if (type.equals("l64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.6/linux-amd64-20160304-01.tar.gz";
            Relative = "linux-amd64";
        } else if (type.equals("l32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.6/linux-386-20160304-01.tar.gz";
            Relative = "linux-386";
        } else if (type.equals("larm")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.6/linux-arm-20160304-01.tar.gz";
            Relative = "linux-arm";
        } else return;
        File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }

        if (!(temp.mkdir())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }
        downloadUpdateItem tmpDow = new downloadUpdateItem("generatedown.tmp", URL, temp, "");
        System.out.println("Downloading...");
        tmpDow.execute();
        System.out.println("Hashing");
        String hashResult = downloadUpdateItem.hash(new File(temp, "generatedown.tmp"));
        temp.delete();
        String message = "[{\"ID\":\"dcrd.tar.gz\",\"type\":" + updateConstants.DOWNLOAD + ",\"url\":\"" + URL + "\",\"hash\":\"" + hashResult + "\"},{\"ID\":\"dcrd.tar.gz\",\"type\":" + updateConstants.UNTARGZ + "},{\"relativeSource\":\"\",\"ID\":\"" + Relative + "\",\"type\":" + updateConstants.MOVE + "}]";
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
    }

    @Test
    public void buildWindowsToolsUpdate() throws Exception {
        String type = "w64";
        String URL;
        String Relative;
        if (type.equals("w64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.6/windows-amd64-20160304-01.zip";
            Relative = "windows-amd64";
        } else if (type.equals("w32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.6/windows-386-20160304-01.zip";
            Relative = "windows-386";
        } else return;
        File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }

        if (!(temp.mkdir())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }
        downloadUpdateItem tmpDow = new downloadUpdateItem("generatedown.tmp", URL, temp, "");
        System.out.println("Downloading...");
        tmpDow.execute();
        System.out.println("Hashing");
        String hashResult = downloadUpdateItem.hash(new File(temp, "generatedown.tmp"));
        temp.delete();
        String message = "[{\"ID\":\"dcrd.zip\",\"type\":1,\"url\":\"" + URL + "\",\"hash\":\"" + hashResult + "\"},{\"ID\":\"dcrd.zip\",\"type\":3},{\"relativeSource\":\"\",\"ID\":\"" + Relative + "\",\"type\":2}]";
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
    }

    @Test
    public void buildMainUpdate() throws Exception {
        String URL = "http://sg-o.de/upd/V7/DecredJWallet.jar";
        String Updater = "https://github.com/SG-O/DcrdJGUIUpdater/releases/download/0.1/DcrdUpdater.jar";
        String RelativeURL = "DecredJWallet.jar.update";
        String RelativeUpdater = "DcrdUpdater.jar";
        File temp = File.createTempFile("dcrd", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }

        if (!(temp.mkdir())) {
            System.out.println("Could not create temp dir");
            assertTrue(false);
            return;
        }
        downloadUpdateItem tmpDow = new downloadUpdateItem("generatedown1.tmp", URL, temp, "");
        System.out.println("Downloading1...");
        tmpDow.execute();
        tmpDow = new downloadUpdateItem("generatedown2.tmp", Updater, temp, "");
        System.out.println("Downloading2...");
        tmpDow.execute();
        System.out.println("Hashing");
        String hashResult1 = downloadUpdateItem.hash(new File(temp, "generatedown1.tmp"));
        String hashResult2 = downloadUpdateItem.hash(new File(temp, "generatedown2.tmp"));
        temp.delete();
        String message = "[{\"ID\":\"" + RelativeURL + "\",\"type\":1,\"url\":\"" + URL + "\",\"hash\":\"" + hashResult1 + "\"},{\"ID\":\"" + RelativeUpdater + "\",\"type\":1,\"url\":\"" + Updater + "\",\"hash\":\"" + hashResult2 + "\"},{\"relativeSource\":\"\",\"ID\":\"" + RelativeURL + "\",\"relativeDest\":\"" + RelativeURL + "\",\"type\":2},{\"relativeSource\":\"\",\"ID\":\"" + RelativeUpdater + "\",\"relativeDest\":\"" + RelativeUpdater + "\",\"type\":2},{\"ID\":\"" + RelativeUpdater + "\",\"type\":4}]";
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
    }

    @Test
    public void testHashString() throws Exception {
        System.out.println(downloadUpdateItem.hash(new File("D:\\Downloads\\windows-386-20160208-01.zip")));
        assertTrue(true);
    }
}