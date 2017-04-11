package com.example.xoapit.piratenews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xoapit.piratenews.Activities.ContentActivity;
import com.example.xoapit.piratenews.Activities.MainActivity;
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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleFragment extends Fragment {
    private List<Article> mArticles;
    private RecyclerView mRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private int mType;
    private String mUrl;

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
        }catch (Exception e){
            Toast.makeText(getContext(),"No Connection",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMain);
        mRecyclerView.setHasFixedSize(true);

        if (mType == 0) mArticleAdapter = new ArticleAdapter(mArticles, getContext(), 0);
        else if (mType == 1) mArticleAdapter = new ArticleAdapter(mArticles, getContext(), 1);
        else mArticleAdapter = new ArticleAdapter(mArticles, getContext(), 2);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mArticleAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ContentActivity.class);
<<<<<<< HEAD
                        intent.putExtra("URL", mArticles.get(position).getLink());
                        Toast.makeText(getActivity(), mArticles.get(position).getLink(), Toast.LENGTH_SHORT).show();
=======
                        intent.putExtra("URL",mArticles.get(position).getLink());
                        //Toast.makeText(getActivity(),mArticles.get(position).getLink(),Toast.LENGTH_SHORT).show();
>>>>>>> origin/Xoapit
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
<<<<<<< HEAD
            try {
                XmlParser parser = new XmlParser();
                Document document = parser.getDocument(s);
                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListDescription = document.getElementsByTagName("description");

                String img = "";
                String title = "";
                String link = "";
                String time = "";
                for (int i = 0; i < nodeList.getLength(); i++) {
                    try {
                        String cdata = nodeListDescription.item(i + 1).getTextContent();
=======
            XmlParser parser = new XmlParser();
            Document document = parser.getDocument(s);
            NodeList nodeList= document.getElementsByTagName("item");
            NodeList nodeListDescription= document.getElementsByTagName("description");

            String img="";
            String title="";
            String link="";
            String time="";
            if (mType == 1 ){
                for(int i=0; i<4 ;i++){
                    try {
                        String cdata = nodeListDescription.item(i+1).getTextContent();
>>>>>>> origin/Xoapit
                        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                        Matcher matcher = p.matcher(cdata);
                        if (matcher.find()) {
                            img = matcher.group(1);
                        }

                        Element element = (Element) nodeList.item(i);
                        title = parser.getValue(element, "title");
                        link = parser.getValue(element, "link");
                        time = parser.getValue(element, "pubDate");
<<<<<<< HEAD
                        mArticles.add(new Article(title, img, link, time));
                    } catch (Exception e) {
=======
                        mArticles.add(new Article(title,img,link,time));
                    }catch (Exception e){
>>>>>>> origin/Xoapit
                        Toast.makeText(getActivity(), "Error when parse", Toast.LENGTH_SHORT).show();
                    }
                }
                writeArticlesOffline((ArrayList<Article>) mArticles);
                mArticleAdapter.notifyDataSetChanged();
                super.onPostExecute(s);
            }catch (Exception e){
                Toast.makeText(getContext(),"Not Connected",Toast.LENGTH_LONG).show();
            }
<<<<<<< HEAD
=======
            else {
                for(int i=0; i<nodeList.getLength();i++){
                    try {
                        String cdata = nodeListDescription.item(i+1).getTextContent();
                        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                        Matcher matcher = p.matcher(cdata);
                        if (matcher.find()) {
                            img = matcher.group(1);
                        }

                        Element element = (Element) nodeList.item(i);
                        title = parser.getValue(element, "title");
                        link = parser.getValue(element, "link");
                        time = parser.getValue(element, "pubDate");
                        mArticles.add(new Article(title,img,link,time));
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Error when parse", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            mArticleAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
>>>>>>> origin/Xoapit
        }
    }

    private String readDataFromUrl(String theUrl) {
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
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
