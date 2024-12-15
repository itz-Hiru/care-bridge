package com.hirumitha.care.bridge.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hirumitha.care.bridge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedProductRequestActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "request_notifications";
    private EditText nameEditText, locationEditText, contactNumberEditText, productNameEditText, productCategoryEditText, productQuantityEditText;
    private Spinner donorNameSpinner;
    private CardView requestButton;
    private DatabaseReference notificationRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_product_request_form);

        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        nameEditText = findViewById(R.id.name);
        locationEditText = findViewById(R.id.location);
        contactNumberEditText = findViewById(R.id.contact_number);
        productNameEditText = findViewById(R.id.product_name);
        productCategoryEditText = findViewById(R.id.product_category);
        productQuantityEditText = findViewById(R.id.product_quantity);
        donorNameSpinner = findViewById(R.id.donor_name);
        requestButton = findViewById(R.id.request_btn);

        String productName = getIntent().getStringExtra("productName");
        String productCategory = getIntent().getStringExtra("productCategory");
        ArrayList<String> donorNames = getIntent().getStringArrayListExtra("donorNames");

        productNameEditText.setText(productName);
        productNameEditText.setEnabled(false);
        productCategoryEditText.setText(productCategory);
        productCategoryEditText.setEnabled(false);

        if (donorNames != null && !donorNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, donorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            donorNameSpinner.setAdapter(adapter);

            if (donorNames.size() == 1) {
                donorNameSpinner.setSelection(0);
                donorNameSpinner.setEnabled(false);
            }
        } else {
            Toast.makeText(this, R.string.no_donor_names_available, Toast.LENGTH_SHORT).show();
        }

        requestButton.setOnClickListener(v -> submitRequestForm());

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitRequestForm() {
        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String contactNumber = contactNumberEditText.getText().toString().trim();
        String productName = productNameEditText.getText().toString().trim();
        String productCategory = productCategoryEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String donorName = donorNameSpinner.getSelectedItem() != null ? donorNameSpinner.getSelectedItem().toString() : null;

        if (name.isEmpty() || location.isEmpty() || contactNumber.isEmpty() || productName.isEmpty() ||
                productCategory.isEmpty() || productQuantity.isEmpty() || donorName == null) {
            Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        String notificationTitle = getString(R.string.new_product_request_from_s) + name;
        String notificationBody = getString(R.string.a_request_has_been_made_for_s) + productName + getString(R.string.by_s) + name;

        sendNotificationToAll(notificationTitle, notificationBody, name, location, contactNumber, productCategory, productQuantity, donorName);
        showSystemNotification(name, productName);
    }

    private void sendNotificationToAll(String title, String body, String name, String location, String contactNumber, String productCategory, String productQuantity, String donorName) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("body", body);
        notificationData.put("requesterName", name);
        notificationData.put("location", location);
        notificationData.put("contactNumber", contactNumber);
        notificationData.put("productCategory", productCategory);
        notificationData.put("productName", productNameEditText.getText().toString());
        notificationData.put("quantity", productQuantity);
        notificationData.put("donorName", donorName);

        notificationRef.push().setValue(notificationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.notification_sent_successfully_s, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.failed_to_send_notification_s, Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("MissingPermission")
    private void showSystemNotification(String requesterName, String productName) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.new_product_request_from_ss) + requesterName)
                .setContentText(getString(R.string.a_request_for_ss) + productName + getString(R.string.has_been_submitted_ss))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Request Notifications";
            String description = "Channel for product request notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}