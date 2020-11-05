package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<SearchResult> searchResults;

    TextView title;
    LinearLayout[] recipeInfoLayouts;
    TextView[] textViews; // 하이퍼링크 처리 효과를 위해 객체 참조를 위한 array선언
    LinearLayout resultBox;

    int dummyId = 20; // id값이 겹치는 것을 막는 더미값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("요리왕 쿠킹");

        title = (TextView) findViewById(R.id.titleText);
        resultBox = (LinearLayout) findViewById(R.id.resultBox);

        Intent intent = getIntent();
        showSearchData(intent); // 전달받은 intent를 해석하는 메소드

        recipeInfoLayouts = new LinearLayout[searchResults.size()];
        textViews = new TextView[searchResults.size()];

        showSearchResult(); // 해석한 데이터들을 가지고 검색 결과를 출력하는 메소드
    }

    private void showSearchData(Intent intent)
    {
        // 검색 키워드를 받는다.
        String searchKeyword = intent.getStringExtra("KeyWord");
        title.setText(searchKeyword + " 검색 결과");

        // api에서 파싱한 데이터 ArrayList를 받는다.
        searchResults = intent.getParcelableArrayListExtra("SearchResults");
    }

    private void showSearchResult()
    {
        // 검색 결과를 하나의 LinearLayout에 title, description 두개를 담아 scrollView에 담는다.

        // title과 description을 담은 Layout 설정
        LinearLayout.LayoutParams layoutSetting = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSetting.setMargins(10, 15, 10, 25);

        // title과 description의 layout_*** 설정
        LinearLayout.LayoutParams widgetSetting = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        widgetSetting.setMargins(5, 5, 5, 5);

        for(int i=0; i<recipeInfoLayouts.length; i++)
        {
            // api에서 파싱한 데이터를 순차적으로 참조함
            SearchResult sr = searchResults.get(i);

            //title과 description을 담을 LinearLayout을 생성하고 속성을 설정함
            recipeInfoLayouts[i] = new LinearLayout(this);
            recipeInfoLayouts[i].setId(i+dummyId);
            recipeInfoLayouts[i].setOrientation(LinearLayout.VERTICAL);
            recipeInfoLayouts[i].setOnClickListener(LinkToInternet);
            recipeInfoLayouts[i].setLayoutParams(layoutSetting);

            //title TextView을 생성하고 속성을 설정함
            TextView title = new TextView(this);
            title.setText(sr.getTitle());
            title.setTextSize(16);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(0xFF0000FF);
            title.setLayoutParams(widgetSetting);
            textViews[i] = title;

            //description TextView을 생성하고 속성을 설정함
            TextView description = new TextView(this);
            description.setText(sr.getDescription());
            description.setTextSize(12);
            description.setTypeface(Typeface.DEFAULT);
            description.setLayoutParams(widgetSetting);

            // LinearLayout에 title과 description을 담고 그것을(LinearLayout)을 다시 ScrollView에 담음
            recipeInfoLayouts[i].addView(title);
            recipeInfoLayouts[i].addView(description);
            resultBox.addView(recipeInfoLayouts[i]);
        }
    }

    // 해당 검색 결과를(LinearLayout별로 구분되어있음) 클릭했을 경우의 이벤트를 처리
    // MainActivity에서 넘어온 검색 결과를 index별로 구분해서 해당 데이터의 blogLink를 참조하고
    // 인터넷 창을 띄우도록 intent를 설정해 놓음(현재로선 최선...)
    View.OnClickListener LinkToInternet = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            int idx = v.getId();
            idx -= dummyId;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResults.get(idx).getBlogLink()));
            startActivity(intent);

            // 클릭됬던 것은 보라색으로 처리해서 하이퍼링크 효과
            textViews[idx].setTextColor(0xFF705DA8);
        }
    };
}