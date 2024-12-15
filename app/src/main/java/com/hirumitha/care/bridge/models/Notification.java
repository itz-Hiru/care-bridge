package com.hirumitha.care.bridge.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    private String id;
    private String title;
    private String body;
    private String requesterName;
    private String location;
    private String contactNumber;
    private String productCategory;
    private String productName;
    private String productQuantity;
    private String donorName;

    public Notification() {
    }

    public Notification(String id, String title, String body, String requesterName, String location,
                        String contactNumber, String productCategory, String productName,
                        String productQuantity, String donorName) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.requesterName = requesterName;
        this.location = location;
        this.contactNumber = contactNumber;
        this.productCategory = productCategory;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.donorName = donorName;
    }

    protected Notification(Parcel in) {
        id = in.readString();
        title = in.readString();
        body = in.readString();
        requesterName = in.readString();
        location = in.readString();
        contactNumber = in.readString();
        productCategory = in.readString();
        productName = in.readString();
        productQuantity = in.readString();
        donorName = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(requesterName);
        dest.writeString(location);
        dest.writeString(contactNumber);
        dest.writeString(productCategory);
        dest.writeString(productName);
        dest.writeString(productQuantity);
        dest.writeString(donorName);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getLocation() {
        return location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getDonorName() {
        return donorName;
    }
}