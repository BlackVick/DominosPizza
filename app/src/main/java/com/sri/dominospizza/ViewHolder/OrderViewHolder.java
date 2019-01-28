package com.sri.dominospizza.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.R;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView orderIdTxt, orderStatusTxt, orderPhoneTxt, orderAddressTxt;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        orderAddressTxt = (TextView)itemView.findViewById(R.id.order_address);
        orderStatusTxt = (TextView)itemView.findViewById(R.id.order_status);
        orderIdTxt = (TextView)itemView.findViewById(R.id.order_id);
        orderPhoneTxt = (TextView)itemView.findViewById(R.id.order_phone);

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
