package com.sri.dominospizza.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.R;

/**
 * Created by Scarecrow on 2/14/2018.
 */

public class NewsFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView newsPicture;
    public TextView newsTitle, newsDetail, newsTime;
    private ItemClickListener itemClickListener;


    public NewsFeedViewHolder(View itemView) {
        super(itemView);

        newsPicture = (ImageView)itemView.findViewById(R.id.news_image);
        newsTitle = (TextView)itemView.findViewById(R.id.news_head);
        newsDetail = (TextView)itemView.findViewById(R.id.news_detail);
        newsTime = (TextView)itemView.findViewById(R.id.news_time);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }
}
