/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 02.02.2016.
 */
public class settingsUi extends JDialog{
    private JButton cancel;
    private JButton ok;
    private JTextField addr;
    private JTextField user;
    private JPasswordField password;
    private JCheckBox testnet;
    private JCheckBox tls;
    private JCheckBox encrypt;
    private JPasswordField encryptpPassword;
    private JPanel contentPane;
    private JTextField transac;
    private JCheckBox autoUpdate;

    private settings set;

    public settingsUi(settings set) {
        setContentPane(contentPane);
        setTitle("Settings");
        setModal(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        this.set = set;
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new Dimension(400, 350));
        setSize(400,350);
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

        encrypt.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (encrypt.isSelected()){
                    encryptpPassword.setEnabled(true);
                    encryptpPassword.setEditable(true);
                } else {
                    encryptpPassword.setEnabled(false);
                    encryptpPassword.setEditable(false);
                    encryptpPassword.setText("");
                }
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
        setSettings();
        setVisible(true);
    }

    private void onOK() {
// add your code here
        boolean wasFirstRun = set.isFirstrun();
        set.setRPCAddr(addr.getText());
        set.setRPCUser(user.getText());
        set.setRPCPass(String.valueOf(password.getPassword()));
        set.setDoAutoUpdate(autoUpdate.isSelected());
        set.setTestnet(testnet.isSelected());
        set.setRPCtls(tls.isSelected());
        try{
            set.setTransactionsToLoade(Integer.parseInt(transac.getText()));
        } catch (Exception e){
            new Error("Error", "The number of transactions to load must be a number smaller than " + Integer.MAX_VALUE + "!");
            return;
        }
        if (!encrypt.isSelected()){
            set.saveConfig();
        } else if (String.valueOf(encryptpPassword.getPassword()).equals("")){
            new Error("Error", "No encryption password entered!");
            return;
        } else {
            set.saveConfig(String.valueOf(encryptpPassword.getPassword()));
        }
        if (!wasFirstRun) {
            new Error("Success", "You will have to restart for some of the new settings to take affect!");
        }
       dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void setSettings(){
        addr.setText(set.getRPCAddr());
        user.setText(set.getRPCUser());
        password.setText(set.getRPCPass());
        testnet.setSelected(set.isTestnet());
        tls.setSelected(set.isRPCtls());
        encrypt.setSelected(set.isEncryption());
        transac.setText(String.valueOf(set.getTransactionsToLoad()));
        autoUpdate.setSelected(set.isDoAutoUpdate());
        if (set.isEncryption()){
            encryptpPassword.setEnabled(true);
            encryptpPassword.setEditable(true);
        } else {
            encryptpPassword.setEnabled(false);
            encryptpPassword.setEditable(false);
        }
        encryptpPassword.setText("");
    }

}
