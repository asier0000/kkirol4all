package configuration;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigXML {

    private static String configFile = "config.xml";

    private String businessLogicNode;
    private String businessLogicPort;
    private String businessLogicName;

    private static String dbFilename;

    private boolean isDatabaseInitialized;
    private boolean businessLogicLocal;
    private boolean databaseLocal;

    private String databaseNode;
    private int databasePort;

    private String user;
    private String password;
    private String locale;

    private static ConfigXML theInstance = new ConfigXML();

    private ConfigXML() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile);
            if (inputStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo de configuración: " + configFile);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("config");
            Element config = (Element) list.item(0);

            String value = ((Element) config.getElementsByTagName("businessLogic").item(0)).getAttribute("local");
            businessLogicLocal = value.equals("true");

            businessLogicNode = getTagValue("businessLogicNode", config);
            businessLogicPort = getTagValue("businessLogicPort", config);
            businessLogicName = getTagValue("businessLogicName", config);
            locale = getTagValue("locale", config);

            dbFilename = getTagValue("dbFilename", config);

            value = ((Element) config.getElementsByTagName("database").item(0)).getAttribute("local");
            databaseLocal = value.equals("true");

            String dbOpenValue = ((Element) config.getElementsByTagName("database").item(0)).getAttribute("initialize");
            isDatabaseInitialized = dbOpenValue.equals("true");

            databaseNode = getTagValue("databaseNode", config);
            databasePort = Integer.parseInt(getTagValue("databasePort", config));
            user = getTagValue("user", config);
            password = getTagValue("password", config);

        } catch (Exception e) {
            System.out.println("Error al cargar la configuración desde " + configFile);
            e.printStackTrace();
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }

    public static ConfigXML getInstance() {
        return theInstance;
    }

    public String getBusinessLogicNode() {
        return businessLogicNode;
    }

    public String getBusinessLogicPort() {
        return businessLogicPort;
    }

    public String getBusinessLogicName() {
        return businessLogicName;
    }

    public String getDbFilename() {
        return dbFilename;
    }

    public boolean isDatabaseInitialized() {
        return isDatabaseInitialized;
    }

    public boolean isBusinessLogicLocal() {
        return businessLogicLocal;
    }

    public boolean isDatabaseLocal() {
        return databaseLocal;
    }

    public String getDatabaseNode() {
        return databaseNode;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getLocale() {
        return locale;
    }
}
