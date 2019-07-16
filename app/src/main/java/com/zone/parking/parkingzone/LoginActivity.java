package com.zone.parking.parkingzone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    public Button BTREGISTER;
    public EditText user;
    public EditText pass;
    public Button login;
    public CheckBox rm;
    public TextView fgtv;
    public String uname,upass;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME="prefs" ;
    private static final String KEY_REMEMBER="remember" ;
    private static final String KEY_USERNAME="username" ;
    private static final String KEY_PASS="pass" ;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    
    
    //git



    public void init() {
        BTREGISTER = (Button) findViewById(R.id.BTREGISTER);

        BTREGISTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent Reg = new Intent(LoginActivity.this, RegitrationActivity.class);
                startActivity(Reg);
                finish();
            }
        });
    }
    public Button BTLOGIN;

    public void Start(){
        BTLOGIN=(Button)findViewById(R.id.BTLOGIN);
        BTLOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userLogin();

            }
        });
    }

    private void userLogin() {
        String lemail = user.getText().toString().trim();
        String lpassword = pass.getText().toString().trim();

        if (lemail.isEmpty()) {
            user.setError("Email is required");
            user.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(lemail).matches()) {
            user.setError("Please enter a valid email");
            user.requestFocus();
            return;
        }

        if (lpassword.isEmpty()) {
            pass.setError("Password is required");
            pass.requestFocus();
            return;
        }

        if (lpassword.length() < 6) {
            pass.setError("Minimum lenght of password should be 6");
            pass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(lemail,lpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
                        finish();
                        Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        uname=user.getText().toString();
                        upass=pass.getText().toString();
                        intent.putExtra("NAME",uname);
                        intent.putExtra("PASS",upass);
                        startActivity(intent);
                    }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public  void cast(){
        user=(EditText)findViewById(R.id.ETUsername);
        pass=(EditText)findViewById(R.id.ETPASS);
        login=(Button)findViewById(R.id.BTLOGIN);
        mAuth = FirebaseAuth.getInstance();
        fgtv = (TextView)findViewById(R.id.tvfg);

    }

    public void rememberMe(){
        sharedPreferences=getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        user=(EditText)findViewById(R.id.ETUsername);
        pass=(EditText)findViewById(R.id.ETPASS);
        rm=(CheckBox)findViewById(R.id.checkBox);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);

        if(sharedPreferences.getBoolean(KEY_REMEMBER,false)){
            rm.setChecked(true);
        }else{
            rm.setChecked(false);
        }
        user.setText(sharedPreferences.getString(KEY_USERNAME,""));
        pass.setText(sharedPreferences.getString(KEY_PASS,""));
        user.addTextChangedListener(this);
        pass.addTextChangedListener(this);
        rm.setOnCheckedChangeListener( this);
    }

    private void mamagePrefs(){
        if(rm.isChecked()){
            editor.putString(KEY_USERNAME,user.getText().toString().trim());
            editor.putString(KEY_PASS,pass.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER,true);
            editor.apply();
        }else{
            editor.putBoolean(KEY_REMEMBER,false);
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASS);
            editor.apply();
        }

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

        Intent mainIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        cast();
        Start();
        rememberMe();

        fgtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forg = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(forg);
                finish();
            }
        });


        }





    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mamagePrefs();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mamagePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        mamagePrefs();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mamagePrefs();
    }
}
