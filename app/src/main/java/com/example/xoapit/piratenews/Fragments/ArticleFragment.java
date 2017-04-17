package com.example.xoapit.piratenews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.xoapit.piratenews.Activities.SettingActivity.readFromFile;
import static java.security.AccessController.getContext;

public class ArticleFragment extends Fragment {
    protected List<Article> mArticles;
    protected RecyclerView mRecyclerView;
    protected ArticleAdapter mArticleAdapter;
    protected int mType;
    protected String mUrl;

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
            Toast.makeText(getContext(), "No Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMain);
        mRecyclerView.setHasFixedSize(true);

        int typeBoldNormalHotNews = 0;
        int typeHotNews = 1;
        int typeNormalNews = 2;
        if (mType == typeBoldNormalHotNews) {
            mArticleAdapter = new ArticleAdapter(mArticles, getContext(), typeBoldNormalHotNews);
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
                        startActivityForResult(intent, 1);
                    }
                })
        );

        // Inflate the layout for this fragment
        return view;
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
                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListDescription = document.getElementsByTagName("description");

                String img = "";
                String title = "";
                String link = "";
                String time = "";

                String substance = "";
                int numberOfArticles = nodeList.getLength();
                int numberOfHotNews = 5;
                if (mType == 1) numberOfArticles = numberOfHotNews;
                ArrayList<String> titleArticles= new ArrayList<>();
                for (int i = 0; i < numberOfArticles; i++) {
                    try {
                        Element element = (Element) nodeList.item(i);
                        title = parser.getValue(element, "title");
                        link = parser.getValue(element, "link");
                        time = parser.getValue(element, "pubDate");
                        img = parser.getValue(element, "image");

                        Element elementDescription = (Element) nodeListDescription.item(i + 1);
                        String description = elementDescription.getTextContent();
                        substance = description.substring(0, description.indexOf("br /") - 1);
                        substance = substance.replaceAll("&amp;nbsp;", " ");
                        substance = substance.replaceAll("&#160;", " ");

                        if(!titleArticles.contains(title)){
                            titleArticles.add(title);
                            mArticles.add(new Article(title, substance, img, link, time));
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error when parse", Toast.LENGTH_SHORT).show();
                    }
                }
              
                //writeArticlesOffline((ArrayList<Article>) mArticles);
                if (mType != 1) {
                    Collections.sort(mArticles, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            Article article1 = (Article) o1;
                            Article article2 = (Article) o2;
                            return article1.getTitle().compareToIgnoreCase(article2.getTitle());
                        }
                    });
                }

                mArticleAdapter.notifyDataSetChanged();
                super.onPostExecute(s);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }

    public static String readDataFromUrl(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            // create a url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
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
            fis = getContext().openFileInput("articles");
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
        return articles;
    }

    private void writeArticlesOffline(ArrayList<Article> articles) {

        FileOutputStream fos = null;
        try {
            fos = getContext().openFileOutput("articles", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(articles);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
