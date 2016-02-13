/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 01.02.2016.
 */

public class MainWindow extends JFrame {
    // Instance attributes used in this example
    private JPanel topPanel;
    private JPanel controlPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private JMenuBar menu;
    private JButton details, sendFunds, getFunds;
    private JLabel ballance;

    private settings set;

    public TableModel tableModel;

    // Constructor of main frame
    public MainWindow(final settings set) {
        // Set the frame characteristics
        setTitle("DecredJWallet");
        setSize(600, 400);
        setMinimumSize(new Dimension(400, 300));
        this.set = set;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        setLocationRelativeTo(null);

        menu = new JMenuBar();
        JMenu sub = new JMenu("Decred");
        JMenuItem item = new JMenuItem("Refresh");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        sub.add(item);
        item = new JMenuItem("Unlock");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unlock();
            }
        });
        sub.add(item);
        item = new JMenuItem("Force Lock");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lock();
            }
        });
        sub.add(item);
        sub.addSeparator();
        item = new JMenuItem("Settings");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new settingsUi(set);
            }
        });
        sub.add(item);
        menu.add(sub);
        sub = new JMenu("Help");
        item = new JMenuItem("About");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });
        sub.add(item);
        menu.add(sub);
        setJMenuBar(menu);

        // Create a panel to hold all other components
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        getContentPane().add(topPanel);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        details = new JButton("More Details");
        details.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDetails();
            }
        });

        sendFunds = new JButton("New Transaction");
        sendFunds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSend();
            }
        });
        ballance = new JLabel("Balance: " + String.format("%.8f",fixedPoint.longToCoin(settings.getBackend().getBalance())));

        getFunds = new JButton("Receive");
        getFunds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new getFunds();
            }
        });

        controlPanel.add(details);
        controlPanel.add(ballance);
        controlPanel.add(sendFunds);
        controlPanel.add(getFunds);


        // Create a new table instance
        table = new JTable(new transactionTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel = table.getModel();
        // Add the table to a scrolling pane
        scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(controlPanel, BorderLayout.PAGE_END);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

    }

    private void doSend() {
        new newTransaction();
    }

    private void unlock() {
        new Unlock();
    }

    private void refresh(){
        ((transactionTableModel)tableModel).changeData(settings.getBackend().listTransactions(set.getTransactionsToLoad()));
        ballance.setText("Ballance: " + String.format("%.8f",fixedPoint.longToCoin(settings.getBackend().getBalance())));
    }

    private void about(){
        new About();
    }

    private void getDetails(){
        if (table.getSelectedRow() >= 0){
            transaction t = ((transactionTableModel)table.getModel()).getValueAtRow(table.getSelectedRow());
            new TransactionGui(t);
        }
    }

    private void lock() {
        settings.getBackend().lockWallet();
        new Error("Success", "Locked");
    }
}