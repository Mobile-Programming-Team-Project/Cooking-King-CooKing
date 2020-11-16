/*Writer : 최근표
 File_Name : FirebasePose.java
 Function : : 파이어베이스 데이터베이스에 저장할 값들을 객체에 저장하여 올리기위한 객체생성 class
 class의 값을 담기위해 Parcel을 사용했고, HashMap의 형태로 객체를 저장함. */

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
    public String title; // 제목
    public String writer; // 작성자
    public String material; // 재료
    public String recipe; // 요리과정
    public String image_Bitmap; // 사진

    // 값 지정
    public FirebasePost(String title, String writer, String material, String recipe,String image_Bitmap) {
        this.title = title;
        this.writer = writer;
        this.material = material;
        this.recipe = recipe;
        this.image_Bitmap = image_Bitmap;
    }

    //Method : writeToParcel()
    //Function : 객체에 담긴 데이터를 직렬화하여 dest에 순차적으로 class 내부에 있는 데이터를 저장해주는 함수
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(writer);
        dest.writeString(material);
        dest.writeString(recipe);
        dest.writeString(image_Bitmap);
    }

    // 객체를 받았을 때 직렬화를 풀어주는 기능
    public FirebasePost(Parcel src)
    {
        this.title = src.readString();
        this.writer = src.readString();
        this.material = src.readString();
        this.recipe = src.readString();
        this.image_Bitmap = src.readString();
    }
    @Override
    public int describeContents() {return 0;}

    //parcel된 객체데이터를 저장하는 Creator
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

    // result HashMap에 제목, 작성자 등의 값을 key,value 값으로 넣어줌
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
