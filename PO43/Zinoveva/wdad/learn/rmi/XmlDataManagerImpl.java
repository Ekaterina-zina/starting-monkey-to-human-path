package PO43.Zinoveva.wdad.learn.rmi;

import PO43.Zinoveva.wdad.learn.xml.XmlTask;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class XmlDataManagerImpl implements XmlDataManager {

    private String path = "src/PO43/Zinoveva/wdad/learn/xml/restaurant.xml";
    private XmlTask xmlWorker;


    XmlDataManagerImpl() throws IOException, ParserConfigurationException, SAXException{
        xmlWorker = new XmlTask(path);
    }

    public XmlDataManagerImpl(String p) throws IOException, ParserConfigurationException, SAXException {
        path = p;
        xmlWorker = new XmlTask(path);
    }

    //Получаем дату последнего выполненного официантом заказа
    public Date lastOfficiantWorkDate(Officiant officiant) throws ParseException, RemoteException, NoSuchOfficiantException {
        return xmlWorker.lastOfficiantWorkDate(officiant);
    }

    //Получаем список всех заказов указанной даты
    public List<Order> getOrders(Date date){
        List<Order> orders;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        orders = xmlWorker.getOrders(calendar);
        return orders;
    }

    //Изменяем имя официанта
    public void changeOfficiantName(Officiant oldOfficiant, Officiant newOfficiant) throws IOException{
        xmlWorker.changeOfficiantName(oldOfficiant.getFirstName(), oldOfficiant.getSecondName(), newOfficiant.getFirstName(), newOfficiant.getSecondName());
    }

    //Получаем сумму по всем выполненным официантом заказах в указанную дату
    public int earningsTotal(Officiant officiant, Date date){
        int result;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        result = xmlWorker.earningsTotal(officiant.getSecondName(), calendar);
        return result;
    }

    //Удаляем информацию об указанном дне
    public void removeDay(Date date) throws IOException{
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        xmlWorker.removeDay(calendar);
    }
}
