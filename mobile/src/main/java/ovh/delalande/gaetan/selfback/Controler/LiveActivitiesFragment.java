package ovh.delalande.gaetan.selfback.Controler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;

import java.util.ArrayList;

import ovh.delalande.gaetan.shared.Model.Sensor;
import ovh.delalande.gaetan.shared.Model.SensorsInstance;
import ovh.delalande.gaetan.selfback.R;
import ovh.delalande.gaetan.selfback.Util.CSVEditor;
import ovh.delalande.gaetan.shared.Util.DataMapKeys;

@SuppressLint("ValidFragment")
public class LiveActivitiesFragment extends Fragment {
    private static final String TAG = "SelfBACK/LiveFragment";

    private View rootView;

    private TextView activityTextView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private String activityTitle;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.live_activities_fragment, container, false);

        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(activityReceiver, new IntentFilter(DataMapKeys.ACTIVITY_TITLE));
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(dataReceiver, new IntentFilter(DataMapKeys.SENSOR_DATA));

        activityTextView = (TextView) rootView.findViewById(R.id.activity_title);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.live_activities_recycle_view);
        recyclerView.setHasFixedSize(true);

        recyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new LiveActivitiesFragment.RecyclerViewAdapter(SensorsInstance.getInstance().getSelectedSensorList());
        recyclerView.setAdapter(recyclerViewAdapter);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.live_fragment_appbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                setDataRecorder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Save data in CSV file
    private void setDataRecorder() {
        // Ask permission to read and write on storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && rootView != null && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199);
        }
        // Show explorer to choose directory
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(this.getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.show();
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                CSVEditor.getInstance().setPath(path);
                Log.d(TAG, "SELECTED_PATH : " + path);
                CSVEditor.getInstance().createFile();
            }
        });
    }

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            activityTitle = intent.getStringExtra(DataMapKeys.ACTIVITY_TITLE);
            activityTextView.setText(activityTitle);
        }
    };

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle dataMap = intent.getBundleExtra(DataMapKeys.SENSOR_DATA);
            Log.d(TAG, "Live data received : " + dataMap);
            for (int i = 0; i < SensorsInstance.getInstance().getSelectedSensorList().size(); i++) {
                if (dataMap.get(DataMapKeys.SENSOR_TYPE).equals(SensorsInstance.getInstance().getSelectedSensorList().get(i).getType())){
                    SensorsInstance.getInstance().getSelectedSensorList().get(i).setValues((float[]) dataMap.get(DataMapKeys.SENSOR_DATA));
                    CSVEditor.getInstance().writeData(SensorsInstance.getInstance().getSelectedSensorList().get(i), activityTitle);
                }
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }
    };

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private ArrayList<Sensor> sensors;

        public RecyclerViewAdapter(ArrayList<Sensor> sensors) {
            this.sensors = sensors;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private Sensor item;

            private TextView nameTextView;
            private ListView valuesListView;
            private ValueAdapter valueAdapter;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.sensor_name);
                valuesListView = (ListView) itemView.findViewById(R.id.value_list);
            }

            public void setItem(Sensor item){
                this.item = item;
                nameTextView.setText(this.item.getName());
                valueAdapter = new ValueAdapter(getActivity(), item);
                this.valuesListView.setAdapter(valueAdapter);
            }
        }

        private class ValueAdapter extends BaseAdapter{

            private Context context;
            private LayoutInflater inflater;
            private Sensor sensor;

            public ValueAdapter(Context context, Sensor sensor){
                this.context = context;
                this.sensor = sensor;
                this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return this.sensor.getValues().length;
            }

            @Override
            public Object getItem(int position) {
                return this.sensor.getValues()[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = inflater.inflate(R.layout.live_value_item, parent, false);

                TextView valueTextView = (TextView) rowView.findViewById(R.id.sensor_value);
                TextView unitTextView = (TextView) rowView.findViewById(R.id.sensor_value_unit);
                TextView typeTextView = (TextView) rowView.findViewById(R.id.sensor_value_type);

                typeTextView.setText(this.sensor.getValueTitle()[position]);

                valueTextView.setText(""+this.sensor.getValues()[position]);
                unitTextView.setText(this.sensor.getUnit());

                return rowView;
            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.live_activities_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setItem(this.sensors.get(position));
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
}
