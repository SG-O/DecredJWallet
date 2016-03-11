/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class newTransaction extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField addr;
    private JTextField value;
    private JSlider feeSlider;
    private JLabel feeValue;
    private JTabbedPane tabbs;
    private JLabel addressLabel;
    private JLabel valueLabel;
    private JPanel advancedTabContent;
    private JButton addButton;
    private JLabel donationMessage;
    private JPanel aTab;
    private JPanel advancedTab;

    private ArrayList<JTextField[]> advancedInputs = new ArrayList<JTextField[]>();

    private Coin baseUnit = new Coin();
    private settings set;

    public newTransaction(settings set) {
        this(null, new Coin(), set);
    }

    public newTransaction(address to, Coin amount, settings set) {
        setContentPane(contentPane);
        setTitle("New Transaction");
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        setSize(400, 400);
        this.set = set;
        getRootPane().setDefaultButton(buttonOK);

        if (to != null) {
            addr.setText(to.toString());
        }
        if (amount == null) {
            amount = new Coin();
        }
        value.setText(amount.toString());

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        feeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateFeeText();
            }
        });
        getBaseUnit();
        if (set.isFairDonation()) {
            Coin donation = baseUnit;
            if (set.isFairDonationCustomAmount()) {
                donation = set.getFairDonationCustom();
            }
            donationMessage.setText("You will donate " + donation);
        } else {
            donationMessage.setText("");
        }
        setLocationRelativeTo(null);
        advancedTab = new JPanel(new GridLayout(0, 1, 0, 5));
        advancedTab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        advancedTabContent.add(advancedTab, BorderLayout.NORTH);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createEntry();
                advancedTab.revalidate();
            }
        });
        createEntry();
        setVisible(true);
    }

    private void onOK() {
// add your code here
        Coin value = null;
        HashMap<address, Coin> temp = new HashMap<address, Coin>();
        if (tabbs.getSelectedIndex() == 1) {
            for (int i = 0; i < advancedInputs.size(); i++) {
                JTextField[] inputs = advancedInputs.get(i);
                if (inputs != null) {
                    address ad = new address(inputs[0].getText(), 2);
                    if (ad.isEmpty()) {
                        new Error("Error", "No address entered for target " + (i + 1) + ".");
                        return;
                    }
                    if (!ad.check()) {
                        new Error("Error", "Invalid address: " + ad);
                        return;
                    }
                    try {
                        value = new Coin(inputs[1].getText());
                    } catch (Exception e) {
                        new Error("Error", "Value for " + ad + " can not be read.");
                        return;
                    }
                    if (value.getFixedPointAmount() > 0) {
                        if (temp.containsKey(addr)) {
                            Coin tmpcoin = temp.get(addr);
                            temp.remove(addr);
                            try {
                                value.add(tmpcoin);
                            } catch (Exception e) {
                                value = tmpcoin;
                            }
                        }
                        temp.put(ad, value);
                    }
                }
            }
            if (temp.size() < 1) {
                new Error("Error", "No transaction entered.");
                return;
            }
        } else {
            try {
                value = new Coin(this.value.getText());
            } catch (Exception e) {
                new Error("Error", "Value can not be read.");
                return;
            }
            if (addr.getText().equals("")) {
                new Error("Error", "No address entered.");
                return;
            }
            if (value.getFixedPointAmount() <= 0) {
                new Error("Error", "Please enter a number that is greater than zero");
                return;
            }
        }
        decredBackend backend = settings.getBackend();
        if (backend == null){
            new Error("Error", "Wallet not connected.");
            dispose();
            return;
        }
        if (!backend.checkConnection()){
            new Error("Error", "Wallet not connected");
            dispose();
            return;
        }
        if (baseUnit == null) {
            new Error("Error", "Can not set TX Fee.");
            dispose();
            return;
        }

        try {
            Coin txFee = new Coin(baseUnit);
            txFee.mul(feeSlider.getValue());
            backend.setTxFee(txFee);
            String ID = "";
            if (tabbs.getSelectedIndex() == 1) {
                ID = backend.sendMany(temp);
            } else if (value != null) {
                address parsedAddress = new address(addr.getText(), 2);
                if (parsedAddress.isEmpty()) {
                    new Error("Error", "No address entered.");
                    return;
                }
                if (!parsedAddress.check()) {
                    new Error("Error", "Invalid address: " + parsedAddress);
                    return;
                }
                ID = backend.sendToAddress(parsedAddress, value);
            }
            new SelectableMessage("Success", "TXID:" + ID);
        } catch (status s){
            if (s.getStatus() == status.LOCKED){
                new Unlock();
                return;
            } else{
                new Error("Error", s.toString());
            }
        } catch (Exception e) {
            new Error("Error", e.toString());
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void getBaseUnit(){
        decredBackend backend = settings.getBackend();
        if (backend == null){
            new Error("Error", "Wallet not connected");
            dispose();
            return;
        }
        if (!backend.checkConnection()){
            new Error("Error", "Wallet not connected");
            dispose();
            return;
        }
        try {
            baseUnit = backend.getRelayFee();
            updateFeeText();
        } catch (status s){
            new Error("Error", s.toString());
        }
    }

    private void createEntry() {
        JTextField[] tempFields = new JTextField[2];
        Dimension sizeLabel = new Dimension(60, 16);
        final JPanel subItem0 = new JPanel();
        subItem0.setLayout(new BoxLayout(subItem0, BoxLayout.X_AXIS));
        final JLabel infoText = new JLabel(addressLabel.getText());
        infoText.setMinimumSize(sizeLabel);
        infoText.setPreferredSize(sizeLabel);
        infoText.setMaximumSize(sizeLabel);
        JTextField input = new JTextField();
        tempFields[0] = input;
        subItem0.add(infoText);
        subItem0.add(input);
        advancedTab.add(subItem0);

        final JPanel subItem1 = new JPanel();
        subItem1.setLayout(new BoxLayout(subItem1, BoxLayout.X_AXIS));
        final JLabel infoTextValue = new JLabel(valueLabel.getText());
        infoTextValue.setMinimumSize(sizeLabel);
        infoTextValue.setPreferredSize(sizeLabel);
        infoTextValue.setMaximumSize(sizeLabel);
        final JTextField inputValue = new JTextField();
        tempFields[1] = inputValue;
        subItem1.add(infoTextValue);
        subItem1.add(inputValue);
        advancedTab.add(subItem1);
        advancedInputs.add(tempFields);
        final int index = advancedInputs.size() - 1;
        final JSeparator sep = new JSeparator();
        final JButton delButton = new JButton("Remove");
        delButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        final JPanel subItem2 = new JPanel();
        subItem2.setLayout(new BoxLayout(subItem2, BoxLayout.X_AXIS));
        subItem2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        subItem2.add(Box.createHorizontalGlue());
        subItem2.add(delButton);
        advancedTab.add(subItem2);
        advancedTab.add(sep);
        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                advancedTab.remove(delButton);
                advancedTab.remove(infoTextValue);
                advancedTab.remove(infoText);
                advancedTab.remove(inputValue);
                advancedTab.remove(sep);
                advancedTab.remove(subItem0);
                advancedTab.remove(subItem1);
                advancedTab.remove(subItem2);
                advancedInputs.set(index, null);
                advancedTab.revalidate();
            }
        });
    }

    private void updateFeeText(){
        try {
            Coin txFee = new Coin(baseUnit);
            txFee.mul(feeSlider.getValue());
            feeValue.setText(txFee.toString());
            if (set.isFairDonation()) {
                Coin donation = txFee;
                if (set.isFairDonationCustomAmount()) {
                    donation = set.getFairDonationCustom();
                }
                donationMessage.setText("You will donate " + donation);
            } else {
                donationMessage.setText("");
            }
        } catch (Exception e) {
        }

    }
}
