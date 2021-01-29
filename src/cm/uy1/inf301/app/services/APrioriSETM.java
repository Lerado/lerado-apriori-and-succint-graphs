package cm.uy1.inf301.app.services;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import cm.uy1.inf301.app.services.datastructures.Item;
import cm.uy1.inf301.app.services.datastructures.Itemset;

public class APrioriSETM {
	
	protected int minSupport;
	protected ArrayList<Item> items;
	protected boolean[][] transactions;
	protected ArrayList<Itemset> candidates;
	protected ArrayList<Itemset> TIDCandidates;
	protected ArrayList<Itemset> currentFrequentItemsets;
	protected ArrayList<ArrayList<Itemset>> frequentItemsets;
	protected int numberOfFrequentItemsets;
	protected int level;
	protected long execTime;
	protected String startDate;

	public APrioriSETM() {
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
        this.candidates = new ArrayList<Itemset>();
        for (final Item s : this.items) {
            final Itemset candidate = new Itemset(new Item[] { s });
            this.candidates.add(candidate);
        }
        this.level = 1;
    }
    
    private void levelOneTIDCandidates() {
        this.TIDCandidates = new ArrayList<Itemset>();
        boolean[][] transactions;
        for (int length = (transactions = this.transactions).length, i = 0; i < length; ++i) {
            final boolean[] transaction = transactions[i];
            final ArrayList<Item> castTransaction = Utils.booleanArrayToLine(transaction, this.items);
            for (final Item item : castTransaction) {
            	Itemset TIDCandidate = new Itemset(new Item[] { item } );
            	TIDCandidate.setIndex(i+1);
                this.TIDCandidates.add(TIDCandidate);
            }
        }
        this.level = 1;
    }
    
    private void countSupports() {
        boolean[][] transactions;
        for (int length = (transactions = this.transactions).length, i = 0; i < length; ++i) {
            final boolean[] transaction = transactions[i];
            for (final Itemset candidate : this.candidates) {
                if (Utils.searchItemsetIn(candidate, transaction)) {
                    candidate.increment();
                }
            }
        }
    }
    
    public boolean filterCandidate(final Itemset candidate) {
        boolean checker = false;
        final ArrayList<Itemset> subsets = (ArrayList<Itemset>)candidate.listItemsetsOfLowerSize();
        for (final Itemset subset : subsets) {
            for (final Itemset frequentItemset : this.currentFrequentItemsets) {
                if (subset.equalsIgnoringSupport(frequentItemset)) {
                    checker = true;
                    break;
                }
            }
        }
        return checker;
    }
    
    private void eliminateCandidates() {
        this.currentFrequentItemsets = new ArrayList<Itemset>();
        for (final Itemset v : this.candidates) {
            if (v.support() >= this.minSupport) {
                this.currentFrequentItemsets.add(v);
                ++this.numberOfFrequentItemsets;
            }
        }
        if (!this.currentFrequentItemsets.isEmpty()) {
            this.frequentItemsets.add(this.currentFrequentItemsets);
        }
    }
    
    class SortCandidates implements Comparator<Itemset> {

		@Override
		public int compare(Itemset o1, Itemset o2) {
			// TODO Auto-generated method stub
			return o1.items().toString().compareTo(o2.items().toString());
		}
    	
    }
    
    public void aPrioriSETM() {
        this.startDate = (new Date()).toString();
        this.execTime = System.currentTimeMillis();
        this.levelOneCandidates();
        this.countSupports();
        this.eliminateCandidates();
        this.levelOneTIDCandidates();
        while (!this.currentFrequentItemsets.isEmpty()) {
        	
        	this.TIDCandidates.clear();
        	for (int length = this.transactions.length, i = 0; i < length; ++i) {
        		final boolean[] transaction = transactions[i];
        		ArrayList<Itemset> transactionLargeItemsets = Utils.subset(this.currentFrequentItemsets, transaction);
        		for(final Itemset largeItemset : transactionLargeItemsets) {
        			this.candidates = Utils.oneExtension(largeItemset, transaction, this.items);
        			for(Itemset transactionCandidate : this.candidates) {
        				transactionCandidate.setIndex(i+1);
        				this.TIDCandidates.add(transactionCandidate);
        			}	
        		}
        	}
        	
        	this.TIDCandidates.sort(new SortCandidates());
        	this.currentFrequentItemsets = new ArrayList<Itemset>();
        	int index = 0;
        	while(index < this.TIDCandidates.size()) {
        		int skipper = index + 1;
        		Itemset TIDCandidate = this.TIDCandidates.get(index); TIDCandidate.increment();
        		if(skipper < this.TIDCandidates.size())
	        		while(this.TIDCandidates.get(skipper).equals(TIDCandidate)) {
	        			TIDCandidate.increment();
	        			++skipper;
	        			if(skipper == this.TIDCandidates.size()) break;
	        		}
        		if(TIDCandidate.getSupport() >= this.minSupport) {
        			this.currentFrequentItemsets.add(TIDCandidate);
        			++this.numberOfFrequentItemsets;
        		}
        		
        		index = skipper;
        	}
        	
        	if(!this.currentFrequentItemsets.isEmpty()) this.frequentItemsets.add(currentFrequentItemsets);
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
