package PO43.Zinoveva.wdad.learn.rmi;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Order implements Serializable {
    private Officiant officiant;
    private List<Item> items;

    public Order(Officiant of){
        officiant = of;
        items = new LinkedList<>();
    }

    public Order(Officiant of, List<Item> list){
        officiant = of;
        items = list;
    }

    public void add(Item item){
        items.add(item);
    }

    public String toString(){
        String result = "";
        result += "Офицант: " + officiant.getFirstName() + " " + officiant.getSecondName() + "\n";
        for (Item item: items
             ) {
            result += item.toString() + "\n";
        }
        return result;
    }
}
