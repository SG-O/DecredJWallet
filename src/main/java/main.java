/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.apache.commons.lang3.SystemUtils;
import update.update;

import java.io.File;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 28.01.2016.
 */

public class main {
    /*
        The main method is where the the main wallet implementation starts at.
    */

    public static void main(String[] args) {
        StartUp startScreen = new StartUp();
        startScreen.setStatus("Loading settings");
        File Updater = new File("DcrdUpdater.jar"); //Is there an old updater present? If yes, delete it.
        if(Updater.exists()){
            Updater.delete();
        }
        settings set = new settings(); //Load the users current settings.
        if (set.isFirstrun()) {
            new settingsUi(set); //If this is the first start of this wallet, show the Settings Dialog and let the user change his settings.
        }
        if (set.isDoAutoUpdate()){ //If the user wants this, check for new updates, inform him about any and if wanted update.
            startScreen.setStatus("Checking for updates");
            if (update.checkUpdates()){
                UpdateAvailable uA = new UpdateAvailable("A new Update is available! Do you want to update now?");
                if (uA.getResult()){
                    if(!update.doUpdate()){
                        new Error("Error", "Could not Update! Please try again.");
                    }
                }
            }
        }
        if (SystemUtils.IS_OS_WINDOWS){ //If the user is running windows and does not already have the Decred binaries installed, offer the user the option to download them.
            if ((!(new File("dcrd.exe")).exists())||(!(new File("dcrwallet.exe")).exists())){
                UpdateAvailable uA = new UpdateAvailable("Decred is not installed do you want to download it now?");
                if (uA.getResult()){
                    if(!update.getTools()){
                        new Error("Error", "Could not Update! Please try again.");
                    }
                }
            }
        }
        Decred binaries = new Decred(set.getRPCUser(), set.getRPCPass(), set.isRPCtls(), set.isTestnet());
        try {
            if (set.isDoAutoUpdate()) {
                if (SystemUtils.IS_OS_WINDOWS) { //Check if the user wants this if he has the latest Decred binaries and download them if not.
                    long uT = update.checkTools();
                    if (((long) binaries.checkVersion()) < uT) {
                        UpdateAvailable uA = new UpdateAvailable("Decred binaries are out of date! Update now?");
                        if (uA.getResult()) {
                            if (!update.getTools()) {
                                new Error("Error", "Could not Update! Please try again.");
                            } else {
                                set.setToolsVersion(uT);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        startScreen.setStatus("Starting Decred");
        try {
            if (!binaries.startDecred()) {
                new Error("Could not start decred", "Could not start the decred executable. Maybe another instance is already running. Continuing startup.");
            }
            startScreen.setProgress(0.25f);
            startScreen.setStatus("Starting Wallet");
            boolean walletStartup = binaries.startWallet();
            if ((!walletStartup) && (!binaries.isEncrypted())) {
                new Error("Could not start wallet", "Could not start the wallet executable. Maybe another instance is already running. Continuing startup.");
            }
            if ((!walletStartup) && (binaries.isEncrypted())) {
                UnlockPublic decrypt = new UnlockPublic(binaries);
                if (!decrypt.isSuccess()) {
                    binaries.terminate();
                    System.exit(0);
                }
            }
        } catch (Exception e){
            new Error("Error", "Decred executables not found!");
            binaries.terminate();
            startScreen.dispose();
            System.exit(0);
        }
        int i = 0;

        set.connect(); //Now we try to connect to the freshly started backend.
        startScreen.setProgress(0.5f);
        startScreen.setStatus("Connecting to backend");
        while (!settings.getBackend().checkConnection()){
            try {
                i++;
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (i > 30){
                new Error("Error", "Could not start Decred!"); //If we could not connect in time exit with an error message
                startScreen.dispose();
                new settingsUi(set);
                binaries.terminate();
                System.exit(0);
            }
            set.connect();
        }
        startScreen.setProgress(0.75f);
        startScreen.setStatus("Waiting for wallet to finish loading");
        while (true) { //Wait for the wallet to load.
            try {
                settings.getBackend().getBalance();
                break;
            } catch (status ignored) {
            }
            try {
                i++;
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (i > 100){
                new Error("Error", "Could not start Decred!");
                new settingsUi(set);
                startScreen.dispose();
                binaries.terminate();
                System.exit(0);
            }
        }
        startScreen.dispose(); //Get rid of the loading screen and show the main window

        Overview window = new Overview(set);
        window.setVisible(true);

        boolean catchingUp = false;

        while (window.isVisible()){ //In the future there might be some background tasks we want to perform here but for the moment, we do nothing.
            try {
                i++;
                long nowBlock;
                long maxBlock;
                int sleeptime = 30000;
                if (binaries.parseDecred()) {
                    maxBlock = binaries.getMaxBlock();
                    nowBlock = settings.getBackend().getBlockCount();
                    System.out.println("Catching up: Block " + nowBlock + " of " + maxBlock);
                    if (maxBlock > nowBlock + 10) {
                        catchingUp = true;
                    }
                }
                if (catchingUp) {
                    sleeptime = 100;
                    maxBlock = binaries.getMaxBlock();
                    nowBlock = settings.getBackend().getBlockCount();
                    window.setStatus("Catching up: Block " + nowBlock + " of " + maxBlock);
                    if (maxBlock == nowBlock) {
                        catchingUp = false;
                        window.resetStatus();
                        System.out.println("Caught up");
                    }
                }
                Thread.sleep(sleeptime);
                window.refresh();
            } catch (InterruptedException ignored) {
            } catch (status status) {
            }
        }
        binaries.terminate();
    }
}
