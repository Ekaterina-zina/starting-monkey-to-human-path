package PO43.Zinoveva.wdad.learn.rmi;

import java.io.Serializable;

public class Officiant implements Serializable {

    private String firstName;
    private String secondName;

    public Officiant(String fn, String sn){
        firstName = fn;
        secondName = sn;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getSecondName(){
        return secondName;
    }
}
