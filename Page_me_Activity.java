package com.example.music;

//import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.music.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Page_me_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PageMe", "onCreate called");

        // ✅ 设置内容铺满状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // ✅ 状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_page_me);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) {
            Log.e("PageMe", "bottom_navigation not found!");
        } else {
            Log.d("PageMe", "bottom_navigation found!");
        }
        setContentView(R.layout.activity_page_me);

        // 默认加载 "我" 页面
        loadFragment(new MeFragment());

        // 底部导航切换

        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_me:
                    loadFragment(new MeFragment());
                    return true;
                case R.id.nav_community:
                    loadFragment(new CommunityFragment());
                    return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


}