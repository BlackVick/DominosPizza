package com.sri.dominospizza.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.R;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodName;
    public ImageView foodImage;

    private ItemClickListener itemClickListener;



    public FoodViewHolder(View itemView) {
        super(itemView);

        foodName = (TextView)itemView.findViewById(R.id.food_name);
        foodImage = (ImageView)itemView.findViewById(R.id.food_image);

        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
