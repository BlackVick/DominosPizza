package com.sri.dominospizza;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.sri.dominospizza.Model.User;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    MaterialEditText phoneEdt, passwordEdt;
    FButton signInBtn;
    com.rey.material.widget.CheckBox ckbRemember;
    TextView forgotPass;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Sign-In");

        phoneEdt = (MaterialEditText)findViewById(R.id.signInPhoneEdt);
        passwordEdt = (MaterialEditText)findViewById(R.id.signInPasswordEdt);
        signInBtn = (FButton)findViewById(R.id.signIn_signInBtn);
        ckbRemember = (com.rey.material.widget.CheckBox)findViewById(R.id.ckbRemember);
        forgotPass = (TextView)findViewById(R.id.forgotPassword);

        //remember me
        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    if (ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY, phoneEdt.getText().toString());
                        Paper.book().write(Common.PWD_KEY, passwordEdt.getText().toString());
                    }

                    final android.app.AlertDialog mDialog = new SpotsDialog(SignIn.this);
                    mDialog.show();

                    if (phoneEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty()) {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                    } else
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child(phoneEdt.getText().toString()).exists()) {
                                    mDialog.dismiss();

                                    User user = dataSnapshot.child(phoneEdt.getText().toString()).getValue(User.class);
                                    user.setPhone(phoneEdt.getText().toString());

                                    if (user.getPassword().equals(passwordEdt.getText().toString())) {
                                        Toast.makeText(SignIn.this, "Sign In Successful !", Toast.LENGTH_SHORT).show();
                                        Intent signin = new Intent(SignIn.this, Branches.class);
                                        Common.currentUser = user;
                                        startActivity(signin);
                                        finish();

                                        table_user.removeEventListener(this);

                                    } else {
                                        Toast.makeText(SignIn.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "User Does Not Exist in Database", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                }
                else
                {
                    Toast.makeText(SignIn.this, "Check Your Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText phone = (MaterialEditText)forgot_view.findViewById(R.id.forgotPhoneEdt);
        final MaterialEditText secure = (MaterialEditText)forgot_view.findViewById(R.id.confirmSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(phone.getText().toString()).getValue(User.class);

                        if (phone.getText().toString().isEmpty() || secure.getText().toString().isEmpty()) {

                            Toast.makeText(SignIn.this, "Please Enter Valid Information !", Toast.LENGTH_SHORT).show();
                        }
                        else if (dataSnapshot.child(phone.getText().toString()).exists()){
                            if (user.getSecureCode().equals(secure.getText().toString()))
                                Toast.makeText(SignIn.this, "Your Password : " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this, "Wrong Security Code", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(SignIn.this, "Number Not Exist", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SignIn.this, "Process Cancelled Internally", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
