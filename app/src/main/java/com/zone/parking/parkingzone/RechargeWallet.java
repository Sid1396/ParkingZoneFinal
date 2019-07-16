package com.zone.parking.parkingzone;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class RechargeWallet extends AppCompatActivity {

    TextView balance;
    Button recharge;
    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    DatabaseReference parkingSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wallet);

        balance=(TextView)findViewById(R.id.TVBAL);
        recharge=(Button) findViewById(R.id.BTNRECHARGE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser uid = mAuth.getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        parkingSlots = FirebaseDatabase.getInstance().getReference();
        final String id = uid.getUid();

        fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String bal = documentSnapshot.getString("points");
                balance.setText(bal);
            }
        });

        }
        public void browser1(View view){
            Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("paytmmp://cash_wallet?featuretype=sendmoneymobile&recipient=8976273361"));
            startActivity(browserIntent);

        }
}

