package com.sri.dominospizza;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.Model.Category;
import com.sri.dominospizza.ViewHolder.MenuViewHolder;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView fullNameTxt;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    RecyclerView recycler_menu;
    String providerId = "";
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (getIntent() != null)
                    providerId = getIntent().getStringExtra("ProviderId");
                if (!providerId.isEmpty() && providerId != null) {


                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadMenu(providerId);
                    else {
                        Toast.makeText(getBaseContext(), "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (getIntent() != null)
                    providerId = getIntent().getStringExtra("ProviderId");
                if (!providerId.isEmpty() && providerId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadMenu(providerId);
                    else {
                        Toast.makeText(getBaseContext(), "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });


        Paper.init(this); //initialize the SQLite phone database

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Main.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        fullNameTxt = (TextView)headerView.findViewById(R.id.fullNameTxt);
        fullNameTxt.setText(Common.currentUser.getName());

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);


    }

    private void loadMenu(String providerId) {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class,
                category.orderByChild("providerId").equalTo(providerId)) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category model, int position) {
                menuViewHolder.menuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(menuViewHolder.menuImage);
                final Category clickItem = model;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodlist = new Intent(Main.this, FoodList.class);
                        foodlist.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadMenu(providerId);

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Main.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Main.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_signout) {
            Paper.book().destroy();
            Intent signoutIntent = new Intent(Main.this, Login.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signoutIntent);

        } else if (id == R.id.nav_changePassword){
            showChangePasswordDialog();

        } else if (id == R.id.nav_settings){
            showSettingsDialog();

        }else if (id == R.id.nav_news) {
            Paper.book().destroy();
            Intent newsIntent = new Intent(Main.this, NewsFeed.class);
            startActivity(newsIntent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingsDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setTitle("Settings");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.settings_layout, null);

        final CheckBox ckbSettings = (CheckBox)layout_setting.findViewById(R.id.ckb_sub_news);

        //remember state of checkbox
        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals(false))
            ckbSettings.setChecked(false);
        else
            ckbSettings.setChecked(true);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ckbSettings.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                    //write value
                    Paper.book().write("sub_new", "true");
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);

                    //write value
                    Paper.book().write("sub_new", "false");
                }
            }
        });

        alertDialog.setView(layout_setting);
        alertDialog.show();
    }

    private void showChangePasswordDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please Fill In All Information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText password = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_Pass);
        final MaterialEditText newPassword = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_NewPass);
        final MaterialEditText confirmPass = (MaterialEditText)layout_pwd.findViewById(R.id.changePass_ConfirmNewPass);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Change Password here

                //always use android.app.alertdialog for spotsDialog
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Main.this);
                waitingDialog.show();

                //check pass
                if (password.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty() && confirmPass.getText().toString().isEmpty())
                {
                    Toast.makeText(Main.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.getText().toString().equals(Common.currentUser.getPassword())) {
                        if (newPassword.getText().toString().equals(confirmPass.getText().toString())) {

                            Map<String, Object> passwordUpdate = new HashMap<>();
                            passwordUpdate.put("password", newPassword.getText().toString());

                            DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                            user.child(Common.currentUser.getPhone()).updateChildren(passwordUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(Main.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(Main.this, "New Password Do not Match :P", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Main.this, "Wrong Password :P", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}
