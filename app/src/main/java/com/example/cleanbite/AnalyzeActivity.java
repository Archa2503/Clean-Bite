package com.example.cleanbite;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeActivity extends AppCompatActivity {
    private String apikey = "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";
    private TextView textView;
    private TextView textView2;
    private String stringEndPointURL = "https://api.openai.com/v1/chat/completions";
    private FirebaseFirestore db;
    private String toxicityLevel; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        textView = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        db = FirebaseFirestore.getInstance();

        GaugeView gaugeView1 = findViewById(R.id.gaugeView1);
        // Set the initial value of the gauge view
        //gaugeView1.setValue(0,);

        Button predictButton = findViewById(R.id.button);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userInputArray = getIntent().getStringArrayExtra("enteredIngredients");
                if (userInputArray != null && userInputArray.length > 0) {
                    StringBuilder userInputBuilder = new StringBuilder();
                    for (String ingredient : userInputArray) {
                        userInputBuilder.append(ingredient.toLowerCase()).append(", ");
                    }
                    // Remove the trailing comma and space
                    String userInput = userInputBuilder.substring(0, userInputBuilder.length() - 2);
                    try {
                        predictToxicity(userInput);
                        fetchIngredientsFromDatabase(userInputArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle case where no ingredients are entered
                    Log.e("AnalyzeActivity", "No ingredients entered.");
                }
            }
        });
    }

    private void predictToxicity(String userInput) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");

            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("Predict the toxicity of the content from the list and give some suggestion for the usage of that item also give a toxicity level of high medium or low:");
            contentBuilder.append(" - ").append(userInput);  // Add user input to the content
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

                    // Extract the toxicity level from the response
                    toxicityLevel = extractToxicityLevel(stringText);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                textView.setText(stringText);
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
    }

    private String extractToxicityLevel(String response) {
        // Use a regular expression or string manipulation to extract the toxicity level
        // from the response string. You can customize this method based on the format
        // of the response from the OpenAI API.

        // For example, if the response contains "Toxicity Level: High", you could use:
        String regex = "Toxicity Level: (Low|Medium|High)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }

        // If the toxicity level is not found, return a default value
        return "low";
    }

    private void fetchIngredientsFromDatabase(String[] userInputArray) {
        db.collection("ingredients")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            StringBuilder result = new StringBuilder();
                            int totalScore = 0;
                            int ingredientCount = userInputArray.length;
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String ingredientName = document.getString("name");
                                for (String userInput : userInputArray) {
                                    if (userInput.toLowerCase().contains(ingredientName.toLowerCase())) {
                                        String toxicityLevel = document.getString("toxicityLevel");
                                        int score = getToxicityScore(toxicityLevel);
                                        totalScore += score;
                                        result.append(userInput).append(": ").append(toxicityLevel).append(" (").append(score).append("/100)\n");
                                    }
                                }
                            }
                            int averageScore = totalScore / ingredientCount;
                            result.append("\nAverage Toxicity Rating: ").append(averageScore).append("/100");
                            textView2.setText(result.toString());

                            // Update the gauge view with the average score and toxicity level
                            GaugeView gaugeView1 = findViewById(R.id.gaugeView1);
                            gaugeView1.setValue(averageScore, toxicityLevel);
                        } else {
                            textView2.setText("No ingredients found in the database.");
                        }
                    } else {
                        Log.e("AnalyzeActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private int getToxicityScore(String toxicityLevel) {
        switch (toxicityLevel.toLowerCase()) {
            case "low":
                return 25;
            case "medium":
                return 50;
            case "high":
                return 75;
            default:
                return 0;
        }
    }
}