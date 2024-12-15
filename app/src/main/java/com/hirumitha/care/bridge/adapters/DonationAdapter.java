package com.hirumitha.care.bridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.models.Donation;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private final Context context;
    private final List<Donation> donations;
    private final OnDonationClickListener listener;

    public DonationAdapter(Context context, List<Donation> donations, OnDonationClickListener listener) {
        this.context = context;
        this.donations = donations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donations.get(position);
        holder.productName.setText(donation.getProductName());

        Glide.with(context)
                .load(donation.getImageUrl())
                .placeholder(R.drawable.default_product)
                .into(holder.productImage);

        holder.itemView.setOnClickListener(v -> listener.onDonationClick(donation));
    }

    @Override
    public int getItemCount() {
        return donations.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

    public interface OnDonationClickListener {
        void onDonationClick(Donation donation);
    }
}