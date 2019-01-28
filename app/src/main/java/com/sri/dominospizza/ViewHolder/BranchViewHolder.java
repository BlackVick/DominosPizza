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

public class BranchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView logo;
    public TextView name, address, phone;
    private ItemClickListener itemClickListener;

    public BranchViewHolder(View itemView) {
        super(itemView);

        logo = (ImageView)itemView.findViewById(R.id.branch_image);
        name = (TextView)itemView.findViewById(R.id.branch_name);
        address = (TextView)itemView.findViewById(R.id.branch_address);
        phone = (TextView)itemView.findViewById(R.id.branch_phone);

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
