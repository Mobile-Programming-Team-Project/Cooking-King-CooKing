package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Share_recipe extends AppCompatActivity{
    private DatabaseReference mPostReference;
    String TITLE;
    String writer;
    String material;
    String recipe;

    ImageView iv;
    Button update;
    int gallery_image = 200;
    EditText editTitle;
    EditText editWriter;
    EditText editMaterial;
    EditText editRecipe;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recipe);


        iv = (ImageView) findViewById(R.id.imageView);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,gallery_image);
            }
        });
        editTitle = (EditText) findViewById(R.id.title);
        editWriter = (EditText) findViewById(R.id.writer);
        editMaterial = (EditText) findViewById(R.id.material);
        editRecipe = (EditText) findViewById(R.id.recipe);

        update = (Button) findViewById(R.id.button2);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TITLE = editTitle.getText().toString();
                writer = editWriter.getText().toString();
                material = editMaterial.getText().toString();
                recipe = editRecipe.getText().toString();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_image && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            iv.setImageURI(selectedImageUri);

        }

    }
    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(TITLE, writer, material,recipe);
            postValues = post.toMap();
        }
        childUpdates.put("/title_list/" + TITLE, postValues);
        mPostReference.updateChildren(childUpdates);
    }



}
