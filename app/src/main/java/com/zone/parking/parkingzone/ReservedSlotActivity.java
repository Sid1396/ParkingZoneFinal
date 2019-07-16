package com.zone.parking.parkingzone;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReservedSlotActivity extends AppCompatActivity {
    public Button Cancel;
    public TextView Rname,slotno,pinno,points,RDate,RTime,RHours,Rptime,Rretime;
    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    DatabaseReference parkingSlots;
    CircleImageView pp;
    private DatabaseReference UsersRef;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_slot);
        cast();
        DataDisplay();
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                final AlertDialog.Builder builder = new AlertDialog.Builder(ReservedSlotActivity.this);
                builder.setMessage("Are you sure want to Cancel the Reservation ? ");
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
                        FirebaseUser uid = mAuth.getCurrentUser();
                        final String id = uid.getUid();

                        if(!slotno.getText().toString().trim().equals("No_Slot")){
                            parkingSlots.child(slotno.getText().toString().trim()).child("status").setValue("Empty");
                            fdb.collection("User").document(id).update("slot","No_Slot");
                        }else {
                            Toast.makeText(ReservedSlotActivity.this, "You didnt Reserved any Slot", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }






        });

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(pp);


                    }else {
                        Toast.makeText(ReservedSlotActivity.this, "Profile picture does not exixt", Toast.LENGTH_SHORT).show();
                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void cast(){
        Rname=(TextView)findViewById(R.id.RNAME);
        slotno=(TextView)findViewById(R.id.RSLOTNO);
        pinno=(TextView)findViewById(R.id.RPINNO);
        points=(TextView)findViewById(R.id.RPOINTS);
        RDate=(TextView)findViewById(R.id.RDATE);
        RTime=(TextView)findViewById(R.id.RTIME);
        RHours=(TextView)findViewById(R.id.RRESERVETIME);
        Cancel=(Button)findViewById(R.id.BTCANCEL);
        Rptime=(TextView)findViewById(R.id.PARKEDTIME);
        Rretime=(TextView)findViewById(R.id.RETRIVETIME);
        pp=(CircleImageView)findViewById(R.id.RS_profile);
        mAuth = FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void DataDisplay(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser uid = mAuth.getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        parkingSlots = FirebaseDatabase.getInstance().getReference();
        final String id = uid.getUid();

        fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String Uname = documentSnapshot.getString("name");
                Rname.setText(Uname);
                String Upoints = documentSnapshot.getString("points");
                points.setText(Upoints);
                String Sno=documentSnapshot.getString("slot");
                slotno.setText(Sno);
                String ukey = documentSnapshot.getString("key");
                pinno.setText(ukey);
                String rd = documentSnapshot.getString("Reserved_Date");
                RDate.setText(rd);
                String rt = documentSnapshot.getString("Reserved_Time");
                RTime.setText(rt);
                String rh = documentSnapshot.getString("Reserved_Hours");
                RHours.setText(rh);
                String pt = documentSnapshot.getString("Parked_Time");
                Rptime.setText(pt);
                String ret = documentSnapshot.getString("Retrive_Time");
                Rretime.setText(ret);



            }
        });
        }




}

