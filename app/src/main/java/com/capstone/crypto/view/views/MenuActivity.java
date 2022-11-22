package com.capstone.crypto.view.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.capstone.crypto.view.fragments.HomeFragment;
import com.capstone.crypto.view.fragments.MypageFragment;
import com.capstone.crypto.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MenuActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private MypageFragment mypageFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        preference = intent.getStringExtra("name");
        fragmentManager.beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Bundle bundle= new Bundle();
                        bundle.putString("preference", preference);
                        homeFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.info:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.setting:
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

}
