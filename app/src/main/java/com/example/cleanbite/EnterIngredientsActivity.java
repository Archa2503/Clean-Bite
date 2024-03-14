package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EnterIngredientsActivity extends AppCompatActivity {

    private EditText ingredientEditText;
    private Button addIngredientButton;
    private Button doneButton;
    private List<String> ingredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ingredients);

        ingredientEditText = findViewById(R.id.ingredientEditText);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        doneButton = findViewById(R.id.doneButton);


        ingredientsList = new ArrayList<>();

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ingredientEditText.getText().toString().trim();
                if (!ingredient.isEmpty()) {
                    ingredientsList.add(ingredient);
                    ingredientEditText.setText("");


                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterIngredientsActivity.this, DisplayIngredientsActivity.class);
                intent.putExtra("enteredIngredients", ingredientsList.toArray(new String[0]));
                startActivity(intent);
            }
        });

    }
}
