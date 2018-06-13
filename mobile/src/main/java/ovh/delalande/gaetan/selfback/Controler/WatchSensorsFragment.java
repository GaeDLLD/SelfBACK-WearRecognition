package ovh.delalande.gaetan.selfback.Controler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import ovh.delalande.gaetan.selfback.R;
import ovh.delalande.gaetan.selfback.Util.SenderService;
import ovh.delalande.gaetan.shared.Model.Sensor;
import ovh.delalande.gaetan.shared.Model.SensorsInstance;
import ovh.delalande.gaetan.shared.Util.DataMapKeys;

public class WatchSensorsFragment extends Fragment {

    private static final String TAG = "SelfBACK/SensorsList";

    private View rootView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.watch_sensors_fragment, container, false);
        Log.d(TAG, "onCreateView");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.sensor_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private int position;

            private TextView nameTextView;
            private TextView unitTextView;
            private Switch switchView;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                nameTextView = (TextView) itemView.findViewById(R.id.item_title);
                unitTextView = (TextView) itemView.findViewById(R.id.sensor_unit);
                switchView = (Switch) itemView.findViewById(R.id.item_switch);
                switchView.setClickable(false);
            }

            public void setItem(int position) {
                this.position = position;
                nameTextView.setText(SensorsInstance.getInstance().getAllSensorList().get(position).getName());
                unitTextView.setText("Unit : " + SensorsInstance.getInstance().getAllSensorList().get(position).getUnit());
                for (Sensor sensor : SensorsInstance.getInstance().getSelectedSensorList())
                    if (sensor.getType() == SensorsInstance.getInstance().getAllSensorList().get(position).getType())
                        switchView.setChecked(true);
            }

            @Override
            public void onClick(View v) {
                for (Sensor sensor : SensorsInstance.getInstance().getSelectedSensorList()){
                    if (sensor.getType() == SensorsInstance.getInstance().getAllSensorList().get(position).getType()){
                        SensorsInstance.getInstance().getSelectedSensorList().remove(sensor);
                        switchView.setChecked(false);
                        SenderService.getInstance(rootView.getContext()).sendSensor(sensor.getType(), DataMapKeys.ACTION_REMOVE);
                        return;
                    }
                }
                SensorsInstance.getInstance().getSelectedSensorList().add(
                        SensorsInstance.getInstance().getAllSensorList().get(position));
                switchView.setChecked(true);
                SenderService.getInstance(rootView.getContext()).sendSensor(
                        SensorsInstance.getInstance().getAllSensorList().get(position).getType(),
                        DataMapKeys.ACTION_ADD);
            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.watch_sensor_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setItem(position);
        }

        @Override
        public int getItemCount() {
            return SensorsInstance.getInstance().getAllSensorList().size();
        }
    }
}
