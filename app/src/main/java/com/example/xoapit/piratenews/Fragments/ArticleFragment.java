package com.example.xoapit.piratenews.Fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xoapit.piratenews.Activities.ContentActivity;
import com.example.xoapit.piratenews.Adapter.ArticleAdapter;
import com.example.xoapit.piratenews.Model.Article;
import com.example.xoapit.piratenews.Others.RecyclerItemClickListener;
import com.example.xoapit.piratenews.Others.XmlParser;
import com.example.xoapit.piratenews.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class ArticleFragment extends Fragment {
    private List<Article> mArticles;
    private RecyclerView mRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private int mType;
    private String mUrl;
    private boolean mIsOnline = true;
    private NotificationCompat.Builder mBuilder;
    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;

    public ArticleFragment(String url, int type) {
        this.mType = type;
        this.mUrl = url;
        mArticles = new ArrayList<Article>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    new ReadData().execute(mUrl);
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "No Connection", Toast.LENGTH_SHORT).show();
            mIsOnline = false;
        }
        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setAutoCancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMain);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    private void initArticle() {
        if (!mIsOnline) {
            mArticles = readArticlesOffline();
        }
        if (!mArticles.isEmpty()) {
            int typeBothNormalHotNews = 0;
            int typeHotNews = 1;
            int typeNormalNews = 2;
            if (mType == typeBothNormalHotNews) {
                mArticleAdapter = new ArticleAdapter(mArticles, getContext(), typeBothNormalHotNews);
            } else if (mType == typeHotNews) {
                mArticleAdapter = new ArticleAdapter(mArticles, getContext(), typeHotNews);
            } else {
                mArticleAdapter = new ArticleAdapter(mArticles, getContext(), typeNormalNews);
            }
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mArticleAdapter);
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getActivity(), ContentActivity.class);
                            intent.putExtra("URL", mArticles.get(position).getLink());
                            intent.putExtra("IMG", mArticles.get(position).getImg());
                            intent.putExtra("TITLE", mArticles.get(position).getTitle());
                            startActivityForResult(intent, 1);
                        }
                    })
            );
        }else{
            Toast.makeText(getContext(),"Not Conected",Toast.LENGTH_SHORT).show();
        }
    }

    private class ReadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return readDataFromUrl(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                mIsOnline = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                XmlParser parser = new XmlParser();
                Document document = parser.getDocument(s);
                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListDescription = document.getElementsByTagName("description");

                Element elementNotification = (Element) nodeList.item(0);
                String notificationTitle = parser.getValue(elementNotification,"title");
                String notificationLink = parser.getValue(elementNotification,"link");
                String notificationDate = parser.getValue(elementNotification,"pubDate");
                Notify(notificationTitle, notificationDate,notificationLink);

                int numberOfArticles = nodeList.getLength();
                int numberOfHotNews = 5;
                if (mType == 1) numberOfArticles = numberOfHotNews;
                ArrayList<String> titleArticles = new ArrayList<>();
                for (int i = 0; i < numberOfArticles; i++) {
                    try {
                        Element element = (Element) nodeList.item(i);
                        String title = parser.getValue(element, "title");
                        String link = parser.getValue(element, "link");
                        String time = parser.getValue(element, "pubDate");
                        String img = parser.getValue(element, "image");

                        Element elementDescription = (Element) nodeListDescription.item(i + 1);
                        String description = elementDescription.getTextContent();
                        String substance = description.substring(0, description.indexOf("br /") - 1);
                        substance = substance.replaceAll("&amp;nbsp;", " ");
                        substance = substance.replaceAll("amp;amp;", " ");
                        substance = substance.replaceAll("&#160;", " ");

                        if (!titleArticles.contains(title)) {
                            titleArticles.add(title);
                            mArticles.add(new Article(title, substance, img, link, time));
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error Parse", Toast.LENGTH_SHORT).show();
                    }
                }

                writeArticlesOffline((ArrayList<Article>) mArticles);

                int typeHotNews = 1;
                if (mType != typeHotNews) {
                    Collections.sort(mArticles, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            Article article1 = (Article) o1;
                            Article article2 = (Article) o2;
                            return article1.getTitle().compareToIgnoreCase(article2.getTitle());
                        }
                    });
                }
                super.onPostExecute(s);
            } catch (android.net.ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                mIsOnline = false;
            }
            initArticle();
            if(!mArticles.isEmpty()){
                mArticleAdapter.notifyDataSetChanged();
            }
            super.onPostExecute(s);
        }
    }

    public static String readDataFromUrl(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the url connection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private ArrayList<Article> readArticlesOffline() {
        FileInputStream fis = null;
        ArrayList<Article> articles = null;
        try {
            fis = getContext().openFileInput("articles.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            articles = (ArrayList<Article>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(articles==null){
            return (ArrayList) Collections.emptyList();
        }
        return articles;
    }

    private boolean writeArticlesOffline(ArrayList<Article> articles) {

        FileOutputStream fos = null;
        try {
            fos = getContext().openFileOutput("articles.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(articles);
            oos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //setting notification
    private void Notify(String notificationTitle, String notificationDate, String notificationURL){
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker("Tin mới nhất");
        mBuilder.setWhen(System.currentTimeMillis() + 10*1000);
        mBuilder.setContentTitle(notificationTitle);
        mBuilder.setContentText(notificationDate);

        Intent intent = new Intent(getContext(), ContentActivity.class);
        intent.putExtra("URL", notificationURL);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), MY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        manager.notify(1,notification);
    }
}
