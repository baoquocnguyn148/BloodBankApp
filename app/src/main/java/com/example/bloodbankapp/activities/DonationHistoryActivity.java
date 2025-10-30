package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.widget.Toast; // Import Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.DonationHistoryAdapter; // Assuming you have this adapter
import com.example.bloodbankapp.database.DatabaseHelper; // ✅ Use DatabaseHelper
import com.example.bloodbankapp.models.Donation; // Assuming you have this model
import com.example.bloodbankapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

public class DonationHistoryActivity extends AppCompatActivity {

    private RecyclerView rvDonationHistory;
    private DonationHistoryAdapter adapter; // ✅ Use the adapter

    // private UserDAO userDAO; // ❌ Remove UserDAO
    private DatabaseHelper dbHelper; // ✅ Use DatabaseHelper instead
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming your layout file is named activity_donation_history.xml
        // If the file name is different (like item_donation_history.xml), change it here.
        setContentView(R.layout.activity_donation_history);

        Toolbar toolbar = findViewById(R.id.toolbar); // Make sure R.id.toolbar exists in your layout
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Donation History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Handle toolbar navigation click
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            // Fallback if toolbar isn't found or set correctly
            // You might want to add a simple back button or log an error
            System.err.println("Toolbar not found or not set correctly in DonationHistoryActivity");
        }


        // Make sure R.id.rv_donation_history exists in your layout
        rvDonationHistory = findViewById(R.id.rv_donation_history);

        mAuth = FirebaseAuth.getInstance();
        // userDAO = new UserDAO(this); // ❌ Remove UserDAO instantiation
        dbHelper = new DatabaseHelper(this); // ✅ Instantiate DatabaseHelper

        setupRecyclerView();
        loadDonationHistory();
    }

    private void setupRecyclerView() {
        rvDonationHistory.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with an empty list
        adapter = new DonationHistoryAdapter(new ArrayList<>());
        rvDonationHistory.setAdapter(adapter);
    }

    private void loadDonationHistory() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            // Optionally finish the activity or redirect to login
            // finish();
            return;
        }

        String userEmail = firebaseUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // User currentUser = userDAO.getUserByEmail(userEmail); // ❌ Use dbHelper instead
        User currentUser = dbHelper.getUserByEmail(userEmail); // ✅ Get user info via DatabaseHelper

        if (currentUser != null) {
            int userId = currentUser.getUserId();
            // TODO: Implement a method in DatabaseHelper to get donation history for a specific user ID.
            // For now, let's assume you get a list of Donation objects.
            // List<Donation> userDonations = dbHelper.getDonationsByUserId(userId);

            // --- Placeholder Data ---
            // Replace this with actual data loading logic once dbHelper.getDonationsByUserId is implemented
            List<Donation> userDonations = new ArrayList<>();
            userDonations.add(new Donation("Central Blood Bank", 1, "2025-08-15", "Completed"));
            userDonations.add(new Donation("City General Hospital", 1, "2025-05-02", "Completed"));
            // --- End Placeholder ---

            if (userDonations != null && !userDonations.isEmpty()) {
                // Update the adapter's data
                // adapter.updateData(userDonations); // Create this method in your adapter if needed
                adapter = new DonationHistoryAdapter(userDonations); // Or re-create the adapter
                rvDonationHistory.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No donation history found.", Toast.LENGTH_SHORT).show();
                // Optionally show a message in the UI instead of a Toast
            }
        } else {
            Toast.makeText(this, "Could not retrieve user details from local database.", Toast.LENGTH_SHORT).show();
            // Handle error, maybe user exists in Firebase Auth but not in local DB?
        }
    }

    // This method is usually handled by the toolbar's navigation click listener now
    // @Override
    // public boolean onSupportNavigateUp() {
    //     onBackPressed();
    //     return true;
    // }
}