package com.techtown.cookingkingcooking_ver2;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost implements Parcelable {
    public String title;
    public String writer;
    public String material;
    public String recipe;
    public String image_Bitmap;

    public FirebasePost(String title, String writer, String material, String recipe,String image_Bitmap) {
        this.title = title;
        this.writer = writer;
        this.material = material;
        this.recipe = recipe;
        this.image_Bitmap = image_Bitmap;
    }

    public FirebasePost(Parcel src)
    {
        this.title = src.readString();
        this.writer = src.readString();
        this.material = src.readString();
        this.recipe = src.readString();
        this.image_Bitmap = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(writer);
        dest.writeString(material);
        dest.writeString(recipe);
        dest.writeString(image_Bitmap);
    }

    @Override
    public int describeContents() {return 0;}

    public static final Creator<FirebasePost> CREATOR = new Creator<FirebasePost>() {
        @Override
        public FirebasePost createFromParcel(Parcel source) {
            return new FirebasePost(source);
        }

        @Override
        public FirebasePost[] newArray(int size) {
            return new FirebasePost[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("writer", writer);
        result.put("material", material);
        result.put("recipe", recipe);
        result.put("image_Uri",image_Bitmap);
        return result;
    }
}