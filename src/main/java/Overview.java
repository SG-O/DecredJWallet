/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 24.02.2016.
 */
public class Overview extends JFrame {
    public TableModel tableModel;
    private JPanel topPanel;
    private JPanel statusBar;
    private JButton getFunds;
    private JButton details;
    private JButton sendFunds;
    private JPanel transactions;
    private JLabel balance;
    private JLabel statusString;
    private JLabel currentBlock;
    private JMenuBar menu;
    private JTable table;
    private JScrollPane tableScroll;
    private settings set;
    private Decred binaries;

    public Overview(final settings set, final Decred binaries) {
        setTitle("DecredJWallet");
        setSize(600, 400);
        setMinimumSize(new Dimension(400, 300));
        this.set = set;
        this.binaries = binaries;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        generateMenu();
        setJMenuBar(menu);

        setContentPane(topPanel);
        generateTable();

        details.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDetails();
            }
        });
        sendFunds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new newTransaction(set);
            }
        });
        getFunds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new getFunds();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        statusString.setText("");
        refresh();
    }

    private void generateTable() {
        table = new JTable(new transactionTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel = table.getModel();
        // Add the table to a scrolling pane
        tableScroll = new JScrollPane(table);
        transactions.add(tableScroll, BorderLayout.CENTER);
    }

    private void generateMenu() {
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
        item = new JMenuItem("Console");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Console(binaries);
            }
        });
        sub.add(item);
        item = new JMenuItem("Settings");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new settingsUi(set);
            }
        });
        sub.add(item);
        menu.add(sub);
        sub = new JMenu("Help");
        item = new JMenuItem("Donate");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new newTransaction(set.getDonationAddress(), new Coin(2), set);
                } catch (Exception e1) {
                }
            }
        });
        sub.add(item);
        item = new JMenuItem("Fair Donation");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new FairDonation(set);
            }
        });
        sub.add(item);
        item = new JMenuItem("About");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });
        sub.add(item);
        menu.add(sub);
    }

    private void getDetails() {
        if (table.getSelectedRow() >= 0) {
            transaction t = ((transactionTableModel) table.getModel()).getValueAtRow(table.getSelectedRow());
            new TransactionGui(t);
        }
    }

    private void unlock() {
        new Unlock();
    }

    public void refresh() {
        Coin balanceValue;
        Coin unconfirmedBalance;
        try {
            ((transactionTableModel) tableModel).changeData(settings.getBackend().listTransactions(set.getTransactionsToLoad()));
        } catch (status status) {
            return;
        }
        try {
            balanceValue = settings.getBackend().getBalance();
            currentBlock.setText("Block: " + String.valueOf(settings.getBackend().getWalletBlockCount()));
            unconfirmedBalance = settings.getBackend().getUnconfirmedBalance();
        } catch (status status) {
            System.out.println(status);
            return;
        }
        balance.setText(balanceValue.toString() + " (" + unconfirmedBalance.toString() + ")");
    }

    private void about() {
        new About();
    }

    private void lock() {
        settings.getBackend().lockWallet();
        new Error("Success", "Locked");
    }

    public void setStatus(String newSatus) {
        statusString.setText(newSatus);
    }

    public void resetStatus() {
        statusString.setText("");
    }
}
