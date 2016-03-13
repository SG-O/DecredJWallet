/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import java.io.File;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 28.01.2016.
 */

public class main {
    /*
        The main method is where the the main wallet implementation starts at.
    */

    public static void main(String[] args) {
        String[] javaVersion = System.getProperty("java.version").split("\\.");
        long javaVersionNumber = 0;
        if (javaVersion.length >= 2) {
            javaVersionNumber = Long.parseLong(javaVersion[0]) * 1000000;
            javaVersionNumber += Long.parseLong(javaVersion[1]) * 1000;
            System.out.println(javaVersionNumber);
        }

        if (javaVersionNumber < 1008000) {
            new Error("Could not start", "Your Java version " + javaVersion + " is too old! The minimum required version is 1.8");
        }

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
            updateWrapper upd = new updateWrapper(updateWrapper.MAIN_UPDATE, set);
            if (upd.updateAvailable(softwareInfo.getVersion())) {
                UpdateAvailable uA = new UpdateAvailable("A new Update is available! Do you want to update now?");
                if (uA.getResult()){
                    if (!upd.run()) {
                        new Error("Error", "Could not Update! Please try again.");
                    }
                }
            }
        }
        final Decred binaries = new Decred(set.getRPCUser(), set.getRPCPass(), set.isRPCtls(), set.isTestnet());
        if (updateWrapper.updatableOS(updateWrapper.TOOLS_UPDATE, set)) { //If the user is running an updatable os and does not already have the Decred binaries installed, offer the user the option to download them.
            if (!binaries.checkFiles()) {
                UpdateAvailable uA = new UpdateAvailable("Decred is not installed do you want to download it now?");
                if (uA.getResult()){
                    updateWrapper upd = new updateWrapper(updateWrapper.TOOLS_UPDATE, set);
                    if (!upd.run()) {
                        new Error("Error", "Could not Update! Please try again.");
                    }
                }
            }
        }
        try {
            if (set.isDoAutoUpdate()) {
                if (updateWrapper.updatableOS(updateWrapper.TOOLS_UPDATE, set)) { //Check if the user wants this,if he has the latest Decred binaries and download them if not.
                    updateWrapper upd = new updateWrapper(updateWrapper.TOOLS_UPDATE, set);
                    if (upd.updateAvailable((long) binaries.checkVersion())) {
                        UpdateAvailable uA = new UpdateAvailable("Decred binaries are out of date! Update now?");
                        if (uA.getResult()) {
                            if (!upd.run()) {
                                new Error("Error", "Could not Update! Please try again.");
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
            System.out.println(e);
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
                new Error("Error", "Could not connect to dcrwallet!"); //If we could not connect in time exit with an error message
                startScreen.dispose();
                new settingsUi(set);
                System.out.println(binaries.getDcrdContent() + "\n" + binaries.getWalletContent());
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
                new Error("Error", "Could not load wallet data!");
                new settingsUi(set);
                startScreen.dispose();
                System.out.println(binaries.getDcrdContent() + "\n" + binaries.getWalletContent());
                binaries.terminate();
                System.exit(0);
            }
        }
        startScreen.dispose(); //Get rid of the loading screen and show the main window

        final Overview window = new Overview(set, binaries);
        window.setVisible(true);
        final boolean[] catchingUp = {false};
        Thread updater = new Thread() {
            public void run() {

                while (binaries.allRunning()) {
                    try {
                        long nowBlock;
                        long maxBlock;
                        long walletBlock;
                        int sleeptime = 30000;
                        if (!catchingUp[0]) {
                            if (binaries.parseDecred()) {
                                maxBlock = binaries.getMaxBlock();
                                nowBlock = settings.getBackend().getBlockCount();
                                walletBlock = settings.getBackend().getWalletBlockCount();
                                System.out.println("Catching up: Block " + nowBlock + " of " + maxBlock);
                                if (maxBlock > nowBlock + 10) {
                                    catchingUp[0] = true;
                                }
                                if (nowBlock > walletBlock + 10) {
                                    catchingUp[0] = true;
                                }
                            }
                        }
                        if (catchingUp[0]) {
                            sleeptime = 200;
                            maxBlock = binaries.getMaxBlock();
                            nowBlock = settings.getBackend().getBlockCount();
                            walletBlock = settings.getBackend().getWalletBlockCount();
                            window.setStatus("Catching up: Block " + nowBlock + " of " + maxBlock + " (Wallet at " + walletBlock + ")");
                            if ((maxBlock <= nowBlock) && (nowBlock <= walletBlock + 10)) {
                                catchingUp[0] = false;
                                window.resetStatus();
                                System.out.println("Caught up");
                            }
                        }
                        if (!catchingUp[0]) window.refresh();
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException ignored) {
                    } catch (status status) {
                    }
                }
            }
        };

        updater.start();
        while (window.isVisible()){ //In the future there might be some background tasks we want to perform here but for the moment, we do nothing.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        binaries.terminate();
        while (binaries.decredRunning() || binaries.walletRunning()) {
        }
        System.exit(0);
    }
}
