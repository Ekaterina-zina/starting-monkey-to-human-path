package PO43.Zinoveva.wdad.data.managers;

class TestPreferencesManager {
    public static void main(String[] args) {
        try{
        PreferencesManager pm = PreferencesManager.getInstance();
        System.out.println(pm.isCreateRegistry());
        System.out.println(pm.getRegistryAddress());
        System.out.println(pm.getRegistryPort());
        System.out.println(pm.getPolicyPath());
        System.out.println(pm.getUseCodeBaseOnly());
        System.out.println(pm.getClassProvider());}
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
