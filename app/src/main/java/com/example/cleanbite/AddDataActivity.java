package com.example.cleanbite;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        Button addDataButton = findViewById(R.id.addDataButton);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToFirestore();
            }
        });
    }

    private void addDataToFirestore() {
        CollectionReference ingredientsRef = db.collection("ingredients");

        // Add all ingredients
        addIngredient(ingredientsRef, "wheat flour", "Low", "Wheat flour is generally safe for consumption in moderate amounts.");
        addIngredient(ingredientsRef, "sugar", "Medium", "Excessive consumption of sugar can lead to various health issues like obesity and diabetes.");
        addIngredient(ingredientsRef, "palmoil", "Medium", "Palm oil contains saturated fats which, when consumed in excess, can increase cholesterol levels.");
        addIngredient(ingredientsRef, "cocoapowder", "Low", "Cocoa powder is a natural ingredient with minimal toxicity.");
        addIngredient(ingredientsRef, "high fructose cornsyrup", "High", "High fructose corn syrup has been linked to obesity and other health issues when consumed excessively.");
        addIngredient(ingredientsRef, "soylecithin", "Low", "Soy lecithin is generally considered safe for consumption.");
        addIngredient(ingredientsRef, "salt", "Low", "Consuming too much salt can lead to high blood pressure and other health problems.");
        addIngredient(ingredientsRef, "baking soda", "Low", "Baking soda is safe for consumption in small amounts but excessive intake should be avoided.");
        addIngredient(ingredientsRef, "artificial flavor", "High", "Artificial flavors may contain chemicals that can have adverse health effects if consumed in large quantities.");
        addIngredient(ingredientsRef, "vanillin", "Medium", "Vanillin is an artificial flavoring agent commonly used in food products. It's generally considered safe, but excessive intake may cause allergic reactions in some individuals.");
        addIngredient(ingredientsRef, "monosodium glutamate (MSG)", "Medium", "MSG is a flavor enhancer that can cause adverse reactions such as headaches and nausea in sensitive individuals when consumed in large quantities.");
        addIngredient(ingredientsRef, "trans fat", "High", "Trans fats are known to increase the risk of heart disease and other health issues.");
        addIngredient(ingredientsRef, "artificial sweeteners", "Medium", "Artificial sweeteners like aspartame and saccharin may have adverse effects on health when consumed in excess.");
        addIngredient(ingredientsRef, "preservatives", "Medium", "Some preservatives like sulfites and nitrates may cause allergic reactions and other health issues in sensitive individuals.");
        addIngredient(ingredientsRef, "color additives", "Medium", "Certain color additives like Red 40 and Yellow 5 may cause allergic reactions and behavioral problems in some individuals.");
        addIngredient(ingredientsRef, "hydrogenated oils", "High", "Hydrogenated oils contain trans fats which increase the risk of heart disease and other health issues.");
        addIngredient(ingredientsRef, "sulfites", "Medium", "Sulfites are commonly used as preservatives in various foods and may cause allergic reactions in some individuals.");
        addIngredient(ingredientsRef, "nitrites", "Medium", "Nitrites and nitrates are often used as preservatives in processed meats and may have harmful effects on health when consumed in excess.");
        addIngredient(ingredientsRef, "nitrates", "Medium", "Nitrites and nitrates are often used as preservatives in processed meats and may have harmful effects on health when consumed in excess.");
        addIngredient(ingredientsRef, "artificial preservatives", "Medium", "Artificial preservatives like BHA and BHT have been associated with health concerns including cancer.");
        addIngredient(ingredientsRef, "caffeine", "Medium", "Caffeine in moderate amounts is generally safe for most people but excessive intake can lead to adverse effects like insomnia and increased heart rate.");
        addIngredient(ingredientsRef, "alcohol", "High", "Excessive alcohol consumption can lead to various health problems including liver disease and addiction.");
        addIngredient(ingredientsRef, "hydrolyzed vegetable protein", "High", "Hydrolyzed vegetable protein may contain hidden MSG and can cause similar adverse reactions in sensitive individuals.");
        addIngredient(ingredientsRef, "carrageenan", "Medium", "Carrageenan, a thickening agent, may cause digestive issues in some individuals.");
        addIngredient(ingredientsRef, "aspartame", "Medium", "Aspartame, an artificial sweetener, has been linked to various health concerns but is considered safe for most people in moderate amounts.");
        addIngredient(ingredientsRef, "sodium benzoate", "Medium", "Sodium benzoate, a common preservative, may cause allergic reactions in some individuals.");
        addIngredient(ingredientsRef, "propylene glycol", "Medium", "Propylene glycol is used as a food additive but excessive intake may cause digestive issues.");
        addIngredient(ingredientsRef, "titanium dioxide", "Medium", "Titanium dioxide is used as a whitening agent in various foods but may have adverse effects on health when consumed in large quantities.");
        addIngredient(ingredientsRef, "butylated hydroxyanisole ", "Medium", "BHA is a common food preservative but has been associated with health concerns including cancer.");
        addIngredient(ingredientsRef, "bha", "Medium", "BHA is a common food preservative but has been associated with health concerns including cancer.");
        addIngredient(ingredientsRef, "butylated hydroxytoluene (BHT)", "Medium", "BHT is a synthetic antioxidant used as a food preservative but has been linked to health concerns including cancer.");
        addIngredient(ingredientsRef, "potassium bromate", "High", "Potassium bromate, used in bread-making, has been classified as a potential carcinogen and may pose health risks.");
        addIngredient(ingredientsRef, "parabens", "Medium", "Parabens are used as preservatives in various food and cosmetic products but have been associated with health concerns including hormone disruption.");

        Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show();
    }

    private void addIngredient(CollectionReference ingredientsRef, String name, String toxicityLevel, String explanation) {
        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("name", name);
        ingredient.put("toxicityLevel", toxicityLevel);
        ingredient.put("explanation", explanation);

        ingredientsRef.add(ingredient)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Success message or action if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failure message or action if needed
                    }
                });
    }}
