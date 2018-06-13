package ovh.delalande.gaetan.selfback.Controler;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ovh.delalande.gaetan.selfback.Model.Page;
import ovh.delalande.gaetan.selfback.R;

public class HorizontalViewPager extends Fragment {

    private ViewPager horizontalViewPager;
    private ArrayList<VerticalViewPagerFragment> horizontalFragmentArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.horizontal_viewpager, container, false);

        horizontalViewPager = (ViewPager) rootView.findViewById(R.id.horizontal_viewpager);
        horizontalFragmentArrayList = new ArrayList<>();
        horizontalFragmentArrayList.add(new VerticalViewPagerFragment(
                new Page(rootView.getContext(), R.string.stand_page_label, R.drawable.ic_stand_white_24dp),
                new Page(rootView.getContext(), R.string.seat_page_label, R.drawable.ic_seat_white_24dp),
                new Page(rootView.getContext(), R.string.lying_page_label, R.drawable.ic_lying_white_24dp)));
        horizontalFragmentArrayList.add(new VerticalViewPagerFragment(
                new Page(rootView.getContext(), R.string.walk_up_page_label, R.drawable.ic_up_white_24dp),
                new Page(rootView.getContext(), R.string.walk_page_label, R.drawable.ic_walk_white_24dp),
                new Page(rootView.getContext(), R.string.walk_down_page_label, R.drawable.ic_down_white_24dp)));
        horizontalFragmentArrayList.add(new VerticalViewPagerFragment(
                new Page(rootView.getContext(), R.string.run_up_page_label, R.drawable.ic_up_white_24dp),
                new Page(rootView.getContext(), R.string.run_page_label, R.drawable.ic_run_white_24dp),
                new Page(rootView.getContext(), R.string.run_down_page_label, R.drawable.ic_down_white_24dp)));
        horizontalFragmentArrayList.add(new VerticalViewPagerFragment(
                new Page(rootView.getContext(), R.string.bike_page_label, R.drawable.ic_bike_white_48dp)));

        horizontalViewPager.setOffscreenPageLimit(horizontalFragmentArrayList.size());
        horizontalViewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return horizontalFragmentArrayList.get(position);
            }

            @Override
            public int getCount() {
                return horizontalFragmentArrayList.size();
            }
        });

        return rootView;
    }

    @SuppressLint("ValidFragment")
    public static class VerticalViewPagerFragment extends Fragment{

        private static final String TAG = "VerticalFragment";
        private VerticalViewPager verticalViewPager;
        private VerticalViewPagerAdapter verticalViewPagerAdapter;
        private View rootView;
        public ViewPager.SimpleOnPageChangeListener verticalPageChangeListener;

        Page[] fragments;

        public VerticalViewPagerFragment(Page... fragments){
            this.fragments = fragments;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.vertical_viewpager, container, false);
            verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.vertical_viewpager);
            verticalViewPagerAdapter = new VerticalViewPagerAdapter(getChildFragmentManager(), fragments);
            verticalViewPager.setAdapter(verticalViewPagerAdapter);
            verticalViewPager.setCurrentItem(1); // Middle item in 3 view pager

            verticalViewPager.addOnPageChangeListener(verticalPageChangeListener);

            return rootView;
        }

        private static class VerticalViewPagerAdapter extends FragmentStatePagerAdapter{

            ArrayList<Fragment> pagesArrayList = new ArrayList<>();

            public VerticalViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {
                super(fm);
                for (Fragment fragment : fragments) pagesArrayList.add(fragment);
            }

            @Override
            public int getCount() {
                return pagesArrayList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return pagesArrayList.get(position);
            }
        }
    }
}
