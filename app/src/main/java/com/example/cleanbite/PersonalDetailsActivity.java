package com.example.cleanbite;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, mobileEditText, dobEditText, heightEditText, weightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        dobEditText = findViewById(R.id.dobEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);

        // Set email address automatically from Firebase (replace with your Firebase code)
        // Assuming you have a Firebase user object with email information
        // Replace "user.getEmail()" with the actual method to get the email from Firebase user object
        emailEditText.setText("user@example.com");

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPersonalDetails();
            }
        });
    }

    private void submitPersonalDetails() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String height = heightEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();

        // Validate input fields if needed
        if (name.isEmpty() || mobile.isEmpty() || dob.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate age from date of birth
        // Example code to calculate age:
        // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // Date birthDate = sdf.parse(dob);
        // long ageInMillis = System.currentTimeMillis() - birthDate.getTime();
        // int age = (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));

        // Display a toast with the entered details (for demonstration)
        String message = "Name: " + name + "\nEmail: " + email + "\nMobile: " + mobile + "\nDOB: " + dob + "\nHeight: " + height + " cm\nWeight: " + weight + " kg";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Store the details in Firebase or a local database

        // Finish this activity
        finish();
    }
}