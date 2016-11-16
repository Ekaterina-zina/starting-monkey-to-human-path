package PO43.Zinoveva.wdad.learn.rmi;

import PO43.Zinoveva.wdad.data.managers.PreferencesManager;
import PO43.Zinoveva.wdad.utils.PreferencesConstantManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            //Получаем экземпляр PreferencesManager для дальнейшего получения данных о сервере
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            //Получаем значения порта, на котором будет располагаться сервер и регистр
            int port = Integer.valueOf(preferencesManager.getProperty(PreferencesConstantManager.REGISTRY_PORT));
            //Создаем регистр на порте указанном в appconfig
            Registry registry = LocateRegistry.createRegistry(port);
            //Создаем обзект интерфейса для взаимодействия с XML
            XmlDataManager xmlDataManager = new XmlDataManagerImpl();
            //Даем возможность объекту получать удаленные вызовы
            java.rmi.server.UnicastRemoteObject.exportObject(xmlDataManager, 3000);
            //Заносим экземпляр интерфейса в регистр с указанием имени
            registry.bind("xmlDataManager", xmlDataManager);
            preferencesManager.addBindedObject("xmlDataManager","XmlDataManagerImpl");
            System.err.println("Сервер готов");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
