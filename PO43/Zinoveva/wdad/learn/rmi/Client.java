package PO43.Zinoveva.wdad.learn.rmi;

import PO43.Zinoveva.wdad.data.managers.PreferencesManager;
import PO43.Zinoveva.wdad.utils.PreferencesConstantManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        try {
            //Получаем экземпляр PreferencesManager для дальнейшего получения данных о сервере
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            //Получаем адрес сервера с Регистром объектов
            String registryAddress = preferencesManager.getProperty(PreferencesConstantManager.REGISTRY_ADDRESS);
            //и порт сервера
            int registryPort = Integer.valueOf(preferencesManager.getProperty(PreferencesConstantManager.REGISTRY_PORT));
            //Получаем ссылку на регистр сервера по указанным параметрам
            Registry registry = LocateRegistry.getRegistry(registryAddress, registryPort);
            //Создаем экземпляр интерфейса для взаимодействия с данными в XML
            XmlDataManager xmlDataManager =
                    ((XmlDataManager) registry.lookup("xmlDataManager"));
            Calendar calendar = Calendar.getInstance();
            //Проверка работы возврата даты последнего заказа
            calendar.setTime(xmlDataManager.lastOfficiantWorkDate(new Officiant("Аркадий", "petrov")));
            //Проверка работы исключения
            //calendar.setTime(xmlDataManager.lastOfficiantWorkDate(new Officiant("Неизвестный", "Официант")));
            System.out.println(calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR));
            //проверка работы получения заказов по дате
            List<Order> orders = xmlDataManager.getOrders(calendar.getTime());
            System.out.println(orders.size());
            for (Order order :
                    orders) {
                System.out.println(order.toString());
            }
            //Смена имени официанта
            xmlDataManager.changeOfficiantName(new Officiant("Василий", "ivanov"), new Officiant("Артем", "sidorov"));
            //Проверка получения общей заработанной официантом суммы
            System.out.println(xmlDataManager.earningsTotal(new Officiant("Сергей", "petrov"), calendar.getTime()));
        } catch (NoSuchOfficiantException ex){
            System.err.println("Официант " + ex.officiant.getFirstName() + " " + ex.officiant.getSecondName() + " не найден!");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
