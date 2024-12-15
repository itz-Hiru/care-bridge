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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.fragments.HomeFragment;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "notification_channel";

    private EditText nameEditText, locationEditText, contactNumberEditText, productNameEditText, productQuantityEditText;
    private Spinner productCategorySpinner;
    private CardView requestButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);

        databaseReference = FirebaseDatabase.getInstance().getReference("Requests");

        nameEditText = findViewById(R.id.name);
        locationEditText = findViewById(R.id.location);
        contactNumberEditText = findViewById(R.id.contact_number);
        productNameEditText = findViewById(R.id.product_name);
        productQuantityEditText = findViewById(R.id.product_quantity);
        productCategorySpinner = findViewById(R.id.product_category);
        requestButton = findViewById(R.id.btn_request);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(adapter);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitRequest() {
        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String contactNumber = contactNumberEditText.getText().toString().trim();
        String productCategory = productCategorySpinner.getSelectedItem().toString();
        String productName = productNameEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || contactNumber.isEmpty() || productName.isEmpty() || productQuantity.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> requestDetails = new HashMap<>();
        requestDetails.put("name", name);
        requestDetails.put("location", location);
        requestDetails.put("contactNumber", contactNumber);
        requestDetails.put("productCategory", productCategory);
        requestDetails.put("productName", productName);
        requestDetails.put("productQuantity", productQuantity);

        databaseReference.push().setValue(requestDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.requested_successfully, Toast.LENGTH_SHORT).show();
                    addNotificationToDatabase(name, location, contactNumber, productCategory, productName, productQuantity);
                    showSystemNotification(name, productName);
                    navigateToHomeFragment();
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.failed_to_send_request, Toast.LENGTH_SHORT).show());
    }

    private void addNotificationToDatabase(String name, String location, String contactNumber, String productCategory, String productName, String productQuantity) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        String title = getString(R.string.new_product_request_from) + name;
        String body = getString(R.string.a_request_has_been_made_for) + productName + getString(R.string.by) + name;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("body", body);
        notificationData.put("requesterName", name);
        notificationData.put("location", location);
        notificationData.put("contactNumber", contactNumber);
        notificationData.put("productCategory", productCategory);
        notificationData.put("productName", productNameEditText.getText().toString());
        notificationData.put("productQuantity", productQuantity);

        notificationRef.push().setValue(notificationData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, R.string.notification_sent_successfully, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, R.string.failed_to_send_notification, Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("MissingPermission")
    private void showSystemNotification(String requesterName, String productName) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.new_product_request_from_n) + requesterName)
                .setContentText(getString(R.string.a_request_for) + productName + getString(R.string.has_been_submitted))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Requests Channel";
            String description = "Channel for product request notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void navigateToHomeFragment() {
        Fragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_id, homeFragment)
                .commit();
    }
}