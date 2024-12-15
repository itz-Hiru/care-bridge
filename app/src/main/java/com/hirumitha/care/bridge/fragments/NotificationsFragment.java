package com.hirumitha.care.bridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.activities.NotificationDetailActivity;
import com.hirumitha.care.bridge.adapters.NotificationAdapter;
import com.hirumitha.care.bridge.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName;
    private FirebaseFirestore firestore;
    private ListView notificationsListView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private DatabaseReference notificationRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        profileImage = view.findViewById(R.id.user_profile_picture);
        profileName = view.findViewById(R.id.user_welcome_text);
        notificationsListView = view.findViewById(R.id.notifications_list_view);
        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        firestore = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationList);
        notificationsListView.setAdapter(adapter);

        fetchNotifications();
        loadUserProfile();

        notificationsListView.setOnItemClickListener((parent, view1, position, id) -> {
            Notification notification = notificationList.get(position);
            showNotificationDetails(notification);
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String imageUrl = documentSnapshot.getString("profileImageUrl");

                            profileName.setText(name != null ? name : getString(R.string.d_user));

                            if (imageUrl != null) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.default_profile_picture)
                                        .into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.default_profile_picture);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.error_loading_profile) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void fetchNotifications() {
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        notificationList.add(0, notification);
                    }
                }

                if (notificationList.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_notifications_available, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getString(R.string.failed_to_load_notifications) + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNotificationDetails(Notification notification) {
        if (notification != null) {
            Intent intent = new Intent(getContext(), NotificationDetailActivity.class);
            intent.putExtra("notification", notification);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.notification_data_unavailable, Toast.LENGTH_SHORT).show();
        }
    }
}