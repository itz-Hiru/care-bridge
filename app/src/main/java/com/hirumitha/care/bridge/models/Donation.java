package com.hirumitha.care.bridge.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.PropertyName;

public class Donation implements Parcelable {
    private String donationId;
    private String name;
    private String location;
    private String contact;
    private String category;
    private String productName;
    private String productQuantity;
    private String productDescription;
    private String imageUrl;

    public Donation() {
    }

    public Donation(String donationId, String name, String location, String contact, String category,
                    String productName, String productQuantity, String productDescription, String imageUrl) {
        this.donationId = donationId;
        this.name = name;
        this.location = location;
        this.contact = contact;
        this.category = category;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productDescription = productDescription;
        this.imageUrl = imageUrl;
    }

    protected Donation(Parcel in) {
        donationId = in.readString();
        name = in.readString();
        location = in.readString();
        contact = in.readString();
        category = in.readString();
        productName = in.readString();
        productQuantity = in.readString();
        productDescription = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Donation> CREATOR = new Creator<>() {
        @Override
        public Donation createFromParcel(Parcel in) {
            return new Donation(in);
        }

        @Override
        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(donationId);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(contact);
        dest.writeString(category);
        dest.writeString(productName);
        dest.writeString(productQuantity);
        dest.writeString(productDescription);
        dest.writeString(imageUrl);
    }

    @PropertyName("donationId")
    public String getDonationId() {
        return donationId;
    }

    @PropertyName("name")
    public String getDonorName() {
        return name;
    }

    @PropertyName("location")
    public String getDonorLocation() {
        return location;
    }

    @PropertyName("contact")
    public String getDonorContact() {
        return contact;
    }

    @PropertyName("category")
    public String getCategory() {
        return category;
    }

    @PropertyName("productName")
    public String getProductName() {
        return productName;
    }

    @PropertyName("productQuantity")
    public String getProductQuantity() {
        return productQuantity;
    }

    @PropertyName("productDescription")
    public String getProductDescription() {
        return productDescription;
    }

    @PropertyName("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }
}