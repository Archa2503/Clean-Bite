package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;

public class DisplayIngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ingredients);

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText("Entered Ingredients");

        LinearLayout ingredientsLayout = findViewById(R.id.ingredientsLayout);

        // Retrieve entered ingredients from the previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("enteredIngredients")) {
            String[] enteredIngredients = intent.getStringArrayExtra("enteredIngredients");
            if (enteredIngredients != null) {
                for (String ingredient : enteredIngredients) {
                    // Add TextView for each entered ingredient
                    TextView textView = new TextView(this);
                    textView.setText(ingredient);
                    ingredientsLayout.addView(textView);
                }
            }
        }

        // Retrieve recognized text from the previous activity
        if (intent != null && intent.hasExtra("recognizedText")) {
            String recognizedText = intent.getStringExtra("recognizedText");
            if (recognizedText != null && !recognizedText.isEmpty()) {
                // Add TextView for displaying recognized text
                TextView recognizedTextView = new TextView(this);
                recognizedTextView.setText(recognizedText);
                ingredientsLayout.addView(recognizedTextView);
            }
        }

        // Back button to finish the activity
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the activity and go back
            }
        });

        // Analyze button to start the AnalyzeActivity
        Button analyzeButton = findViewById(R.id.analyzeButton);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the entered ingredients from the intent
                String[] enteredIngredients = getIntent().getStringArrayExtra("enteredIngredients");

                // Log the entered ingredients
                if (enteredIngredients != null && enteredIngredients.length > 0) {
                    Log.d("DisplayIngredients", "Entered ingredients:");
                    for (String ingredient : enteredIngredients) {
                        Log.d("DisplayIngredients", ingredient);
                    }
                } else {
                    Log.e("DisplayIngredients", "No ingredients entered.");
                }

                // Save the entered ingredients to the "scanHistory" collection
                saveToScanHistory(enteredIngredients);

                // Start the AnalyzeActivity and pass the list of ingredients
                Intent intent = new Intent(DisplayIngredientsActivity.this, AnalyzeActivity.class);
                intent.putExtra("enteredIngredients", enteredIngredients);
                startActivity(intent);
            }
        });
    }

    private void saveToScanHistory(String[] enteredIngredients) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a new document in the "scanHistory" collection with a unique ID
            db.collection("scanHistory")
                    .add(new HashMap<String, Object>() {
                        {
                            put("userId", userId);
                            put("ingredients", Arrays.asList(enteredIngredients));
                            // Add any other fields you want to store
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("AnalyzeActivity", "Data saved to Firestore");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("AnalyzeActivity", "Error saving data to Firestore", e);
                        }
                    });
        } else {
            Log.e("AnalyzeActivity", "User not authenticated");
        }
    }
}
