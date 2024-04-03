package com.example.cleanbite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScanHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScanHistoryAdapter adapter;
    private List<ScanHistoryItem> scanHistoryList;
    private static final String TAG = "ScanHistoryActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerViewScanHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scanHistoryList = new ArrayList<>();
        adapter = new ScanHistoryAdapter(scanHistoryList);
        recyclerView.setAdapter(adapter);

        fetchScanHistoryData();
    }

    private void fetchScanHistoryData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user logged in, handle accordingly
            return;
        }

        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("scanHistory")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ScanHistoryItem> scanHistoryItems = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                List<String> ingredients = (List<String>) document.get("ingredients");
                                scanHistoryItems.add(new ScanHistoryItem(timestamp, ingredients));
                            }
                            scanHistoryList.clear();
                            scanHistoryList.addAll(scanHistoryItems);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private static class ScanHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView;
        RecyclerView ingredientsRecyclerView;

        ScanHistoryViewHolder(View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            ingredientsRecyclerView = itemView.findViewById(R.id.ingredientsRecyclerView);
        }
    }

    private class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryViewHolder> {
        private List<ScanHistoryItem> scanHistoryList;

        ScanHistoryAdapter(List<ScanHistoryItem> scanHistoryList) {
            this.scanHistoryList = scanHistoryList;
        }

        @NonNull
        @Override
        public ScanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_history, parent, false);
            return new ScanHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScanHistoryViewHolder holder, int position) {
            ScanHistoryItem item = scanHistoryList.get(position);
            Timestamp timestamp = item.getTimestamp();
            Date date = timestamp.toDate();
            String formattedDate = DateFormat.getDateTimeInstance().format(date);
            holder.timestampTextView.setText(formattedDate);

            holder.ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.ingredientsRecyclerView.setAdapter(new IngredientsAdapter(item.getIngredients()));
        }

        @Override
        public int getItemCount() {
            return scanHistoryList.size();
        }
    }

    private static class ScanHistoryItem {
        private Timestamp timestamp;
        private List<String> ingredients;

        ScanHistoryItem(Timestamp timestamp, List<String> ingredients) {
            this.timestamp = timestamp;
            this.ingredients = ingredients;
        }

        Timestamp getTimestamp() {
            return timestamp;
        }

        List<String> getIngredients() {
            return ingredients;
        }
    }

    private static class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
        private List<String> ingredients;

        IngredientsAdapter(List<String> ingredients) {
            this.ingredients = ingredients;
        }

        @NonNull
        @Override
        public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new IngredientsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
            String ingredient = ingredients.get(position);
            holder.textView.setText(ingredient);
        }

        @Override
        public int getItemCount() {
            return ingredients.size();
        }

        static class IngredientsViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            IngredientsViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}