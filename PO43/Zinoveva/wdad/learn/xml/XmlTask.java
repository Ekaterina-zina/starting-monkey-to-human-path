package PO43.Zinoveva.wdad.learn.xml;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Calendar;

public class XmlTask {

    private Document doc;
    private String path = "src/PO43/Zinoveva/wdad/learn/xml/restaurant.xml";
    private static final String YEAR_ATTRIBUTE_NAME = "year";
    private static final String MONTH_ATTRIBUTE_NAME = "month";
    private static final String DAY_ATTRIBUTE_NAME = "day";
    private static final String OFFICIANT_FIRST_NAME = "firstname";
    private static final String OFFICIANT_SECOND_NAME = "secondname";

    public XmlTask() throws IOException, ParserConfigurationException, SAXException {
        makeDoc();
    }

    //формируем первоначальный документ, с которым в дальнейшем будем работать, при создании экземпляра класса,
    private void makeDoc() throws IOException, ParserConfigurationException, SAXException {
        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fXmlFile);
        //проверяем, везде ли присутствует тэг totalcost и везде ли его содержание верно
        checkTotalCost();
    }

    private void checkTotalCost() throws IOException {
        //получаем все элементы order из документа
        NodeList orderList = doc.getElementsByTagName("order");
        NodeList itemsList;
        NodeList totalCostList;
        int totalCost;
        boolean isTotalCostExist;
        //проходим в цикле по всем тегам order
        for (int i = 0; i < orderList.getLength(); i++) {
            //устанавливаем первоначальный totalCost и устанавливаем в boolean переменную то, что мы его еще не встретили
            totalCost = 0;
            isTotalCostExist = false;
            //получаем список элементов item и элементов totalCost текущего ордера
            itemsList = ((Element) orderList.item(i)).getElementsByTagName("item");
            totalCostList = ((Element) orderList.item(i)).getElementsByTagName("totalcost");
            //получаем правильное значение totalCost складывая стоимость всех item в order
            for (int j = 0; j < itemsList.getLength(); j++) {
                totalCost += Integer.parseInt(itemsList.item(j).getAttributes().getNamedItem("cost").getNodeValue());
            }
            //проверяем наличие тега totalCost тем, что список полученных элементов с таким названием больше нуля
            if (totalCostList.getLength() > 0) {
                //сразу приравниваем реальный totalCost, вместо того, что было изначально, и меняем boolean переменную, сообщая, что totalCost имеется
                totalCostList.item(0).setTextContent(String.valueOf(totalCost));
                isTotalCostExist = true;
                writeDoc();
            }
            if (!isTotalCostExist) {
                //если boolean переменная все еще false, значит тега totalCost нет, создаем такой элемент и присваеваем ему значение
                Element totalCostElement = doc.createElement("totalcost");
                totalCostElement.setTextContent(String.valueOf(totalCost));
                //затем "присоединяем" новообразованный дочерний элемент к текущему ордеру и обновляем xml документ
                orderList.item(i).appendChild(totalCostElement);
                writeDoc();
            }
        }
    }

    private void writeDoc() throws IOException {
        //сохраняем xml документ с использованием серилиализации dom объектов ?
        DOMImplementationLS domImplementationLS =
                (DOMImplementationLS) doc.getImplementation().getFeature("LS", "3.0");
        LSOutput lsOutput = domImplementationLS.createLSOutput();
        FileOutputStream outputStream = new FileOutputStream(path);
        lsOutput.setByteStream(outputStream);
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        lsSerializer.write(doc, lsOutput);
        outputStream.close();
    }

    /* Возвращает суммарную выручку
    заданного официанта, за указанный день */

    public int earningsTotal(String officiantSecondName, Calendar date) {
        //получаем список всех элементов-дат
        NodeList dateList = doc.getElementsByTagName("date");
        NodeList orderList;
        NodeList orderNodes;
        NamedNodeMap dateAttributes;
        int earningsTotal = 0;
        boolean isOfficiantFounded = false;
        for (int i = 0; i < dateList.getLength(); i++) {
            //заранее получаем аттрибуты даты, чтобы не обращаться к ним напрямую через ноду без необходимости
            dateAttributes = dateList.item(i).getAttributes();
            //проверяем, совпадает ли значение аттрибутов даты, с датой, которая была передана в качестве параметра (месяц в calendar считается с нуля, из за этого добавляем единицу)
            if ((Integer.valueOf(dateAttributes.getNamedItem(DAY_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(MONTH_ATTRIBUTE_NAME).getNodeValue()) == (date.get(Calendar.MONTH) + 1)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(YEAR_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.YEAR))) {
                //если дата совпала, то получаем список всех order внутри и начинаем проходить циклом по всем элементам внутри
                orderList = ((Element) dateList.item(i)).getElementsByTagName("order");
                for (int j = 0; j < orderList.getLength(); j++) {                    
                    orderNodes = orderList.item(j).getChildNodes();
                    isOfficiantFounded = false;
                    for (int k = 0; k < orderNodes.getLength(); k++) {                        
                        //Сначала ищем ноды, соответствующие оффицианту, если встречаем ноду с таким именем, то сравниваем имя официанта
                        if (("officiant".equals(orderNodes.item(k).getNodeName())) &&
                                orderNodes.item(k).getAttributes().getNamedItem(OFFICIANT_SECOND_NAME).getNodeValue().equals(officiantSecondName)) {
                            //если мы встречаем официанта, то исходя из того, что официант всегда идет перед totalCost, значит после него будет идти необходимая нам нода totalCost
                            isOfficiantFounded = true;
                        }
                        if ((isOfficiantFounded) && (orderNodes.item(k).getNodeName().equals("totalcost"))) {
                            //соответсвенно, заносим текущее значение totalCost в общую сумму полученную официантом
                            earningsTotal += Integer.parseInt(orderNodes.item(k).getTextContent());
                        }
                    }
                }
            }
        }
        return earningsTotal;
    }

    /*
    * Считывает данные из xml документа, удаляет из них информацию по заданному дню
    */

    public void removeDay(Calendar date) throws IOException {
        //получаем список всех элементов-дат
        NodeList dateList = doc.getElementsByTagName("date");
        //получаем количество элементов-дат
        int datesAmount = dateList.getLength();
        NamedNodeMap dateAttributes;
        for (int i = 0; i < datesAmount; i++) {
            //заранее получаем аттрибуты даты, чтобы не обращаться к ним напрямую через ноду без необходимости
            dateAttributes = dateList.item(i).getAttributes();
            //проверяем, совпадает ли значение аттрибутов даты, с датой, которая была передана в качестве параметра (месяц в calendar считается с нуля, из за этого добавляем единицу)
            if ((Integer.valueOf(dateAttributes.getNamedItem(DAY_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(MONTH_ATTRIBUTE_NAME).getNodeValue()) == (date.get(Calendar.MONTH) + 1)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(YEAR_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.YEAR))) {
                //если условие выполняется, то переходим к родительской ноде текущей ноды, и удаляем из нее текущую ноду
                //и после удаления сдвигаем цикл и i-ое цикла на единицу, т.к. сам список нод сдвинулся на единицу
                dateList.item(i).getParentNode().removeChild(dateList.item(i));
                i--;
                datesAmount--;
            }
        }
        writeDoc();
    }

    //Изменяем имя официанта
    public void changeOfficiantName(String oldFirstName, String oldSecondName, String newFirstName, String newSecondName) throws IOException {
        //получаем список всех элементов-официантов
        NodeList officiantList = doc.getElementsByTagName("officiant");
        for (int i = 0; i < officiantList.getLength(); i++) {
            //заранее получаем аттрибуты нод официанта, чтобы не обращаться к ним напрямую через ноду без необходимости
            NamedNodeMap officiantAttributes = officiantList.item(i).getAttributes();
            //проверяем каждую атрибуты каждой ноды официанта на совпадение с переданными параметрами
            if ((officiantAttributes.getNamedItem(OFFICIANT_FIRST_NAME).getNodeValue().equals(oldFirstName)) &&
                    (officiantAttributes.getNamedItem(OFFICIANT_SECOND_NAME).getNodeValue().equals(oldSecondName))) {
                //как только срабатывает условие изменяем старые значения аттрибутов на новые
                officiantAttributes.getNamedItem(OFFICIANT_FIRST_NAME).setNodeValue(newFirstName);
                officiantAttributes.getNamedItem(OFFICIANT_SECOND_NAME).setNodeValue(newSecondName);
            }
        }
        writeDoc();
    }
}
