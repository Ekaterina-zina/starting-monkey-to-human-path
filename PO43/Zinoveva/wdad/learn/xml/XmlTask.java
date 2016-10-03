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
    private String path = "src/PO43/Zinoveva/wdad/learn/xml/first.xml";

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
        int totalCost;
        boolean isTotalCostExist;
        for (int i = 0; i < orderList.getLength(); i++) {
            totalCost = 0;
            isTotalCostExist = false;
            for (int j = 0; j < orderList.item(i).getChildNodes().getLength(); j++) {
                if (orderList.item(i).getChildNodes().item(j).getNodeName().equals("item")) {
                    totalCost += Integer.parseInt(orderList.item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
                } else if (orderList.item(i).getChildNodes().item(j).getNodeName().equals("totalcost")) {
                    orderList.item(i).getChildNodes().item(j).setTextContent(String.valueOf(totalCost));
                    isTotalCostExist = true;
                    writeDoc();
                }
            }
            if (!isTotalCostExist) {
                Element totalCostElement = doc.createElement("totalcost");
                totalCostElement.setTextContent(String.valueOf(totalCost));
                orderList.item(i).appendChild(totalCostElement);
                writeDoc();
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
        boolean isOfficiantFounded = false;
        for (int i = 0; i < dateList.getLength(); i++) {
            if ((Integer.valueOf(dateList.item(i).getAttributes().item(0).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateList.item(i).getAttributes().item(1).getNodeValue()) == date.get(Calendar.MONTH)) &&
                    (Integer.valueOf(dateList.item(i).getAttributes().item(2).getNodeValue()) == date.get(Calendar.YEAR))) {
                orderList = dateList.item(i).getChildNodes();
                for (int j = 0; j < orderList.getLength(); j++) {
                    if (orderList.item(j).getNodeName().equals("order")) {
                        orderNodes = orderList.item(j).getChildNodes();
                        for (int k = 0; k < orderNodes.getLength(); k++) {
                            if ((orderNodes.item(k).getNodeName().equals("officiant")) &&
                                    orderNodes.item(k).getAttributes().item(1).getNodeValue().equals(officiantSecondName)) {
                                isOfficiantFounded = true;
                            }
                            if ((isOfficiantFounded) && (orderNodes.item(k).getNodeName().equals("totalcost"))) {
                                return Integer.parseInt(orderNodes.item(k).getTextContent());
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    /*
    * Считывает данные из xml документа, удаляет из них информацию по заданному дню
    */

    public void removeDay(Calendar date) throws IOException {
        NodeList dateList = doc.getElementsByTagName("date");
        int datesAmount = dateList.getLength();
        for (int i = 0; i < datesAmount; i++) {
            if ((Integer.valueOf(dateList.item(i).getAttributes().item(0).getNodeValue()) == date.get(Calendar.DAY_OF_MONTH)) &&
                    (Integer.valueOf(dateList.item(i).getAttributes().item(1).getNodeValue()) == date.get(Calendar.MONTH)) &&
                    (Integer.valueOf(dateList.item(i).getAttributes().item(2).getNodeValue()) == date.get(Calendar.YEAR))) {
                dateList.item(i).getParentNode().removeChild(dateList.item(i));
                i--;
                datesAmount--;
            }
        }
        writeDoc();
    }

    //Изменяем имя официанта
    public void changeOfficiantName(String oldFirstName, String oldSecondName, String newFirstName, String newSecondName) throws IOException {
        NodeList officiantList = doc.getElementsByTagName("officiant");
        for (int i = 0; i < officiantList.getLength(); i++) {
            NamedNodeMap officiantAttributes = officiantList.item(i).getAttributes();
            if ((officiantAttributes.item(0).getNodeValue().equals(oldFirstName)) &&
                    (officiantAttributes.item(1).getNodeValue().equals(oldSecondName))) {
                officiantAttributes.item(0).setNodeValue(newFirstName);
                officiantAttributes.item(1).setNodeValue(newSecondName);
            }
        }
        writeDoc();
    }
}
