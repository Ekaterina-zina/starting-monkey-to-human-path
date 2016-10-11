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

    public XmlTask() throws IOException, ParserConfigurationException, SAXException {
        makeDoc();
    }

    private void makeDoc() throws IOException, ParserConfigurationException, SAXException {
        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fXmlFile);
        checkTotalCost();
    }

    private void checkTotalCost() throws IOException {
         NodeList orderList = doc.getElementsByTagName("order");
        NodeList itemsList;
        NodeList totalCostList;
        int totalCost;
        boolean isTotalCostExist;
        for (int i = 0; i < orderList.getLength(); i++) {
            totalCost = 0;
            isTotalCostExist = false;
            itemsList = ((Element) orderList.item(i)).getElementsByTagName("item");
            totalCostList = ((Element) orderList.item(i)).getElementsByTagName("totalcost");
            for (int j = 0; j < itemsList.getLength(); j++) {
                totalCost += Integer.parseInt(itemsList.item(j).getAttributes().getNamedItem("cost").getNodeValue());
            }
            if (totalCostList.getLength() > 0) {
                totalCostList.item(0).setTextContent(String.valueOf(totalCost));
                isTotalCostExist = true;
            }
            if (!isTotalCostExist) {
                Element totalCostElement = doc.createElement("totalcost");
                totalCostElement.setTextContent(String.valueOf(totalCost));
                orderList.item(i).appendChild(totalCostElement);
                updateDoc();
            }
        }
    }

    private void writeDoc() throws IOException {
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
        NodeList dateList = doc.getElementsByTagName("date");
        NodeList orderList;
        NodeList orderNodes;
        NamedNodeMap dateAttributes;
        int earningsTotal = 0;
        boolean isOfficiantFounded = false;
        for (int i = 0; i < dateList.getLength(); i++) {
            dateAttributes = dateList.item(i).getAttributes();
            if ((Integer.valueOf(dateAttributes.getNamedItem(DAY_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(MONTH_ATTRIBUTE_NAME).getNodeValue()) == (date.get(Calendar.MONTH) + 1)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(YEAR_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.YEAR))) {
                orderList = ((Element) dateList.item(i)).getElementsByTagName("order");
                for (int j = 0; j < orderList.getLength(); j++) {
                    orderNodes = orderList.item(j).getChildNodes();
                    for (int k = 0; k < orderNodes.getLength(); k++) {
                        if (("officiant".equals(orderNodes.item(k).getNodeName())) &&
                                orderNodes.item(k).getAttributes().getNamedItem(OFFICIANT_SECOND_NAME).getNodeValue().equals(officiantSecondName)) {
                            isOfficiantFounded = true;
                        }
                        if ((isOfficiantFounded) && (orderNodes.item(k).getNodeName().equals("totalcost"))) {
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
        NodeList dateList = doc.getElementsByTagName("date");
        int datesAmount = dateList.getLength();
        NamedNodeMap dateAttributes;
        for (int i = 0; i < datesAmount; i++) {
            dateAttributes = dateList.item(i).getAttributes();
            if ((Integer.valueOf(dateAttributes.getNamedItem(DAY_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(MONTH_ATTRIBUTE_NAME).getNodeValue()) == (date.get(Calendar.MONTH) + 1)) &&
                    (Integer.valueOf(dateAttributes.getNamedItem(YEAR_ATTRIBUTE_NAME).getNodeValue()) == date.get(Calendar.YEAR))) {
                dateList.item(i).getParentNode().removeChild(dateList.item(i));
                i--;
                datesAmount--;
            }
        }
        updateDoc();
    }

    //Изменяем имя официанта
    public void changeOfficiantName(String oldFirstName, String oldSecondName, String newFirstName, String newSecondName) throws IOException {
        NodeList officiantList = doc.getElementsByTagName("officiant");
        for (int i = 0; i < officiantList.getLength(); i++) {
            NamedNodeMap officiantAttributes = officiantList.item(i).getAttributes();
            if ((officiantAttributes.getNamedItem(OFFICIANT_FIRST_NAME).getNodeValue().equals(oldFirstName)) &&
                    (officiantAttributes.getNamedItem(OFFICIANT_SECOND_NAME).getNodeValue().equals(oldSecondName))) {
                officiantAttributes.getNamedItem(OFFICIANT_FIRST_NAME).setNodeValue(newFirstName);
                officiantAttributes.getNamedItem(OFFICIANT_SECOND_NAME).setNodeValue(newSecondName);
            }
        }
        updateDoc();
    }
}
