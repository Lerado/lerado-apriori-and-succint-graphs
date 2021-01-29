package cm.uy1.inf301.app.services.datastructures;

import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import cm.uy1.newObjects.BinaryTree;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;

public class DataReader
{
    protected File datasetExcelFile;
    protected XSSFWorkbook currentExcelWorkbook;
    protected XSSFSheet currentSheet;
    protected Iterator<Row> rowIterator;
    protected Iterator<Cell> cellIterator;
    protected ArrayList<String> aRow;
    protected ArrayList<String> aTransaction;
    protected ArrayList<ArrayList<String>> stack;
    protected long currentRowNumber;
    
    public DataReader() {
    }
    
    public DataReader(final File datasetFile) {
        try {
            this.datasetExcelFile = datasetFile;
            this.currentExcelWorkbook = new XSSFWorkbook(this.datasetExcelFile);
            this.currentSheet = this.currentExcelWorkbook.getSheetAt(0);
            (this.rowIterator = (Iterator<Row>)this.currentSheet.rowIterator()).next();
            this.currentRowNumber = 1L;
            this.stack = new ArrayList<ArrayList<String>>();
        }
        catch (InvalidFormatException | IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public ArrayList<String> getTransaction() {
        return this.aTransaction;
    }
    
    public long getCurrentRowNumber() {
        return this.currentRowNumber;
    }
    
    public String getFilePath() {
        return this.datasetExcelFile.getAbsolutePath();
    }
    
    protected void nextRow() {
        this.aRow = new ArrayList<String>();
        if (this.rowIterator.hasNext()) {
            final Row row = this.rowIterator.next();
            ++this.currentRowNumber;
            this.cellIterator = (Iterator<Cell>)row.cellIterator();
            while (this.cellIterator.hasNext()) {
                final Cell cell = this.cellIterator.next();
                switch (cell.getCellType()) {
                    default: {
                        continue;
                    }
                    case STRING: {
                        this.aRow.add(cell.getStringCellValue());
                        continue;
                    }
                    case NUMERIC: {
                        this.aRow.add(String.valueOf(cell.getNumericCellValue()));
                        continue;
                    }
                }
            }
        }
    }
    
    public void nextTransaction() {
        this.aTransaction = new ArrayList<String>();
        this.nextRow();
        String invoiceNum = this.aRow.get(0);
        if (!this.stack.isEmpty()) {
            invoiceNum = this.stack.get(0).get(0);
            this.aTransaction.add(this.stack.remove(0).get(1));
        }
        if (invoiceNum.compareTo((String)this.aRow.get(0)) == 0) {
            do {
                this.aTransaction.add(this.aRow.get(1));
                invoiceNum = this.aRow.get(0);
                this.nextRow();
                if (this.aRow.isEmpty()) {
                    break;
                }
            } while (invoiceNum.compareTo((String)this.aRow.get(0)) == 0);
        }
        this.stack.add(this.aRow);
    }
    
    public boolean canStillRead() {
        return !this.aRow.isEmpty();
    }
    
    public void close() throws IOException {
        this.currentExcelWorkbook.close();
    }
    
    public void flush() {
        this.currentSheet = this.currentExcelWorkbook.getSheetAt(0);
        (this.rowIterator = (Iterator<Row>)this.currentSheet.rowIterator()).next();
        this.cellIterator = null;
        this.currentRowNumber = 1L;
        this.stack = new ArrayList<ArrayList<String>>();
        this.aTransaction = null;
        this.aRow = null;
    }
    
    public void saveItems() {
        this.flush();
        final BinaryTree<String> tree = new BinaryTree<String>();
        tree.useAsBinarySearchTree();
        this.nextRow();
        while (this.canStillRead()) {
            tree.insert(this.aRow.get(1));
            this.nextRow();
        }
        try {
            final File dir = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, "items.save"))));
            oos.writeObject(tree.toArrayList());
            oos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public void saveTransactions() {
        this.flush();
        final ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
        this.nextTransaction();
        while (this.canStillRead()) {
            transactions.add(this.aTransaction);
            this.nextTransaction();
        }
        transactions.add(this.aTransaction);
        try {
            final File dir = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, "transactions.save"))));
            oos.writeObject(transactions);
            oos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}