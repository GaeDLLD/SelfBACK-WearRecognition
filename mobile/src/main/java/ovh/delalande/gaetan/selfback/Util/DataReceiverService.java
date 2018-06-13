package ovh.delalande.gaetan.selfback.Util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import io.realm.Realm;
import ovh.delalande.gaetan.shared.Util.DataMapKeys;
import ovh.delalande.gaetan.shared.Util.SenderPath;

public class DataReceiverService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "SelfBACK/DataReceiver";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        Log.d(TAG, "DataReceiverService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DataReceiverService done");
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "Data received");

        for (DataEvent dataEvent : dataEventBuffer){
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                if (path.startsWith(SenderPath.ACTIVITY_TITLE)){
                    String activityTitle = DataMapItem.fromDataItem(dataItem).getDataMap().getString(DataMapKeys.ACTIVITY_TITLE);
                    Log.d(TAG, "onDataChanged: " + activityTitle);
                    Intent intent = new Intent(DataMapKeys.ACTIVITY_TITLE);
                    intent.putExtra(DataMapKeys.ACTIVITY_TITLE, activityTitle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }else if (path.startsWith(SenderPath.SENSOR_DATA)){
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap().getDataMap(DataMapKeys.SENSOR_DATA);
                    Log.d(TAG, "Receive data : " + dataMap.toString());
                    Intent intent = new Intent(DataMapKeys.SENSOR_DATA);
                    intent.putExtra(DataMapKeys.SENSOR_DATA, dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }else if (path.startsWith(SenderPath.SENSOR_TYPE)){
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap().getDataMap(DataMapKeys.SENSOR_TYPE);
                    Log.d(TAG, "onDataChanged: " + dataMap);
                    Intent intent = new Intent(DataMapKeys.SENSOR_TYPE);
                    intent.putExtra(DataMapKeys.SENSOR_TYPE, dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }
}
