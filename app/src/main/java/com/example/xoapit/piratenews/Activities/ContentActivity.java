package com.example.xoapit.piratenews.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.xoapit.piratenews.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.view.View.INVISIBLE;
import static com.example.xoapit.piratenews.Activities.SettingActivity.readSettingFromFile;

public class ContentActivity extends AppCompatActivity {
    private WebView mWebView;
    private int mDefaultPercentage = 100;
    private int mFontSizeZoom = mDefaultPercentage;
    private String mSizeSetting = "";
    private String mFontSetting = "Helvetica";
    private String mThemeSetting = "light";
    private String mLinkWeb = "google.com.vn";
    private String mTitle = null;
    private String mImg = null;

    private ShareActionProvider mShareActionProvider;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndSetSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initToolbar();
        ContentActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        Intent intent = getIntent();
        mLinkWeb = intent.getStringExtra("URL");
        mTitle = intent.getStringExtra("TITLE");
        mImg = intent.getStringExtra("IMG");

        mWebView = (WebView) findViewById(R.id.webView);
        setSettingWebView(mWebView);

        //parse url webpage to get only content of article, don't have ads
        new ParseUrl().execute(mLinkWeb);
    }

    private void checkAndSetSetting() {
        try {
            String settingInfo = readSettingFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            mSizeSetting = arrSetting[0];
            mFontSetting = arrSetting[1];
            mThemeSetting = arrSetting[2];
            if (mThemeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
            } else {
                setTheme(R.style.LightTheme);
            }
            int stepTextSize = 20;
            mFontSizeZoom = mDefaultPercentage + stepTextSize * Integer.parseInt(mSizeSetting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setArticleContent(String codeHtmlOfWeb) {
        mWebView.loadData(codeHtmlOfWeb, "text/html; charset=UTF-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
        MenuItem shareItem = menu.findItem(R.id.menuItemShare);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        myShareActionProvider.setShareIntent(createShareIntent());
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String link = mLinkWeb;
        String title = mTitle;
        String text = mTitle + "\n" + mLinkWeb;
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        String img = mImg;
        if (img != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(img));
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.setType("text/plain");
        }
        return shareIntent;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.btnXemTrangGoc: {
                mWebView.loadUrl(mLinkWeb);
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
            if (mThemeSetting.equals("dark")) {
                textColor = "color:#dcd0d1;";
            }
            String fontFamily = "font-family: '" + mFontSetting + "', sans-serif;";
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

    private void setSettingWebView(WebView webView){
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
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(mFontSizeZoom);
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarContent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<b>PirateNews</b>"));
    }
}
