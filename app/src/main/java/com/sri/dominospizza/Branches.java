package com.sri.dominospizza;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Interface.ItemClickListener;
import com.sri.dominospizza.Model.Provider;
import com.sri.dominospizza.Model.Token;
import com.sri.dominospizza.ViewHolder.BranchViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Branches extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference branches;
    FirebaseRecyclerAdapter<Provider, BranchViewHolder> adapter;
    RecyclerView recycler_branches;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Provider, BranchViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stores");

        //if there is a valid user login, update token to server
        if (Common.currentUser != null) {
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
        Paper.init(this);

        //Firebase Initialization
        database = FirebaseDatabase.getInstance();
        branches = database.getReference("Provider");

        recycler_branches = (RecyclerView)findViewById(R.id.recycler_branches);
        recycler_branches.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_branches.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getBaseContext()))
            loadMenu();
        else
        {
            Toast.makeText(getBaseContext(), "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //One the search bar is clicked.. brings up the list and reduces the number on entering search index
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Branch Address");
        loadSuggest();

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recycler_branches.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Provider, BranchViewHolder>(
                Provider.class,
                R.layout.branch_list,
                BranchViewHolder.class,
                branches.orderByChild("address").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(BranchViewHolder viewHolder, Provider model, int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.address.setText(model.getAddress());
                Picasso.with(getBaseContext()).load(model.getLogo())
                        .into(viewHolder.logo);

                final Provider local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //fix intent
                        Intent foodDetail = new Intent(Branches.this, Main.class);
                        foodDetail.putExtra("ProviderId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recycler_branches.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        branches
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Provider item = postSnapshot.getValue(Provider.class);
                            suggestList.add(item.getAddress());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Branches.this, "The Was an Internal Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Provider, BranchViewHolder>(Provider.class, R.layout.branch_list, BranchViewHolder.class, branches) {
            @Override
            protected void populateViewHolder(final BranchViewHolder viewHolder, final Provider model, int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.address.setText(model.getAddress());
                viewHolder.phone.setText(model.getPhone());
                Picasso.with(getBaseContext()).load(model.getLogo())
                        .into(viewHolder.logo);

                viewHolder.setItemClickListener(new ItemClickListener() {

                    Provider provider = new Provider(viewHolder.phone.toString(), viewHolder.name.toString(), viewHolder.address.toString());

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        provider.setPhone(viewHolder.phone.getText().toString());

                        Intent branchList = new Intent(Branches.this, Main.class);
                        branchList.putExtra("ProviderId", adapter.getRef(position).getKey());
                        Common.currentProvider = provider;
                        startActivity(branchList);
                    }
                });
            }
        };
        recycler_branches.setAdapter(adapter);
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);//false because the token is from a client app and not an administrator app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ordersMenu) {
            //fix the intent
            Intent orderIntent = new Intent(Branches.this, OrderStatus.class);
            startActivity(orderIntent);
        }

        if (id == R.id.exitMenu){
            Paper.book().destroy();
            Intent signoutIntent = new Intent(Branches.this, Login.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
