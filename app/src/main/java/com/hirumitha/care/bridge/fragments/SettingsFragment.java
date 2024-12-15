package com.hirumitha.care.bridge.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hirumitha.care.bridge.R;
import com.bumptech.glide.request.target.CustomTarget;
import com.hirumitha.care.bridge.activities.AboutActivity;
import com.hirumitha.care.bridge.activities.ContactUsActivity;
import com.hirumitha.care.bridge.activities.EditProfileActivity;
import com.hirumitha.care.bridge.activities.GetStartedActivity;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profilePhone;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profilePhone = view.findViewById(R.id.profile_phone);
        CardView btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        CardView btnGeneral = view.findViewById(R.id.btn_language);
        CardView btnAbout = view.findViewById(R.id.btn_about);
        CardView btnContactUs = view.findViewById(R.id.btn_contact_us);
        CardView btnLogout = view.findViewById(R.id.btn_logout);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth here

        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnGeneral.setOnClickListener(v -> {
            showLanguageSelectionDialog();
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        btnContactUs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContactUsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.logout)
                    .setMessage(R.string.are_you_sure_you_want_to_log_out)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        auth.signOut();
                        Intent intent = new Intent(getActivity(), GetStartedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    private void loadUserProfile() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String phone = documentSnapshot.getString("phone");
                            String imageUrl = documentSnapshot.getString("profileImageUrl");

                            profileName.setText(name != null ? name : getString(R.string.user));
                            profilePhone.setText(phone != null ? phone : getString(R.string.n_a));

                            if (imageUrl != null) {
                                Glide.with(this)
                                        .asBitmap()
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                profileImage.setImageBitmap(resource);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            } else {
                                profileImage.setImageResource(R.drawable.default_profile_picture);
                            }
                        }
                    });
        }
    }

    private void showLanguageSelectionDialog() {
        final String[] languages = {getString(R.string.default_english), getString(R.string.sinhala), getString(R.string.tamil)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_language);
        builder.setItems(languages, (dialog, which) -> {
            switch (which) {
                case 0:
                    setLocale("default");
                    break;
                case 1:
                    setLocale("si");
                    break;
                case 2:
                    setLocale("ta");
                    break;
            }
        });
        builder.show();
    }

    private void setLocale(String langCode) {
        Locale locale;
        if (langCode.equals("default")) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            locale = new Locale(langCode);
        }
        Locale.setDefault(locale);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putString("app_language", langCode).apply();

        getActivity().recreate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}