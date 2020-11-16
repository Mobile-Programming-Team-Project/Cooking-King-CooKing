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

// user_Recipe.java : 회원들이 직접 레시피를 게시하면 등록되는 화면을 나타내는 기능

public class user_Recipe extends AppCompatActivity {

    FirebasePost fb; //MainActivity에서 받아오는 FirebasePost를 담을 객체
    ImageView userImage; // 게시한 레시피의 음식 사진
    TextView userTitle; // 게시한 레시피의 요리 제목
    TextView userWriter; // 게시한 레시피의 작성자
    TextView userMaterial; // 게시한 레시피의 요리 재료
    TextView userRecipe; // 게시한 레시피의 요리 과정
    Bitmap bitmap; // 이미지를 String에서 bitmap으로 변환하는 과정에서 담을 bitmap


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recipe);

        userImage = (ImageView) findViewById(R.id.imageView2);
        userTitle = (TextView) findViewById(R.id.textView3);
        userWriter = (TextView) findViewById(R.id.textView4);
        userMaterial = (TextView) findViewById(R.id.textView5);
        userRecipe = (TextView) findViewById(R.id.textView6);

        Intent intent = getIntent(); // MainActivity에서의 전달을 받는 intent
        show(intent);

        userImage.setImageBitmap(bitmap);
        userImage.getLayoutParams().width=750;// 이미지 크기 조정
        userImage.getLayoutParams().height=750;// 이미지 크기 조정
        userTitle.setText(fb.title); // 요리 제목 설정해줌
        userWriter.setText(fb.writer); // 작성자 값 설정
        userMaterial.setText(fb.material); // 요리재료 설정
        userRecipe.setText(fb.recipe); // 요리과정 설정
    }

    //Method:StringToBitmap()
    //Function:String형태의 이미지값을 Bitmap 값으로 전환해주는 함수
   public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT); //Base64를 통해서 String을 decoding하여 배열에 저장
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length); //byte형태로 저장된 이미지를 Bitmap으로 변환
            return bitmap;
        } catch (Exception e) { // 예외처리
            e.getMessage();
            return null;
        }
    }

    //Method:show()
    //Function:fb객체를 MainActivity에서 받은 FirebasePose객체로 초기화하고 String형태의 이미지를 bitmap형으로 변환시켜줌
    private void show(Intent intent)
    {
        fb = (FirebasePost) intent.getParcelableExtra("firebasePost");
        bitmap = StringToBitmap(fb.image_Bitmap);
    }
}
