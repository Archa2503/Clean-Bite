package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class DiseaseDetailsActivity extends AppCompatActivity {

    private LinearLayout healthDetailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        healthDetailsContainer = findViewById(R.id.healthDetailsContainer);
        Button addHealthDetailsButton = findViewById(R.id.addHealthDetailsButton);
        Button nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the next page or perform any desired action
                Intent intent = new Intent(DiseaseDetailsActivity.this, Dashboard.class);
                startActivity(intent);
            }
        });

        addHealthDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHealthDetailsCard();
            }
        });
    }

    private void addHealthDetailsCard() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View healthDetailsView = inflater.inflate(R.layout.health_details_item, null);
        healthDetailsContainer.addView(healthDetailsView);

        // Find EditText views and submit button within the inflated layout
        EditText diseaseNameEditText = healthDetailsView.findViewById(R.id.diseaseNameEditText);
        EditText durationEditText = healthDetailsView.findViewById(R.id.durationEditText);
        EditText sideEffectsEditText = healthDetailsView.findViewById(R.id.sideEffectsEditText);
        Button submitButton = healthDetailsView.findViewById(R.id.submitButton);

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve text from EditText fields
                String diseaseName = diseaseNameEditText.getText().toString().trim();
                String duration = durationEditText.getText().toString().trim();
                String sideEffects = sideEffectsEditText.getText().toString().trim();

                // Check if any field is empty
                if (diseaseName.isEmpty() || duration.isEmpty() || sideEffects.isEmpty()) {
                    Toast.makeText(DiseaseDetailsActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new document in Firestore's "diseasedetails" collection
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("diseasedetails").document()
                        .set(new DiseaseDetailsModel(diseaseName, duration, sideEffects))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully written to Firestore
                                Toast.makeText(DiseaseDetailsActivity.this, "Disease details saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle errors
                                Toast.makeText(DiseaseDetailsActivity.this, "Failed to save disease details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
