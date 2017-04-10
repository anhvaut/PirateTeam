package com.example.xoapit.piratenews.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xoapit.piratenews.Activities.MainActivity;
import com.example.xoapit.piratenews.Adapter.ArticleAdapter;
import com.example.xoapit.piratenews.Model.Article;
import com.example.xoapit.piratenews.R;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {
    private List<Article> mArticles;
    private RecyclerView mRecyclerView;
    private int mType;
    private String mUrl;

    public ArticleFragment(String url, int type) {
        this.mType = type;
        this.mUrl = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, null);

        mArticles = new ArrayList<Article>();
        initData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMain);
        mRecyclerView.setHasFixedSize(true);

        ArticleAdapter articleAdapter;
        if (mType == 0) articleAdapter = new ArticleAdapter(mArticles, getContext(), 0);
        else if (mType == 1) articleAdapter = new ArticleAdapter(mArticles, getContext(), 1);
        else articleAdapter = new ArticleAdapter(mArticles, getContext(), 2);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(articleAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void initData() {
        mArticles.add(new Article("Vietnamese man found decapitated after falling from 15th floor in Singapore", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("Ngh? si d?i l?p dòi lu?n t?i T?ng th?ng Philippines vì ch?u thua Trung Qu?c", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("Putin: Cáo bu?c Nga can thi?p b?u c? M? là '?o tu?ng và d?i trá'", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("Vietnam criticizes US award for detained blogger 'Mother Mushroom'", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("toi la ai", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("toi la ai", "link anh", "link lienket", "10:10 30/3/2017"));
        mArticles.add(new Article("toi la ai", "link anh", "link lienket", "10:10 30/3/2017"));
    }
}
