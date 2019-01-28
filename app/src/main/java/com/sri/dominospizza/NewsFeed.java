package com.sri.dominospizza;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.Model.Food;
import com.sri.dominospizza.Model.News;
import com.sri.dominospizza.ViewHolder.NewsFeedViewHolder;

public class NewsFeed extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase db;
    DatabaseReference news;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<News, NewsFeedViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("News Feed");

        db = FirebaseDatabase.getInstance();
        news = db.getReference("News");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_news);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.newsRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadNews();
                }else {
                    Toast.makeText(NewsFeed.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadNews();
                }else {
                    Toast.makeText(NewsFeed.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void loadNews() {
        adapter = new FirebaseRecyclerAdapter<News, NewsFeedViewHolder>(News.class, R.layout.news_item, NewsFeedViewHolder.class, news) {
            @Override
            protected void populateViewHolder(NewsFeedViewHolder viewHolder, News model, int position) {
                viewHolder.newsTitle.setText(model.getNewsTitle());
                viewHolder.newsDetail.setText(model.getNewsDetail());
                viewHolder.newsTime.setText(model.getTime());
                Picasso.with(getBaseContext()).load(model.getNewsImage())
                        .into(viewHolder.newsPicture);

                final News local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

}

