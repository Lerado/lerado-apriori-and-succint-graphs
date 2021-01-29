package cm.uy1.inf301.app.services;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;

import cm.uy1.inf301.app.services.datastructures.Item;
import cm.uy1.inf301.app.services.datastructures.Itemset;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class APrioriAIS
{
    protected int minSupport;
    protected ArrayList<Item> items;
    protected boolean[][] transactions;
    protected ArrayList<Itemset> levelOneCandidates;
    protected Hashtable<Itemset, ArrayList<Itemset>> candidates;
    protected ArrayList<Itemset> transactionCandidates;
    protected ArrayList<Itemset> currentFrequentItemsets;
    protected ArrayList<ArrayList<Itemset>> frequentItemsets;
    protected int numberOfFrequentItemsets;
    protected int level;
    protected long execTime;
    protected String startDate;
    
    public APrioriAIS() {
        this.level = 0;
        this.execTime = 0L;
        this.startDate = "";
        this.frequentItemsets = new ArrayList<ArrayList<Itemset>>();
        this.numberOfFrequentItemsets = 0;
        this.readItems();
        this.readTransactions();
    }
    
    public void reset() {
    	
    	this.level = 0;
        this.execTime = 0L;
        this.startDate = "";
        this.frequentItemsets = new ArrayList<ArrayList<Itemset>>();
        this.numberOfFrequentItemsets = 0;
    }
    
    public void configure(final double support_min) {
        this.minSupport = (int)(support_min * this.transactions.length / 100.0);
    }
    
    @SuppressWarnings("unchecked")
	public void readItems() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "items.save"))));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            final ArrayList<String> items_ = (ArrayList<String>)ois.readObject();
            ois.close();
            this.items = new ArrayList<Item>();
            int i = 0;
            for (final String s : items_) {
                this.items.add(new Item(s, i++));
            }
        }
        catch (ClassNotFoundException | IOException ex2) {
        	ex2.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
	public void readTransactions() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar + "transactions.save"))));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.transactions = Utils.toBooleanArray((ArrayList<ArrayList<String>>)ois.readObject(), this.items);
            ois.close();
        }
        catch (ClassNotFoundException | IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private void levelOneCandidates() {
        this.levelOneCandidates = new ArrayList<Itemset>();
        for (final Item s : this.items) {
            final Itemset candidate = new Itemset(new Item[] { s });
            this.levelOneCandidates.add(candidate);
        }
        this.level = 1;
    }
    
    private void countSupports() {
        boolean[][] transactions;
        for (int length = (transactions = this.transactions).length, i = 0; i < length; ++i) {
            final boolean[] transaction = transactions[i];
            for (final Itemset candidate : this.levelOneCandidates) {
                if (Utils.searchItemsetIn(candidate, transaction)) {
                    candidate.increment();
                }
            }
        }
    }
    
    private void eliminateLevelOneCandidates() {
        this.currentFrequentItemsets = new ArrayList<Itemset>();
        for (final Itemset v : this.levelOneCandidates) {
            if (v.support() >= this.minSupport) {
                this.currentFrequentItemsets.add(v);
                ++this.numberOfFrequentItemsets;
            }
        }
        if (!this.currentFrequentItemsets.isEmpty()) {
            this.frequentItemsets.add(this.currentFrequentItemsets);
        }
    }
    
    public void eliminateCandidates() {
    	
    	this.currentFrequentItemsets = new ArrayList<Itemset>();
    	for (ArrayList<Itemset> candidates : this.candidates.values()) {
    		for(Itemset candidate : candidates) {
    			if(candidate.support() >= this.minSupport) {
//    				System.out.println(candidate);
    				this.currentFrequentItemsets.add(candidate);
    				++this.numberOfFrequentItemsets;
    			}
    		}
    	}
    	if(!currentFrequentItemsets.isEmpty()) this.frequentItemsets.add(this.currentFrequentItemsets);
    }
    
    public void aPrioriAIS() {
    	
        this.startDate = (new Date()).toString();
        this.execTime = System.currentTimeMillis();
        this.levelOneCandidates();
        this.countSupports();
        this.eliminateLevelOneCandidates();

        while (!this.currentFrequentItemsets.isEmpty()) {
        	
            this.candidates = new Hashtable<Itemset, ArrayList<Itemset>>();
            
            boolean[][] transactions;
            
            for (int length = (transactions = this.transactions).length, i = 0; i < length; ++i) {
                
            	final boolean[] transaction = transactions[i];
                ArrayList<Itemset> transactionLargeItemsets = Utils.subset(this.currentFrequentItemsets, transaction);
                
                for (final Itemset largeItemset : transactionLargeItemsets) {
                	
                    this.transactionCandidates = Utils.oneExtension(largeItemset, transaction, this.items);
                    
                    for (final Itemset transactionCandidate : this.transactionCandidates) {
                    	
                    	ArrayList<Itemset> possibleGenerators = transactionCandidate.listItemsetsOfLowerSize();
                    	int position = -1;
                    	
                    	for(final Itemset possibleGenerator : possibleGenerators) {
                    		
                    		if(this.candidates.get(possibleGenerator) == null) continue;
                    			
                    		position = this.candidates.get(possibleGenerator).indexOf(transactionCandidate);
                    		if(position > -1) {
                    			this.candidates.get(possibleGenerator).get(position).increment();
//                   				System.out.println(this.candidates.get(possibleGenerator).get(position));
                    			break;
                   			}
                    	}
                    	
                    	if(position == -1) {
                			if(!this.candidates.containsKey(largeItemset)) this.candidates.put(largeItemset, new ArrayList<Itemset>());
                			transactionCandidate.increment();
                			this.candidates.get(largeItemset).add(transactionCandidate);
                		}
                    }
                }
            }
            this.eliminateCandidates();
        }
        
        this.execTime = System.currentTimeMillis() - this.execTime;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void saveStatistics() {
    	try {
	        final ArrayList statistics = new ArrayList();
	        statistics.add(this.startDate);
	        statistics.add(this.execTime);
	        statistics.add(this.numberOfFrequentItemsets);
	        statistics.add(this.minSupport);
	        final File dir = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, "statistics.save"))));
	        oos.writeObject(statistics);
	        oos.close();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void saveFrequentItemsets() {
        try {
            final File dir = new File(String.valueOf(System.getProperty("user.home")) + File.separatorChar + "Data Mining App" + File.separatorChar + "archives" + File.separatorChar);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, "frequentItemsets.save"))));
            oos.writeObject(this.frequentItemsets);
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
