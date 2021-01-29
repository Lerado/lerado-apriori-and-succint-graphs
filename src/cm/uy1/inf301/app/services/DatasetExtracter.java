package cm.uy1.inf301.app.services;
import java.io.File;
import java.io.IOException;

import cm.uy1.inf301.app.services.datastructures.DataReader;


public class DatasetExtracter
{
    DataReader reader;
    
    public DatasetExtracter(final File datasetFile) {
        this.reader = new DataReader(datasetFile);
    }
    
    public DatasetExtracter(final String datasetPath) {
        this.reader = new DataReader(new File(datasetPath));
    }
    
    public static boolean checkFiles() {
        final File f1 = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "items.save");
        final File f2 = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "transactions.save");
        return f1.exists() && f2.exists();
    }
    
    public void saveItems() {
    	System.out.println("Start items");
        this.reader.saveItems();
        System.out.println("Stop items");
    }
    
    public void saveTransactions() {
    	System.out.println("Start transactions");
        this.reader.saveTransactions();
        System.out.println("Stop transactions");
    }
    
    public void close() {
    	try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}