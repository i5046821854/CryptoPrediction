package com.capstone.crypto.view.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.capstone.crypto.view.fragments.ChatFragment;
import com.capstone.crypto.view.fragments.HomeFragment;
import com.capstone.crypto.view.fragments.MypageFragment;
import com.capstone.crypto.R;
import com.capstone.crypto.view.fragments.PredictionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MenuActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private MypageFragment mypageFragment = new MypageFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private PredictionFragment predictionFragment = new PredictionFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String preference;
    private String userId;
    private Bundle bundle;
    private int img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        preference = intent.getStringExtra("name");
        userId = intent.getStringExtra("id");
        img = intent.getIntExtra("img", 1);
        System.out.println(img);
        System.out.println("userID : " + userId);
        bundle= new Bundle();
        bundle.putString("preference", preference);
        bundle.putString("id", userId);
        bundle.putInt("img", img);
        homeFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        homeFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.prediction:
                        predictionFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, predictionFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.chat:
                        chatFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, chatFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.info:
                        mypageFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, mypageFragment).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void changeFrag(int idx ,Bundle newBundle){
        if(idx == 1){ //to home
            bundle = newBundle;
            homeFragment.setArguments(bundle);
            System.out.println(bundle.getString("preference"));
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
        }else if(idx == 2){
            mypageFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, mypageFragment).commitAllowingStateLoss();
        }else if(idx == 3){
            mypageFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, chatFragment).commitAllowingStateLoss();
        }
    }

}
