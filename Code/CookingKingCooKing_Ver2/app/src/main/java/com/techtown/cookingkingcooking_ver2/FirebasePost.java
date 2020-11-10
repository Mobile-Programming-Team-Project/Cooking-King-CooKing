package com.techtown.cookingkingcooking_ver2;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
@IgnoreExtraProperties
public class FirebasePost {
    public String title;
    public String writer;
    public String material;
    public String recipe;

    public FirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public FirebasePost(String title, String writer, String material, String recipe) {
        this.title = title;
        this.writer = writer;
        this.material = material;
        this.recipe = recipe;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("writer", writer);
        result.put("material", material);
        result.put("recipe", recipe);
        return result;
    }

}

