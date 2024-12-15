package com.hirumitha.care.bridge.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hirumitha.care.bridge.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        findViewById(R.id.btn_whatsapp).setOnClickListener(v -> {
            String phoneNumber = "+94725508919";
            String url = "https://wa.me/" + phoneNumber;
            openURL(url);
        });

        findViewById(R.id.btn_facebook).setOnClickListener(v -> {
            String facebookUrl = "https://www.facebook.com/share/pmf76hxtcvadppLB/";
            openURL(facebookUrl);
        });

        findViewById(R.id.btn_telegram).setOnClickListener(v -> {
            String telegramUsername = "x_hiru_xx";
            String url = "https://t.me/" + telegramUsername;
            openURL(url);
        });

        findViewById(R.id.btn_instagram).setOnClickListener(v -> {
            String instagramUrl = "https://www.instagram.com/x_hiru23/";
            openURL(instagramUrl);
        });

        findViewById(R.id.btn_email).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "hirumithakuladewanew@gmail.com", null));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Intent chooserIntent = Intent.createChooser(intent, getString(R.string.open_with));
            startActivity(chooserIntent);
        }
    }
}