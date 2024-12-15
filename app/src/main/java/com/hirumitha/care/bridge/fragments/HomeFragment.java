package com.hirumitha.care.bridge.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.adapters.CarouselAdapter;
import com.hirumitha.care.bridge.adapters.DonationAdapter;
import com.hirumitha.care.bridge.activities.DonateActivity;
import com.hirumitha.care.bridge.models.Donation;
import com.hirumitha.care.bridge.activities.RequestActivity;
import com.hirumitha.care.bridge.activities.DonationDetailActivity;
import com.hirumitha.care.bridge.activities.ProductSearchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ImageView profileImage, themeChanger;
    private TextView profileName;
    private FirebaseFirestore firestore;
    private PieChart pieChart;
    private SearchView searchView;
    private RecyclerView latestDonationsRecyclerView;
    private RecyclerView searchResultsRecyclerView;
    private DonationAdapter latestDonationsAdapter;
    private DonationAdapter searchResultsAdapter;
    private ArrayList<Donation> latestDonationsList;
    private ArrayList<Donation> searchResultsList;
    private ViewPager2 imageCarouselViewPager;
    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = rootView.findViewById(R.id.search_view);
        profileImage = rootView.findViewById(R.id.user_profile_picture);
        themeChanger = rootView.findViewById(R.id.theme_changer);
        profileName = rootView.findViewById(R.id.user_welcome_text);
        pieChart = rootView.findViewById(R.id.pie_chart);
        latestDonationsRecyclerView = rootView.findViewById(R.id.latest_donations_recycler_view);
        searchResultsRecyclerView = rootView.findViewById(R.id.search_results_recycler_view);
        imageCarouselViewPager = rootView.findViewById(R.id.imageCarouselViewPager);

        firestore = FirebaseFirestore.getInstance();
        handler = new Handler(Looper.getMainLooper());

        setupCarousel();
        setupSearchView();
        loadUserProfile();
        fetchDonationData();
        setupLatestDonationsRecyclerView();
        setupSearchResultsRecyclerView();
        loadLatestDonations();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            themeChanger.setImageResource(R.drawable.ic_dark_mode);
        } else {
            themeChanger.setImageResource(R.drawable.ic_light_mode);
        }

        setupThemeToggle();

        CardView donateButton = rootView.findViewById(R.id.button_donate);
        CardView requestButton = rootView.findViewById(R.id.button_request);
        donateButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), DonateActivity.class)));
        requestButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RequestActivity.class)));

        return rootView;
    }

    private void setupCarousel() {
        List<Integer> images = Arrays.asList(
                R.drawable.carousel_image_01,
                R.drawable.carousel_image_02,
                R.drawable.carousel_image_03,
                R.drawable.carousel_image_04,
                R.drawable.carousel_image_05
        );

        CarouselAdapter carouselAdapter = new CarouselAdapter(images);
        imageCarouselViewPager.setAdapter(carouselAdapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (imageCarouselViewPager.getCurrentItem() + 1) % images.size();
                imageCarouselViewPager.setCurrentItem(nextItem);
                handler.postDelayed(runnable, 3000);
            }
        };
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                } else {
                    searchForProduct(newText);
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void searchForProduct(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Donations");
        String searchText = query.toLowerCase();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Donation> searchResults = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null && donation.getProductName().toLowerCase().contains(searchText)) {
                        searchResults.add(donation);
                    }
                }
                showSearchResults(searchResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getString(R.string.search_failed) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showSearchResults(ArrayList<Donation> searchResults) {
        searchResultsList.clear();
        searchResultsList.addAll(searchResults);
        searchResultsAdapter.notifyDataSetChanged();
        searchResultsRecyclerView.setVisibility(searchResults.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupSearchResultsRecyclerView() {
        searchResultsList = new ArrayList<>();
        searchResultsAdapter = new DonationAdapter(getActivity(), searchResultsList, donation -> {
            navigateToSearchedProductDetail(donation);
        });

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
    }

    private void navigateToSearchedProductDetail(Donation donation) {
        Intent intent = new Intent(getActivity(), ProductSearchActivity.class);
        intent.putExtra("donation", donation);
        startActivity(intent);
    }

    private void navigateToProductDetail(Donation donation) {
        Intent intent = new Intent(getActivity(), DonationDetailActivity.class);
        intent.putExtra("donation", donation);
        startActivity(intent);
    }

    private void loadUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && isAdded()) {
                            String name = documentSnapshot.getString("name");
                            String imageUrl = documentSnapshot.getString("profileImageUrl");

                            profileName.setText(name != null ? name : getString(R.string.df_user));

                            if (imageUrl != null) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.default_profile_picture)
                                        .into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.default_profile_picture);
                            }
                        }
                    });
        }
    }

    private void setupThemeToggle() {
        themeChanger.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themeChanger.setImageResource(R.drawable.ic_light_mode);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeChanger.setImageResource(R.drawable.ic_dark_mode);
            }
        });
    }

    private void fetchDonationData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Donations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Integer> categoryCount = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null) {
                        String category = donation.getCategory();
                        categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                    }
                }
                displayPieChart(categoryCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), R.string.failed_to_load_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPieChart(Map<String, Integer> categoryCount) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        Map<String, Integer> categoryColors = new HashMap<>();
        categoryColors.put("Food and Water", Color.parseColor("#FF0000"));
        categoryColors.put("Education Materials", Color.parseColor("#FF00B8"));
        categoryColors.put("Electronic and Communication", Color.parseColor("#000000"));
        categoryColors.put("Shelter Items", Color.parseColor("#FFC700"));
        categoryColors.put("Medical Supplies", Color.parseColor("#0019FF"));
        categoryColors.put("Hygiene Supplies", Color.parseColor("#42FF00"));
        categoryColors.put("Clothing", Color.parseColor("#FF8A00"));

        int defaultColor = Color.GRAY;

        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            Integer color = categoryColors.getOrDefault(entry.getKey(), defaultColor);
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(0f);

        PieData data = new PieData(dataSet);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.pie_data_color));
        data.setValueTextSize(12f);

        pieChart.setData(data);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(ContextCompat.getColor(getContext(), R.color.pie_data_color));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.card_color));
        pieChart.setHoleRadius(50f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(0f, 0f, 0f, 0f);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(Color.BLACK);

        pieChart.invalidate();
    }

    private void setupLatestDonationsRecyclerView() {
        latestDonationsList = new ArrayList<>();
        latestDonationsAdapter = new DonationAdapter(getActivity(), latestDonationsList, donation -> {
            navigateToProductDetail(donation);
        });
        latestDonationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        latestDonationsRecyclerView.setAdapter(latestDonationsAdapter);
    }

    private void loadLatestDonations() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Donations");
        databaseReference.limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latestDonationsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null) {
                        latestDonationsList.add(donation);
                    }
                }
                latestDonationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), R.string.failed_to_load_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}