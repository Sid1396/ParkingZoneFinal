package com.zone.parking.parkingzone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import javax.annotation.Nullable;

 public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
     String u, p;
     TextView sl1, sl2, sl3, sl4, sl5, sl6, s1, s2, s3, s4, s5, s6;
     Button bt1, bt2, bt3, bt4, bt5, bt6;
     public String un;
     public String pts;
     FirebaseFirestore fdb;
     DatabaseReference parkingSlots;
     FirebaseAuth mAuth;
     public int selected_hr_pts;
     int  current_points;
     int rem_pts;
     String rmpts;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_welcome);
         cast();
         mAuth = FirebaseAuth.getInstance();
         parkingSlots = FirebaseDatabase.getInstance().getReference();
         fdb = FirebaseFirestore.getInstance();
         FirebaseUser uid = mAuth.getCurrentUser();
         final String id = uid.getUid();

         fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                 //un = documentSnapshot.getString("name");
                //Toast.makeText(WelcomeActivity.this, "Welcome "+un, Toast.LENGTH_LONG).show();

             }
         });

         dataDisplay();
         buttonPressed();

         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(final View view) {

                 fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                     @Override
                     public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                         String pin = documentSnapshot.getString("key");
                         Snackbar.make(view, "Your Pin For Reserved Slot :"+pin, Snackbar.LENGTH_LONG)
                                 .setAction("Action", null).show();

                     }
                 });

             }
         });

         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                 this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();

         NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
         navigationView.setNavigationItemSelectedListener(this);
     }


     private void buttonPressed() {
         FirebaseUser uid = mAuth.getCurrentUser();
         final String id = uid.getUid();

         fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                 pts = documentSnapshot.getString("points");
                final String ifstatus = documentSnapshot.getString("slot");



                 bt1.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {

                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_1");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_1").update("key",k);
                                         fdb.collection("key").document("Slot_no_1").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);

                                         parkingSlots.child("Slot_no_1").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_1").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 1 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});





                 bt2.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {

                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_2");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_2").update("key",k);
                                         fdb.collection("key").document("Slot_no_2").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);

                                         parkingSlots.child("Slot_no_2").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_2").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 2 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});


                 bt3.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {
                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_3");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_3").update("key",k);
                                         fdb.collection("key").document("Slot_no_3").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);

                                         parkingSlots.child("Slot_no_3").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_3").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 3 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});



                 bt4.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {
                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_4");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_4").update("key",k);
                                         fdb.collection("key").document("Slot_no_4").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);


                                         parkingSlots.child("Slot_no_4").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_4").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 4 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});


                 bt5.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {


                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {
                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_5");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_5").update("key",k);
                                         fdb.collection("key").document("Slot_no_5").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);


                                         parkingSlots.child("Slot_no_5").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_5").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 5 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});


                 bt6.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {


                         if (ifstatus.equals("No_Slot")) {

                             AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                             View mView= getLayoutInflater().inflate(R.layout.spinner_layout,null);
                             builder.setTitle("Select no of hours you want to park:");
                             final Spinner mSpinner = (Spinner)mView.findViewById(R.id.spinner);
                             ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeActivity.this,
                                     android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Hours));
                             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                             mSpinner.setAdapter(adapter);

                             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     String Selected_hrs = mSpinner.getSelectedItem().toString();
                                     if(Selected_hrs.equals("1 Hr")){
                                         selected_hr_pts=100;
                                     }else if(Selected_hrs.equals("2 Hr")){
                                         selected_hr_pts=200;
                                     }else if(Selected_hrs.equals("3 Hr")){
                                         selected_hr_pts=300;
                                     }else if(Selected_hrs.equals("4 Hr")){
                                         selected_hr_pts=400;
                                     }else if(Selected_hrs.equals("5 Hr")){
                                         selected_hr_pts=500;
                                     }else if(Selected_hrs.equals("6 Hr")){
                                         selected_hr_pts=600;
                                     }
                                     current_points= Integer.parseInt(pts);
                                     if(selected_hr_pts<=current_points) {
                                         rem_pts = current_points-selected_hr_pts;
                                         rmpts = String.valueOf(rem_pts);
                                         String k = random();
                                         fdb.collection("User").document(id).update("points",rmpts);
                                         fdb.collection("User").document(id).update("slot","Slot_no_6");
                                         fdb.collection("User").document(id).update("key",k);
                                         fdb.collection("key").document("Slot_no_6").update("key",k);
                                         fdb.collection("key").document("Slot_no_6").update("id",id);
                                         fdb.collection("User").document(id).update("Reserved_Time",currentTime());
                                         fdb.collection("User").document(id).update("Reserved_Date",currentDate());
                                         fdb.collection("User").document(id).update("Reserved_Hours",Selected_hrs);


                                         parkingSlots.child("Slot_no_6").child("status").setValue("Reserved");
                                         parkingSlots.child("Slot_no_6").child("key").setValue(k);
                                         dataDisplay();
                                         Toast.makeText(WelcomeActivity.this, "Parking Slot 6 Reserved Successfully", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(getApplicationContext(),"Insufficient points",Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });

                             builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                 }
                             });
                             builder.setView(mView);
                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();

                         }else {
                             Toast.makeText(WelcomeActivity.this, "Sorry ! You already booked one slot", Toast.LENGTH_SHORT).show();
                         }
                     }});

             }

         });

     }

     public void dataDisplay() {

         parkingSlots.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 String slot1 = dataSnapshot.child("Slot_no_1").child("status").getValue().toString();
                 String slot2 = dataSnapshot.child("Slot_no_2").child("status").getValue().toString();
                 String slot3 = dataSnapshot.child("Slot_no_3").child("status").getValue().toString();
                 String slot4 = dataSnapshot.child("Slot_no_4").child("status").getValue().toString();
                 String slot5 = dataSnapshot.child("Slot_no_5").child("status").getValue().toString();
                 String slot6 = dataSnapshot.child("Slot_no_6").child("status").getValue().toString();

              if(slot1.equals("Parked")|| slot1.equals("Reserved")){
                   bt1.setEnabled(false);
               }else if(slot1.equals("Empty")){
                   bt1.setEnabled(true);
               }

                if(slot2.equals("Parked")|| slot2.equals("Reserved")){
                    bt2.setEnabled(false);
                }else if(slot2.equals("Empty")){
                    bt2.setEnabled(true);
                }


                if(slot3.equals("Parked")|| slot3.equals("Reserved")){
                    bt3.setEnabled(false);
                }else if(slot3.equals("Empty")){
                    bt3.setEnabled(true);
                }

                if(slot4.equals("Parked")|| slot4.equals("Reserved")){
                    bt4.setEnabled(false);
                }else if(slot4.equals("Empty")){
                    bt4.setEnabled(true);
                }

                if(slot5.equals("Parked")|| slot5.equals("Reserved")){
                    bt5.setEnabled(false);
                }else if(slot5.equals("Empty")){
                    bt5.setEnabled(true);
                }


                if(slot6.equals("Parked")|| slot6.equals("Reserved")){
                    bt6.setEnabled(false);
                }else if(slot6.equals("Empty")){
                    bt6.setEnabled(true);
                }



                 s1.setText(slot1);
                 s2.setText(slot2);
                 s3.setText(slot3);
                 s4.setText(slot4);
                 s5.setText(slot5);
                 s6.setText(slot6);


             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {


             }
         });


     }

     @Override
     public void onBackPressed() {
         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         if (drawer.isDrawerOpen(GravityCompat.START)) {
             drawer.closeDrawer(GravityCompat.START);
         } else {

             final AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
             builder.setMessage("Are you sure want to Exit ? ");
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
                     finish();
                 }
             });

             AlertDialog alertDialog = builder.create();
             alertDialog.show();

         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.welcome, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_logout) {
             mAuth.signOut();
             Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
             startActivity(intent);
             finish();
             return true;
         }

         return super.onOptionsItemSelected(item);
     }

     @SuppressWarnings("StatementWithEmptyBody")
     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         // Handle navigation view item clicks here.
         int id = item.getItemId();

         if (id == R.id.nav_MyProfile) {
             Intent intent= new Intent(WelcomeActivity.this,ProfileActivity.class);
             startActivity(intent);

         } else if (id == R.id.nav_ReservedSlot) {
             Intent intent= new Intent(WelcomeActivity.this,ReservedSlotActivity.class);
             startActivity(intent);


         }else if (id == R.id.nav_Settings) {
             Intent intent= new Intent(WelcomeActivity.this,ManageAccount.class);
             startActivity(intent);
         }else if (id == R.id.nav_RechargeWallet) {
             Intent intent= new Intent(WelcomeActivity.this,RechargeWallet.class);
             startActivity(intent);
         }

         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
         return true;
     }

     private void cast() {
         sl1 = (TextView) findViewById(R.id.SLOT1);
         sl2 = (TextView) findViewById(R.id.SLOT2);
         sl3 = (TextView) findViewById(R.id.SLOT3);
         sl4 = (TextView) findViewById(R.id.SLOT4);
         sl5 = (TextView) findViewById(R.id.SLOT5);
         sl6 = (TextView) findViewById(R.id.SLOT6);

         s1 = (TextView) findViewById(R.id.STATUS1);
         s2 = (TextView) findViewById(R.id.STATUS2);
         s3 = (TextView) findViewById(R.id.STATUS3);
         s4 = (TextView) findViewById(R.id.STATUS4);
         s5 = (TextView) findViewById(R.id.STATUS5);
         s6 = (TextView) findViewById(R.id.STATUS6);

         bt1 = (Button) findViewById(R.id.BTN1);
         bt2 = (Button) findViewById(R.id.BTN2);
         bt3 = (Button) findViewById(R.id.BTN3);
         bt4 = (Button) findViewById(R.id.BTN4);
         bt5 = (Button) findViewById(R.id.BTN5);
         bt6 = (Button) findViewById(R.id.BTN6);
     }


     private String currentTime(){
         String ct = DateFormat.getTimeInstance().format(new Date());
         return ct;
     }

     private  String currentDate(){
         String cd = DateFormat.getDateInstance().format(new Date());
         return cd;
     }

     private String random() {
         Random rand = new Random();
         String r = String.valueOf(rand.nextInt(10000));
         return r;

         }
     public void startAlert(String text) {
         int i = 0;
         i = Integer.parseInt(text);
         Intent intent = new Intent(this, MyBroadcastReceiver.class);
         PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
         AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
         alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i *1000), pendingIntent);
         Toast.makeText(this, "Alarm set in " + i + " seconds", Toast.LENGTH_LONG).show();
     }

 }