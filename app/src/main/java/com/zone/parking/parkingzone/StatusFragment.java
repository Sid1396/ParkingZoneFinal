package com.zone.parking.parkingzone;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class StatusFragment extends Fragment {

    public TextView vstatus1,vstatus2,vstatus3,vstatus4,vstatus5,vstatus6;
    FirebaseFirestore fdb;
    FirebaseAuth mAuth;
    DatabaseReference parkingSlots;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_status, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        parkingSlots = FirebaseDatabase.getInstance().getReference();
        fdb = FirebaseFirestore.getInstance();
        vstatus1=view.findViewById(R.id.Vstatus1);
        vstatus2=view.findViewById(R.id.Vstatus2);
        vstatus3=view.findViewById(R.id.Vstatus3);
        vstatus4=view.findViewById(R.id.Vstatus4);
        vstatus5=view.findViewById(R.id.Vstatus5);
        vstatus6=view.findViewById(R.id.Vstatus6);


    parkingSlots.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String slot1 = dataSnapshot.child("Slot_no_1").child("status").getValue().toString();
            String slot2 = dataSnapshot.child("Slot_no_2").child("status").getValue().toString();
            String slot3 = dataSnapshot.child("Slot_no_3").child("status").getValue().toString();
            String slot4 = dataSnapshot.child("Slot_no_4").child("status").getValue().toString();
            String slot5 = dataSnapshot.child("Slot_no_5").child("status").getValue().toString();
            String slot6 = dataSnapshot.child("Slot_no_6").child("status").getValue().toString();


            vstatus1.setText(slot1);
            vstatus2.setText(slot2);
            vstatus3.setText(slot3);
            vstatus4.setText(slot4);
            vstatus5.setText(slot5);
            vstatus6.setText(slot6);


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }



    });



    }
}
