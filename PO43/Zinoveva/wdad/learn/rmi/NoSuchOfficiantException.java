package PO43.Zinoveva.wdad.learn.rmi;

import java.io.Serializable;

public class NoSuchOfficiantException extends Exception implements Serializable  {
    public Officiant officiant;
    public NoSuchOfficiantException(Officiant of) {
        officiant = of;
    }
}
