package ovh.delalande.gaetan.selfback.Controler;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;

import ovh.delalande.gaetan.selfback.R;

public class MainActivity extends FragmentActivity implements WearableNavigationDrawerView.OnItemSelectedListener{
    private static final String TAG = "MainWearActivity";

    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        //SenderService.getInstance(this);

        // Top Navigation Drawer
        mWearableNavigationDrawer = (WearableNavigationDrawerView) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationDrawerAdapter(this));
        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer.addOnItemSelectedListener(this);

        settingFragment = new SettingFragment();


        // Initialise fist view as Setting fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HorizontalViewPager()).commit();
    }

    @Override
    public void onItemSelected(int pos) {
        switch (pos){
            case 0 :
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HorizontalViewPager()).commit();
                break;
            case 1 :
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingFragment).commit();
                break;
        }
    }
}
