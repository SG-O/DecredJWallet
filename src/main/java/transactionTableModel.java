/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import javax.swing.table.AbstractTableModel;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 01.02.2016.
 */
public class transactionTableModel  extends AbstractTableModel {
    private String[] columnNames = {"ID", "Address", "Amount", "Confirmations", "Category"};
    private transaction[] data = null;

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        if (data == null) return 0;
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        if (data == null) return "";
        if (row > data.length) return "";
        if (col > columnNames.length) return "";
        transaction temp = data[row];
        switch (col) {
            case 0:
                return temp.getID();
            case 1:
                return temp.getAddress();
            case 2:
                return String.format("%.8f", fixedPoint.longToCoin(temp.getAmount()));
            case 3:
                return temp.getConfirmations();
            case 4:
                return temp.getCategory();
            default:
                return "";
        }
    }

    public void changeData(transaction[] newData){
        if (data == null) data = new transaction[0];
        boolean greater = newData.length > data.length;
        boolean less = newData.length < data.length;
        int oldLength = data.length;
        data = newData;
        if (greater)fireTableRowsInserted(oldLength, data.length -1);
        if (less)fireTableRowsDeleted(data.length, oldLength-1);
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public transaction getValueAtRow(int c){
        if (c < 0) c = 0;
        if (c >= data.length) c = data.length - 1;
        return data[c];
    }

}
