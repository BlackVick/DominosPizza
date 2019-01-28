package com.sri.dominospizza;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Database.Database;
import com.sri.dominospizza.Model.Food;
import com.sri.dominospizza.Model.Order;

public class FoodDetail extends AppCompatActivity {

    //fnd = food name in details
    //fpd = food pride in details
    //fdd = food description in details
    //fid = food image in details
    TextView fnd, fpd, fdd;
    ImageView fid;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fab;
    ElegantNumberButton numberButton;
    String foodId="";
    Food currentFood;
    FirebaseDatabase database;
    DatabaseReference foods;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.btnCart);



            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isExists = new Database(getBaseContext()).checkFoodExists(foodId, Common.currentUser.getPhone());
                    if (!isExists) {
                        new Database(getBaseContext()).addToCart(new Order(
                                Common.currentUser.getPhone(),
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount(),
                                currentFood.getImage()

                        ));
                        Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), foodId);
                        Toast.makeText(FoodDetail.this, "Order Increased", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        fnd = (TextView)findViewById(R.id.food_name_dets);
        fpd = (TextView)findViewById(R.id.food_price_dets);
        fid = (ImageView)findViewById(R.id.food_image_dets);
        fdd =(TextView)findViewById(R.id.food_description_dets);

        //rating for update
        //ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()){

            if (Common.isConnectedToInternet(getBaseContext()))
                getDetailFood(foodId);
            else
            {
                Toast.makeText(FoodDetail.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    //Rating Dialog for update
    /*private void showRatingDialog() {
       // new App
    }*/

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(fid);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                fpd.setText(currentFood.getPrice());
                fnd.setText(currentFood.getName());
                fdd.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
