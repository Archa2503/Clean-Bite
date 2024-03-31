package com.example.cleanbite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HealthDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_details);

        mAuth = FirebaseAuth.getInstance();

        // Check if health details exist in the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("health_details").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Health details already exist, redirect to Dashboard
                        startActivity(new Intent(HealthDetailsActivity.this, Dashboard.class));
                        finish(); // Finish this activity
                    } else {
                        // Health details do not exist, proceed with setting up the activity
                        setupActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch health details
                    Toast.makeText(HealthDetailsActivity.this, "Failed to fetch health details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupActivity() {
        // Find and set up EditText fields
        EditText nameEditText = findViewById(R.id.names);
        EditText emailEditText = findViewById(R.id.email);
        emailEditText.setEnabled(false);
        EditText dobEditText = findViewById(R.id.DOB);
        dobEditText.setEnabled(false); // Disable editing
        EditText ageEditText = findViewById(R.id.age);
        ageEditText.setEnabled(false); // Disable editing
        EditText heightEditText = findViewById(R.id.height);
        EditText weightEditText = findViewById(R.id.weight);

        // Set up CheckBox and RadioGroup
        android.widget.CheckBox conditionsCheckBox = findViewById(R.id.conditions);

        // Find email validation TextView
        TextView emailValidationTextView = findViewById(R.id.emailValidationTextView);

        // Fetch user data from Firestore and populate EditText fields
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Retrieve user data
                    String userEmail = documentSnapshot.getString("email");
                    String userDOB = documentSnapshot.getString("dob");
                    int userAge = documentSnapshot.getLong("age").intValue(); // Assuming "age" is stored as a numeric field

                    // Update EditText fields with user data
                    emailEditText.setText(userEmail);
                    dobEditText.setText(userDOB);
                    ageEditText.setText(String.valueOf(userAge));
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch user data
                    Toast.makeText(HealthDetailsActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Set up click listener for the Date of Birth EditText
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing since the field is disabled
            }
        });

        // Set up submit button click listener
        Button submitButton = findViewById(R.id.button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve and validate height and weight inputs
                String heightStr = heightEditText.getText().toString().trim();
                String weightStr = weightEditText.getText().toString().trim();

                // Validate height and weight inputs
                if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
                    // Convert height and weight strings to appropriate data types
                    double height = Double.parseDouble(heightStr);
                    double weight = Double.parseDouble(weightStr);

                    // Fetch additional data needed for HealthDetails
                    String name = nameEditText.getText().toString().trim(); // Get name from EditText
                    String email = emailEditText.getText().toString().trim(); // Get email from EditText

                    // Prepare data to be saved
                    HealthDetails healthDetails = new HealthDetails(name, email, null, null, height, weight);

                    // Get current user ID
                    String userId = mAuth.getCurrentUser().getUid();

                    // Save health details to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("health_details").document(userId).set(healthDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Health details saved successfully, navigate to Dashboard
                                    startActivity(new Intent(HealthDetailsActivity.this, Dashboard.class));
                                    finish(); // Finish this activity
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to save health details
                                    Toast.makeText(HealthDetailsActivity.this, "Failed to save health details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Show error message if height or weight is empty
                    Toast.makeText(HealthDetailsActivity.this, "Please enter both height and weight.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}