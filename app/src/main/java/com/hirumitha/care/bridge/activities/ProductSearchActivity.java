package com.hirumitha.care.bridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.models.Donation;

import java.util.ArrayList;

public class ProductSearchActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productHeader, productName, productCategory, productQuantity, productDescription, donorName, donorLocation, donorContact;
    private CardView requestFormButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        productHeader = findViewById(R.id.product_name_header);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.product_category);
        productQuantity = findViewById(R.id.product_quantity);
        productDescription = findViewById(R.id.product_description);
        donorName = findViewById(R.id.donor_name);
        donorLocation = findViewById(R.id.donor_location);
        donorContact = findViewById(R.id.donor_contact);
        requestFormButton = findViewById(R.id.request_btn);

        Donation donation = getIntent().getParcelableExtra("donation");

        ArrayList<String> donorNames = getDonorNames(donation);

        if (donation != null) {
            String productNameText = donation.getProductName();
            String capitalizedProductName = productNameText.substring(0, 1).toUpperCase() + productNameText.substring(1).toLowerCase();

            productHeader.setText(capitalizedProductName);
            productName.setText(productNameText);
            productCategory.setText(donation.getCategory());
            productQuantity.setText(donation.getProductQuantity());
            productDescription.setText(donation.getProductDescription());
            donorName.setText(donation.getDonorName());
            donorLocation.setText(donation.getDonorLocation());
            donorContact.setText(donation.getDonorContact());

            Glide.with(this)
                    .load(donation.getImageUrl())
                    .placeholder(R.drawable.default_product)
                    .into(productImage);

            requestFormButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductSearchActivity.this, SelectedProductRequestActivity.class);
                intent.putExtra("productName", donation.getProductName());
                intent.putExtra("productCategory", donation.getCategory());
                intent.putStringArrayListExtra("donorNames", donorNames);
                startActivity(intent);
            });
        }
    }

    private ArrayList<String> getDonorNames(Donation donation) {
        ArrayList<String> donorNames = new ArrayList<>();
        if (donation != null && donation.getDonorName() != null) {
            donorNames.add(donation.getDonorName());
        }
        return donorNames;
    }
}