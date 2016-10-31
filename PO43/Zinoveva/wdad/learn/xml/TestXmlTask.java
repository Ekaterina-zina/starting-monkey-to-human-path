package PO43.Zinoveva.wdad.learn.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class TestXmlTask {
    public static void main(String[] args) {
        try {
            XmlTask test = new XmlTask();
            String date = "2016.09.22";
            Calendar calendar = Calendar.getInstance();
            DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            calendar.setTime(sdf.parse(date));
            System.out.println(test.earningsTotal("ivanov", calendar));
            //test.removeDay(calendar);
            test.changeOfficiantName("Аркадий", "sidorov", "Аркадий", "petrov");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
