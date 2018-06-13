package ovh.delalande.gaetan.shared.Model;

import ovh.delalande.gaetan.shared.Util.SensorsByType;

public class Sensor {

    int type;
    String name = "Unknown";
    float[] values = {};
    String unit;
    String[] valueTitle;

    public Sensor(int type){
        this.type = type;
        boolean found = false;
        for (int i = 0; i < SensorsByType.values().length; i++) {
            if (SensorsByType.values()[i].type == type){
                this.name = SensorsByType.values()[i].name;
                this.unit = SensorsByType.values()[i].unit;
                found = true;
            }
        }
        if (!found) {
            this.name = "Unknown sensor";
            this.unit = "";
        }
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
        switch (values.length){
            case 3 : valueTitle = new String[]{"X Axis", "Y Axis", "Z Axis"}; break;
            case 6 : valueTitle = new String[]{"X Axis", "Y Axis", "Z Axis", "X Axis", "Y Axis", "Z Axis"}; break;
            default : valueTitle = new String[]{"Value"}; break;
        }
    }

    public String[] getValueTitle() {
        return valueTitle;
    }

    public String getValueTitleAsString() {
        String titles = "";
        for (String title : valueTitle) titles += ", " + title;
        return titles;
    }

    public String getValuesAsString() {
        String sValues = "";
        for (float value : values) sValues += ", " + value;
        return sValues;
    }

    public String getUnit() {
        return unit;
    }
}
