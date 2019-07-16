package com.zone.parking.parkingzone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class VerificationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new StatusFragment());
    }



    private boolean loadFragment(Fragment fragment){
        if (fragment != null){


            getSupportFragmentManager().beginTransaction().replace(R.id.FRAGMENTCONTAINER,fragment).commit();
            return true;

        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()){

            case R.id.navigation_status:
                fragment = new  StatusFragment();
                break;

            case R.id.navigation_park:
                fragment = new ParkingFragment();
                break;

            case R.id.navigation_retrive:
                fragment = new RetriveFragment();
                break;

        }


        return loadFragment(fragment);
    }
}
