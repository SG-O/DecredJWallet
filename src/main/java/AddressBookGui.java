import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 11.03.2016.
 */
public class AddressBookGui extends JDialog {
    private JButton cancelButton;
    private JTextField commentField;
    private JTextField addressField;
    private JTextField nameField;
    private JButton addButton;
    private JPanel contentPane;
    private JPanel book;
    private JButton removeButton;
    private JButton sendToButton;
    private JCheckBox saveCheckBox;
    private JTable table;
    private JScrollPane tableScroll;
    private TableModel tableModel;

    private boolean doTransaction;
    private addressBook adrBook;
    private settings set;

    public AddressBookGui(addressBook adrBook, settings set, boolean doTransaction) {
        this.doTransaction = doTransaction;
        this.set = set;
        setContentPane(contentPane);
        setTitle("Address Book");
        setAlwaysOnTop(true);
        setModal(true);

        if (!doTransaction) {
            sendToButton.setVisible(false);
        }

        this.adrBook = adrBook;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("favicon.png")));
        } catch (IOException e) {
        }

        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new Dimension(550, 400));
        setSize(550, 400);
        getRootPane().setDefaultButton(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAddress();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAddress();
            }
        });

        sendToButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendTo();
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
        generateTable();
        setVisible(true);
    }

    private void generateTable() {
        table = new JTable(new addressBookTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel = table.getModel();
        ((addressBookTableModel) tableModel).changeData(adrBook);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // Add the table to a scrolling pane
        tableScroll = new JScrollPane(table);
        book.add(tableScroll, BorderLayout.CENTER);
    }

    private void removeAddress() {
        if (table.getSelectedRow() < 0) return;
        adrBook.removeEntry(((addressBookTableModel) tableModel).getValueAtRow(table.getSelectedRow()));
        ((addressBookTableModel) tableModel).changeData(adrBook);
    }

    public addressBookEntry getSelected() {
        if (table.getSelectedRow() < 0) return null;
        return ((addressBookTableModel) tableModel).getValueAtRow(table.getSelectedRow());
    }

    private void addAddress() {
        if (addressField.getText().trim().length() < (decredConstants.getNetConstants(set).getPubKeyHashAddrID().length + 5)) {
            new Error("Error", "Address is too short.");
            return;
        }
        address temp = new address(addressField.getText().trim(), decredConstants.getNetConstants(set).getPubKeyHashAddrID().length);
        if (!temp.check()) {
            new Error("Error", "Invalid address: " + temp);
            return;
        }
        String nameText = nameField.getText().trim();
        if (nameText.length() == 0) {
            nameText = temp.toString();
        }
        adrBook.addEntry(new addressBookEntry(temp, nameText, commentField.getText().trim()));
        ((addressBookTableModel) tableModel).changeData(adrBook);
    }

    private void onCancel() {
        if (saveCheckBox.isSelected()) {
            adrBook.saveAddressBook();
        }
        dispose();
    }

    private void sendTo() {
        onCancel();
        if (doTransaction && (table.getSelectedRow() >= 0)) {
            new newTransaction(((addressBookTableModel) tableModel).getValueAtRow(table.getSelectedRow()).getAdr(), new Coin(), set);
        }
    }
}

