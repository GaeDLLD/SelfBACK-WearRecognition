package ovh.delalande.gaetan.selfback.Controler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import io.realm.Realm;
import ovh.delalande.gaetan.selfback.R;
import ovh.delalande.gaetan.selfback.Util.DataReceiverService;
import ovh.delalande.gaetan.selfback.Util.SenderService;
import ovh.delalande.gaetan.shared.Model.SensorsInstance;
import ovh.delalande.gaetan.shared.Util.DataMapKeys;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "SelfBACK/MainActivity";

    private DrawerLayout drawerLayout;

    private WatchSensorsFragment watchSensorsFragment = new WatchSensorsFragment();
    private LiveActivitiesFragment liveActivitiesFragment = new LiveActivitiesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Realm.init(this);

        SensorsInstance.getInstance();
        startService(new Intent(this, DataReceiverService.class));
        startService(new Intent(this, SenderService.class));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set first fragment on start application as Live Activities
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, liveActivitiesFragment).commit();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(DataMapKeys.WATCH_NAME));
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String watchName = intent.getStringExtra(DataMapKeys.WATCH_NAME);
            setWatchName(watchName);
            Log.d(TAG, "Watch name received : " + watchName);
        }
    };

    private void setWatchName(String watchName){
        TextView connectedWatch = (TextView) findViewById(R.id.connected_watch);
        connectedWatch.setText("Connected to " + watchName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_live_activities :
                getSupportActionBar().setTitle(R.string.app_name);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, liveActivitiesFragment).commit();
                break;
            case R.id.nav_watch_sensors :
                getSupportActionBar().setTitle(R.string.sensor_settings_title);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, watchSensorsFragment).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
