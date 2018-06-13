package ovh.delalande.gaetan.selfback.Controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ovh.delalande.gaetan.selfback.R;

public class NavigationDrawerAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter{

    private Context context;

    private ArrayList<DrawerFragment> drawerFragments = new ArrayList();

    public NavigationDrawerAdapter(Context context) {
        this.context = context;

        drawerFragments.add(new DrawerFragment(R.string.activities_title, R.drawable.ic_run_white_24dp));
        drawerFragments.add(new DrawerFragment(R.string.setting_title, R.drawable.ic_settings_white_24dp));
    }

    @Override
    public CharSequence getItemText(int pos) {
        return drawerFragments.get(pos).title;
    }

    @Override
    public Drawable getItemDrawable(int pos) {
        return context.getDrawable(drawerFragments.get(pos).imagePath);
    }

    @Override
    public int getCount() {
        return drawerFragments.size();
    }

    @SuppressLint("ValidFragment")
    private class DrawerFragment extends Fragment {

        private String title;
        private int imagePath;

        private View rootView;

        public DrawerFragment(int titlePath, int imagePath){
            this.title = context.getString(titlePath);
            this.imagePath = imagePath;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.page_fragment, container, false);

            TextView titleTextView = (TextView) rootView.findViewById(R.id.page_label);
            titleTextView.setText(title);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.page_image);
            imageView.setImageResource(imagePath);

            return rootView;
        }
    }

}
