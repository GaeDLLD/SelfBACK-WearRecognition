package ovh.delalande.gaetan.selfback.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import ovh.delalande.gaetan.shared.Util.DataMapKeys;
import ovh.delalande.gaetan.shared.Util.SenderPath;

public class SenderService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "SelfBACK/SenderService";

    private static SenderService instance;
    public static SenderService getInstance(Context context) {
        if (instance == null) instance = new SenderService(context);
        return instance;
    }

    private GoogleApiClient googleApiClient;
    private Context context;

    public SenderService(Context context){
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Node> connectedNodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

                Log.d(TAG,"Listing nodes");

                for (Node node:connectedNodes) {
                    if(node.isNearby()){
                        Log.d(TAG, "Connected to " + node.getDisplayName());
                        Intent intent = new Intent(DataMapKeys.WATCH_NAME);
                        intent.putExtra(DataMapKeys.WATCH_NAME, node.getDisplayName());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }else{
                        Log.d(TAG, node.getDisplayName() + "isn't nearby, can't send data");
                    }
                }
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sendSensor(int sensorType, int action){
        DataMap dataMap = new DataMap();
        dataMap.putInt(DataMapKeys.SENSOR_TYPE, sensorType);
        dataMap.putInt(DataMapKeys.ACTION_LIST, action);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SenderPath.SENSOR_TYPE);
        putDataMapRequest.getDataMap().putDataMap(DataMapKeys.SENSOR_TYPE, dataMap);
        send(putDataMapRequest, SenderPath.SENSOR_TYPE);
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
}
