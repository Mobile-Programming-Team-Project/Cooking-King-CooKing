package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LinearLayout[] recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("요리왕 쿠킹");

        recipes = new LinearLayout[]{findViewById(R.id.recipe1),
                findViewById(R.id.recipe2),
                findViewById(R.id.recipe3)};

        for(int i=0; i<recipes.length; i++)
        {
            for(int k=0; k<5; k++)
            {
                makeImageView(recipes[i], i+k);
            }
        }
    }

    private void makeImageView(LinearLayout root, int id)
    {
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.ic_launcher_foreground); //사진을 추후에 추가(지금은 기본 내장된 사진으로 대체)
        iv.setId(id);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);
        iv.setLayoutParams(lp);

        root.addView(iv);
    }

    public void onClickSearchBtn(View v)
    {
        Toast.makeText(this, "검색기능 구현 예정", Toast.LENGTH_SHORT).show();
    }
}