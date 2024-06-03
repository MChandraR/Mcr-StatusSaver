package com.mcr.statussaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.mcr.statussaver.app.Core;
import com.mcr.statussaver.ui.FirstFragment;
import com.mcr.statussaver.ui.SecondFragment;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView NavBar;
    private  Fragment fragment;
    private Core appCore;
    private int fragmentId;
    private SecondFragment SF;
    private FirstFragment FF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LauncherActivity LA = new LauncherActivity();
        fragmentId = 0;

        FF = new FirstFragment();
        SF = new SecondFragment();

        appCore = new Core();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HomeActivity.this.finishAffinity();
            }
        };

        NavBar = findViewById(R.id.HomeActivityNavBar);
        frameLayout = findViewById(R.id.HomeActivityFrameLayout);

        NavBar.setItemActiveIndicatorColor(getColorStateList(R.color.light_blue));



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(appCore.checkPermissionR(this) && appCore.checkPermission(this)){
                fragment = new FirstFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.HomeActivityFrameLayout,fragment).commit();
                fragmentId = 1;
                addEvent();
            }else{
                Toast.makeText(this, "Izin penyimpanan tidak diberikan ! Menutup aplikasi.", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable,3000);
            }
        }else{
            if(appCore.checkPermission(this)){
                fragment = new FirstFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.HomeActivityFrameLayout,fragment).commit();
                fragmentId = 1;
                addEvent();
            }else{
                Toast.makeText(this, "Izin penyimpanan tidak diberikan ! Menutup aplikasi.", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable,3000);
            }
        }

       // NavBar.setItemIconTintList(getColorStateList(R.color.light_blue));

    }



    private void addEvent(){
        if(NavBar != null){
            NavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(appCore.checkPermission(HomeActivity.this)){
                        Fragment fragments = null;
                        switch (item.getItemId()){
                            case R.id.navbar_value_first :
                                fragments = FF;
                                break;
                            case R.id.navbar_value_second:
                                fragments = SF;
                                break;
                            default:
                                fragments = FF;
                        }
                        if(fragments!=fragment){
                            fragment = fragments;
                            getSupportFragmentManager().beginTransaction().replace(R.id.HomeActivityFrameLayout,fragments).commit();
                        }
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
       if(fragment == SF){
           if(SF.searchInput.isFocused()){
               SF.searchInput.clearFocus();
               finishAffinity();
           }else{
               finishAffinity();
           }
       }else{
           finishAffinity();
       }
    }
}