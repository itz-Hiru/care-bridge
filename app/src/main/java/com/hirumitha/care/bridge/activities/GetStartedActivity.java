package com.hirumitha.care.bridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.adapters.FoodViewPagerAdapter;

public class GetStartedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        int[] layouts = new int[] {
                R.layout.slide_page1,
                R.layout.slide_page2,
                R.layout.slide_page3,
                R.layout.slide_page4
        };

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FoodViewPagerAdapter adapter = new FoodViewPagerAdapter(layouts);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        View getStartedButton = findViewById(R.id.get_start_btn);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}