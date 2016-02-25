/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 04.02.2016.
 */
public class CreateWalletGUI extends JDialog{
    private JButton ok;
    private JButton cancel;
    private JCheckBox useSeed;
    private JTextArea seed;
    private JPasswordField walletPassword;
    private JPasswordField walletPasswordRepeat;
    private JCheckBox securePublic;
    private JPasswordField publicPassword;
    private JPasswordField publicPasswordRepeat;
    private JPanel contentPane;

    public CreateWalletGUI() {
        setContentPane(contentPane);
        setTitle("Transaction Details");
        setModal(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        seed.setVisible(false);

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        useSeed.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (useSeed.isSelected()){
                    seed.setEnabled(true);
                    seed.setVisible(true);
                } else {
                    seed.setEnabled(false);
                    seed.setVisible(false);
                    seed.setText("");
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
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new Dimension(550, 400));
        setSize(600,400);
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

        if (securePublic.isSelected() && (!Arrays.equals(publicPassword.getPassword(), publicPasswordRepeat.getPassword()))){
            new Error("Error", "Public wallet encryption passwords don't match!");
            return;
        }

        if (securePublic.isSelected() && publicPassword.getPassword().length < 1){
            new Error("Error", "If you want to encrypt your public wallet, please enter a password!");
            return;
        }

        if (useSeed.isSelected() && seed.getText().equals("")){
            new Error("Error", "Please enter the seed to use or uncheck that you want to use your own seed!");
            return;
        }

        try {
            Process test = Runtime.getRuntime().exec("dcrwallet --create");
            BufferedReader error = new BufferedReader(new InputStreamReader(test.getErrorStream()));
            test.waitFor();
            System.out.println(error.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }


        dispose();
    }
}
