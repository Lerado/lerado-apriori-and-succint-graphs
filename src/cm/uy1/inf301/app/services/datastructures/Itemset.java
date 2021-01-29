package cm.uy1.inf301.app.services.datastructures;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.ArrayList;
import java.io.Serializable;

public class Itemset implements Comparable<Itemset>, Serializable
{
    private static final long serialVersionUID = -5411523906962900520L;
    protected ArrayList<Item> items;
    protected int size;
    protected int support;
    protected int index;
    
    public Itemset() {
        this.items = new ArrayList<Item>();
        this.size = 0;
        this.support = 0;
    }
    
    public Itemset(final Item... aListOfItems) {
        this.items = new ArrayList<Item>();
//        ArraySorting.quickSort(aListOfItems);
        for (int i = 0; i < aListOfItems.length; ++i) {
            this.items.add(aListOfItems[i]);
        }
        this.items.sort(null);
        this.size = this.items.size();
        this.support = 0;
    }
    
    public Itemset(final Collection<? extends Item> set) {
        (this.items = new ArrayList<Item>(set)).sort(null);
        this.size = this.items.size();
        this.support = 0;
    }
    
    public Itemset(final Itemset anotherItemset) {
        (this.items = new ArrayList<Item>(anotherItemset.items)).sort(null);
        this.size = this.items.size();
        this.support = anotherItemset.getSupport();
    }
    
    protected void resize() {
        this.size = this.items.size();
    }
    
    public Item get(final int index) {
        return this.items.get(index);
    }
    
    public ArrayList<Item> items() {
        return this.items;
    }
    
    public ArrayList<String> itemsToString() {
        final ArrayList<String> temp = new ArrayList<String>();
        for (final Item i : this.items) {
            temp.add(i.toString());
        }
        return temp;
    }
    
    public int support() {
        return this.getSupport();
    }
    
    public int size() {
        return this.size;
    }
    
    public void set(final int index, final Item newItem) {
        this.items.set(index, newItem);
    }
    
    public int getSupport() {
        return this.support;
    }
    
    public void setSupport(final int newSupport) {
        this.support = newSupport;
    }
    
    public int index() {
    	return this.index;
    }
    
    public void setIndex(int pIndex) {
    	this.index = pIndex;
    }
    
    public void add(final Item anItem) {
        this.items.add(anItem);
        this.items.sort(null);
        this.resize();
    }
    
    public void add(final Item anItem, final boolean sort) {
        this.items.add(anItem);
        if(sort)
        	this.items.sort(null);
        this.resize();
    }
    
    public void addAll(final Collection<? extends Item> set) {
        this.items.addAll(set);
        this.items.sort(null);
        this.resize();
    }
    
    public void addAll(final Itemset set) {
        this.items.addAll(set.items);
        this.items.sort(null);
        this.resize();
    }
    
    public void remove(final Item anItem) {
        this.items.remove(anItem);
        this.resize();
    }
    
    public void removeAt(final int index) {
        this.items.remove(index);
        this.resize();
    }
    
    public void removeAll(final Collection<? extends Item> set) {
        this.items.removeAll(set);
        this.resize();
    }
    
    public void removeAll(final Itemset set) {
        this.items.removeAll(set.items);
        this.resize();
    }
    
    public void increment() {
        this.support = this.getSupport() + 1;
    }
    
    public void reset() {
        this.support = 0;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.items.toString()) + ": " + String.valueOf(this.getSupport());
    }
    
    public String toStringIgnoringSupport() {
        return this.items.toString();
    }
    
    public boolean[] toBooleanArray(final ArrayList<Item> items_) {
        final boolean[] booleanArray = new boolean[items_.size()];
        Arrays.fill(booleanArray, false);
        for (final Item i : this.items) {
            booleanArray[items_.indexOf(i)] = true;
        }
        return booleanArray;
    }
    
    @Override
    public boolean equals(final Object anotherItemset) {
        return ((Itemset)anotherItemset).items.containsAll(this.items)
        		&& this.size == ((Itemset)anotherItemset).size;
    }
    
    @Override
    public int hashCode() {
    	return this.items().toString().hashCode();
    }
    
    public boolean equalsIgnoringSupport(final Itemset anotherItemset) {
        return anotherItemset.items.containsAll(this.items) && this.size == anotherItemset.size;
    }
    
    public boolean isIn(final ArrayList<Itemset> set) {
        boolean check = false;
        for (final Itemset i : set) {
            check = this.equalsIgnoringSupport(i);
            if (check) {
                break;
            }
        }
        return check;
    }
    
    public int searchIn(final ArrayList<Itemset> set) {
        boolean found = false;
        int index = 0;
        for (final Itemset i : set) {
            found = this.equalsIgnoringSupport(i);
            if (found) {
                break;
            }
            ++index;
        }
        return found ? index : -1;
    }
    
    public ArrayList<Itemset> listItemsetsOfLowerSize() {
        final ArrayList<Itemset> result = new ArrayList<Itemset>();
        for (int skipper = this.size() - 1; skipper >= 0; --skipper) {
            final Itemset subItemset = new Itemset();
            for (int i = 0; i < this.size(); ++i) {
                if (i != skipper) {
                    subItemset.add(this.get(i));
                }
            }
            result.add(subItemset);
        }
        return result;
    }
    
    public boolean contains(final Itemset itemset) {
        return this.items.containsAll(itemset.items);
    }
    
    public boolean contains(final Item item) {
        return this.items.contains(item);
    }
    
    @Override
    public int compareTo(final Itemset o) {
        return Integer.valueOf(this.getSupport()).compareTo(o.getSupport());
    }
    
    public static void main(String[] args) {
    	Itemset i,j;
    	i= new Itemset();
    	j = new Itemset();
    	i.add(new Item("1"));
    	j.add(new Item("1"));
    	
    	Hashtable<Itemset, String> array = new Hashtable<Itemset, String>();
    	array.put(j, "Something is here");
    	
    	System.out.println(array.get(i));
    }
}