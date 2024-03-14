package com.example.cleanbite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HealthDetailsActivity extends AppCompatActivity {

    private EditText dobEditText;
    private EditText ageEditText;
    private EditText emailEditText;
    private TextView emailValidationTextView;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_details);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find and set up EditText fields
        EditText nameEditText = findViewById(R.id.names);
        emailEditText = findViewById(R.id.email); // Correct initialization
        dobEditText = findViewById(R.id.DOB); // Correct initialization
        ageEditText = findViewById(R.id.age); // Correct initialization
        EditText phoneEditText = findViewById(R.id.Phone);

        // Set up CheckBox and RadioGroup
        android.widget.CheckBox conditionsCheckBox = findViewById(R.id.conditions);
        android.widget.RadioGroup radioGroup = findViewById(R.id.radioGroup);

        // Find email validation TextView
        emailValidationTextView = findViewById(R.id.emailValidationTextView);

        // Set up click listener for the Date of Birth EditText
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up submit button click listener
        Button submitButton = findViewById(R.id.button);
        EditText finalAgeEditText = ageEditText;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the email EditText
                String email = emailEditText.getText().toString();

                // Validate the email
                if (!isValidEmail(email)) {
                    // Email is not valid, show an error message
                    Toast.makeText(HealthDetailsActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve the phone number EditText
                String phoneNumber = phoneEditText.getText().toString();

                // Validate the phone number using regex
                if (!isValidPhoneNumber(phoneNumber)) {
                    // Phone number is not valid, show an error message
                    Toast.makeText(HealthDetailsActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve the date of birth EditText
                String dobStr = dobEditText.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dob = null;
                try {
                    dob = sdf.parse(dobStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }

                // Calculate age
                int age = calculateAge(dob);

                // Set the age in the ageEditText
                finalAgeEditText.setText(String.valueOf(age));

                // Check if the conditions checkbox is checked
                if (!conditionsCheckBox.isChecked()) {
                    // Conditions checkbox is not checked, show an error message
                    Toast.makeText(HealthDetailsActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display details in a popup
                showDetailsPopup(nameEditText.getText().toString(), email, phoneNumber, dobStr, String.valueOf(age));



                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String name = ((EditText) findViewById(R.id.names)).getText().toString();
                    String mail = ((EditText) findViewById(R.id.email)).getText().toString();
                    String date = ((EditText) findViewById(R.id.DOB)).getText().toString();
                    String ag = ((EditText) findViewById(R.id.age)).getText().toString();
                    String phone = ((EditText) findViewById(R.id.Phone)).getText().toString();

                    // Check for null or empty fields
                    if (name.isEmpty() || mail.isEmpty() || date.isEmpty() || ag.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(HealthDetailsActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a HealthDetails object with the details
                    HealthDetails healthDetails = new HealthDetails(name, mail, date, ag, phone);

                    // Access Firestore instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Add a new document with a generated ID under the "health_details" collection
                    db.collection("health_details").document(userId)
                            .set(healthDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(HealthDetailsActivity.this, "Health details added successfully", Toast.LENGTH_SHORT).show();
                                    // Redirect to a new activity to enter user details
                                    Intent intent = new Intent(HealthDetailsActivity.this, UserDetailsActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HealthDetailsActivity.this, "Error adding health details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display selected date in EditText
                        if (dobEditText != null) {
                            dobEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression to validate a phone number
        String phoneRegex = "^(?:\\+?\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$";
        return phoneNumber.matches(phoneRegex);
    }

    private int calculateAge(Date dob) {
        Calendar dobCal = Calendar.getInstance();
        dobCal.setTime(dob);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private boolean isValidEmail(String email) {
        // Simple email validation using regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void showDetailsPopup(String name, String email, String phone, String dob, String age) {
        // Create a dialog with custom layout
        Dialog dialog = new Dialog(HealthDetailsActivity.this);
        dialog.setContentView(R.layout.popup_layout);

        // Set text views with user details
        TextView nameTextView = dialog.findViewById(R.id.names);
        nameTextView.setText("Name: " + name);

        TextView emailTextView = dialog.findViewById(R.id.email);
        emailTextView.setText("Email: " + email);

        TextView phoneTextView = dialog.findViewById(R.id.Phone);
        phoneTextView.setText("Phone: " + phone);

        TextView dobTextView = dialog.findViewById(R.id.DOB);
        dobTextView.setText("Date of Birth: " + dob);

        TextView ageTextView = dialog.findViewById(R.id.age);
        ageTextView.setText("Age: " + age);

        // Show the dialog
        dialog.show();
    }


}


