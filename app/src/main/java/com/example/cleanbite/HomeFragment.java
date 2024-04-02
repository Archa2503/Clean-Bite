package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView greetingTextView;
    private TextView usernameTextView;
    private FloatingActionButton enterIngredientsFab;
    private FloatingActionButton Complaint;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        greetingTextView = view.findViewById(R.id.textView4);
        usernameTextView = view.findViewById(R.id.textView3);
        enterIngredientsFab = view.findViewById(R.id.floatingActionButton2);
        View scanFab = view.findViewById(R.id.floatingActionButton1);
        Complaint= view.findViewById(R.id.floatingActionButton3);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        enterIngredientsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start EnterIngredientActivity when FloatingActionButton is clicked
                startActivity(new Intent(getActivity(), EnterIngredientsActivity.class));
            }
        });

        scanFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanActivity.class));
            }
        });

        Complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FileComplaintActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateGreetingMessage();
        fetchUserName();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void fetchUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userListener = db.collection("health_details").document(userId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            // Handle errors
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("name");
                            usernameTextView.setText("Hi " + username);
                        }
                    });
        }
    }

    private void updateGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        greetingTextView.setText(greeting);
    }
}
