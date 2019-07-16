package com.zone.parking.parkingzone;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;


public class RetriveFragment extends Fragment {


    public TextView vr1,vr2,vr3,vr4,vr5,vr6;
    public Button vrbtn1,vrbtn2,vrbtn3,vrbtn4,vrbtn5,vrbtn6;
    FirebaseFirestore fdb;
    FirebaseAuth mAuth;
    DatabaseReference parkingSlots;
    public EditText Rkey;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "FC:A8:9A:00:6B:6F";










    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






        mAuth = FirebaseAuth.getInstance();
        parkingSlots = FirebaseDatabase.getInstance().getReference();
        fdb = FirebaseFirestore.getInstance();

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTState();

        vr1=view.findViewById(R.id.VRstatus1);
        vr2=view.findViewById(R.id.VRstatus2);
        vr3=view.findViewById(R.id.VRstatus3);
        vr4=view.findViewById(R.id.VRstatus4);
        vr5=view.findViewById(R.id.VRstatus5);
        vr6=view.findViewById(R.id.VRstatus6);


        vrbtn1=view.findViewById(R.id.VRBTN1);
        vrbtn2=view.findViewById(R.id.VRBTN2);
        vrbtn3=view.findViewById(R.id.VRBTN3);
        vrbtn4=view.findViewById(R.id.VRBTN4);
        vrbtn5=view.findViewById(R.id.VRBTN5);
        vrbtn6=view.findViewById(R.id.VRBTN6);
        Rkey=view.findViewById(R.id.ETRKEY);

        ButtonPressed();

        parkingSlots.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String slot1 = dataSnapshot.child("Slot_no_1").child("status").getValue().toString();
                String slot2 = dataSnapshot.child("Slot_no_2").child("status").getValue().toString();
                String slot3 = dataSnapshot.child("Slot_no_3").child("status").getValue().toString();
                String slot4 = dataSnapshot.child("Slot_no_4").child("status").getValue().toString();
                String slot5 = dataSnapshot.child("Slot_no_5").child("status").getValue().toString();
                String slot6 = dataSnapshot.child("Slot_no_6").child("status").getValue().toString();


                if(slot1.equals("Reserved")|| slot1.equals("Empty")){
                    vrbtn1.setEnabled(false);
                }else if(slot1.equals("Parked")){
                    vrbtn1.setEnabled(true);
                }

                if(slot2.equals("Reserved")|| slot2.equals("Empty")){
                    vrbtn2.setEnabled(false);
                }else if(slot2.equals("Parked")){
                    vrbtn2.setEnabled(true);
                }


                if(slot3.equals("Reserved")|| slot3.equals("Empty")){
                    vrbtn3.setEnabled(false);
                }else if(slot3.equals("Parked")){
                    vrbtn3.setEnabled(true);
                }

                if(slot4.equals("Reserved")|| slot4.equals("Empty")){
                    vrbtn4.setEnabled(false);
                }else if(slot4.equals("Parked")){
                    vrbtn4.setEnabled(true);
                }

                if(slot5.equals("Reserved")|| slot5.equals("Empty")){
                    vrbtn5.setEnabled(false);
                }else if(slot5.equals("Parked")){
                    vrbtn5.setEnabled(true);
                }


                if(slot6.equals("Reserved")|| slot6.equals("Empty")){
                    vrbtn6.setEnabled(false);
                }else if(slot6.equals("Parked")){
                    vrbtn6.setEnabled(true);
                }

                vr1.setText(slot1);
                vr2.setText(slot2);
                vr3.setText(slot3);
                vr4.setText(slot4);
                vr5.setText(slot5);
                vr6.setText(slot6);






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {

        Toast.makeText(getActivity(), title + " - " + message, Toast.LENGTH_LONG).show();

    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

    private void ButtonPressed() {




        vrbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_1").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_1").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("1");

                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });





        vrbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_2").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_2").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("2");

                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        vrbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_3").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_3").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("3");
                        }
                    }
                });
            }
        });

        vrbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_4").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_4").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("4");
                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        vrbtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_5").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_5").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("5");
                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vrbtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String entered_key = Rkey.getText().toString();
                fdb.collection("key").document("Slot_no_6").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String id = documentSnapshot.getString("id");
                        String ukey = documentSnapshot.getString("key");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_6").child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                            fdb.collection("User").document(id).update("Retrive_Time",currentTime());
                            sendData("6");
                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void timeFetch(final String id , String pt, String rt, String cpts){


      String diff =  timeDiffrence(pt,rt);
        updatePoints(cpts,diff,id);

    }

    private void updatePoints(String cp, String diff,String id) {

        int currentPoints = Integer.parseInt(cp);
        int calculatedPoints = Integer.parseInt(diff);
        int finalPoints = currentPoints-calculatedPoints;
        String fp = String.valueOf(finalPoints);
       // fdb.collection("User").document(id).update("calculated_points",fp);
   //     upts.setText(fp);
     //   String getxt = upts.getText().toString();
        // Toast.makeText(getActivity(), ""+getxt, Toast.LENGTH_SHORT).show();
       // fdb.collection("key").document("Slot_no_1").update("points",fp);
       // fdb.collection("User").document(id).update("points",fp);


    }




    private String currentTime(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        String ct = df.format(Calendar.getInstance().getTime());
        return ct;
    }




    private static String timeDiffrence(String ptime, String rtime) {

        String diff = null;
        try {
            //Dates to compare
            String CurrentDate = ptime;
            String FinalDate = rtime;


            SimpleDateFormat dates = new SimpleDateFormat("HH:mm");

            Date date1 = dates.parse(CurrentDate);
            Date date2 = dates.parse(FinalDate);

            long difference = date2.getTime() - date1.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;


            long elapsedHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;

            long elapsedMinutes = difference / minutesInMilli;
            difference = difference % minutesInMilli;


       //     diff = String.valueOf(elapsedHours+":"+elapsedMinutes);

            int hours = Integer.parseInt(String.valueOf(elapsedHours));
            int minutes = Integer.parseInt(String.valueOf(elapsedMinutes));
            int final_points = (hours*60)+minutes;
            diff = String.valueOf(final_points);



            // Toast.makeText(getActivity(), + elapsedHours+" : "+elapsedMinutes, Toast.LENGTH_SHORT).show();
            // Log.e("HERE","HERE: " + dayDifference)

        } catch (Exception exception) {
            //  Log.e("DIDN'T WORK", "exception " + exception);
            //  Toast.makeText(getActivity(), "exception " + exception, Toast.LENGTH_SHORT).show();
        }


        return diff;
    }



}
