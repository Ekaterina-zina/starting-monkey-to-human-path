package PO43.Zinoveva.wdad.learn.rmi;

import java.io.Serializable;

public class Item implements Serializable {
    private String name;
    private int cost;

    public Item(String n, int c){
        name = n;
        cost = c;
    }

    public String toString(){
        return "Название: " + name + " Цена: " + cost;
    }
}
