package com.example.cleanbite;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AnalyzeActivity extends AppCompatActivity {
    private String apikey = "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";
    private TextView textView;
    private String stringEndPointURL = "https://api.openai.com/v1/chat/completions";
    private String output = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        textView = findViewById(R.id.textView1);
        db = FirebaseFirestore.getInstance();

        GaugeView gaugeView1 = findViewById(R.id.gaugeView1);
        gaugeView1.setValue(60);

        Button button = findViewById(R.id.button);
        final EditText editText = findViewById(R.id.editText); // Assuming your EditText has id editText

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    String[] ingredients = userInput.split(",\\s*");
                    if (ingredients.length > 0) {
                        try {
                            model(ingredients);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("AnalyzeActivity", "No ingredients found in the user input.");
                        // Handle case where no ingredients are found
                    }
                } else {
                    Log.e("AnalyzeActivity", "User input is empty.");
                    // Handle case where user input is empty
                }
            }
        });
    }

    public void model(String[] ingredients) throws JSONException {
        output = "";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");

            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("Predict the toxicity of the content from the list and give a rating out of 5:");
            for (String ingredient : ingredients) {
                contentBuilder.append(" - ").append(ingredient);
            }
            String content = contentBuilder.toString();
            jsonObjectMessage.put("content", content);

            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put("messages", jsonArrayMessage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                stringEndPointURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                output = output + stringText + "\n";
                textView.setText(output);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AnalyzeActivity", "Error in OpenAI API request", error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + apikey);
                mapHeader.put("Content-Type", "application/json");

                return mapHeader;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int intTimeoutPeriod = 60000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

        for (String ingredient : ingredients) {
            DocumentReference docRef = db.collection("ingredients").document(ingredient);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String content = documentSnapshot.getString("content");
                        output += ingredient + ": " + content + "\n";
                        textView.setText(output);
                    } else {
                        Log.d("AnalyzeActivity", "No such document");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("AnalyzeActivity", "Error getting document", e);
                }
            });
        }
    }
}
