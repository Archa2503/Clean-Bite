package com.example.cleanbite;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FileComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);

        // Set action bar title
        getSupportActionBar().setTitle("File Complaint");

        // Attach Button
        ImageButton attachButton = findViewById(R.id.attachButton);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add functionality to attach button
                // For example: Open file picker to select a file to attach
            }
        });

        // Send Button
        ImageButton sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add functionality to send button
                // For example: Send email to the specified recipient
            }
        });
    }
}
