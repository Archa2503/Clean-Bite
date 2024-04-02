package com.example.cleanbite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FileComplaintActivity extends AppCompatActivity {
    private TextView email, subject, message;
    private ImageButton button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);
        email = findViewById(R.id.email1);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
        button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senEmail();
            }
        });
    }

    private void senEmail() {
        String mEmail = email.getText().toString();
        String mSubject = subject.getText().toString();
        String mMessage = message.getText().toString();
        JavaMailApi javaMailAPI = new JavaMailApi(this, mEmail, mSubject, mMessage) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Show a toast message
                Toast.makeText(FileComplaintActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
            }
        };
        javaMailAPI.execute();
    }
}
