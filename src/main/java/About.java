/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 04.02.2016.
 */
public class About extends JDialog{
    private JButton ok;
    private JTextPane textmain;
    private JPanel contentPane;
    private JScrollPane scroll1;

    public About() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(400, 400));
        setSize(800,600);
        getRootPane().setDefaultButton(ok);

        ok.addActionListener(new ActionListener() {
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

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setLocationRelativeTo(null);
        scroll1.setWheelScrollingEnabled(true);
        scroll1.updateUI();
        textmain.scrollRectToVisible(new Rectangle(0,0));
        scroll1.scrollRectToVisible(new Rectangle(0,0));
        setVisible(true);
    }

    private void onOK(){
        dispose();
    }
}
