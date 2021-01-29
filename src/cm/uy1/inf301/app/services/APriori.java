package cm.uy1.inf301.app.services;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;

import cm.uy1.inf301.app.services.datastructures.Item;
import cm.uy1.inf301.app.services.datastructures.Itemset;

import java.util.ArrayList;
import java.util.Date;

public class APriori
{
    protected int minSupport;
    protected ArrayList<Item> items;
    protected boolean[][] transactions;
    protected ArrayList<Itemset> candidates;
    protected ArrayList<Itemset> currentFrequentItemsets;
    protected ArrayList<ArrayList<Itemset>> frequentItemsets;
    protected int numberOfFrequentItemsets;
    protected int level;
    protected long execTime;
    protected String startDate;
    
    public APriori() {
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
    
    private void aPrioriGen(final ArrayList<Itemset> previousFrequentItemsets) {
        this.candidates = new ArrayList<Itemset>();
        if (!previousFrequentItemsets.isEmpty()) {
            for (int i = 0; i < previousFrequentItemsets.size(); ++i) {
                final Itemset cand1 = previousFrequentItemsets.get(i);
                for (int j = i + 1; j < previousFrequentItemsets.size(); ++j) {
                    final Itemset cand2 = previousFrequentItemsets.get(j);
                    assert cand1.size() == cand2.size();
                    final Itemset candidate = new Itemset(cand1);
                    candidate.reset();
                    int nDifferent = 0;
                    for (int k = 0; k < cand2.size(); ++k) {
                        boolean checker = false;
                        final Item s2 = cand2.get(k);
                        for (int l = 0; l < cand1.size(); ++l) {
                            final Item s3 = cand1.get(l);
                            if (s3 == s2) {
                                checker = true;
                                break;
                            }
                        }
                        if (!checker) {
                            ++nDifferent;
                            candidate.add(s2);
                        }
                    }
                    assert nDifferent > 0;
                    if (nDifferent == 1 && !candidate.isIn(this.candidates) && this.filterCandidate(candidate)) {
                        this.candidates.add(candidate);
                    }
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
    
    public void aPriori() {
        this.startDate = (new Date()).toString();
        this.execTime = System.currentTimeMillis();
        this.levelOneCandidates();
        while (!this.candidates.isEmpty()) {
            this.countSupports();
            this.eliminateCandidates();
            this.aPrioriGen(this.currentFrequentItemsets);
            if (!this.candidates.isEmpty()) {
                ++this.level;
            }
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
    	catch (IOException e) {
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