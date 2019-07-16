package com.zone.parking.parkingzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {

    EditText email,password;
    Button submitBtn, updateEmail,btnChangePass;
    FirebaseFirestore fdb;
    FirebaseAuth mAuth;
 //   FirebaseUser uid = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


   //     final String id = uid.getUid();
  //      fdb = FirebaseFirestore.getInstance();
        email = (EditText)findViewById(R.id.edEmail);
        submitBtn = (Button)findViewById(R.id.sbtn);
        updateEmail = (Button)findViewById(R.id.upemail);
        password = (EditText)findViewById(R.id.change_pass);
        btnChangePass = (Button)findViewById(R.id.btnChange_pass);



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();


                auth.sendPasswordResetEmail(getEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Verification Email sent, check your email", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getEmail = email.getText().toString();
             /*   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseUser uid = mAuth.getCurrentUser();
                final String id = uid.getUid();*/


                user.updateEmail(getEmail)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                             //       FirebaseUser uid = mAuth.getCurrentUser();
                               //     final String id = uid.getUid();
                                    Toast.makeText(ForgotPassword.this, "Email id has been updated..", Toast.LENGTH_LONG).show();
                                    //
                              //      fdb.collection("User").document(id).update("email",getEmail);
                                }
                            }
                        });

            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = password.getText().toString();


                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Reg = new Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(Reg);
        finish();
    }
}
