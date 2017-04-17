package com.example.xoapit.piratenews.Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.xoapit.piratenews.Adapter.ArticleAdapter;
import com.example.xoapit.piratenews.Fragments.ArticleFragment;
import com.example.xoapit.piratenews.Fragments.CategoryFragment;
import com.example.xoapit.piratenews.Model.Article;
import com.example.xoapit.piratenews.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.xoapit.piratenews.Activities.SettingActivity.readFromFile;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String settingInfo = readFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            String themeSetting=arrSetting[2];
            if (themeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
            }else{
                setTheme(R.style.LightTheme);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //apply new toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);

        LinearLayout layout = (LinearLayout) findViewById(R.id.llActivityMain);
        AlphaAnimation animation = new AlphaAnimation(0.0f , 1.0f ) ;
        animation.setFillAfter(true);
        animation.setDuration(1000);
        //apply the animation ( fade In ) to your LAyout
        layout.startAnimation(animation);

        //dua fragment vao frame
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.content_frame,new ArticleFragment("http://vietnamnet.vn/rss/home.rss",0)).commit();
        getSupportActionBar().setTitle(Html.fromHtml("<b>Trang Chủ</b>"));
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nav_item_trang_chu: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new ArticleFragment("http://vietnamnet.vn/rss/home.rss",0)).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Trang Chủ</b>"));
                        break;
                    }
                    case R.id.nav_item_thoi_su: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("thoisu")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Thời Sự</b>"));
                        break;
                    }
                    case R.id.nav_item_the_gioi: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("thegioi")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Thế Giới</b>"));
                        break;
                    }
                    case R.id.nav_item_giai_tri: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("giaitri")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Giải Trí</b>"));
                        break;
                    }
                    case R.id.nav_item_phap_luat: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("phapluat")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Pháp Luật</b>"));
                        break;
                    }
                    case R.id.nav_item_the_thao: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("thethao")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Thể Thao</b>"));
                        break;
                    }
                    case R.id.nav_item_giao_duc: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("giaoduc")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Giáo Dục</b>"));
                        break;
                    }
                    case R.id.nav_item_suc_khoe: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("suckhoe")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Sức Khỏe</b>"));
                        break;
                    }
                    case R.id.nav_item_ban_doc: {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new CategoryFragment("bandoc")).commit();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>Bạn Đọc</b>"));
                        break;
                    }
                    case R.id.nav_item_setting: {
                        Intent intent= new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    }
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
        switch (item.getItemId()){
            case R.id.btnSearch: {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
