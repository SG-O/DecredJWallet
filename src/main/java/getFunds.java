/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 09.02.2016.
 */
public class getFunds extends JDialog{
    private JButton buttonOK;
    private JComboBox addressSelect;
    private JTextField ammount;
    private JButton newAddressButton;
    private JLabel qrcode;
    private JPanel contentPane;
    private JTextField uri;

    public getFunds() {
        setContentPane(contentPane);
        setTitle("Get Payed");
        setModal(true);
        setMinimumSize(new Dimension(750, 410));
        setSize(750, 410);
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

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        ammount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateQr();
            }
        });

        addressSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateQr();
            }
        });

        newAddressButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newAddress();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setLocationRelativeTo(null);
        ammount.setText(String.format("%.8f",0f));

        fill();
        setVisible(true);
    }

    private void onOK() {
        dispose();
    }

    private void fill(){
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
            address[] addresses = backend.getAddresses("default");
            for (int i = 0; i < addresses.length; i++){
                addressSelect.addItem(addresses[i].toString());
            }
        } catch (status s){
            new Error("Error", s.toString());
        }
    }

    private void setQr(String addr, String amm){
        amm = amm.replace(',','.');
        if (amm.startsWith(".")){
            amm = "0" + amm;
        }
        try {
            double out = Double.parseDouble(amm);
            amm = String.format("%.8f",out);
            amm = amm.replace(',','.');
        } catch (Exception e){
            return;
        }
        String qrText = "decred:"+ addr +"?amount=" + amm;
        ByteArrayOutputStream stream = QRCode.from(qrText).withSize(300,300).withErrorCorrection(ErrorCorrectionLevel.Q).stream();
        try {
            BufferedImage image= ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
            qrcode.setIcon(new ImageIcon(image));
            uri.setText(qrText);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    private void updateQr(){
        try {
            setQr((String) addressSelect.getSelectedItem(), ammount.getText());
        } catch (Exception ex){
        }
    }

    private void newAddress(){
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
            boolean exists = true;
            String address = "";
            int n = 0;
            while (exists) {
                exists = false;
                address = backend.getNewAddress().toString();
                for (int i = 0; i < addressSelect.getItemCount(); i++) {
                    if (address.equals(addressSelect.getItemAt(i))) {
                        exists = true;
                        break;
                    }
                }
                if (n > addressSelect.getItemCount()+3) {
                    new Error("Error", "Could not create address");
                    return;
                }
                n++;
            }
            addressSelect.addItem(address);
            addressSelect.setSelectedIndex(addressSelect.getItemCount() - 1);
        } catch (status s){
            new Error("Error", s.toString());
        }
    }
}
