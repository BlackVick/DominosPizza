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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.Model.User;

import dmax.dialog.SpotsDialog;

public class SignUp extends AppCompatActivity {

    MaterialEditText phoneEdt, nameEdt, passwordEdt, repeatPassEdt, secureCode;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Sign-Up");

        phoneEdt = (MaterialEditText)findViewById(R.id.newPhoneEdt);
        nameEdt = (MaterialEditText)findViewById(R.id.newNameEdt);
        passwordEdt = (MaterialEditText)findViewById(R.id.newPasswordEdt);
        secureCode = (MaterialEditText)findViewById(R.id.secureCodeEdt);
        signUpBtn = (Button)findViewById(R.id.signUp_signUpBtn);
        repeatPassEdt = (MaterialEditText)findViewById(R.id.newPasswordRepeatEdt);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //this is to check for internet connection
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //dialog showing progress
                    final android.app.AlertDialog mdialog = new SpotsDialog(SignUp.this);
                    mdialog.show();

                    //first check if the text boxes are empty.
                    if (phoneEdt.getText().toString().isEmpty() || nameEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty() || secureCode.getText().toString().isEmpty()) {
                        mdialog.dismiss();
                        Toast.makeText(SignUp.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                    } else
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Check if user already in Database
                                if (dataSnapshot.child(phoneEdt.getText().toString()).exists()) {
                                    mdialog.dismiss();
                                    Toast.makeText(SignUp.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                                } else if (passwordEdt.getText().toString().equals(repeatPassEdt.getText().toString())) {
                                    mdialog.dismiss();
                                    User user = new User(nameEdt.getText().toString(), passwordEdt.getText().toString(), secureCode.getText().toString());
                                    table_user.child(phoneEdt.getText().toString()).setValue(user);

                                    Toast.makeText(SignUp.this, "Sign Up Successful\n You Can Sign In", Toast.LENGTH_SHORT).show();
                                    Intent signUpIntent = new Intent(SignUp.this, SignIn.class);
                                    startActivity(signUpIntent);
                                    finish();
                                }
                                else {
                                    mdialog.dismiss();
                                    Toast.makeText(SignUp.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SignUp.this, "Process Cancelled Internally", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
                else
                {
                    Toast.makeText(SignUp.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

}
