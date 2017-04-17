package com.example.xoapit.piratenews.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.xoapit.piratenews.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.xoapit.piratenews.Activities.SettingActivity.readFromFile;
import static com.example.xoapit.piratenews.Fragments.ArticleFragment.readDataFromUrl;

public class ContentActivity extends AppCompatActivity {
    private WebView webView;
    private int defaultPercentage = 100;
    private int fontSizeZoom = defaultPercentage;
    private String sizeSetting = "";
    private String fontSetting = "Helvetica";
    private String themeSetting = "light";
    private String linkWeb="google.com.vn";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String settingInfo = readFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            sizeSetting = arrSetting[0];
            fontSetting = arrSetting[1];
            themeSetting = arrSetting[2];
            if (themeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
            } else {
                setTheme(R.style.LightTheme);
            }
            int stepTextSize = 20;
            fontSizeZoom = defaultPercentage + stepTextSize * Integer.parseInt(sizeSetting);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        linkWeb = intent.getStringExtra("URL");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarContent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<b>PirateNews</b>"));

        webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(INVISIBLE);
            }
        });

        ContentActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(fontSizeZoom);

        //parse url webpage to get only content of article, don't have ads
        new ParseUrl().execute(linkWeb);
    }

    private void setArticleContent(String codeHtmlOfWeb) {
        webView.loadData(codeHtmlOfWeb, "text/html; charset=UTF-8", null);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.btnXemTrangGoc: {
                webView.loadUrl(linkWeb);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class ParseUrl extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String articleContent = "";
            try {
                //get code html from url to create document and parse html xml
                Document document = Jsoup.connect(params[0]).get();
                Elements elementsBody = document.select("body");
                Element elementBody = elementsBody.get(0);
                Element elementArticleContent = elementBody.getElementById("ArticleContent");
                articleContent = elementArticleContent.outerHtml();
                articleContent = articleContent.replaceAll("a href", "a");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "<body>\n" + articleContent + "\n<body>\n";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String textColor = "color:#282830;";
            if (themeSetting.equals("dark")) {
                textColor = "color:#dcd0d1;";
            }
            String fontFamily = "font-family: '" + fontSetting + "', sans-serif;";
            setArticleContent("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />" +
                    "<meta name=\"viewport\" content=\"user-scalable=no, width=device-width\">\n" +
                    "<link href=\"https://fonts.googleapis.com/css?family=Anton|Arimo|Dancing+Script|" +
                    "Josefin+Slab|Lobster|Montserrat|Open+Sans+Condensed:300|Pacifico|Pattaya&amp;" +
                    "subset=latin-ext,vietnamese\" rel=\"stylesheet\">" +
                    "<style>img{display: inline; height: auto; max-width: 100%;}</style>\n" +
                    "<style>body{" + textColor + fontFamily + " max-width: 100%;}</style>\n" +
                    "</head>" +
                    "<body>" +
                    s +
                    "</body>" +
                    "</html>");
        }
    }
}
