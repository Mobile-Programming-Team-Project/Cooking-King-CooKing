/*
Writer: 조상연, 최근표
File Name: MainActivity.java
Function: 해당 파일은 메인 화면을 구성할때 구동되는 파일이다.
        void onCreate(): 해당 class에서 사용되는 멤버 변수들이 초기화되고 초기화면에 보여지는 10개의 레시피 사진들이 표시되는 AsyncTask를 호출함
                        10개의 레시피 중 5개는 api를 통해, 5개는 firebase를 통해 표시된다.(-최근표-)
        void makeImageView(): 초기화면에서 10개의 레시피가 표시되는데, 각각의 레피시의 Image를 보여기위해 ImageView객체를 동적 생성하는 메소드
        void onClickSearchBtn(): 사용자가 검색버튼을 눌렀을때의 이벤트 처리를 위한 메소드. 검색 키워드를 가지고 searchAcitivity.class를 호출함(-조상연-)
        class GetXMLTask: onCreate()에서 URL(= address)의 데이터를 XML의 형태로 받아와 필요한 자료구조(Recipe.java) 형태로 가공하는 AsyncTask(-조상연-)
        class GetImageTask: GetXMLTask에서 받아온 각각의 레시피의 이미지 URL을 받아 해당 데이터를 bitmap으로 바꿔주고 그 bitmap을 makeImageView()메소드에서
                            생성한 ImageView 객체에 알맞게 매칭하는 AsyncTask(-조상연-)
        class FBThread: Firebase의 RealTime DataBase에서 사용자들이 입력한 데이터를 받아와 출력하는 Thread 클래스이다. 각각의 레시피에 대한 내용은
                        FirebasePost 클래스의 형태로 저장되고 해당 객체는 user_Recipe.java로 Activity를 넘어가는 객체로 사용된다.(자세한 내용은   
                        FireBasePost.java 파일 참고(최근표))(-최근표-)
 */

package com.techtown.cookingkingcooking_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    LinearLayout recipesCategoryAPI;        // API 데이터에서 받아오는 레시피를 각각 5개의 imageview 객체를 담는다.
    LinearLayout recipesCategoryFIREBASE;   // FireBase각각 5개의 imageview 객체를 담는다.

    ImageView[] apiImages; // 동적으로 생성되는 ImageView 객체를 참조하기 위한 변수(api에서 받아오는 변수를 참조), 조상연
    ImageView[] FBImages; // 동적으로 생성되는 ImageView 객체를 참조하기 위한 변수(FireBase에서 받아오는 변수를 참조), 최근표

    Recipe[] apiRecipe; // api에서 받아오는 레시피를 필요한 형태로 가공하고 각각의 레시피에 참조하기 위한 변수, 조상연
    FirebasePost[] FBRecipe; // FireBase에서 받아오는 레시피의 정보를 가공하여 해당 자료형(객체)에 저장하고 참조하기 위한 변수, 최근표

    Document doc = null; // XML파일을 파싱하기 위한 변수(-조상연-)

    EditText searchEditText; // 검색 키워드를 getString()하기 위한 editText객체 선언
    ImageButton searchBtn; // 검색 버튼을 눌렀을때의 이벤트 처리를 위한 Button 객체 선언
    ImageButton shareBtn; // 레시피 게시글 작성하는 버튼 객체 선언

    // 식품식약청의 무료 레시피 DB api 주소
    String address = "http://openapi.foodsafetykorea.go.kr/api/b205d0f499cf47098c8e/COOKRCP01/xml/";
    int imagesIdx = 0; // 변수들을 참조하기 위한 공통된 index객체 선언
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("요리왕 쿠킹");

        // 파이어베이스에서 값을 받아오는 객체 초기화 추후에 FBThread로 객체 생성\\
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 해당 Activity에서 다루는 위젯을 참조하는 부분\\
        recipesCategoryAPI = findViewById(R.id.recipe1); // api에서 받아오는 데이터를 표시하는 layout
        recipesCategoryFIREBASE = findViewById(R.id.recipe2); // firebase에서 받아오는 데이터를 표시하는 layout

        apiImages = new ImageView[5]; // 해당 변수에 api에서 받아오는 레시피의 이미지가 저장되는 ImageView객체가 저장됨
        apiRecipe = new Recipe[5];    // 해당 변수에 api에서 받아온 레시피의 정보를 Recipe 객체의 형태로 가공하여 저장함

        FBImages = new ImageView[5]; // 해당 변수에 Firebase에서 받아오는 레시피의 이미지가 저장되는 ImageView객체가 저장됨
        FBRecipe = new FirebasePost[5]; // 해당 변수에 Firebase에서 받아온 레시피의 정보를 FirebasePost 객체의 형태로 가공하여 저장함

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchBtn = (ImageButton) findViewById(R.id.searchBtn);

        // 공유하기 버튼을 클릭했을 때의 이벤트를 처리하기 위한 onClickListener, 최근표\\
        shareBtn = (ImageButton) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Share_recipe.class);
                startActivityForResult(intent,101);
            }
        });

        /* makeImageView() 메소드를 통해 레시피의 사진을 담은 ImageView를 동적으로 생성함
        자세한 내용은 makeImageView() 메소드 주석 참고(조상연) */
        for (int k = 0; k < 10; k++) {
            if(k < 5) makeImageView(recipesCategoryAPI, k);
            else makeImageView(recipesCategoryFIREBASE, k);
        }

        /* 1000가지의 음식을 램덤으로 5개를 뽑음(한 숫자를 뽑고 거기서 부터 5개까지)
        해당 api의 주소를 담고 AsyncTask를 상속하는 GetXMLTast 클래스를 실행
        자세한 내용은 GetXMLTask 클래스 주석 참고(조상연)*/
        Random random = new Random();
        int startRow = random.nextInt(995);
        address += startRow + "/" + (startRow+4) + "/"; //api URL에 추가
        GetXMLTask task = new GetXMLTask();
        task.execute(address);

        // Firebase에서 값을 가져오기 위한 Thread class 생성 후 실행, 최근표\\
        FBThread fbt = new FBThread(mDatabase);
        fbt.start();
    }

    /*
    Writer: 조상연
    Method: makeImageView()
    Function: ImageView를 동적으로 생성하여 표시하는 부분
    api로 얻은 레시피의 사진을 보여주기 위해 생성한다. */
    private void makeImageView(LinearLayout root, int id)
    {
        ImageView iv = new ImageView(this);
        iv.setId(id);

        // 레시피를 클릭했을때 이벤트 처리, 자세한 내용은 해당 메소드 참고(조상연)
        iv.setOnClickListener(onClickRecipeImage);

        // ImageView 객체의 Layout 속성을 설정함\\
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(lp);

        iv.getLayoutParams().width = 325;
        iv.getLayoutParams().height = 325;

        root.addView(iv);

        // 동적으로 생성된 ImageView 객체를 참조하기위한 array
        if(id < 5)
            apiImages[id] = iv;
        else
            FBImages[id-5] = iv;
    }

    /*
    Writer: 조상연
    Method: onClickSearchBtn()
    Function: 초기화면에서 돋보기 모양의 ImageButton의 onClick 이벤트 처리 메소드
            검색기능은 Naver 검색 api를 이용하였음
            자세한 Naver search Api는 ApiSearch.java에서 참고(조상연)*/
    public void onClickSearchBtn(View v)
    {
        String searchKeyword = searchEditText.getText().toString(); //사용자가 입력한 검색 키워드

        if(searchKeyword.getBytes().length > 0)
        {
            Thread thread = new ApiSearch(searchKeyword);
            thread.start();
            try {
                Thread.sleep(1000); // 검색 결과를 처리하기 위한 Thread 대기 처리
            } catch (Exception e) {}

            // ApiSearch의 멤버 변수 searchResults와 SearchResult.java 참조
            ArrayList<SearchResult> searchResults = ApiSearch.searchResults;

            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            intent.putExtra("KeyWord", searchKeyword);
            intent.putParcelableArrayListExtra("SearchResults", searchResults);

            startActivity(intent);
        }
        else {return;}
    }


    /*
    Writer: 조상연
    Variable: onClickRecipeImage
    Function: makeImageView를 통해 생성된 imageView 객체의 OnClick 이벤트 처리를 위한 OnClickListener 변수 생성 */
    View.OnClickListener onClickRecipeImage =  new View.OnClickListener()
    {
        @SuppressLint("ResourceType")
        @Override
        public void onClick(View v)
        {
            if(v.getId() < 5)
            {
                Intent intent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
                // Recipe 객체는 api를 파싱할때 생성되는 객체이다. 자세한 내용은 Recipe.java 참고(조상연)
                Recipe sendRecipe = apiRecipe[v.getId()];

                intent.putExtra("recipeInfo", sendRecipe); // Recipe class 자체를 다음 Activity로 넘겨줌
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), user_Recipe.class);
                // FirebasePost 클래스는 api를 파싱할때 생성되는 객체이다. 자세한 내용은 FirebasePost.java 참고(최근표)
                FirebasePost sendRecipe = FBRecipe[v.getId()-5];

                intent.putExtra("firebasePost", sendRecipe);
                intent.putExtra("bitmap", sendRecipe.image_Bitmap);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };


    /*
    Writer: 조상연
    sub Class: GetXMLTask
    Function: 식품식약처의 레시피 DB를 담고 있는 무료 api를 비동기로 파싱함*/
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        ProgressDialog progressDlg;

        @Override
        //XML 데이터를 파싱하는 동안 프로그래스바를 돌려서 다운받는 동안 행동 제한하기
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected Document doInBackground(String... urls) {
            /*
            해당 class를 실행할때 받은 api Address(= urls[0])를 xml형식으로 받아와서 Document 자료형인
            doc 변수에 저장하고 해당 doc변수를 리턴 리턴된 doc변수는 아래 onPostExrcute() 메소드에서 활용됨 */
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            /*
            doInBackground() 메소드에서 전달받은 Document 변수를 파싱하는 메소드
            각각의 데이터(레시피)가 <row>태그로 구분되어 지기 대문에 row단위로 끊고
            <row> 태그에서 필요한 데이터만 뽑아내는 과정이다.*/

            progressDlg.dismiss();

            //row태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("row");
            //row 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

            for(int i = 0; i< nodeList.getLength(); i++)
            {
                // Recipe 객체를 참조하기 위해 매번 loop마다 객체 생성
                // 해당 Recipe 객체의 멤버 변수는 setㅁㅁㅁ() 메소드를 통해 갱신
                apiRecipe[i] = new Recipe();

                //row(레시피)데이터에서 원하는 데이터를 추출하는 과정
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node; //row(레시피)엘리먼트 노드

                //음식이름\\
                NodeList nameList  = fstElmnt.getElementsByTagName("RCP_NM");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                apiRecipe[i].setName(((Node) nameList.item(0)).getNodeValue());

                NodeList temp; //레시피에 대한 각종 데이터를 참조할 NodeList 선언

                //조리방법\\
                temp = fstElmnt.getElementsByTagName("RCP_WAY2");
                apiRecipe[i].setWay(temp.item(0).getChildNodes().item(0).getNodeValue());

                //종류\\
                temp = fstElmnt.getElementsByTagName("RCP_PAT2");
                apiRecipe[i].setCategory(temp.item(0).getChildNodes().item(0).getNodeValue());

                //재료\\
                temp = fstElmnt.getElementsByTagName("RCP_PARTS_DTLS");
                //<RCP_PARTS_DTLS>식재료 나열</RCP_PARTS_DTLS> => <RCP_PARTS_DTLS> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 나열된 식재료 data의 string 값
                apiRecipe[i].setFoodIngredients(temp.item(0).getChildNodes().item(0).getNodeValue());

                //조리순서&조리순서 이미지 URL\\
                String manual = "";
                String imgUrls = "";
                for(int k=1; k<=20; k++)
                {
                    String ManualTag = "MANUAL"; //레시피의 조리순서를 담고있는 테그이름(순서는 1~20까지 존재)
                    String ManualImage = "MANUAL_IMG"; //레시피의 조리순서에 대한 이미지 URL(1~20까지 존재)
                    if(k < 10) {ManualTag += "0"+k; ManualImage += "0"+k;} //순서에 따라 순서값을 추가
                    else {ManualTag += k; ManualImage += k;}

                    temp = fstElmnt.getElementsByTagName(ManualImage);
                    if(temp.item(0).getChildNodes().item(0) != null)
                    {imgUrls += temp.item(0).getChildNodes().item(0).getNodeValue() +"\n";}

                    temp = fstElmnt.getElementsByTagName(ManualTag);
                    if(temp.item(0).getChildNodes().item(0) != null)
                    {manual += temp.item(0).getChildNodes().item(0).getNodeValue() +"\n";}
                    else {break;}
                }
                apiRecipe[i].setManual(manual);
                apiRecipe[i].setManualImages(imgUrls.split("\n"));

                //열량\\
                temp = fstElmnt.getElementsByTagName("INFO_ENG");
                double calorie = Double.parseDouble(temp.item(0).getChildNodes().item(0).getNodeValue());
                apiRecipe[i].setCalorie(calorie);

                //이미지 파일 셋팅\\
                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MAIN"); // 이미지경로(소)
                if(temp.item(0).getChildNodes().item(0) != null)
                {
                    String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                    apiRecipe[i].setImageMain(imageAddress);

                    //이미지를 화면에 출력하기 위한 AsyncTask호출
                    // 자세한 내용은 GetImageTask class 참고(조상연)
                    new GetImageTask().execute(imageAddress);
                }

                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MK"); // 이미지경로(대)
                if(temp.item(0).getChildNodes().item(0) != null)
                {
                    String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                    apiRecipe[i].setImageSub(imageAddress);
                }
            }
            super.onPostExecute(doc);
        }
    }

    /*
    Writer: 조상연
    sub Class: GetImageTask
    Function: GetXMLTask에서 받아오는 각각의 레시피 URL에서 사진을 bitmap의 형식으로 변경하는 class*/
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
            if(result == null) {apiImages[imagesIdx].setImageResource(R.drawable.no_img);} //이미지가 없을 경우를 처리
            else {apiImages[imagesIdx].setImageBitmap(result);}
            apiRecipe[imagesIdx++].setBitmapMain(result);
        }
    }

    /*
    Writer: 최근표
    sub Class: FBThread
    Function: Firebase에서 레피시 정보를 가져오도록 하는 Thread class*/
    private class FBThread extends Thread
    {
        DatabaseReference df; // 각각의 레시피 정보를 참조하기 위한 변수

        //생성자\\
        public FBThread(DatabaseReference mdbrf) {df = mdbrf.child("title_list");}

        @Override
        public void run() {
            super.run();

            // Firebase 내부의 값을 참조하기 위해서 addValueEvenListener 생성\\
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Method : onDataChange()
                    // Function : 객체의 값이 update 되어 data의 값이 변화하면
                    /* 가져온 데이터를 OnDataChange메소드 안에서 불러온 DataSnapshot을 통해
                     데이터에 접근하여 FBRecipe배열에 할당해줌.*/

                    int i = 0; // FBRecipe와 FBImages의 요소들에 접근하기 위한 index변수
                    
                    // Firebase에 작성된 각각의 node를 참조하여 값을 받음\\
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String bm = ds.child("image_Uri").getValue(String.class);
                        String material = ds.child("material").getValue(String.class);
                        String recipe = ds.child("recipe").getValue(String.class);
                        String title = ds.child("title").getValue(String.class);
                        String writer = ds.child("writer").getValue(String.class);

                        // 값을 모두 받아 FirebasePost 객체 생성\\
                        FBRecipe[i] = new FirebasePost(title, writer, material, recipe, bm);
                        
                        // String 형태의 bitmap을 bitmap으로 변경하는 코드\\
                        try {
                            byte[] encodeByte = Base64.decode(bm, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            FBImages[i].setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        i++; // 인덱스 값 갱신
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}