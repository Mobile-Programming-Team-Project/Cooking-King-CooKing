package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Share_recipe extends AppCompatActivity{
    private DatabaseReference mPostReference;
    String TITLE;
    String writer;
    String material;
    String recipe;
    String food_image;
    String sort = "title";

    ImageView iv; // 요리 이미지 선택
    Button update; // 게시하기 버튼
    Button home; // 홈으로 가는 버튼
    int gallery_image = 200; // 이미지 크기
    EditText editTitle;
    EditText editWriter;
    EditText editMaterial;
    EditText editRecipe;


    ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> arrayData = new ArrayList<String>();
    static ArrayList<String> arrayIndex =  new ArrayList<String>();

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
                startActivityForResult(intent,gallery_image); // 앨범 접근
            }
        });
        editTitle = (EditText) findViewById(R.id.title);
        editWriter = (EditText) findViewById(R.id.writer);
        editMaterial = (EditText) findViewById(R.id.material);
        editRecipe = (EditText) findViewById(R.id.recipe);

        update = (Button) findViewById(R.id.button2);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 게시하기 버튼 눌렀을 때의 이벤트
                TITLE = editTitle.getText().toString();
                writer = editWriter.getText().toString();
                material = editMaterial.getText().toString();
                recipe = editRecipe.getText().toString();
                postFirebaseDatabase(true); //파이어베이스 데이터베이스에 게시하는 함수
                Toast.makeText(getApplicationContext(),"게시글이 등록되었습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        home = (Button) findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 앨범접근 후 이미지 변경 설정을 가능하게 해주는 기능

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_image && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            iv.setImageURI(selectedImageUri); // 선택된 이미지를 기본이미지로 변경

        }

    }
    public void postFirebaseDatabase(boolean add){ // 데이터베이스에 저장해주는 함수
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(TITLE, writer, material,recipe);
            postValues = post.toMap();
        }
        childUpdates.put("/title_list/" + TITLE, postValues);
        mPostReference.updateChildren(childUpdates);
    }/*
    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayData.clear();
                arrayIndex.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    String[] info = {get.title, get.writer, get.material, get.recipe};
                    String Result = setTextLength(info[0],10) + setTextLength(info[1],10) + setTextLength(info[2],10) + setTextLength(info[3],10);
                    arrayData.add(Result);
                    arrayIndex.add(key);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] + info[3]);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayData);
                arrayAdapter.notifyDataSetChanged();
            }

            private String setTextLength(String text, int length) {
                if (text.length() < length) {
                    int gap = length - text.length();
                    for (int i = 0; i < gap; i++) {
                        text = text + " ";
                    }
                }
                return text;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild(sort);
        sortbyAge.addListenerForSingleValueEvent(postListener);*/
    }



