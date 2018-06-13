package ovh.delalande.gaetan.selfback.Util;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import ovh.delalande.gaetan.shared.Util.DataMapKeys;
import ovh.delalande.gaetan.shared.Util.SenderPath;

public class DataReceiverService extends WearableListenerService  {

    private static final String TAG = "SelfBACK/MsgReceiver";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for (DataEvent dataEvent : dataEventBuffer){
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                if (path.startsWith(SenderPath.SENSOR_TYPE)){
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
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message : " + messageEvent.getPath());
    }
}
