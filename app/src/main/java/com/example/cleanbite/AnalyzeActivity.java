// AnalyzeActivity.java

package com.example.cleanbite;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeActivity extends AppCompatActivity {
    private String apikey = "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";
    private TextView textView, textview2;
    private GaugeView gaugeView;
    private String stringEndPointURL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        textView = findViewById(R.id.textView1);
        gaugeView = findViewById(R.id.gaugeView);
        Button predictButton = findViewById(R.id.button);
        Button healthAssessmentButton = findViewById(R.id.healthAssessmentButton);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] enteredIngredients = getIntent().getStringArrayExtra("enteredIngredients");
                if (enteredIngredients != null && enteredIngredients.length > 0) {
                    Log.d("AnalyzeActivity", "Ingredients: " + Arrays.toString(enteredIngredients));
                    String userInput = String.join(", ", enteredIngredients);
                    try {
                        predictToxicity(userInput);
                        compareIngredientsWithFirestore(userInput);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("AnalyzeActivity", "No ingredients entered.");
                }
            }
        });

        healthAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDiseaseDetails();
            }
        });
    }

    private void predictToxicity(String userInput) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            jsonObject.put("temperature", 0.1); // Example temperature value
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
        // Define the patterns
        String pattern1 = "Toxicity\\slevel:\\s(\\d+)";
        String pattern2 = "Toxicity\\sof\\soverall\\sproduct:\\s(\\d+)";

        // Compile the patterns
        Pattern regex1 = Pattern.compile(pattern1);
        Pattern regex2 = Pattern.compile(pattern2);

        // Match the patterns against the response
        Matcher matcher1 = regex1.matcher(response);
        Matcher matcher2 = regex2.matcher(response);

        // Check if either pattern matches
        if (matcher1.find()) {
            return Float.parseFloat(matcher1.group(1));
        } else if (matcher2.find()) {
            return Float.parseFloat(matcher2.group(1));
        } else {
            return 0.0f; // Return a default value if no match is found
        }
    }

    private void compareIngredientsWithFirestore(String userInput) {
        String[] userIngredients = userInput.split(",\\s*");
        for (int i = 0; i < userIngredients.length; i++) {
            userIngredients[i] = userIngredients[i].trim().toLowerCase(); // Normalize the ingredient
        }

        TextView textView2 = findViewById(R.id.textView2);
        new FetchIngredientsTask(textView2).execute(userIngredients);
    }

    private class FetchIngredientsTask extends AsyncTask<String[], Void, List<String>> {
        private TextView textView2;

        FetchIngredientsTask(TextView textView2) {
            this.textView2 = textView2;
        }

        @Override
        protected List<String> doInBackground(String[]... params) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String[] userIngredients = params[0];
            List<String> results = new ArrayList<>();

            for (String ingredient : userIngredients) {
                Task<DocumentSnapshot> task = db.collection("ingredients")
                        .document(ingredient)
                        .get();

                try {
                    DocumentSnapshot document = Tasks.await(task);
                    if (document.exists()) {
                        String name = document.getString("name");
                        String explanation = document.getString("explanation");
                        String toxicityLevel = document.getString("toxicityLevel");

                        StringBuilder resultBuilder = new StringBuilder();
                        resultBuilder.append("Name: ").append(name).append("\n");
                        resultBuilder.append("Explanation: ").append(explanation).append("\n");
                        resultBuilder.append("Toxicity Level: ").append(toxicityLevel).append("\n");
                        resultBuilder.append("\n");

                        results.add(resultBuilder.toString());
                    } else {
                        results.add("Error: Ingredient '" + ingredient + "' not found in the database.\n\n");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            for (String result : results) {
                textView2.append(result);
            }
        }
    }

    private void fetchDiseaseDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> diseaseDetailsTask = db.collection("diseasedetails")
                .document("documentId")
                .get();

        diseaseDetailsTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String diseaseName = documentSnapshot.getString("diseaseName");
                    launchHealthAssessmentActivity(diseaseName);
                } else {
                    Log.e("AnalyzeActivity", "Disease details document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("AnalyzeActivity", "Error fetching disease details", e);
            }
        });
    }

    private void launchHealthAssessmentActivity(String diseaseName) {
        String[] enteredIngredients = getIntent().getStringArrayExtra("enteredIngredients");
        if (enteredIngredients != null && enteredIngredients.length > 0) {
            Intent intent = new Intent(AnalyzeActivity.this, HealthAssessmentActivity.class);
            intent.putExtra("enteredIngredients", enteredIngredients);
            intent.putExtra("diseaseName", diseaseName);
            startActivity(intent);
        } else {
            Log.e("AnalyzeActivity", "No ingredients entered.");
        }
    }
}
