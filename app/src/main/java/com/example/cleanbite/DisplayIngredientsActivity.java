package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
                // Start the AnalyzeActivity and pass the list of ingredients
                Intent intent = new Intent(DisplayIngredientsActivity.this, AnalyzeActivity.class);
                intent.putExtra("enteredIngredients", getIntent().getStringArrayExtra("enteredIngredients"));
                startActivity(intent);
            }
        });
    }
}
