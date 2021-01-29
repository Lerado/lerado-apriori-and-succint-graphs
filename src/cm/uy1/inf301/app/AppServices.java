package cm.uy1.inf301.app;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import cm.uy1.inf301.app.services.APriori;
import cm.uy1.inf301.app.services.APrioriAIS;
import cm.uy1.inf301.app.services.APrioriSETM;
import cm.uy1.inf301.app.services.APrioriTID;
import cm.uy1.inf301.app.services.DatasetExtracter;
import cm.uy1.inf301.app.services.Utils;
import cm.uy1.inf301.app.services.datastructures.Graph;
import cm.uy1.inf301.app.services.datastructures.Itemset;
import cm.uy1.inf301.app.services.datastructures.Vertex;

@SuppressWarnings("rawtypes")
public class AppServices {
		
	APriori aPrioriServices;
	APrioriTID aPrioriTIDServices;
	APrioriAIS aPrioriAISServices;
	APrioriSETM aPrioriSETMServices;
	
	ArrayList<ArrayList<Itemset>> frequentItemsets;
	ArrayList statistics;
	
	Graph graph;
	
	
	public AppServices() {
		
		this.aPrioriServices = new APriori();
		this.aPrioriTIDServices = new APrioriTID();
		this.aPrioriAISServices = new APrioriAIS();
		this.aPrioriSETMServices = new APrioriSETM();
		
		this.statistics = new ArrayList();
	}
	
	public static boolean checkFiles() {
		return DatasetExtracter.checkFiles();
	}
	
	public static boolean checkExtension(File f, String extension) {
		
		int lastIndexOf = f.getName().lastIndexOf(".");
		if(lastIndexOf == -1)
			return false;
		else
			return f.getName().substring(lastIndexOf).compareTo(extension) == 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int readItemsSize() {
        ObjectInputStream ois = null;
        ArrayList<String> items = new ArrayList<String>();
        
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "items.save"))));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            items = (ArrayList<String>)ois.readObject();
        }
        catch (ClassNotFoundException | IOException ex2) {
            ex2.printStackTrace();
        }
		return items.size();
    }
    
    @SuppressWarnings("unchecked")
	public static int readTransactionsSize() {
        ObjectInputStream ois = null;
        ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "transactions.save"))));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            transactions = (ArrayList<ArrayList<String>>)ois.readObject();
            ois.close();
        }
        catch (ClassNotFoundException | IOException ex2) {
            ex2.printStackTrace();
        }
        
        return transactions.size();
    }
    
    public static long lastDatasetLoadingDate() {
    	
    	File transactionsFile = new File(String.valueOf(System.getProperty("user.home")) +
    									File.separatorChar + "Data Mining App" +
    									File.separatorChar + "archives" +
    									File.separatorChar + "transactions.save");
    	
    	return transactionsFile.lastModified();
    }
	
	@SuppressWarnings("unchecked")
	public void readFrequentItemsets() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream(
								new File(System.getProperty("user.home")
										+File.separatorChar
										+"Data Mining App"+File.separatorChar
										+"archives"+File.separatorChar+"frequentItemsets.save"))));
		
		this.frequentItemsets = (ArrayList<ArrayList<Itemset>>) ois.readObject();
		ois.close();
	}
	
	public void readStatistics() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream(
								new File(System.getProperty("user.home")
									+File.separatorChar
										+"Data Mining App"+File.separatorChar
										+"archives"+File.separatorChar+"statistics.save"))));

		this.statistics = (ArrayList) ois.readObject();
		ois.close();
	}

	public static boolean saveItemsAndTransactions(File fileInput) {
		
		DatasetExtracter extracter = new DatasetExtracter(fileInput);

		extracter.saveItems();
		extracter.saveTransactions();
		
		extracter.close();
		
		return checkFiles();
	}
	
	public static void clearItemsAndTransactions() {
		
		final File f1 = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "items.save");
        final File f2 = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "transactions.save");
        
        f1.deleteOnExit();
        f2.deleteOnExit();
	}
	
	
	
	public void printResults(FileWriter fw) throws IOException, ClassNotFoundException {
		
		this.readFrequentItemsets();
		this.readStatistics();
		
		fw.write("\n+----------------------------------------------------+\n");
		fw.write("    |                  FREQUENT ITEMSETS                 |\n");
		fw.write("+----------------------------------------------------+\n");
		
		fw.write("\nFound " + statistics.get(2)
		+ " during last mining on the "
				+ statistics.get(0) + " with support "
					+ statistics.get(3) + ".\n");
		fw.write("Execution duration : " + String.valueOf((long)statistics.get(1)) + "ms\n");
		
		
		int i = 1;
		for(ArrayList<Itemset> pack : this.frequentItemsets) {
			fw.write("\n\n-- Found "+ pack.size() + " of size " + i++ + "\n");
			for(Itemset itemset : pack)
				fw.write(itemset.toString()+"\n");
		}
		
		fw.close();
	}
	
	public void printResults(File f) throws ClassNotFoundException, IOException {
		
		FileWriter fw;
		fw = new FileWriter(f);
		this.printResults(fw);
	}
	
	public void saveResults() throws IOException, ClassNotFoundException {
		
		FileWriter fw;
		fw = new FileWriter(new File(
							new File(System.getProperty("user.home")+File.separatorChar+"Data Mining App"+File.separatorChar+"archives"+File.separatorChar),"output.save")
							);
		
		this.printResults(fw);
	}
	
	public String results() throws IOException {
		
		FileReader fr = new FileReader(new File(new File(System.getProperty("user.home")
						+File.separatorChar
						+"Data Mining App"+File.separatorChar+"archives"+File.separatorChar),"output.save"));
		
		String results = "";
		
		int i = 0;
		while((i = fr.read()) != -1)
			results += (char)i;
		fr.close();
		
		return results;
	}
	
	public void runAPriori(final double min_support) {
		
		this.aPrioriServices.reset();
		
		this.aPrioriServices.configure(min_support);
		this.aPrioriServices.aPriori();
		this.aPrioriServices.saveFrequentItemsets();
		this.aPrioriServices.saveStatistics();
	}
	
	public void runAPrioriTID(final double min_support) {
		
		this.aPrioriTIDServices.reset();
		
		this.aPrioriTIDServices.configure(min_support);
		this.aPrioriTIDServices.aPrioriTID();
		this.aPrioriTIDServices.saveFrequentItemsets();
		this.aPrioriTIDServices.saveStatistics();
	}
	
	public void runAPrioriAIS(final double min_support) {
		
		this.aPrioriAISServices.reset();
		
		this.aPrioriAISServices.configure(min_support);
		this.aPrioriAISServices.aPrioriAIS();
		this.aPrioriAISServices.saveFrequentItemsets();
		this.aPrioriAISServices.saveStatistics();
	}
	
	public void runAPrioriSETM(final double min_support) {
		
		this.aPrioriSETMServices.reset();
		
		this.aPrioriSETMServices.configure(min_support);
		this.aPrioriSETMServices.aPrioriSETM();
		this.aPrioriSETMServices.saveFrequentItemsets();
		this.aPrioriSETMServices.saveStatistics();
	}
	
	public void makeGraph(final String[] labels) {
		
		this.graph = new Graph(labels);
	}
	
	public Graph getGraph() {
		
		return this.graph;
	}
	
	public void addVertexToAdjacency(Vertex vertex, Vertex vertexToAppend) {
		
		this.graph.add(vertex, vertexToAppend);
	}
	
	public void removeVertexToAdjacency(Vertex vertex, Vertex vertexToRemove) {
		
		this.graph.remove(vertex, vertexToRemove);
	}

	public String runEncoding(Vertex rootVertex) {
		
		return this.graph.encode(rootVertex);
	}
	
	public Vertex parent(Vertex vertexToComputeParent) throws AssertionError {
		
		if(this.graph.getCode().isBlank()) throw new AssertionError("Encoding has not been performed yet");
		return Utils.findVertex(this.graph.parent(vertexToComputeParent.getIndex()), this.graph.getAdjacency().keySet());
	}
	
	public ArrayList<Vertex> children(Vertex vertexToComputeChildren) throws AssertionError {
		
		if(this.graph.getCode().isBlank()) throw new AssertionError("Encoding has not been performed yet");
		
		ArrayList<Vertex> children = new ArrayList<Vertex>();
		ArrayList<Integer> childrenIndexes = this.graph.children(vertexToComputeChildren.getIndex());
		for(int index : childrenIndexes)
			children.add(Utils.findVertex(index, this.graph.getAdjacency().keySet()));
		
		return children;
	}

	public void resetGraph() {
		
		this.graph = null;
	}
}
