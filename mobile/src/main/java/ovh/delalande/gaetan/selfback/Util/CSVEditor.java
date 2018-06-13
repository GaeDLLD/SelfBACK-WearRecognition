package ovh.delalande.gaetan.selfback.Util;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ovh.delalande.gaetan.shared.Model.Sensor;
import ovh.delalande.gaetan.shared.Model.SensorsInstance;

public class CSVEditor {
    private static final String TAG = "SelfBACK/CSVEditor";
    private static final CSVEditor ourInstance = new CSVEditor();

    public static CSVEditor getInstance() {
        return ourInstance;
    }

    private String path;
    private ArrayList<Pair<Integer, File>> files = new ArrayList<>();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");

    private CSVEditor() {
    }

    public void createFile(){
        for (Sensor sensor : SensorsInstance.getInstance().getSelectedSensorList()){
            File file = new File(path + "/" + sensor.getName()+".csv");
            files.add(new Pair(sensor.getType(),file));
        }
    }

    public void writeData(Sensor sensor, String activity) {
        Log.d(TAG, "writeData: ");
        BufferedWriter writer = null;
        for (Pair<Integer, File> filePair : files){
            if (filePair.first == sensor.getType()){
                try {
                    if (filePair.second.length() == 0){
                        writer = new BufferedWriter(new FileWriter(filePair.second));
                        writer.append("Time, Activity, Sensor" + sensor.getValueTitleAsString() + ", Unit");
                        writer.newLine();
                    }else {
                        writer = new BufferedWriter(new FileWriter(path+ "/" + sensor.getName()+".csv", true));
                        writer.append(dateFormat.format(new Date()));
                        writer.append(", "+activity);
                        writer.append(", "+sensor.getName());
                        writer.append(sensor.getValuesAsString());
                        writer.append(", "+sensor.getUnit());
                        writer.newLine();
                    }
                    Log.d(TAG, "writeData: write successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setPath(String path) {
        this.path = path + "/SelfBACK-" + dateFormat.format(new Date());
        new File(this.path).mkdirs();
    }
}
