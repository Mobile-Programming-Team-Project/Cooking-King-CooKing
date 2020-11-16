/*
Writer: 조상연
File Name: RecipeInfoActivity.java
Function: MainActivity api를 통해 생성된 ImageView 객체를 클릭했을때 넘어가는 Activity이다.
        단순히 데이터를 열거하여 보여주는 형식이고 xml에서 데이터를 보여주기 위해 각각의 TextView객체가 선언되어있다.
        MainActivity에서 받은 Intent에는 Recipe형의 객체가 담아져 있다. 해당 객체에서 특정 레시피의 데이터를 받아 TextView의 setText()로 정보를 표시한다.
        그리고 해당 레시피의 image는 MainActivity에서와 같이 AsyncTask를 이용하여 URL형태의 이미지파일을 bitmap형태로 받아 ImageView에 보여준다.
 */

package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecipeInfoActivity extends AppCompatActivity {

    //레시피의 정보를 보여주기 위해 선언된 위젯 객체들\\
    TextView name;
    TextView category;
    TextView way;
    TextView foodIngredients;
    TextView calorie;
    TextView manual;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        //TextView, ImageView 객체 참조\\
        name = (TextView) findViewById(R.id.name);
        category = (TextView) findViewById(R.id.category);
        way = (TextView) findViewById(R.id.way);
        foodIngredients = (TextView) findViewById(R.id.foodIngredients);
        calorie = (TextView) findViewById(R.id.calorie);
        manual = (TextView) findViewById(R.id.manual);
        image = (ImageView) findViewById(R.id.foodImage);

        // MainActivity에서 받은 intent를 통해 레시피 정보 표시\\
        Intent recipeInfo = getIntent();
        setRecipeInfo(recipeInfo);
    }

    private void setRecipeInfo(Intent intent)
    {
        Recipe info = intent.getParcelableExtra("recipeInfo");
        //Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();

        name.setText(info.getName() +"\n");
        name.setGravity(View.TEXT_ALIGNMENT_CENTER);
        category.setText("요리종류: " + info.getCategory());
        way.setText("조리방법: " + info.getWay());
        calorie.setText("칼로리(1인 기준): "+ info.getCalorie());
        foodIngredients.setText("재료: \n" + info.getFoodIngredients());
        manual.setText("만드는 방법: \n" + info.getManual());

        // 마지막으로 사진을 표시할때에는 AsyncTask를 사용한다.
        GetImageTask task = new GetImageTask();
        task.execute(info.getImageSub());

    }

    /*
    Writer: 조상연
    sub Class: GetImageTask
    Function: GetXMLTask에서 받아오는 각각의 레시피 URL에서 사진을 bitmap의 형식으로 변경하는 class
    해당 클래스는 MainActivity에서 따옴*/
    private class GetImageTask extends AsyncTask<String,Void, Bitmap> {
        /*
        URL을 받아서 해당 URL의 사진을 bitmap으로 변환하는 메소드
        변경된 bitmap은 리턴되어서 onPostExecute()메소드에서 사용됨*/
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0]; //image의 URL 값
                URL url = new URL(img_url); //url값 해석
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream()); //해당 url의 내용을 bitmap으로 변경
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            /*
            이미지를 받아 makeImageView에서 생성한 imageView의 setImageBitmap()을 사용*/
            if(result == null) {image.setImageResource(R.drawable.no_img);} //이미지가 없을 경우를 처리
            else {image.setImageBitmap(result);}
        }
    }
}
