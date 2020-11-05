package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class RecipeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
        setTitle("요리왕 쿠킹");

        Intent recipeInfo = getIntent();
        printRecipeInfo(recipeInfo);
    }

    private void printRecipeInfo(Intent intent)
    {
        Recipe info = intent.getParcelableExtra("recipeInfo");
        Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
    }
}