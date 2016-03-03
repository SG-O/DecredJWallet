/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class Error extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel errorType;

    public Error(String title, String er) {
        setContentPane(contentPane);
        setModal(true);
        setAlwaysOnTop(true);
        setTitle(title);
        getRootPane().setDefaultButton(buttonOK);
        errorType.setText(er);
        buttonOK.addActionListener(new ActionListener() {
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
// add your code here
        dispose();
    }

}
