import javax.swing.table.AbstractTableModel;

public class addressBookTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name", "Address", "Comment"};
    private addressBookEntry[] data = null;

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
        addressBookEntry temp = data[row];
        switch (col) {
            case 0:
                return temp.getName();
            case 1:
                return temp.getAdr().toString();
            case 2:
                return temp.getComment();
            default:
                return "";
        }
    }

    public void changeData(addressBook book) {
        addressBookEntry[] newData = book.getAlphabetical();
        if (data == null) {
            data = new addressBookEntry[0];
        }
        boolean greater = newData.length > data.length;
        boolean less = newData.length < data.length;
        int oldLength = data.length;
        data = newData;
        if (greater) fireTableRowsInserted(oldLength, data.length - 1);
        if (less) fireTableRowsDeleted(data.length, oldLength - 1);
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public addressBookEntry getValueAtRow(int c) {
        if (c < 0) c = 0;
        if (c >= data.length) c = data.length - 1;
        return data[c];
    }

}
