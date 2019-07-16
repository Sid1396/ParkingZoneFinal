package com.zone.parking.parkingzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public TextView nam;
    public TextView mail;
    public TextView pno;
    FirebaseAuth mAuth;
    public TextView cno;
    FirebaseFirestore fdb;
    CircleImageView profilepicture;
    Button Update;
    String currentUserID;
    private DatabaseReference UsersRef;

    ProgressBar progressBar;
    private String downloadUrl;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;



    private Uri ImageUri;
    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cast();
        Datadisplay();



        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });

    }

    private void openGallery()
    {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick &&  resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            profilepicture.setImageURI(ImageUri);


            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your profile image...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            saveImagetoFirebase();


        }
    }

    private void saveImagetoFirebase() {

        final StorageReference filePath = UserProfileImageRef.child("Profile Images").child(currentUserID + ".jpg");
        filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    //            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();


                    downloadUrl = downUri.toString();
                    SavingProfileInformationToDatabase();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "Error occured while uploading profile picture: " + message, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void SavingProfileInformationToDatabase() {
        UsersRef.child(currentUserID).child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){


                    Toast.makeText(ProfileActivity.this, "Profile picture Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();


                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "Error "+message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }






    private void cast() {
        nam = (TextView) findViewById(R.id.TVNAME);
        mail = (TextView) findViewById(R.id.TVMAIL);
        pno = (TextView) findViewById(R.id.TVPNO);
        cno = (TextView) findViewById(R.id.TVCNO);
        profilepicture = (CircleImageView) findViewById(R.id.PROFILEPICTURE);
        Update = (Button) findViewById(R.id.BTNUPDATEPP);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadingBar = new ProgressDialog(this);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference();
    }


    private void Datadisplay() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser uid = mAuth.getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        final String id = uid.getUid();

        fdb.collection("User").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String Uname = documentSnapshot.getString("name");
          //      nam.setText(Uname);
                String Umail = documentSnapshot.getString("email");
                mail.setText(Umail);
                String Upno = documentSnapshot.getString("phno");
                pno.setText(Upno);
                String Ucno = documentSnapshot.getString("carnumber");
                cno.setText(Ucno);
            }
        });

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        String name = dataSnapshot.child("fullname").getValue().toString();
                        nam.setText(name);
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profilepicture);


                    }else {
                        Toast.makeText(ProfileActivity.this, "Profile picture does not exixt", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}



