package PO43.Zinoveva.wdad.learn.rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface XmlDataManager extends java.rmi.Remote {
    int earningsTotal(Officiant officiant, Date date) throws RemoteException;

    void removeDay(Date date) throws IOException;

    void changeOfficiantName(Officiant oldOfficiant, Officiant newOfficiant) throws IOException;

    List<Order> getOrders(Date date) throws RemoteException;

    Date lastOfficiantWorkDate(Officiant officiant) throws ParseException, RemoteException, NoSuchOfficiantException;

}
