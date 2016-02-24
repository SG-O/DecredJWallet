import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 16.02.2016.
 */
public class Decred {
    private static final String ENCRYPTED_WALLET = "invalid passphrase for master public key";
    private static final String WALLET_MISSING = "The wallet does not exist";
    private static final String WALLET_NOT_STARTED = "Can't listen on";
    private static final String WALLET_STARTED = "RPC server listening";
    private static final String WALLET_WRONG_PASSWORD = "invalid passphrase for master public key";

    private static final String DECRED_STARTED = "RPC server listening on";
    private static final String DECRED_NOT_STARTED = "Can't listen on";
    private static final String DECRED_SYNCING = "Syncing to block height";

    private String tlsOptionsDecred = "";
    private String tlsOptionsWallet0 = "";
    private String tlsOptionsWallet1 = "";
    private String netOtionsDecred = "";
    private String netOptionsWallet = "";
    private String username;
    private String password;
    private boolean encrypted = false;
    private BufferedReader dcrdReader, walletReader;
    private Process dcrd, wallet;
    private Queue<String> secondBuffer = new LinkedList<String>();

    private long maxBlock = -1;

    public Decred(String username, String password, boolean tls, boolean testNet) {
        this.username = username;
        this.password = password;
        if (!tls) {
            tlsOptionsDecred = "--notls";
            tlsOptionsWallet0 = "--noclienttls";
            tlsOptionsWallet1 = "--noservertls";
        }
        if (testNet) {
            netOtionsDecred += "--testnet";
            netOptionsWallet += "--testnet";
        }
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public long getMaxBlock() {
        return maxBlock;
    }

    public int checkVersion() throws Exception {
        ProcessBuilder dcrdBuilder = new ProcessBuilder("dcrd", "-V");
        dcrdBuilder.redirectErrorStream(true);
        dcrd = dcrdBuilder.start();
        dcrdReader = new BufferedReader(new InputStreamReader(dcrd.getInputStream()));
        String read = dcrdReader.readLine();
        while (read == null) {
            read = dcrdReader.readLine();
        }
        if (read.contains("version")) {
            read = read.replaceAll("\\D+", "");
            while (dcrd.isAlive()) {
                dcrd.destroy();
            }
            return Integer.parseInt(read);
        } else return -1;
    }

    public boolean checkFiles() {
        boolean d1 = (new File("dcrd").exists()) || (new File("dcrd.exe").exists());
        boolean w1 = (new File("dcrwallet").exists()) || (new File("dcrwallet.exe").exists());
        return d1 && w1;
    }

    public boolean startDecred() throws Exception {
        ProcessBuilder dcrdBuilder = new ProcessBuilder("dcrd", tlsOptionsDecred, netOtionsDecred, "-u", username, "-P", password);
        dcrdBuilder.redirectErrorStream(true);
        dcrd = dcrdBuilder.start();
        dcrdReader = new BufferedReader(new InputStreamReader(dcrd.getInputStream()));
        String read = dcrdReader.readLine();
        while (dcrd.isAlive()) {
            while (read == null && dcrd.isAlive()) {
                read = dcrdReader.readLine();
            }
            if (read == null) return false;
            if (read.contains(DECRED_NOT_STARTED)) {
                System.out.println(read);
                return false;
            }
            if (read.contains(DECRED_STARTED)) {
                return true;
            }
            read = null;
        }
        return false;
    }

    public boolean startWallet() throws Exception {
        return startWallet("");
    }

    public boolean startWallet(String publikKey) throws Exception {
        String tempOptionsWallet = "";
        if (publikKey != null) {
            if (!publikKey.equals("")) {
                tempOptionsWallet = "--walletpass";
            }
        } else publikKey = "";
        ProcessBuilder walletBuilder = new ProcessBuilder("dcrwallet", tlsOptionsWallet0, tlsOptionsWallet1, netOptionsWallet, "-u", username, "-P", password, tempOptionsWallet, publikKey);
        walletBuilder.redirectErrorStream(true);
        wallet = walletBuilder.start();
        walletReader = new BufferedReader(new InputStreamReader(wallet.getInputStream()));
        String read = walletReader.readLine();
        while (wallet.isAlive()) {
            while (read == null && wallet.isAlive()) {
                read = walletReader.readLine();
            }
            if (read == null) return false;
            if (read.contains(ENCRYPTED_WALLET)) {
                wallet.destroyForcibly();
                encrypted = true;
                return false;
            }
            if (read.contains(WALLET_WRONG_PASSWORD)) {
                return false;
            }
            if (read.contains(WALLET_MISSING)) {
                new Error("Wallet missing!", "Create a new wallet with \"dcrwallet --create\"");
                System.exit(0);
            }
            if (read.contains(WALLET_NOT_STARTED)) {
                System.out.println(read);
                return false;
            }
            if (read.contains(WALLET_STARTED)) {
                return true;
            }
            read = null;
        }
        return false;
    }

    public boolean parseDecred() {
        if (dcrdReader == null) return false;
        String read;
        boolean ret = false;
        try {
            while (dcrdReader.ready()) {
                read = dcrdReader.readLine();
                secondBuffer.add(read);
                if (read.contains(DECRED_SYNCING)) {
                    read = read.substring(0, read.indexOf("from peer"));
                    read = read.substring(read.indexOf(DECRED_SYNCING));
                    read = read.replaceAll("\\D+", "");
                    this.maxBlock = Long.parseLong(read);
                    return true;
                }
            }
            return ret;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public String readDecred() {
        parseDecred();
        if (!secondBuffer.isEmpty()) {
            return (secondBuffer.remove());
        } else return null;
    }

    public String readWallet() {
        if (walletReader == null) return null;
        try {
            String read = walletReader.readLine();
            return read;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean decredRunning() {
        if (dcrd == null) return false;
        return dcrd.isAlive();
    }

    public boolean walletRunning() {
        if (wallet == null) return false;
        return wallet.isAlive();
    }

    public boolean allRunning() {
        return decredRunning() && walletRunning();
    }

    public void terminate() {
        if (wallet != null) {
            wallet.destroy();
            while (wallet.isAlive()) {
            }
        }
        if (dcrd != null) {
            dcrd.destroy();
            while (dcrd.isAlive()) {
            }
        }
    }
}
