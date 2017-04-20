package com.example.xoapit.piratenews.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.xoapit.piratenews.Adapter.ArticleAdapter;
import com.example.xoapit.piratenews.Model.Article;
import com.example.xoapit.piratenews.Others.RecyclerItemClickListener;
import com.example.xoapit.piratenews.Others.XmlParser;
import com.example.xoapit.piratenews.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.example.xoapit.piratenews.Activities.SettingActivity.readSettingFromFile;
import static com.example.xoapit.piratenews.Fragments.ArticleFragment.readDataFromUrl;

public class SearchActivity extends AppCompatActivity {

    private String mTextSearch = "";
    private List<Article> mArticles;
    private SearchView mSearchView;
    private ArticleAdapter mArticleAdapter;
    private int mTypeNormalNews = 2;
    private RecyclerView mRecyclerView;
    private ArrayList<String> mTitleArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndSetSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initToolbar();
        SearchActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        mRecyclerView.setHasFixedSize(true);
        mTitleArticles = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem itemSearch = menu.findItem(R.id.btnSearchScreen);

        mSearchView = (SearchView) itemSearch.getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                mTextSearch = text;
                mArticles = new ArrayList<Article>();
                new ReadData().execute("http://vietnamnet.vn/rss/thoi-su.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/the-gioi.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/giai-tri.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/phap-luat.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/the-thao.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/giao-duc.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/suc-khoe.rss");
                new ReadData().execute("http://vietnamnet.vn/rss/ban-doc.rss");

                mArticleAdapter = new ArticleAdapter(mArticles, getApplicationContext(), mTypeNormalNews);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mArticleAdapter);

                mRecyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(SearchActivity.this, ContentActivity.class);
                                intent.putExtra("URL", mArticles.get(position).getLink());
                                intent.putExtra("IMG", mArticles.get(position).getImg());
                                intent.putExtra("TITLE", mArticles.get(position).getTitle());
                                startActivityForResult(intent, 1);
                            }
                        })
                );

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private class ReadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readDataFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                XmlParser parser = new XmlParser();
                Document document = parser.getDocument(s);
                NodeList nodeListItem = document.getElementsByTagName("item");

                int numberOfArticles = nodeListItem.getLength();
                for (int i = 0; i < numberOfArticles; i++) {
                    try {
                        Element element = (Element) nodeListItem.item(i);
                        String title = parser.getValue(element, "title");
                        String link = parser.getValue(element, "link");
                        String time = parser.getValue(element, "pubDate");
                        String img = parser.getValue(element, "image");
                        if (title.indexOf(mTextSearch) != -1) {
                            if (!mTitleArticles.contains(title)) {
                                mTitleArticles.add(title);
                                mArticles.add(new Article(title, "", img, link, time));
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(SearchActivity.this, "Error when parse", Toast.LENGTH_SHORT).show();
                    }
                }
                mArticleAdapter.notifyDataSetChanged();
                super.onPostExecute(s);
            } catch (Exception e) {
                Toast.makeText(SearchActivity.this, "No Connected", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndSetSetting(){
        try {
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

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
