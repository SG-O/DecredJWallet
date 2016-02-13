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
public class TransactionGui extends JDialog{
    private JButton cancel;
    private JButton ok;
    private JTextField id;
    private JTextPane comment;
    private JTextField block;
    private JTextField address;
    private JTextField ammount;
    private JTextField fee;
    private JTextField confirmations;
    private JTextField category;
    private JPanel contentPane;

    private transaction t;

    public TransactionGui(transaction t) {
        setContentPane(contentPane);
        setTitle("Transaction Details");
        setModal(true);
        this.t = t;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new Dimension(550, 400));
        setSize(550,400);
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
        setData();
        setVisible(true);
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onOK() {
// add your code here if necessary
        t.setComment(this.comment.getText());
        dispose();
    }

    private void setData(){
        this.id.setText(t.getID());
        this.block.setText(t.getBlock());
        this.address.setText(t.getAddress());
        this.ammount.setText(String.format("%.8f", fixedPoint.longToCoin(t.getAmount())));
        this.fee.setText(String.format("%.8f", fixedPoint.longToCoin(t.getFee())));
        this.confirmations.setText(String.valueOf(t.getConfirmations()));
        this.category.setText(t.getCategory());
        this.comment.setText(t.getComment());
    }
}
