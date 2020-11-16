package com.techtown.cookingkingcooking_ver2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class user_Recipe extends AppCompatActivity {

    FirebasePost fb;
    ImageView userImage;
    TextView userTitle;
    TextView userWriter;
    TextView userMaterial;
    TextView userRecipe;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recipe);

        userImage = (ImageView) findViewById(R.id.imageView2);
        userTitle = (TextView) findViewById(R.id.textView3);
        userWriter = (TextView) findViewById(R.id.textView4);
        userMaterial = (TextView) findViewById(R.id.textView5);
        userRecipe = (TextView) findViewById(R.id.textView6);

        Intent intent = getIntent();
        showshow(intent);

        userImage.setImageBitmap(bitmap);
        userImage.getLayoutParams().width=750;
        userImage.getLayoutParams().height=750;
        userTitle.setText(fb.title);
        userWriter.setText(fb.writer);
        userMaterial.setText(fb.material);
        userRecipe.setText(fb.recipe);
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void showshow(Intent intent)
    {
        fb = (FirebasePost) intent.getParcelableExtra("firebasePost");
        bitmap = StringToBitmap(fb.image_Bitmap);
    }
}