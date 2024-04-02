package com.example.cleanbite;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeActivity extends AppCompatActivity {
    private String apikey = "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";
    private TextView textView,textview2;

    private GaugeView gaugeView;
    private String stringEndPointURL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        textView = findViewById(R.id.textView1);

        gaugeView = findViewById(R.id.gaugeView);


        Button predictButton = findViewById(R.id.button);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] enteredIngredients = getIntent().getStringArrayExtra("enteredIngredients");
                if (enteredIngredients != null && enteredIngredients.length > 0) {
                    String userInput = String.join(", ", enteredIngredients);
                    try {
                        predictToxicity(userInput);
                        compareIngredientsWithFirestore(userInput); // Call the method to compare with Firestore
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
            contentBuilder.append("Predict the toxicity of the following ingredients and provide suggestions for their usage. Additionally, rate the toxicity level on a scale of 0 to 5, with 0 being non-toxic and 5 being highly toxic. " +
                    "the output shouls in the fromat :" +
                    "toxicity of ingredients :" +
                    "toxicity level :" +
                    "suggestion of usage :" +
                    "toxicity of overall product:");

            contentBuilder.append(" - ").append(userInput);
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

                textView.setText(stringText);

                String toxicityLevel = extractToxicityLevelFromResponse(stringText);
                float numericValue = extractNumericValueFromResponse(stringText);

                gaugeView.setValue(numericValue);
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

    private String extractToxicityLevelFromResponse(String response) {
        return "high";
    }

    private float extractNumericValueFromResponse(String response) {
        String pattern = "Toxicity\\slevel:\\s(\\d+)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(response);

        if (matcher.find()) {
            return Float.parseFloat(matcher.group(1));
        } else {
            return 0.0f;
        }
    }




    private void compareIngredientsWithFirestore(String userInput) {
        TextView textView2 = findViewById(R.id.textView2);
        Log.d("AnalyzeActivity", "User input: " + userInput);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Split the user input into individual ingredients and normalize them
        String[] userIngredients = userInput.split(",\\s*");
        for (int i = 0; i < userIngredients.length; i++) {
            userIngredients[i] = userIngredients[i].trim().toLowerCase(); // Normalize the ingredient
        }

        db.collection("ingredients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder resultBuilder = new StringBuilder();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve specific fields from the document
                                String name = document.getString("name");
                                String explanation = document.getString("explanation");
                                String toxicityLevel = document.getString("toxicityLevel");

                                // Append the retrieved fields to the result string
                                if (name != null && explanation != null && toxicityLevel != null) {
                                    resultBuilder.append("Name: ").append(name).append("\n");
                                    resultBuilder.append("Explanation: ").append(explanation).append("\n");
                                    resultBuilder.append("Toxicity Level: ").append(toxicityLevel).append("\n");
                                    resultBuilder.append("\n");
                                }
                            }
                            String result = resultBuilder.toString();
                            // Set the result text to the TextView
                            textView2.setText(result);
                        } else {
                            Log.d("AnalyzeActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



}
