import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 05.03.2016.
 */
public class seedBackup extends JDialog {
    private JPanel contentPanel;
    private JTextArea seedText;
    private JButton ok;
    private JTextPane info;

    public seedBackup(String txt) {
        setContentPane(contentPanel);
        setModal(true);
        setAlwaysOnTop(true);
        setTitle("Seed");
        setMinimumSize(new Dimension(500, 400));
        setMaximumSize(getMinimumSize());
        getRootPane().setDefaultButton(ok);
        setMinimumSize(contentPanel.getMinimumSize());
        setMaximumSize(contentPanel.getMaximumSize());
        seedText.setWrapStyleWord(true);
        seedText.setText(txt);
        seedText.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                seedText.selectAll();
            }

            public void focusLost(FocusEvent e) {
                seedText.select(0, 0);
            }
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

// call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
// add your code here
        dispose();
    }
}
