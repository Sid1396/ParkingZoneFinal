package com.zone.parking.parkingzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccount extends AppCompatActivity {

    public  TextView updateEmail, deleteAcc, changePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        updateEmail = (TextView)findViewById(R.id.updateEmail);
        deleteAcc = (TextView)findViewById(R.id.delAcc);
        changePass = (TextView)findViewById(R.id.changePass);
        final FirebaseAuth auth = FirebaseAuth.getInstance();



        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Reg = new Intent(ManageAccount.this, ForgotPassword.class);
                startActivity(Reg);
                finish();
            }
        });


        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cp = new Intent(ManageAccount.this, ForgotPassword.class);
                startActivity(cp);
                finish();
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccount.this);
                builder.setMessage("Are you sure want to Delete your Account ? ");
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }

                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      //
                        FirebaseUser user = auth.getCurrentUser();

                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Intent del = new Intent(ManageAccount.this,LoginActivity.class);
                                    startActivity(del);
                                    finish();
                                }
                            }
                        });
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Reg = new Intent(ManageAccount.this, WelcomeActivity.class);
        startActivity(Reg);
        finish();
    }
}
