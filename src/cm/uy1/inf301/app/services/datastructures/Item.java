package cm.uy1.inf301.app.services.datastructures;

import java.util.ArrayList;
import java.io.Serializable;

public class Item implements Comparable<Item>, Serializable
{
    private static final long serialVersionUID = 2750302191166255552L;
    protected String label;
    protected int position;
    
    public Item(final String itemLabel) {
        this.label = itemLabel;
    }
    
    public Item(final String itemLabel, final int itsPosition) {
        this.label = itemLabel;
        this.position = itsPosition;
    }
    
    public String getName() {
        return this.label;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setName(final String newItemLabel) {
        this.label = newItemLabel;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    @Override
    public int compareTo(final Item anotherItem) {
        return this.label.compareTo(anotherItem.label);
    }
    
    @Override
    public boolean equals(final Object anotherItem) {
        return this.compareTo((Item)anotherItem) == 0;
    }
    
    public int positionIn(final ArrayList<Item> list) {
        int position = -1;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) == this) {
                position = i;
            }
        }
        return position;
    }
    
    public static void main(String[] args) {
    	
    	Item i1 = new Item("1");
    	Item i2 = new Item("1");
    	System.out.println(i1.equals(i2));
    }
}