package PO43.Zinoveva.wdad.data.managers;

import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PreferencesManager {
    private static PreferencesManager instance;
    private static String PATH = "src/PO43/Zinoveva/wdad/resources/configuration/appconfig.xml";
    private Document doc;

    private PreferencesManager() throws ParserConfigurationException, IOException, SAXException  {
        makeDoc();
    }

    public static PreferencesManager getInstance() throws ParserConfigurationException, IOException, SAXException {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    //Выстраиваем путь от указанной ноды, непрерывно получая родителя, до тех пор пока он не станет null
    // или #document, что равно окончанию иерархии документа
    private static String getXPath(Node node) {
        Node parent = node.getParentNode();
        if (parent == null || parent.getNodeName().equals("#document")) {
            return node.getNodeName();
        }
        return getXPath(parent) + '.' + node.getNodeName();
    }

    //Устанавливаем значение параметра в appconfig по ключу
    public void setProperty(String key, String value) throws IOException, XPathExpressionException {
        //Заменяем в ключе все точки на слэши, а затем отдаем полученную строчку в xPath,
        //чтобы получить ссылку на ноду по указанному нами пути в иерархии и устанавливаем её значение
        String xPathKey = "/" + key.replace('.', '/');
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression xPathExpression = xpath.compile(xPathKey);
        ((Node) xPathExpression.evaluate(doc, XPathConstants.NODE)).setTextContent(value);
        writeDoc();
    }

    public String getProperty(String key) throws XPathExpressionException {
        //Заменяем в ключе все точки на слэши, а затем отдаем полученную строчку в xPath,
        //чтобы получить ссылку на ноду по указанному нами пути в иерархии и получаем её значение
        String xPathKey = "/" + key.replace('.', '/');
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression xPathExpression = xpath.compile(xPathKey);
        return ((Node) xPathExpression.evaluate(doc, XPathConstants.NODE)).getTextContent();
    }


    public Properties getProperties() throws XPathExpressionException {
        //Через заданное нами значение "//*[not(*)]" получаем все ноды, которые не имеют потомков, а затем,
        //от них, выстраиваем путь вверх по иерархии, чем получаем значение ключа,
        //затем получаем значение самой ноды и заносим это в экземпляр класса Properties
        Properties properties = new Properties();
        String key, value;
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[not(*)]";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            key = getXPath(nodeList.item(i));
            value = getProperty(key);
            properties.put(key, value);
        }
        return properties;
    }


    public void setProperties(Properties prop) throws IOException, XPathExpressionException {
        //Выполняем для каждой пары ключ-значение метод setProperty
        for (String key : prop.stringPropertyNames()
                ) {
            setProperty(key, prop.getProperty(key));
        }
    }

    public void addBindedObject(String name, String className) throws IOException, XPathExpressionException {
        //Проверяем наличие ноды с необходимым именем, если его не существует, то создаем новый элемент с указанными параметрами
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        String key = "/appconfig/rmi/server/bindedobject[@name='" + name + "']";
        XPathExpression xPathExpression = xpath.compile(key);
        Node objectNode = ((Node) xPathExpression.evaluate(doc, XPathConstants.NODE));
        if (objectNode == null){
            Element newNode = doc.createElement("bindedobject");
            newNode.setAttribute("name", name);
            newNode.setAttribute("class", className);
            doc.getElementsByTagName("server").item(0).appendChild(newNode);
            writeDoc();
        }
    }

    public void removeBindedObject(String name) throws IOException, XPathExpressionException {
        //Получаем объект с указанным именем, и вызываем у его родительского элемента метод,
        //удаляющий полученный нами объект
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        String key = "/appconfig/rmi/server/bindedobject[@name='" + name + "']";
        XPathExpression xPathExpression = xpath.compile(key);
        Node objectNode = ((Node) xPathExpression.evaluate(doc, XPathConstants.NODE));
        objectNode.getParentNode().removeChild(objectNode);
        writeDoc();
    }

    @Deprecated
    public boolean isCreateRegistry() {
        NodeList nodeList = doc.getElementsByTagName("createregistry");
        if (nodeList.item(0).getTextContent().equals("yes")) {
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public void setCreateRegistry(boolean createRegistry) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("createregistry");
        if (createRegistry) {
            nodeList.item(0).setTextContent("yes");
        } else {
            nodeList.item(0).setTextContent("no");
        }
        writeDoc();
    }

    @Deprecated
    public String getRegistryAddress() {
        NodeList nodeList = doc.getElementsByTagName("registryaddress");
        return nodeList.item(0).getTextContent();
    }

    @Deprecated
    public void setRegistryAddress(String s) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("registryaddress");
        nodeList.item(0).setTextContent(s);
        writeDoc();
    }

    @Deprecated
    public int getRegistryPort() {
        NodeList nodeList = doc.getElementsByTagName("registryport");
        return Integer.parseInt(nodeList.item(0).getTextContent());
    }

    @Deprecated
    public void setRegistryPort(int registryPort) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("registryport");
        nodeList.item(0).setTextContent(String.valueOf(registryPort));
        writeDoc();
    }

    @Deprecated
    public String getPolicyPath() {
        NodeList nodeList = doc.getElementsByTagName("policypath");
        return nodeList.item(0).getTextContent();
    }

    @Deprecated
    public void setPolicyPath(String s) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("policypath");
        nodeList.item(0).setTextContent(s);
        writeDoc();
    }

    @Deprecated
    public boolean getUseCodeBaseOnly() {
        NodeList nodeList = doc.getElementsByTagName("usecodebaseonly");
        if (nodeList.item(0).getTextContent().equals("yes")) {
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public void setUseCodeBaseOnly(boolean useCodeBaseOnly) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("usecodebaseonly");
        if (useCodeBaseOnly) {
            nodeList.item(0).setTextContent("yes");
        } else {
            nodeList.item(0).setTextContent("no");
        }
        writeDoc();
    }

    @Deprecated
    public String getClassProvider() {
        NodeList nodeList = doc.getElementsByTagName("classprovider");
        return nodeList.item(0).getTextContent();
    }

    @Deprecated
    public void setClassProvider(String classproviderURL) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("classprovider");
        nodeList.item(0).setTextContent(classproviderURL);
        writeDoc();
    }

    private void makeDoc() throws ParserConfigurationException, IOException, SAXException {
            File fXmlFile = new File(PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
    }

    private void writeDoc() throws IOException {
            DOMImplementationLS domImplementationLS =
                    (DOMImplementationLS) doc.getImplementation().getFeature("LS", "3.0");
            LSOutput lsOutput = domImplementationLS.createLSOutput();
            FileOutputStream outputStream = new FileOutputStream(PATH);
            lsOutput.setByteStream(outputStream);
            LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
            lsSerializer.write(doc, lsOutput);
            outputStream.close();
    }
}
