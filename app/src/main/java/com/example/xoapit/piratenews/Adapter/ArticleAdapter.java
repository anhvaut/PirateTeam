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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
        int typeBoldNormalNews = 0;
        int typeHotNews = 1;
        int typeNormalNews = 2;
        if (mType == typeBoldNormalNews) {
            switch (viewType) {
                case 0:
                    itemView = mInflater.inflate(R.layout.item_list_hot_news, parent, false);
                    break;
                default:
                    itemView = mInflater.inflate(R.layout.item_list_news, parent, false);
            }
        } else if (mType == typeHotNews) {
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
        if (mType == 1) {
            holder.mTvSubtance.setText(article.getSubstance());
        } else if (((mType == 0) && (position == 0))) {
            holder.mTvSubtance.setText(article.getSubstance());
        }

        holder.mTvTime.setText(getDateFromTime(article.getTime()));
        try {
            Picasso.with(this.mContext).load(article.getImg()).into(holder.mImgNews);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgNews;
        TextView mTvTitle;
        TextView mTvTime;
        TextView mTvSubtance;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImgNews = (ImageView) itemView.findViewById(R.id.imgItemNews);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvItemTitle);
            mTvTime = (TextView) itemView.findViewById(R.id.tvItemDate);
            mTvSubtance = (TextView) itemView.findViewById(R.id.tvItemSubstance);
        }
    }

    public String getDateFromTime(String time) {
        String date = time.substring(0, 10);//get date from time string
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        int yyToday = calendar.get(Calendar.YEAR);
        int mmToday = calendar.get(Calendar.MONTH);
        int ddToday = calendar.get(Calendar.DATE);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy");
            Date datePublic = simpleDateFormat.parse(date);
            Date today = simpleDateFormat.parse(String.valueOf(mmToday + 1) + "/" + String.valueOf(ddToday) + "/" + String.valueOf(yyToday));
            int subDate = 0;
            if (datePublic.before(today)) {
                subDate = (int) ((today.getTime() - datePublic.getTime()) / (1000 * 60 * 60 * 24));//milisecond*second*minute*hour in a day
            }
            if (subDate == 0) {
                return "vài giờ trước";
            } else if (subDate / 30 > 0) {
                return String.valueOf(subDate / 30) + " tháng trước";
            } else {
                return String.valueOf(subDate) + " ngày trước";
            }
        } catch (ParseException e) {
            return date;
        }
    }
}
