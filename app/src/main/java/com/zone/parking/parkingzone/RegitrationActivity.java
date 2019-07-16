package com.zone.parking.parkingzone;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegitrationActivity extends AppCompatActivity {
    private EditText etname,etmail,etcnum,etpnum,etpass;
    public Button BTREG;
    public Button BTRLOG;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private StorageReference UserProfileImageRef;
    private DatabaseReference UsersRef;
    String currentUserID;
    private String downloadUrl;



    private Uri ImageUri;
    final static int Gallery_Pick = 1;

    public void init(){

        BTREG = (Button)findViewById(R.id.BTREG);
        BTRLOG = (Button)findViewById(R.id.BTRLOG);
        etmail = (EditText) findViewById(R.id.ETMAIL);
        etpass = (EditText)findViewById(R.id.ETPASS);
        etname = (EditText)findViewById(R.id.ETNAME);
        etcnum = (EditText)findViewById(R.id.ETCNUM);
        etpnum = (EditText)findViewById(R.id.ETPNUM);
        progressBar=(ProgressBar)findViewById(R.id.PROGRESSBAR);




        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }




        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_regitration);
            init();


            BTREG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();

                }
            });


            BTRLOG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    finish();
                    Intent Register= new Intent(RegitrationActivity.this,LoginActivity.class);
                    startActivity(Register);

                }
            });

        }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            SendUserToMainActivity();
        }
    }


    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegitrationActivity.this, WelcomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
        private void registerUser() {
            String email = etmail.getText().toString().trim();
            String password = etpass.getText().toString().trim();
            //      String name = etname.getText().toString().trim();
            //     String carnumber = etcnum.getText().toString().trim();
            String phono = etpnum.getText().toString();


            if (email.isEmpty()) {
                etmail.setError("Email is required");
                etmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etmail.setError("Please enter a valid email");
                etmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etpass.setError("Password is required");
                etpass.requestFocus();
                return;
            }

            if (password.length() < 6) {
                etpass.setError("Minimum length of password should be 6");
                etpass.requestFocus();
                return;
            }

            //         progressBar.setVisibility(View.VISIBLE);
          /*  alreadyBooked(phono, new AlreadyBookedCallback() {
                @Override
                public void onCallback(boolean isAlreadyBooked) {
                 if(isAlreadyBooked){*/

            email = etmail.getText().toString().trim();
            password = etpass.getText().toString().trim();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //       progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        FirebaseUser uid = mAuth.getCurrentUser();
                        String id = uid.getUid();
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Registration Successful, Verify your email to login..", Toast.LENGTH_LONG).show();
                                    dataStore();
                                }
                            }
                        });

                              finish();
                              startActivity(new Intent(RegitrationActivity.this,SetupActivity.class));

                    } else {

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

             /*    }
                 }
            });
            }

*/
/*

        public interface AlreadyBookedCallback {
       void onCallback(boolean isAlreadyBooked);
   }
*/

/*    private void alreadyBooked(final String phono, final AlreadyBookedCallback callback) {
        CollectionReference cref=db.collection("User");
        Query q1 = cref.whereEqualTo("phno",phono);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean isExisting = false;
                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    String getph;
                    getph = ds.getString("phno");

                    if (phono.equals(getph)) {
                        Toast.makeText(RegitrationActivity.this, "pphone number alredy exist", Toast.LENGTH_SHORT).show();
                        isExisting = true;
                        callback.onCallback(isExisting);
                    }
                    else if (!phono.equals(getph)){
                        callback.onCallback(!isExisting);
                    }
                }

            }
        });
    }*/


    private void dataStore() {
                String points = String.valueOf(1000);
                String slot = "No_Slot";
                String email = etmail.getText().toString().trim();
              //  String password = etpass.getText().toString().trim();
                String name = etname.getText().toString().trim();
                String carnumber = etcnum.getText().toString().trim();
                String phno = etpnum.getText().toString();
                FirebaseUser uid = mAuth.getCurrentUser();
                String id = uid.getUid();
             //   CollectionReference dbUser  = db.collection("User");
                User user = new User(name,email,carnumber,phno,points,slot);
                db.collection("User").document(id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegitrationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(RegitrationActivity.this);
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


