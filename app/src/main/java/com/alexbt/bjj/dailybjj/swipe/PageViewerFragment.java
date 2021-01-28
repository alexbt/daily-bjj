package com.alexbt.bjj.dailybjj.swipe;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.EntryHelper;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;

import java.util.HashMap;
import java.util.Map;

public class PageViewerFragment extends Fragment {
    private static Data data;
    private View root;

    public static class PagerAdapter extends FragmentPagerAdapter {
        public static int NUM_ITEMS = 8;
        Map<Integer, SingleImageFragment> map = new HashMap<>();

        public PagerAdapter(Activity activity, FragmentManager fragmentManager) {
            super(fragmentManager);
            String cacheDir = FileSystemHelper.getCacheDir(activity.getApplicationContext());

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            if (data == null) {
                data = EntryHelper.getInstance().loadData(cacheDir);
            }

            for (int i = NUM_ITEMS - 2; i < NUM_ITEMS; i++) {
                DailyEntry today = EntryHelper.getInstance().getVideo(data, DateHelper.getLastWeekPlusDays(i));
                map.put(i, new SingleImageFragment(today, i));
            }
            new Thread(() -> {
                for (int i = 0; i < NUM_ITEMS - 2; i++) {
                    DailyEntry today = EntryHelper.getInstance().getVideo(data, DateHelper.getLastWeekPlusDays(i));
                    map.put(i, new SingleImageFragment(today, i));
                }
            }).start();
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return map.get(position);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ViewPager vpPager = (ViewPager) root.findViewById(R.id.vpPager);

        PagerAdapter adapterViewPager = new PagerAdapter(getActivity(), getChildFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(PagerAdapter.NUM_ITEMS - 1);
        return root;
    }
}
