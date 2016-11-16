package PO43.Zinoveva.wdad.data.managers;

import PO43.Zinoveva.wdad.utils.PreferencesConstantManager;

class TestPreferencesManager {
    public static void main(String[] args) {
        try {
            String key = PreferencesConstantManager.CREATE_REGISTRY;
            PreferencesManager pm = PreferencesManager.getInstance();
            pm.setProperty(key, "no");
            System.out.println(pm.getProperty(key));
            pm.setProperty(key, "yes");
            pm.getProperties().list(System.out);
            //pm.getProperties().store(new FileOutputStream("prop.txt"), "something");
            //Properties test = new Properties();
            //test.load(new FileInputStream("prop.txt"));
            //pm.setProperties(test);
            pm.addBindedObject("test", "testObject");
            //pm.removeBindedObject("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
