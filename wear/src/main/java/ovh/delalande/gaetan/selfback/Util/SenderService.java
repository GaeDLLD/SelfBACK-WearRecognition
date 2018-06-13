package ovh.delalande.gaetan.selfback.Util;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ovh.delalande.gaetan.shared.Model.Sensor;
import ovh.delalande.gaetan.shared.Model.SensorsInstance;
import ovh.delalande.gaetan.shared.Util.DataMapKeys;
import ovh.delalande.gaetan.shared.Util.SenderPath;

public class SenderService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "SelfBACK/SenderService";

    private static SenderService instance;
    private static boolean isSending = false;

    public static SenderService getInstance(Context context) {
        if (instance == null) instance = new SenderService(context);
        return instance;
    }

    private Context context;
    private SensorManager sensorManager;
    private ArrayList<Long> senderArraylist = new ArrayList();

    private GoogleApiClient googleApiClient;
    private View currentActivity;

    private SenderService(Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    public boolean isSending() {
        return isSending;
    }

    public void setIsSending(boolean isSending) {
        SenderService.isSending = isSending;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Node> connectedNodes =
                        Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

                Log.d(TAG,"Listing nodes");

                for (Node node:connectedNodes) {
                    if(node.isNearby()){
                        Log.d(TAG, "Sending data to " + node.getDisplayName());
                    }else{
                        Log.d(TAG, node.getDisplayName() + "isn't nearby, can't send data");
                    }
                }
            }
        }).start();
    }

    public void sendSensor(int sensorType, int action){
        DataMap dataMap = new DataMap();
        dataMap.putInt(DataMapKeys.SENSOR, sensorType);
        dataMap.putInt(DataMapKeys.ACTION_LIST, action);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SenderPath.SENSOR);
        putDataMapRequest.getDataMap().putDataMap(DataMapKeys.SENSOR, dataMap);
        send(putDataMapRequest, SenderPath.SENSOR);
    }

    public void onClickActivity(final View newActivity, final String activityName){
        if (!isSending()){
            sendActivity(newActivity, activityName);
            setSenderData();
            currentActivity = newActivity;
        }else{
            if (!currentActivity.equals(newActivity)){
                sendActivity(newActivity, activityName);
                setSenderData();
                setIsSending(true);
            }else {
                sendActivity(newActivity, "Pause recording");
                setIsSending(false);
            }
            currentActivity.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void sendActivity(View view, String name){
        view.setBackgroundColor(this.context.getResources().getColor(ovh.delalande.gaetan.shared.R.color.colorPrimary));
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(SenderPath.ACTIVITY_TITLE);
        dataMapRequest.getDataMap().putString(DataMapKeys.ACTIVITY_TITLE, name);
        send(dataMapRequest, SenderPath.ACTIVITY_TITLE);
    }

    private void setSenderData(){
        setIsSending(true);
        for (Sensor sensor : SensorsInstance.getInstance().getSelectedSensorList())
            new DataSender(sensor.getType()).start();
    }

    public void sendSensorData(int sensor_type, float[] values){
        DataMap dataMap = new DataMap();
        dataMap.putInt(DataMapKeys.SENSOR_TYPE, sensor_type);
        dataMap.putFloatArray(DataMapKeys.SENSOR_DATA, values);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SenderPath.SENSOR_DATA);
        putDataMapRequest.getDataMap().putDataMap(DataMapKeys.SENSOR_DATA, dataMap);
        send(putDataMapRequest, SenderPath.SENSOR_DATA);
    }

    private void send(PutDataMapRequest putDataMapRequest, final String path){
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataRequest.setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess())
                            Log.d(TAG, "Failed to send on " + path + " : "
                                    + dataItemResult.getStatus().getStatusCode());
                        else Log.d(TAG, "Success to send on " + path);
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class DataSender extends Thread implements SensorEventListener {
        public static final String THREAD_NAME= "SelfBACK/DataSender";
        int sensorType;

        public DataSender(int sensorType){
            this.sensorType = sensorType;
            this.setName(DataSender.THREAD_NAME);

            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }

        @Override
        public void run() {
            super.run();
            senderArraylist.add(getId());
            if (isSending()) sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            Log.d(TAG, "onSensorChanged: " + sensorType + " : " + values + " id : " + getId());
            sendSensorData(sensorType, values);
            if (!isSending()) {
                sensorManager.unregisterListener(this);
                new AtomicBoolean(false).set(false);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    }
}
