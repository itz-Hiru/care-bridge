package com.hirumitha.care.bridge.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.models.Donation;
import com.hirumitha.care.bridge.fragments.HomeFragment;

public class DonateActivity extends AppCompatActivity {

    private EditText name, location, contact, productName, productQuantity, productDescription;
    private Spinner productCategory;
    private TextView addProductImage;
    private ImageView productImage;
    private Uri imageUri;
    private CardView donateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_form);

        name = findViewById(R.id.name);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        productName = findViewById(R.id.product_name);
        productQuantity = findViewById(R.id.product_quantity);
        productDescription = findViewById(R.id.product_description);
        productCategory = findViewById(R.id.product_category);
        addProductImage = findViewById(R.id.add_product_image);
        productImage = findViewById(R.id.product_image);
        donateButton = findViewById(R.id.donate_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{getString(R.string.food_and_water), getString(R.string.education_materials), getString(R.string.electronics_and_communication),
                        getString(R.string.shelter_items), getString(R.string.medical_supplies), getString(R.string.hygiene_supplies), getString(R.string.clothing)});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategory.setAdapter(adapter);

        addProductImage.setOnClickListener(v -> showImagePickerDialog());
        donateButton.setOnClickListener(v -> saveDonationToFirebase());

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showImagePickerDialog() {
        CharSequence[] options = {getString(R.string.camera), getString(R.string.gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_an_option);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                ImagePicker.with(this)
                        .cameraOnly()
                        .cropSquare()
                        .start();
            } else {
                ImagePicker.with(this)
                        .galleryOnly()
                        .cropSquare()
                        .start();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
            productImage.setVisibility(View.VISIBLE);
        }
    }

    private void saveDonationToFirebase() {
        String userName = name.getText().toString().trim();
        String userLocation = location.getText().toString().trim();
        String userContact = contact.getText().toString().trim();
        String category = productCategory.getSelectedItem().toString();
        String product = productName.getText().toString().trim().toLowerCase();
        String quantity = productQuantity.getText().toString().trim();
        String description = productDescription.getText().toString().trim();

        if (userName.isEmpty() || userLocation.isEmpty() || userContact.isEmpty() ||
                product.isEmpty() || quantity.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, R.string.please_fill_out_all_fields_and_add_an_image, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("donations/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Donations");
                        String donationId = databaseReference.push().getKey();

                        if (donationId != null) {
                            Donation donation = new Donation(donationId, userName, userLocation, userContact, category, product, quantity, description, uri.toString());

                            databaseReference.child(donationId).setValue(donation)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(DonateActivity.this, R.string.donation_submitted_successfully, Toast.LENGTH_SHORT).show();
                                        clearForm();
                                        navigateToHomeFragment();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(DonateActivity.this, getString(R.string.failed_to_submit_donation) + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(DonateActivity.this, R.string.failed_to_generate_donation_id, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(DonateActivity.this, getString(R.string.failed_to_get_download_url) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DonateActivity.this, getString(R.string.failed_to_upload_image) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        name.setText("");
        location.setText("");
        contact.setText("");
        productName.setText("");
        productQuantity.setText("");
        productDescription.setText("");
        productCategory.setSelection(0);
        productImage.setImageURI(null);
        productImage.setVisibility(View.GONE);
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(DonateActivity.this, HomeFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}