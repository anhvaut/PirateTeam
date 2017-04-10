package com.example.xoapit.piratenews.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xoapit.piratenews.Activities.ContentActivity;
import com.example.xoapit.piratenews.Model.Article;
import com.example.xoapit.piratenews.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Xoapit on 3/30/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {
    private List<Article> mArticles;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mType;  //type index to set recyclerView with many views

    public ArticleAdapter(List<Article> articles, Context context, int type) {
        this.mArticles = articles;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mType = type;
    }

    @Override
    public ArticleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (mType == 0) {
            switch (viewType) {
                case 0:
                    itemView = mInflater.inflate(R.layout.item_list_hot_news, parent, false);
                    break;
                default:
                    itemView = mInflater.inflate(R.layout.item_list_news, parent, false);
            }
        } else if (mType == 1) {
            itemView = mInflater.inflate(R.layout.item_list_hot_news, parent, false);
        } else {
            itemView = mInflater.inflate(R.layout.item_list_news, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.MyViewHolder holder, int position) {
        Article article = mArticles.get(position);
        holder.mTvTitle.setText(article.getTitle());
        holder.mTvTime.setText(article.getTime());
        Picasso.with(this.mContext).load(article.getImg()).into(holder.mImgNews);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgNews;
        TextView mTvTitle;
        TextView mTvTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImgNews = (ImageView) itemView.findViewById(R.id.imgItemNews);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            mTvTime = (TextView) itemView.findViewById(R.id.tvItemDate);
        }
    }
}
