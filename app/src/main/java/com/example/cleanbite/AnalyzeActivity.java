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
                // Retrieve entered ingredients from intent
                String[] enteredIngredients = getIntent().getStringArrayExtra("enteredIngredients");
                if (enteredIngredients != null && enteredIngredients.length > 0) {
                    // Join ingredients into a single string
                    String userInput = String.join(", ", enteredIngredients);
                    try {
                        predictToxicity(userInput);
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
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                textView.setText(stringText);

                // Extract toxicity level and numerical value from the response
                String toxicityLevel = extractToxicityLevelFromResponse(stringText);
                float numericValue = extractNumericValueFromResponse(stringText);

                // Set the value and toxicity level on the GaugeView
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
        // Implement your logic to extract the toxicity level from the response string
        // For example, you could look for a pattern like "toxicity level: high" or similar
        // and return the corresponding toxicity level string
        return "high"; // Replace with your actual implementation
    }

    private float extractNumericValueFromResponse(String response) {
        // Implement your logic to extract the numerical value from the response string
        // For example, you could use regular expressions to find patterns like "numerical value: 4.2"
        // and extract the numeric value as a float
        // Here's a simple example using regular expressions:

        String pattern = "numerical value: (\\d+(\\.\\d+)?)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(response);

        if (matcher.find()) {
            return Float.parseFloat(matcher.group(1)); // Return the matched numeric value as a float
        } else {
            return 0.0f; // Return a default value if no match is found
        }
}}