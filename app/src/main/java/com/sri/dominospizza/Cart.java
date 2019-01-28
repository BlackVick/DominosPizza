package com.sri.dominospizza;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Database.Database;
import com.sri.dominospizza.Model.MyResponse;
import com.sri.dominospizza.Model.Notification;
import com.sri.dominospizza.Model.Order;
import com.sri.dominospizza.Model.Request;
import com.sri.dominospizza.Model.Sender;
import com.sri.dominospizza.Model.Token;
import com.sri.dominospizza.Remote.APIService;
import com.sri.dominospizza.ViewHolder.CartAdapter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    public TextView totalTxt;
    FButton btnPlaceOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //initialize service
        mService = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        totalTxt = (TextView)findViewById(R.id.totalTxt);
        btnPlaceOrder = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadListFood();
                if (cart.size() > 0)
                    showAlertDialog();
                else
                {
                    Toast.makeText(Cart.this, "Your Cart Is Empty !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Thing");
        alertDialog.setMessage("Enter Your Address: ");

        //this is the update where comment tecxtbox was included
        /*final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);*/

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        //this is the
        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.detailAddressTxt);
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.detailCommentTxt);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        totalTxt.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        Common.currentProvider.getPhone(),
                        cart
                );

                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                sendNotificationOrder(order_number);
                //Toast.makeText(Cart.this, "Thank You... Order Placed", Toast.LENGTH_SHORT).show();
                //finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.orderByKey().equalTo(Common.currentProvider.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Token serverToken = postSnapshot.getValue(Token.class);

                            Notification notification = new Notification("You Have A New Order "+order_number, "Domino's Pizza");
                            Sender content = new Sender(serverToken.getToken(), notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {

                                                Toast.makeText(Cart.this, "Thank You... Order Placed", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for (Order order:cart)
            total += ((Integer.parseInt(order.getPrice()) - (Integer.parseInt(order.getDiscount())))) *(Integer.parseInt(order.getQuantity()));
            total = total + 250;

        Locale locale = new Locale("en", "NG");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalTxt.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //remove item by position
        cart.remove(position);
        //remove previous data from sqlite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //update new data list
        for (Order item:cart)
            new Database(this).addToCart(item);
        //refresh food list
        loadListFood();
    }
}
