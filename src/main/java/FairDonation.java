import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 02.03.2016.
 */
public class FairDonation extends JDialog {
    private JPanel rootPanel;
    private JTextArea withAFairDonationTextArea;
    private JCheckBox enableCheckBox;
    private JCheckBox customDonationCheckBox;
    private JTextField value;
    private JButton buttonOK;
    private JLabel valueLable;

    private settings set;

    public FairDonation(settings set) {
        super();
        this.set = set;
        setTitle("Fair Donation");
        setModal(true);
        setMinimumSize(new Dimension(350, 210));
        setSize(350, 210);
        setContentPane(rootPanel);
        enableCheckBox.setSelected(set.isFairDonation());
        customDonationCheckBox.setSelected(set.isFairDonationCustomAmount());
        value.setText(set.getFairDonationCustom().toString());
        updateVisible();
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

        customDonationCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateVisible();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        pack();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);
        setVisible(true);
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onOK() {
        Coin val = new Coin();
        if (customDonationCheckBox.isSelected()) {
            if (value.getText().equals("")) {
                new Error("Error", "Please enter a valid amount");
                return;
            }
            try {
                val = new Coin(value.getText());
            } catch (Exception e) {
                new Error("Error", "Please enter a valid amount");
                return;
            }
            if (val.getFixedPointAmount() <= 0) {
                new Error("Error", "Please enter a number that is greater than zero");
                return;
            }
        }
        set.setFairDonationCustomAmount(customDonationCheckBox.isSelected());
        set.setFairDonationCustom(val);
        set.setFairDonation(enableCheckBox.isSelected());
        set.saveConfig();
        dispose();
    }

    private void updateVisible() {
        if (customDonationCheckBox.isSelected()) {
            value.setEnabled(true);
            valueLable.setEnabled(true);
        } else {
            value.setEnabled(false);
            valueLable.setEnabled(false);
        }
    }
}
