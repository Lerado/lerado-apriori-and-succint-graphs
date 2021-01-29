package cm.uy1.inf301.app.services;
import cm.uy1.inf301.app.services.datastructures.Item;
import cm.uy1.inf301.app.services.datastructures.Itemset;
import cm.uy1.inf301.app.services.datastructures.Vertex;

import java.util.Arrays;
import java.util.Set;
import java.util.ArrayList;

public class Utils
{
    public static boolean[] lineToBooleanArray(final ArrayList<String> line, final ArrayList<Item> items) {
        final boolean[] booleanArray = new boolean[items.size()];
        Arrays.fill(booleanArray, false);
        for (final String s : line) {
            final Item item = new Item(s);
            booleanArray[items.indexOf(item)] = true;
        }
        return booleanArray;
    }
    
    public static ArrayList<Item> booleanArrayToLine(final boolean[] vector, final ArrayList<Item> items) {
        int index = 0;
        final ArrayList<Item> line = new ArrayList<Item>();
        for (final boolean b : vector) {
            if (b) {
                line.add(items.get(index));
            }
            ++index;
        }
        return line;
    }
    
    public static boolean[][] toBooleanArray(final ArrayList<ArrayList<String>> matrix, final ArrayList<Item> items) {
        final boolean[][] booleanArray = new boolean[matrix.size()][items.size()];
        int i = 0;
        for (final ArrayList<String> line : matrix) {
            booleanArray[i++] = lineToBooleanArray(line, items);
        }
        return booleanArray;
    }
    
    public static boolean searchItemsetIn(final Itemset itemset, final boolean[] transaction) {
        boolean match = true;
        for (int i = 0; i < itemset.size(); ++i) {
            match = transaction[itemset.get(i).getPosition()];
            if (!match) {
                break;
            }
        }
        return match;
    }
    
    public static boolean searchIn(final boolean[] itemset, final boolean[] transaction) {
        int i;
        boolean match;
        for (i = 0, match = true; i < itemset.length && match; ++i) {
            if (itemset[i]) {
                match = transaction[i];
            }
        }
        return match;
    }
    
    public static boolean searchIn(final boolean[] itemset, final boolean[][] transactions) {
        boolean match = false;
        for (final boolean[] transaction : transactions) {
            match = searchIn(itemset, transaction);
            if (match) {
                break;
            }
        }
        return match;
    }
    
    public static ArrayList<Itemset> subset(final ArrayList<Itemset> setOfItemsets, final boolean[] transaction) {
        final ArrayList<Itemset> result = new ArrayList<Itemset>();
        for (final Itemset itemset : setOfItemsets) {
            //System.out.print("Itemset: ");
            // System.out.println(itemset);
            if (searchItemsetIn(itemset, transaction)) {
                result.add(itemset);
            }
        }
        return result;
    }
    
    public static ArrayList<Itemset> oneExtension(final Itemset itemset, final boolean[] transaction, final ArrayList<Item> items) {
        final ArrayList<Itemset> result = new ArrayList<Itemset>();
        final ArrayList<Item> excludedItems = booleanArrayToLine(transaction, items);
        excludedItems.removeAll(itemset.items());
        for (final Item item : excludedItems) {
        	if(item.getName().compareTo(itemset.items().get(itemset.size() - 1).getName()) > 0) {
	            Itemset extendedItemset = new Itemset(itemset.items());
	            extendedItemset.add(item);
	            result.add(extendedItemset);
        	}
        }
        return result;
    }
    
    public static Vertex findVertex(int index, Set<Vertex> set) {
    	
    	for(Vertex v: set) {
    		if(v.getIndex() == index) return v;
    	}
    	
    	return null;
    }
}