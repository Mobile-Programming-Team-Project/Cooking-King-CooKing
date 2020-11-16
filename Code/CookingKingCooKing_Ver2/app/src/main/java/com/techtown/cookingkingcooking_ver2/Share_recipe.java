package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Share_recipe.java : 회원들이 자신들의 레시피를 입력하여 공유 할 수 있는 기능을 구현


public class Share_recipe extends AppCompatActivity {
    private DatabaseReference mPostReference;
    String TITLE; // 제목
    String writer; // 작성자
    String material; // 재료
    String recipe; // 요리과정
    String food_image; // 음식 사진

    Bitmap bitmap;

    ImageView iv; // 요리 이미지 선택
    Button update; // 게시하기 버튼
    Button home; // 홈으로 가는 버튼
    int gallery_image = 200; // 이미지 크기
    EditText editTitle; // 제목 입력칸
    EditText editWriter; // 작성자 입력칸
    EditText editMaterial;// 재료 입력칸
    EditText editRecipe;// 요리과정 입력칸

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recipe);


        iv = (ImageView) findViewById(R.id.imageView); //회원이 직접 게시한 이미지가 담길 ImageView

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK); // 인텐트를 통해 갤러리 접근
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // 내장 갤러리 앱에 접근
                startActivityForResult(intent, gallery_image); // 갤러리앱으로 이동
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
                TITLE = editTitle.getText().toString(); //제목 설정
                writer = editWriter.getText().toString(); // 작성자 설정
                material = editMaterial.getText().toString(); // 재료 설정
                recipe = editRecipe.getText().toString(); // 요리과정 설정
                postFirebaseDatabase(true); //파이어베이스 데이터베이스에 게시하는 함수
                Toast.makeText(getApplicationContext(), "게시글이 등록되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        home = (Button) findViewById(R.id.btnHome); // '홈으로 가기'버튼을 눌렀을때의 이벤트
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }); // 동작을 중지하고 앱의 초기화면으로 돌아간다.
    }


    @Override
    //Method : onActivityResult()
    //Function : 앨범접근 후 이미지 변경 설정을 가능하게 해주는 기능
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_image && resultCode == RESULT_OK && data != null && data.getData() != null) { //정상작동일 때

            Uri selectedImageUri = data.getData();// 선택된 이미지 값으로 selectedImageUri 변수의 값을 초기화
            iv.setImageURI(selectedImageUri); // 선택된 이미지를 기본이미지로 변경

            //Uri값으로 저장되어있는 이미지값을 String형으로 변환하여 저장
            // Uri -> bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // bitmap -> String
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, baos);
            byte[] bytes = baos.toByteArray();
            food_image = Base64.encodeToString(bytes, Base64.DEFAULT); //바이트형태로 저장된 image를 String형으로 Encoding
        }

    }

    //Method : postFirebaseDatabase()
    /*Function : 게시하기버튼을 눌렀을 때, 입력된 내용들을 파이어베이스 데이터베이스에 저장해주는 함수
    add가 false일 때는 해당 child에 null값을 넣어주어 데이터를 삭제함.
     */
    public void postFirebaseDatabase(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference(); //파이어베이스 데이터베이스의 Instance를 가져옴
        Map<String, Object> childUpdates = new HashMap<>(); // child의 값을 업데이트 할 HashMap
        Map<String, Object> postValues = null; // 해당 child값을 입력할 HashMap
        if (add) { // 데이터 저장
            FirebasePost post = new FirebasePost(TITLE, writer, material, recipe, food_image); // post객체에 값을 초기화해줌
            postValues = post.toMap(); // postValues HashMap에 값 할당
        }
        childUpdates.put("/title_list/" + TITLE, postValues); // title_list라는 대분류 아래에 postValues의 값이 저장됨
        mPostReference.updateChildren(childUpdates); // children을 데이터베이스에 업데이트
    }
}
