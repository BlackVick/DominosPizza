package com.sri.dominospizza;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    Button signIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init SQLite
        Paper.init(this);

        //if user ticked remember me, login is automatic from this page
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }

        signUp = (Button)findViewById(R.id.signUpBtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(Login.this, SignUp.class);
                startActivity(signUp);
                finish();
            }
        });

        signIn = (Button)findViewById(R.id.signInBtn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(Login.this, SignIn.class);
                startActivity(signIn);
                finish();
            }
        });
    }

    private void login(final String phone, final String pwd) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference("User");

            //loading dialog
            final android.app.AlertDialog mdialog = new SpotsDialog(Login.this);
            mdialog.show();

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mdialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);

                        user.setPhone(phone);

                        if (user.getPassword().equals(pwd)){
                            Toast.makeText(Login.this, "Sign In Successful !", Toast.LENGTH_SHORT).show();
                            Intent signin = new Intent(Login.this, Branches.class);
                            Common.currentUser = user;
                            startActivity(signin);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mdialog.dismiss();
                        Toast.makeText(Login.this, "User Does Not Exist in Database", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Login.this, "Process Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(Login.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
