package com.example.xoapit.piratenews.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.example.xoapit.piratenews.R;

import java.util.HashMap;

public class CategoryFragment extends Fragment {
    private String url = "";
    private String[] tabTitles = new String[]{"Tất cả", "Nổi bật"};
    private HashMap<String, String> hashMapUrlList;

    public CategoryFragment(String category) {
        initHashMapUrlList();
        url = hashMapUrlList.get(category);
    }

    private void initHashMapUrlList() {
        hashMapUrlList = new HashMap<>();
        hashMapUrlList.put("trangchu", "http://vietnamnet.vn/rss/home.rss");
        hashMapUrlList.put("thoisu", "http://vietnamnet.vn/rss/thoi-su.rss");
        hashMapUrlList.put("thegioi", "http://vietnamnet.vn/rss/the-gioi.rss");
        hashMapUrlList.put("giaitri", "http://vietnamnet.vn/rss/giai-tri.rss");
        hashMapUrlList.put("phapluat", "http://vietnamnet.vn/rss/phap-luat.rss");
        hashMapUrlList.put("thethao", "http://vietnamnet.vn/rss/the-thao.rss");
        hashMapUrlList.put("giaoduc", "http://vietnamnet.vn/rss/giao-duc.rss");
        hashMapUrlList.put("suckhoe", "http://vietnamnet.vn/rss/suc-khoe.rss");
        hashMapUrlList.put("bandoc", "http://vietnamnet.vn/rss/ban-doc.rss");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int mNumOfTabs) {
            super(fm);
            this.mNumOfTabs = mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ArticleFragment(url, 2);
                case 1:
                    return new ArticleFragment(url, 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
