package com.techtown.cookingkingcooking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;

    Menu1Fragment mf1;
    Menu2Fragment mf2;
    Menu3Fragment mf3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mf1 = new Menu1Fragment();
        mf2 = new Menu2Fragment();
        mf3 = new Menu3Fragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mf1).commit();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("핫 레시피"));
        tabs.addTab(tabs.newTab().setText("추천 레시피"));
        tabs.addTab(tabs.newTab().setText("나만의 레시피"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = mf1;
                } else if (position == 1) {
                    selected = mf2;
                } else if (position == 2) {
                    selected = mf3;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}