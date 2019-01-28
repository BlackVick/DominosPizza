package com.sri.dominospizza.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;
import com.sri.dominospizza.Cart;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Database.Database;
import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.Model.Order;
import com.sri.dominospizza.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Scarecrow on 2/6/2018.
 */

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_cart_price;
    public ElegantNumberButton cartQuantity;
    public ImageView cartImg;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_cart_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        cartQuantity = (ElegantNumberButton) itemView.findViewById(R.id.cart_quantity_button);
        cartImg = (ImageView)itemView.findViewById(R.id.cartImage);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0,0, getAdapterPosition(), Common.DELETE);
    }
}

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70, 70)
                .centerCrop()
                .into(holder.cartImg);

        holder.cartQuantity.setNumber(listData.get(position).getQuantity());
        holder.cartQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //calculate total amount and update price
                int total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item:orders)
                    total += ((Integer.parseInt(item.getPrice()) - (Integer.parseInt(item.getDiscount())))) * (Integer.parseInt(item.getQuantity()));
                    total = total + 250;
                Locale locale = new Locale("en", "NG");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.totalTxt.setText(fmt.format(total));

            }
        });


        Locale locale = new Locale("en", "NG");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_cart_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
