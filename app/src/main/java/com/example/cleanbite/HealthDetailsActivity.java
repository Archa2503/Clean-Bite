package com.example.cleanbite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HealthDetailsActivity extends AppCompatActivity {

    private EditText dobEditText;
    private EditText ageEditText;
    private EditText emailEditText;
    private TextView emailValidationTextView;
    private EditText heightEditText;
    private EditText weightEditText;
    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_details);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Check if health details exist in the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("health_details").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Health details already exist, redirect to another activity
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
        emailEditText = findViewById(R.id.email);
        emailEditText.setEnabled(false);
        dobEditText = findViewById(R.id.DOB);
        dobEditText.setEnabled(false); // Disable editing
        ageEditText = findViewById(R.id.age);
        ageEditText.setEnabled(false); // Disable editing
        heightEditText = findViewById(R.id.height);
        weightEditText = findViewById(R.id.weight);

        // Set up CheckBox and RadioGroup
        android.widget.CheckBox conditionsCheckBox = findViewById(R.id.conditions);

        // Find email validation TextView
        emailValidationTextView = findViewById(R.id.emailValidationTextView);

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

                // Proceed with saving to Firestore if inputs are valid
            }
        });
    }
}
