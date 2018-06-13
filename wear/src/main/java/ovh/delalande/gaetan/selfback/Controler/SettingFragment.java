package ovh.delalande.gaetan.selfback.Controler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
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


public class SettingFragment extends Fragment {

    private static final String TAG = "SelfBACK/SettingFg";
    private View rootView;

    private WearableRecyclerView recyclerView;
    private WearableRecyclerView.Adapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.wear_recycler_view, container, false);

        recyclerView = (WearableRecyclerView) rootView.findViewById(R.id.setting_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(rootView.getContext()));

        recyclerViewAdapter = new WearRecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

    private class WearRecyclerViewAdapter extends WearableRecyclerView.Adapter<WearRecyclerViewAdapter.ViewHolder> {

        private static final String TAG = "SelfBACK/RVAdapter";

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_sensor_item, parent, false);
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

        public class ViewHolder extends WearableRecyclerView.ViewHolder implements View.OnClickListener{

            private int position;

            private TextView textView;
            private Switch switchView;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                textView = (TextView) view.findViewById(R.id.item_title);
                switchView = (Switch) view.findViewById(R.id.item_switch);
                switchView.setClickable(false);
            }

            public void setItem(int position) {
                this.position = position;
                textView.setText(SensorsInstance.getInstance().getAllSensorList().get(position).getName());
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
    }

}
