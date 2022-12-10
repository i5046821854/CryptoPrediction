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
import com.capstone.crypto.view.fragments.NewsFragment;
import com.capstone.crypto.view.fragments.PredictionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MenuActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private MypageFragment mypageFragment = new MypageFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private PredictionFragment predictionFragment = new PredictionFragment();
    private NewsFragment newsFragment = new NewsFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String preference;
    private String userId;
    private String nickname;
    private Bundle bundle;
    private int img;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        initVars(intent); //initialize variables

        //index fragment : home
        homeFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();

        //initailize bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:  //home frag
                        homeFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.prediction: //prediction frag
                        predictionFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, predictionFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.chat: //chat frag
                        chatFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, chatFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.info: //info frag
                        mypageFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, mypageFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.news: //new frag
                        newsFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, newsFragment).commitAllowingStateLoss();
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

    //when user press other menu, go to that selected menu
    public void changeFrag(int idx ,Bundle newBundle){
        if(idx == 1){ //to home
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            bundle = newBundle;
            homeFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commitAllowingStateLoss();
        }else if(idx == 2){  //to Mypage
            bottomNavigationView.getMenu().getItem(4).setChecked(true);
            mypageFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, mypageFragment).commitAllowingStateLoss();
        }else if(idx == 3){  //to chat
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            chatFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, chatFragment).commitAllowingStateLoss();
        }
        else if(idx == 4){  //to news
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            chatFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, newsFragment).commitAllowingStateLoss();
        }
    }

    //initailize variables
    void initVars(Intent intent){
        preference = intent.getStringExtra("name");
        userId = intent.getStringExtra("id");
        nickname = intent.getStringExtra("nickname");
        img = intent.getIntExtra("img", 1);
        bundle= new Bundle();
        bundle.putString("preference", preference);
        bundle.putString("id", userId);
        bundle.putInt("img", img);
        bundle.putString("nickname", nickname);
    }

}
