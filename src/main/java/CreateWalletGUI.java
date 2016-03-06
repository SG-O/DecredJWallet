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
import java.util.Arrays;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 04.02.2016.
 */
public class CreateWalletGUI extends JDialog{
    private JButton ok;
    private JButton cancel;
    private JCheckBox useSeed;
    private JTextArea seedInput;
    private JPasswordField walletPassword;
    private JPasswordField walletPasswordRepeat;
    private JCheckBox securePublic;
    private JPasswordField publicPassword;
    private JPasswordField publicPasswordRepeat;
    private JPanel contentPane;
    private JSlider strengthSlider;
    private JLabel strength;
    private JLabel strengthLabel;
    private JLabel strengthLabelWeaker;
    private JLabel strengthLabelStronger;

    public CreateWalletGUI() {
        setContentPane(contentPane);
        setTitle("Transaction Details");
        setModal(true);
        setAlwaysOnTop(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        seedInput.setVisible(false);
        strengthSlider.setValue(256);
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        useSeed.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (useSeed.isSelected()){
                    seedInput.setEnabled(true);
                    seedInput.setVisible(true);
                    strengthSlider.setEnabled(false);
                    strength.setEnabled(false);
                    strengthLabel.setEnabled(false);
                    strengthLabelWeaker.setEnabled(false);
                    strengthLabelStronger.setEnabled(false);
                } else {
                    seedInput.setEnabled(false);
                    seedInput.setVisible(false);
                    strengthSlider.setEnabled(true);
                    strengthSlider.setValue(256);
                    strength.setEnabled(true);
                    strengthLabel.setEnabled(true);
                    strengthLabelWeaker.setEnabled(true);
                    strengthLabelStronger.setEnabled(true);
                    seedInput.setText("");
                }
            }
        });

        securePublic.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (securePublic.isSelected()){
                    publicPassword.setEnabled(true);
                    publicPassword.setEditable(true);
                    publicPasswordRepeat.setEnabled(true);
                    publicPasswordRepeat.setEditable(true);
                } else {
                    publicPassword.setEnabled(false);
                    publicPassword.setEditable(false);
                    publicPassword.setText("");
                    publicPasswordRepeat.setEnabled(false);
                    publicPasswordRepeat.setEditable(false);
                    publicPasswordRepeat.setText("");
                }
            }
        });

        strengthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                strength.setText(String.valueOf((strengthSlider.getValue() / 8) * 8));
            }
        });

        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new Dimension(600, 450));
        setSize(600, 450);
        getRootPane().setDefaultButton(ok);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancel.addActionListener(new ActionListener() {
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

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onOK() {
        if (!Arrays.equals(walletPassword.getPassword(), walletPasswordRepeat.getPassword())){
            new Error("Error", "Wallet passwords don't match!");
            return;
        }

        if (walletPassword.getPassword().length < 8) {
            new Error("Error", "Wallet password not strong enough! Enter more than 8 characters.");
            return;
        }

        if (securePublic.isSelected() && (!Arrays.equals(publicPassword.getPassword(), publicPasswordRepeat.getPassword()))){
            new Error("Error", "Public wallet encryption passwords don't match!");
            return;
        }

        if (securePublic.isSelected() && publicPassword.getPassword().length < 1){
            new Error("Error", "If you want to encrypt your public wallet, please enter a password!");
            return;
        }

        if (useSeed.isSelected() && seedInput.getText().equals("")) {
            new Error("Error", "Please enter the seed you want to use or generate a new one by specifying that you don't want to use your own seed!");
            return;
        }
        seed walletSeed = null;
        if (useSeed.isSelected()) {
            try {
                walletSeed = new seed(seedInput.getText().replace("\n", "").replace("\r", ""), true);
            } catch (seedException e) {
                if (e.getException() == seedException.EMPTY_SEED) {
                    new Error("Error", "Please enter the seed you want to use or generate a new one by specifying that you don't want to use your own seed!");
                    return;
                }
                if (e.getException() == seedException.INVALID_WORD) {
                    new Error("Error", "Invalid word \"" + e.getFirstStatus() + "\" found!");
                    return;
                }
                if (e.getException() == seedException.INVALID_HASH) {
                    new Error("Error", "Invalid checksum \"" + e.getFirstStatus() + "\" found! The expected value is \"" + e.getSecondStatus() + ".");
                    return;
                }
                if (e.getException() == seedException.PGP_WORDLIST_NOT_LOADED) {
                    new Error("Error", "Error loading PGP word list! Exiting now.");
                    System.exit(0);
                }
            }
        } else {
            walletSeed = new seed(strengthSlider.getValue());
        }
        if (walletSeed == null) return;
        try {
            new seedBackup(walletSeed.toHashedPGP());
        } catch (seedException e) {
            if (e.getException() == seedException.PGP_WORDLIST_NOT_LOADED) {
                new Error("Error", "Error loading PGP word list! Exiting now.");
                System.exit(0);
            }
            return;
        }
        dispose();
    }
}
