package ovh.delalande.gaetan.shared.Model;

import java.util.ArrayList;

import ovh.delalande.gaetan.shared.Util.SensorsByType;

public class SensorsInstance{
    private static final SensorsInstance ourInstance = new SensorsInstance();
    private static final String TAG = "SelfBACK/SensorInstance";

    private ArrayList<Sensor> allSensorList = new ArrayList<>();
    private ArrayList<Sensor> selectedSensorList = new ArrayList<>();


    public static SensorsInstance getInstance() {
        return ourInstance;
    }

    private SensorsInstance() {
        selectedSensorList.add(new Sensor(android.hardware.Sensor.TYPE_ACCELEROMETER));

        for (int i = 0; i < SensorsByType.values().length; i++) {
            allSensorList.add(new Sensor(SensorsByType.values()[i].type));
        }
    }

    public ArrayList<Sensor> getAllSensorList() {
        return allSensorList;
    }

    public ArrayList<Sensor> getSelectedSensorList() {
        return selectedSensorList;
    }

}
