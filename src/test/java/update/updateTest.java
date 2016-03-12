/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 06.02.2016.
 */
public class updateTest {

    public static void buildOtherToolsUpdate(long version, String type) throws Exception {
        String URL;
        String Relative;
        if (type.equals("l64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/linux-amd64-20160309-01.tar.gz";
            Relative = "linux-amd64";
        } else if (type.equals("l32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/linux-386-20160309-01.tar.gz";
            Relative = "linux-386";
        } else if (type.equals("larm")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/linux-arm-20160309-01.tar.gz";
            Relative = "linux-arm";
        } else if (type.equals("d64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/darwin-amd64-20160309-01.tar.gz";
            Relative = "darwin-amd64";
        } else if (type.equals("d32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/darwin-386-20160309-01.tar.gz";
            Relative = "darwin-386";
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
        FileUtils.deleteDirectory(temp);
        String message = "[{\"ID\":\"dcrd.tar.gz\",\"type\":" + updateConstants.DOWNLOAD + ",\"url\":\"" + URL + "\",\"hash\":\"" + hashResult + "\"},{\"ID\":\"dcrd.tar.gz\",\"type\":" + updateConstants.UNTARGZ + "},{\"relativeSource\":\"\",\"ID\":\"" + Relative + "\",\"type\":" + updateConstants.MOVE + "}]";
        String sig = update.signString(message, "D:\\key.der");
        JSONArray tmpMes = new JSONArray(message);
        JSONObject obj = new JSONObject();
        obj.put("Commands", tmpMes);
        obj.put("Signature", sig);
        writeFile(type, version, obj);
    }

    public static void buildWindowsToolsUpdate(long version, String type) throws Exception {
        String URL;
        String Relative;
        if (type.equals("w64")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/windows-amd64-20160309-01.zip";
            Relative = "windows-amd64";
        } else if (type.equals("w32")) {
            URL = "https://github.com/decred/decred-release/releases/download/v0.0.7/windows-386-20160309-01.zip";
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
        FileUtils.deleteDirectory(temp);
        String message = "[{\"ID\":\"dcrd.zip\",\"type\":1,\"url\":\"" + URL + "\",\"hash\":\"" + hashResult + "\"},{\"ID\":\"dcrd.zip\",\"type\":3},{\"relativeSource\":\"\",\"ID\":\"" + Relative + "\",\"type\":2}]";
        String sig = update.signString(message, "D:\\key.der");
        JSONArray tmpMes = new JSONArray(message);
        JSONObject obj = new JSONObject();
        obj.put("Commands", tmpMes);
        obj.put("Signature", sig);
        System.out.println("Output:");
        System.out.println(obj);
        writeFile(type, version, obj);
    }

    public static void writeFile(String type, long version, JSONObject obj) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("Output:");
        String fileName = "";
        String folderName = "";
        if (type.startsWith("l")) {
            folderName = "linux";
        }
        if (type.startsWith("d")) {
            folderName = "darwin";
        }
        if (type.startsWith("w")) {
            folderName = "win";
        }
        if (type.endsWith("32")) {
            fileName = "32decrd" + version + ".json";
        }
        if (type.endsWith("64")) {
            fileName = "64decrd" + version + ".json";
        }
        if (type.endsWith("arm")) {
            fileName = "armdecrd" + version + ".json";
        }
        File updateFile = new File(internal.storageTools.getSettingsDirectory(), folderName + File.separator + fileName);
        updateFile.getParentFile().mkdir();
        PrintWriter writer = new PrintWriter(updateFile, "UTF-8");
        writer.print(obj.toString());
        writer.close();
        File updateInfoFile = new File(internal.storageTools.getSettingsDirectory(), "dcrdjguitools" + type);
        writer = new PrintWriter(updateInfoFile, "UTF-8");
        writer.print("{\"Server\":\"http://sg-o.de/upd/" + folderName + "/" + fileName + "\",\"Version\":" + version + ",\"MinimalSoftware\":7}");
        writer.close();
        System.out.println(updateFile.toString());
        System.out.println(obj);
    }

    @Test
    public void buildToolUpdates() throws Exception {
        buildOtherToolsUpdate(7, "l32");
        buildOtherToolsUpdate(7, "l64");
        buildOtherToolsUpdate(7, "larm");
        buildOtherToolsUpdate(7, "d32");
        buildOtherToolsUpdate(7, "d64");
        buildWindowsToolsUpdate(7, "w32");
        buildWindowsToolsUpdate(7, "w64");
    }

    @Test
    public void buildMainUpdate() throws Exception {
        String URL = "http://sg-o.de/upd/V8/DecredJWallet.jar";
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
        FileUtils.deleteDirectory(temp);
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