package com.hirumitha.care.bridge.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.models.Notification;

public class NotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Intent intent = getIntent();
        Notification notification = intent.getParcelableExtra("notification");

        TextView titleTextView = findViewById(R.id.header_title);
        ImageView backButton = findViewById(R.id.back_button);
        TextView requesterTextView = findViewById(R.id.requester_name);
        TextView requesterContactTextView = findViewById(R.id.requester_contact);
        TextView requesterLocationTextView = findViewById(R.id.requester_location);
        TextView productNameTextView = findViewById(R.id.product_name);
        TextView productCategoryTextView = findViewById(R.id.product_category);
        TextView productQuantityTextView = findViewById(R.id.product_quantity_notification);
        TextView donorNameTextView = findViewById(R.id.donor_name);

        if (notification != null) {
            titleTextView.setText(notification.getTitle() != null ? notification.getTitle() : "");
            requesterTextView.setText(notification.getRequesterName() != null ? notification.getRequesterName() : "");
            requesterContactTextView.setText(notification.getContactNumber() != null ? notification.getContactNumber() : "");
            requesterLocationTextView.setText(notification.getLocation() != null ? notification.getLocation() : "");
            productNameTextView.setText(notification.getProductName() != null ? notification.getProductName() : "");
            productCategoryTextView.setText(notification.getProductCategory() != null ? notification.getProductCategory() : "");
            productQuantityTextView.setText(notification.getProductQuantity() != null ? notification.getProductQuantity() : "");
            donorNameTextView.setText(notification.getDonorName() != null ? notification.getDonorName() : getString(R.string.no_donors_found_for_this_request));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView qrCodeImageView = findViewById(R.id.qr_code);
        generateQRCode(notification, qrCodeImageView);
    }

    private void generateQRCode(Notification notification, ImageView qrCodeImageView) {
        StringBuilder qrData = new StringBuilder();

        if (notification.getRequesterName() != null) {
            qrData.append(getString(R.string.requester)).append(notification.getRequesterName()).append("\n");
        }
        if (notification.getLocation() != null) {
            qrData.append(getString(R.string.location)).append(notification.getLocation()).append("\n");
        }
        if (notification.getContactNumber() != null) {
            qrData.append(getString(R.string.contact)).append(notification.getContactNumber()).append("\n");
        }
        if (notification.getProductName() != null) {
            qrData.append(getString(R.string.product)).append(notification.getProductName()).append("\n");
        }
        if (notification.getProductCategory() != null) {
            qrData.append(getString(R.string.category)).append(notification.getProductCategory()).append("\n");
        }
        if (notification.getProductQuantity() != null) {
            qrData.append(getString(R.string.quantity)).append(notification.getProductQuantity()).append("\n");
        }
        if (notification.getDonorName() != null) {
            qrData.append(getString(R.string.donor)).append(notification.getDonorName());
        }

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrData.toString(),
                    BarcodeFormat.QR_CODE,
                    200,
                    200
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrCodeImageView.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}