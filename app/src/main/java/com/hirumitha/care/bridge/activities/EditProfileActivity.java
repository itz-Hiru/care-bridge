package com.hirumitha.care.bridge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hirumitha.care.bridge.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Uri profileImageUri;
    private EditText editName, editPhone;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profile_image);
        ImageButton btnAddProfileImage = findViewById(R.id.btn_add_profile_image);
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        TextView btnSave = findViewById(R.id.btn_save);

        loadUserProfileData();

        btnAddProfileImage.setOnClickListener(v -> selectImageOption());

        btnSave.setOnClickListener(v -> saveProfileData());

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectImageOption() {
        String[] options = {getString(R.string.take_photo), getString(R.string.upload_photo), getString(R.string.remove_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_option);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                } else {
                    requestCameraPermission();
                }
            } else if (which == 1) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_GALLERY);
            } else if (which == 2) {
                removeProfileImage();
            }
        });
        builder.show();
    }

    private void removeProfileImage() {
        profileImage.setImageResource(R.drawable.default_profile_picture);
        profileImageUri = null;
        Toast.makeText(this, R.string.profile_photo_removed, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImageUri = getImageUri(photo);
                profileImage.setImageBitmap(getCroppedBitmap(photo));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                profileImageUri = data.getData();
                if (profileImageUri != null) {
                    loadImageAndCrop(profileImageUri);
                }
            }
        }
    }

    private void loadImageAndCrop(Uri uri) {
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(new com.bumptech.glide.request.target.SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        profileImage.setImageBitmap(getCroppedBitmap(resource));
                    }
                });
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, getString(R.string.profileimage), null);
        return Uri.parse(path);
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);

        Bitmap squaredBitmap = Bitmap.createBitmap(bitmap, (width - size) / 2, (height - size) / 2, size, size);
        Bitmap outputBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        return outputBitmap;
    }

    private void saveProfileData() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_name_and_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        if (profileImageUri != null) {
            uploadProfileImage(name, phone);
        } else {
            saveProfileDataToFirebase(name, phone, null);
        }
    }

    private void uploadProfileImage(String name, String phone) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileExtension = getFileExtension(profileImageUri);
        StorageReference profileImageRef = storageRef.child("profileImages/" + userId + "." + fileExtension);

        profileImageRef.putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveProfileDataToFirebase(name, phone, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.image_upload_failed) + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        return mimeType != null ? mimeType.substring(mimeType.lastIndexOf("/") + 1) : "jpg";
    }

    private void saveProfileDataToFirebase(String name, String phone, @Nullable String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("phone", phone);

        if (imageUrl != null) {
            userProfile.put("profileImageUrl", imageUrl);
        } else {
            Map<String, Object> profileUpdates = new HashMap<>();
            profileUpdates.put("profileImageUrl", FieldValue.delete());
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(profileUpdates, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, R.string.profile_image_removed, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.failed_to_remove_profile_image) + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .set(userProfile, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.failed_to_save_profile) + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadUserProfileData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String imageUrl = documentSnapshot.getString("profileImageUrl");

                        editName.setText(name);
                        editPhone.setText(phone);

                        if (imageUrl != null) {
                            Glide.with(this)
                                    .asBitmap()
                                    .load(imageUrl)
                                    .into(new com.bumptech.glide.request.target.SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            profileImage.setImageBitmap(getCroppedBitmap(resource));
                                        }
                                    });
                        } else {
                            profileImage.setImageResource(R.drawable.default_profile_picture);
                        }
                    }
                });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
}