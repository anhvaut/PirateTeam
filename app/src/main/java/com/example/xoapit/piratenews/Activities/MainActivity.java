package com.example.xoapit.piratenews.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.xoapit.piratenews.Fragments.ArticleFragment;
import com.example.xoapit.piratenews.Fragments.CategoryFragment;
import com.example.xoapit.piratenews.R;

import static com.example.xoapit.piratenews.Activities.SettingActivity.readSettingFromFile;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndSetSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);

        LinearLayout layout = (LinearLayout) findViewById(R.id.llActivityMain);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(800);
        //apply the animation ( fade In ) to your Layout
        layout.startAnimation(animation);

        int typeBothNormalAndHotNews = 0;
        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = new ArticleFragment("http://vietnamnet.vn/rss/home.rss", typeBothNormalAndHotNews);
        switchFragment(fragment);
        setTitleMenuWithCategory("trangchu");

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                String articleCategory = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_trang_chu: {
                        articleCategory = "trangchu";
                        int typeBothNormalAndHotNews = 0;
                        Fragment fragment = new ArticleFragment("http://vietnamnet.vn/rss/home.rss", typeBothNormalAndHotNews);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_thoi_su: {
                        articleCategory = "thoisu";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_the_gioi: {
                        articleCategory = "thegioi";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_giai_tri: {
                        articleCategory = "giaitri";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_phap_luat: {
                        articleCategory = "phapluat";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_the_thao: {
                        articleCategory = "thethao";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_giao_duc: {
                        articleCategory = "giaoduc";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_suc_khoe: {
                        articleCategory = "suckhoe";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_ban_doc: {
                        articleCategory = "bandoc";
                        Fragment fragment = new CategoryFragment(articleCategory);
                        switchFragment(fragment);
                        break;
                    }
                    case R.id.nav_item_setting: {
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    }
                }

                if (articleCategory != null) {
                    setTitleMenuWithCategory(articleCategory);
                }
                return false;
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSearch: {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).commit();
    }

    private void checkAndSetSetting() {
        try {
            //read setting from setting file on local
            String settingInfo = readSettingFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            String themeSetting = arrSetting[2];
            if (themeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
            } else {
                setTheme(R.style.LightTheme);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTitleMenuWithCategory(String category) {
        if (category == "trangchu") {
            category = "Trang Chủ";
        } else if (category == "thoisu") {
            category = "Thời Sự";
        } else if (category == "thegioi") {
            category = "Thế Giới";
        } else if (category == "giaitri") {
            category = "Giải Trí";
        } else if (category == "phapluat") {
            category = "Pháp Luật";
        } else if (category == "thethao") {
            category = "Thể Thao";
        } else if (category == "giaoduc") {
            category = "Giáo Dục";
            ;
        } else if (category == "suckhoe") {
            category = "Sức Khỏe";
        } else if (category == "bandoc") {
            category = "Bạn Đọc";
        } else {
            category = "PirateNews";
        }
        getSupportActionBar().setTitle(Html.fromHtml("<b>" + category + "</b>"));
    }
}
