import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 03.03.2016.
 */
public class Console extends JDialog {
    LinkedList<String> decredContentList = new LinkedList<String>();
    private JTabbedPane tabbedPane1;
    private JPanel rootPanel;
    private JTextPane dcrdLog;
    private JTextPane walletLog;
    private JPanel dcrdPanel;
    private Thread refresher;
    private boolean running = true;
    private Decred binaries;

    public Console(final Decred binaries) {
        super();
        this.binaries = binaries;
        setTitle("Fair Donation");
        setModal(true);
        setMinimumSize(new Dimension(600, 600));
        setSize(600, 600);
        setContentPane(rootPanel);
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        dcrdLog.setText(binaries.getDcrdContent());
        walletLog.setText(binaries.getWalletContent());

        refresher = new Thread() {
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    if (binaries.isContentChanged()) {
                        dcrdLog.setText(binaries.getDcrdContent());
                        walletLog.setText(binaries.getWalletContent());
                    }
                }
            }
        };
        refresher.start();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void setDcrdLog(String text) {
        dcrdLog.setText(text);
    }

    public void setWalletLog(String text) {
        walletLog.setText(text);
    }

    private void onCancel() {
        running = false;
        dispose();
    }
}
