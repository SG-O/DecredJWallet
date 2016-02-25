import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class UnlockPublic extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField password;
    private JLabel status;
    private boolean success = false;
    private Decred binaries;

    public UnlockPublic(Decred binaries) {
        this.binaries = binaries;
        setContentPane(contentPane);
        setModal(true);
        setAlwaysOnTop(true);
        setTitle("Unlock Wallet");
        getRootPane().setDefaultButton(buttonOK);
        pack();

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        status.setText("");
        setSize(new Dimension(400, 150));
        setMaximumSize(getSize());
        setMinimumSize(getMaximumSize());
        setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setVisible(true);
    }

    private void onOK() {
        try {
            boolean result = binaries.startWallet(String.valueOf(password.getPassword()));
            if (result) {
                success = true;
            } else {
                status.setText("Wrong password entered!");
                return;
            }
        } catch (Exception e) {
            new Error("Error", "Decred executables not found!");
            binaries.terminate();
            System.exit(0);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public boolean isSuccess() {
        return success;
    }
}
