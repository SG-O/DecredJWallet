/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.apache.commons.lang3.SystemUtils;
import org.codehaus.groovy.ast.expr.FieldExpression;
import update.*;

import java.io.File;
import java.nio.file.Files;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 28.01.2016.
 */

public class main {
    static Process dcrd, wallet;
    /*
        The main method is where the the main wallet implementation starts at.
    */
    public static void main(String[] args) {
        StartUp startScreen = new StartUp();
        File Updater = new File("DcrdUpdater.jar"); //Is there an old updater present? If yes, delete it.
        if(Updater.exists()){
            Updater.delete();
        }
        settings set = new settings(); //Load the users current settings.
        if (set.isFirstrun()) {
            new settingsUi(set); //If this is the first start of this wallet, show the Settings Dialog and let the user change his settings.
        }
        if (set.isDoAutoUpdate()){ //If the user wants this, check for new updates, inform him about any and if wanted update.
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

        if (set.isDoAutoUpdate()){ //TODO: Make this check the real version of the installed Binaries and get rid of this temporary implementation.
            if (SystemUtils.IS_OS_WINDOWS){ //Check if the user wants this if he has the latest Decred binaries and download them if not.
                long uT = update.checkTools(set.getToolsVersion());
                if (set.getToolsVersion()<uT){
                    UpdateAvailable uA = new UpdateAvailable("Decred binaries are out of date! Update now?");
                    if (uA.getResult()){
                        if(!update.getTools()){
                            new Error("Error", "Could not Update! Please try again.");
                        } else {
                            set.setToolsVersion(uT);
                        }
                    }
                }
            }
        }

        //TODO: Check if a wallet has been created and if not, create a new one.

        Runtime runtime = Runtime.getRuntime(); //Now start the back end with the right options
        String optString = "";
        String optStringWallet = "";
        if (!set.isRPCtls()) {
            optString += "--notls ";
            optStringWallet += "--noclienttls --noservertls ";
        }
        if (set.isTestnet()){
            optString += "--testnet ";
            optStringWallet += "--testnet ";
        }
        try {
            dcrd = runtime.exec("dcrd " + optString + "-u " + set.getRPCUser() + " -P " + set.getRPCPass());
            wallet = runtime.exec("dcrwallet " + optStringWallet + "-u " + set.getRPCUser() + " -P " + set.getRPCPass());
        } catch (Exception e){
            new Error("Error", "Decred executables not found!");
            startScreen.dispose();
            System.exit(0);
        }
        int i = 0;

        set.connect(); //Now we try to connect to the freshly started backend.
        startScreen.setProgress(0.33f);
        while (!settings.getBackend().checkConnection()){
            try {
                i++;
                Thread.sleep(100);
            } catch (InterruptedException e2) {
            }
            if (i > 30){
                new Error("Error", "Could not start Decred!"); //If we could not connect in time exit with an error message
                set.setToolsVersion(0);
                startScreen.dispose();
                new settingsUi(set);
                System.exit(0);
            }
            set.connect();
        }
        startScreen.setProgress(0.66f);
        while (settings.getBackend().getBalance() < 0){ //Wait for the wallet to load.
            try {
                i++;
                Thread.sleep(100);
            } catch (InterruptedException e2) {
            }
            if (i > 100){
                new Error("Error", "Could not start Decred!");
                new settingsUi(set);
                startScreen.dispose();
                System.exit(0);
            }
        }
        startScreen.dispose(); //Get rid of the loading screen and show the main window

        MainWindow window = new MainWindow(set);
        window.setVisible(true);
        ((transactionTableModel)window.tableModel).changeData(settings.getBackend().listTransactions(set.getTransactionsToLoad()));

        while (window.isVisible()){ //In the future there might be some background tasks we want to perform here but for the moment, we do nothing.
            try {
                i++;
                Thread.sleep(100);
            } catch (InterruptedException e2) {
            }
        }
        dcrd.destroy(); //End everything.
        wallet.destroy();
    }
}
