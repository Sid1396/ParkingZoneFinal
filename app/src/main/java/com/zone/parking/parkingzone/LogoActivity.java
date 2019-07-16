package com.zone.parking.parkingzone;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class LogoActivity extends AppCompatActivity {

    DatabaseReference parkingSlots;

    private  static int splash = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        parkingSlots =FirebaseDatabase.getInstance().getReference();
   //    generateTable();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(LogoActivity.this, WelcomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, splash);


    }

    private void generateTable() {

        parkingSlots.child("Slot_no_1").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_1").child("key").setValue(random());

        parkingSlots.child("Slot_no_2").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_2").child("key").setValue(random());

        parkingSlots.child("Slot_no_3").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_3").child("key").setValue(random());

        parkingSlots.child("Slot_no_4").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_4").child("key").setValue(random());

        parkingSlots.child("Slot_no_5").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_5").child("key").setValue(random());

        parkingSlots.child("Slot_no_6").child("status").setValue("Empty");
        parkingSlots.child("Slot_no_6").child("key").setValue(random());
    }
    private String random(){
        Random rand = new Random();
        String r = String.valueOf(rand.nextInt(10000 ));
        return r;
    }

}