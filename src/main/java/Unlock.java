/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Unlock extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField password;

    public Unlock() {
        setContentPane(contentPane);
        setTitle("Unlock Wallet");
        setModal(true);
        setMinimumSize(new Dimension(300, 200));
        setSize(300,200);
        getRootPane().setDefaultButton(buttonOK);

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
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
// add your code here
        decredBackend backend = settings.getBackend();
        if (backend == null){
            new Error("Error", "Wallet not connected");
            return;
        }
        if (!backend.checkConnection()){
            new Error("Error", "Wallet not connected");
            return;
        }
        status result = backend.unlockWallet(String.valueOf(password.getPassword()));
        if (result.getStatus() == status.SUCCESS) {
            new Error("Success", "Wallet unlocked");
            dispose();
            return;
        }
        new Error("Error", result.toString());
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
