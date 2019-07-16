package com.zone.parking.parkingzone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

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
import com.google.firestore.v1beta1.Write;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;


public class ParkingFragment extends Fragment  {

    public TextView vspark1,vspark2,vspark3,vspark4,vspark5,vspark6;
    public EditText key;
    public Button vpbtn1,vpbtn2,vpbtn3,vpbtn4,vpbtn5,vpbtn6;
    FirebaseFirestore fdb;
    FirebaseAuth mAuth;
    DatabaseReference parkingSlots;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "FC:A8:9A:00:6B:6F";




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        parkingSlots = FirebaseDatabase.getInstance().getReference();
        fdb = FirebaseFirestore.getInstance();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        vspark1=view.findViewById(R.id.VSpark1);
        vspark2=view.findViewById(R.id.VSpark2);
        vspark3=view.findViewById(R.id.VSpark3);
        vspark4=view.findViewById(R.id.VSpark4);
        vspark5=view.findViewById(R.id.VSpark5);
        vspark6=view.findViewById(R.id.VSpark6);

        vpbtn1=view.findViewById(R.id.VPBTN1);
        vpbtn2=view.findViewById(R.id.VPBTN2);
        vpbtn3=view.findViewById(R.id.VPBTN3);
        vpbtn4=view.findViewById(R.id.VPBTN4);
        vpbtn5=view.findViewById(R.id.VPBTN5);
        vpbtn6=view.findViewById(R.id.VPBTN6);
        key=view.findViewById(R.id.ETKEY);





        parkingSlots.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String slot1 = dataSnapshot.child("Slot_no_1").child("status").getValue().toString();
                String slot2 = dataSnapshot.child("Slot_no_2").child("status").getValue().toString();
                String slot3 = dataSnapshot.child("Slot_no_3").child("status").getValue().toString();
                String slot4 = dataSnapshot.child("Slot_no_4").child("status").getValue().toString();
                String slot5 = dataSnapshot.child("Slot_no_5").child("status").getValue().toString();
                String slot6 = dataSnapshot.child("Slot_no_6").child("status").getValue().toString();


                if(slot1.equals("Parked")|| slot1.equals("Empty")){
                    vpbtn1.setEnabled(false);
                }else if(slot1.equals("Reserved")){
                    vpbtn1.setEnabled(true);
                }

                if(slot2.equals("Parked")|| slot2.equals("Empty")){
                    vpbtn2.setEnabled(false);
                }else if(slot2.equals("Reserved")){
                    vpbtn2.setEnabled(true);
                }


                if(slot3.equals("Parked")|| slot3.equals("Empty")){
                    vpbtn3.setEnabled(false);
                }else if(slot3.equals("Reserved")){
                    vpbtn3.setEnabled(true);
                }

                if(slot4.equals("Parked")|| slot4.equals("Empty")){
                    vpbtn4.setEnabled(false);
                }else if(slot4.equals("Reserved")){
                    vpbtn4.setEnabled(true);
                }

                if(slot5.equals("Parked")|| slot5.equals("Empty")){
                    vpbtn5.setEnabled(false);
                }else if(slot5.equals("Reserved")){
                    vpbtn5.setEnabled(true);
                }


                if(slot6.equals("Parked")|| slot6.equals("Empty")){
                    vpbtn6.setEnabled(false);
                }else if(slot6.equals("Reserved")){
                    vpbtn6.setEnabled(true);
                }

                vspark1.setText(slot1);
                vspark2.setText(slot2);
                vspark3.setText(slot3);
                vspark4.setText(slot4);
                vspark5.setText(slot5);
                vspark6.setText(slot6);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ButtonPressed();

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

    private String currentTime(){

        DateFormat df = new SimpleDateFormat("HH:mm");
        String ct = df.format(Calendar.getInstance().getTime());

        //  String ct = new SimpleDateFormat("HH:mm").format(new Date());

       // String ct = DateFormat.getTimeInstance().format(new Date());
        return ct;
    }
    private void ButtonPressed() {






        vpbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_1").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_1").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("1");
                        }else{
                           // Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vpbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_2").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_2").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("2");
                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vpbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_3").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_3").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("3");
                        }else{
                          //  Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vpbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_4").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");
                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_4").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("4");

                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vpbtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_5").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");

                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_5").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("5");

                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        vpbtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String entered_key = key.getText().toString();
                fdb.collection("key").document("Slot_no_6").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        String ukey = documentSnapshot.getString("key");
                        String uid = documentSnapshot.getString("id");

                        if(ukey.equals(entered_key)){
                            parkingSlots.child("Slot_no_6").child("status").setValue("Parked");
                            fdb.collection("User").document(uid).update("Parked_Time",currentTime());
                            sendData("6");

                        }else{
                            Toast.makeText(getActivity(), "Invalid Key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parking, container, false);
    }
}



