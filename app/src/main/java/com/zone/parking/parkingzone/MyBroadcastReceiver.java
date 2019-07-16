package com.zone.parking.parkingzone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MyBroadcastReceiver extends BroadcastReceiver {

    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    DatabaseReference parkingSlots;


    @Override
    public void onReceive(Context context, Intent intent) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser uid = mAuth.getCurrentUser();
        final String id = uid.getUid();

        fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String slot = documentSnapshot.getString("slot");
                if(!slot.equals("No_Slot")){
                    parkingSlots.child(slot).child("status").setValue("Empty");

                }





            }
        });


        Toast.makeText(context, "Booking canceled", Toast.LENGTH_SHORT).show();
    }
}
