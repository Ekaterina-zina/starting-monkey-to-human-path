package PO43.Zinoveva.wdad.learn.xml;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import PO43.Zinoveva.wdad.learn.rmi.*;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    public XmlTask(String p) throws IOException, ParserConfigurationException, SAXException {
        path = p;
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

    public List<Order> getOrders(Calendar date) {
        //Предварительно создаем список для занесения туда заказов
        List<Order> result = new LinkedList<>();
        //Получаем все элементы дат из документа
        NodeList dateList = doc.getElementsByTagName("date");
        //Получаем общее количество полученных дат
        int datesAmount = dateList.getLength();
        //Заранее создаем экземпляр для хранения атрибутов дат, чтобы к атрибутам непосредственно через указание элемента
        NamedNodeMap dateAttributes;
        for (int i = 0; i < datesAmount; i++) {
            dateAttributes = dateList.item(i).getAttributes();
            //Сравниванием введенную дату, с текущей датой из документа
            if ((Integer.valueOf(dateAttributes.getNamedItem(DAY_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(MONTH_ATTRIBUTE_NAME).getNodeValue()) == (date.get(Calendar.MONTH) + 1)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(YEAR_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.YEAR))) {
                //Как только находим необходимую нам дату, получаем все элементы заказов из нее и передаем их по очередности в отдельный метод,
                //который возвращает нам экземпляры класса Order для последующего занесения их в список
                NodeList orders = ((Element) dateList.item(i)).getElementsByTagName("order");
                for (int j = 0; j < orders.getLength(); j++){
                    result.add(getOrder(orders.item(j).getChildNodes()));
                }
            }
        }
        return result;
    }

    private Order getOrder(NodeList orderNodes){
        Order order;
        Officiant currentOfficiant;
        NamedNodeMap attributes;
        //Получаем аттрибуты текущего официанта
        attributes = ((Element) orderNodes).getElementsByTagName("officiant").item(0).getAttributes();
        //Создаем экземпляр класса официанта с получением данных из аттрибутов элементов
        currentOfficiant = new Officiant(attributes.getNamedItem("firstname").getNodeValue(), attributes.getNamedItem("secondname").getNodeValue());
        //Создаем пустой экземпляр Order с  указанием текущего официанта
        order = new Order(currentOfficiant);
        //Получаем список блюд заказа
        NodeList itemsList = ((Element) orderNodes).getElementsByTagName("item");
        for (int i = 0; i < itemsList.getLength(); i++){
            attributes = itemsList.item(i).getAttributes();
            //поочередно добавляем каждое из них в текущий экземпляр класса Order
            order.add(new Item(attributes.getNamedItem("name").getNodeValue(), Integer.valueOf(attributes.getNamedItem("cost").getNodeValue())));
        }
        return order;
    }

    //Получаем дату последнего выполненного официантом заказа
    public Date lastOfficiantWorkDate(Officiant officiant) throws ParseException, NoSuchOfficiantException {
        //получаем список всех элементов-дат
        NodeList dateList = doc.getElementsByTagName("date");
        NamedNodeMap currentDateAttributes;
        NamedNodeMap officiantAttributes;
        //Так как нам необходимо последняя дата, а они расположены в XML документе в хронологическом порядке, то идем с конца списка дат
        for (int i = dateList.getLength()-1; i > 0; i--){
            //Получаем список официантов из текущей даты
            NodeList officiantNodes = ((Element) dateList.item(i)).getElementsByTagName("officiant");
            for (int j = 0; j < officiantNodes.getLength(); j++){
                //Получаем аттрибуты текущего официанта и проверяем, совпадают ли они с нужными
                officiantAttributes = officiantNodes.item(j).getAttributes();
                if((officiantAttributes.getNamedItem("firstname").getNodeValue().equals(officiant.getFirstName())) &&
                        officiantAttributes.getNamedItem("secondname").getNodeValue().equals(officiant.getSecondName())){
                    //Если мы встречаем нужного официанта, то получаем аттрибуты текущей даты
                    currentDateAttributes = dateList.item(i).getAttributes();
                    //Задаем формат представления этой даты
                    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    //Затем формируем строку из аттрибутов элемента даты в указанном нами формате
                    String dateString = currentDateAttributes.getNamedItem("month").getNodeValue() + "/"
                            + currentDateAttributes.getNamedItem("day").getNodeValue() + "/"
                            + currentDateAttributes.getNamedItem("year").getNodeValue();
                    //И возвращаем результат преобразования строки в Дату
                    return formatter.parse(dateString);
                }
            }
        }
        //Если мы прошли весь цикл и не встретили официанта, то выбрасываем исключение
        throw new NoSuchOfficiantException(officiant);
    }
}
