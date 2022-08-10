package com.pfiev.englishcontest;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.pfiev.englishcontest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int account_type = extras.getInt(GlobalConstant.ACCOUNT_TYPE);
            //The key argument here must match that used in the other activity
            Log.i(TAG, "Navigate from GG = 0 , FB = 1, TW = 2 : "+account_type);
        }

//        AppBarConfiguration appBarConfiguration =
//                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = findViewById(R.id.toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //initToolBar();
    }

//    @Override
//    protected void initToolBar() {
//        super.initToolBar();
//        final ActionBar actionbar = getSupportActionBar();
//        if (actionbar == null) {
//            return;
//        }
//        actionbar.setCustomView(R.layout.main_custom_action_bar_layout);
//        actionbar.setDisplayShowCustomEnabled(true);
//        actionbar.setDisplayShowTitleEnabled(false);
//
//        mContainerActionBarLayout = actionbar.getCustomView().findViewById(R.id.container_actionbar_layout);
//        mContainerActionBarLayout.setOnClickListener(mUserNameClickListener);
//
//        mUserProfileImageLayout = actionbar.getCustomView().findViewById(R.id.avatar_layout);
//
//        mUserProfileImageView = actionbar.getCustomView().findViewById(R.id.avatar);
//        mUserProfileImageView.setEnabled(false);
//        UiUtils.setHoverText(MainActivity.this, mUserProfileImageView);
//
//        mUserName = actionbar.getCustomView().findViewById(R.id.username);
//    }

}