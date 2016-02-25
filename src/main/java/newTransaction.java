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

public class newTransaction extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField addr;
    private JTextField value;
    private JSlider feeSlider;
    private JLabel feeValue;

    private Coin baseUnit;

    public newTransaction() {
        setContentPane(contentPane);
        setTitle("New Transaction");
        setModal(true);
        setMinimumSize(new Dimension(400, 200));
        setSize(400,200);
        getRootPane().setDefaultButton(buttonOK);

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
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
// add your code here
        Coin value;
        try {
            value = new Coin(this.value.getText());
        } catch (Exception e){
            new Error("Error", "Value cannot be read.");
            return;
        }
        if (addr.getText().equals("")){
            new Error("Error", "No address entered");
            return;
        }
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
        if (baseUnit == null) {
            new Error("Error", "Can not set TX Fee");
            dispose();
            return;
        }

        try {
            Coin txFee = new Coin(baseUnit);
            txFee.mul(feeSlider.getValue());
            backend.setTxFee(txFee);
            String ID = backend.sendToAddress(addr.getText(), value);
            new Error("Success", "TXID:" + ID);
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

    private void updateFeeText(){
        try {
            Coin txFee = new Coin(baseUnit);
            txFee.mul(feeSlider.getValue());
            feeValue.setText(txFee.toString());
        } catch (Exception e) {
        }

    }
}
