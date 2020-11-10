package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    LinearLayout[] recipesCategory;
    ImageView[] images;
    Recipe[] recipes;
    Document doc = null;

    String address = "http://openapi.foodsafetykorea.go.kr/api/b205d0f499cf47098c8e/COOKRCP01/xml/";
    int imagesIdx = 0;
    ImageButton plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("요리왕 쿠킹");

        recipesCategory = new LinearLayout[]{findViewById(R.id.recipe1),
                findViewById(R.id.recipe2),
                findViewById(R.id.recipe3)};
        images = new ImageView[15];
        recipes = new Recipe[15];

        int idIdx = 0;
        for (int i = 0; i < recipesCategory.length; i++) {
            for (int k = 0; k < 5; k++) {
                makeImageView(recipesCategory[i], idIdx++);
            }
        }

        // 1000가지의 음식을 램덤으로 15개를 뽑음(한 숫자를 뽑고 거기서 부터 15개까지)
        Random random = new Random();
        int startRow = random.nextInt(986);

        address += startRow + "/" + (startRow+14) + "/";

        GetXMLTask task = new GetXMLTask();
        task.execute(address);

        plus = (ImageButton) findViewById(R.id.imageButton);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Share_recipe.class);
                startActivityForResult(intent,101);
            }
        });

    }

    private void makeImageView(LinearLayout root, int id) {
        ImageView iv = new ImageView(this);
        iv.setId(id);
        iv.setOnClickListener(onClickRecipeImage);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(lp);

        iv.getLayoutParams().width = 325;
        iv.getLayoutParams().height = 325;

        root.addView(iv);
        images[id] = iv;
    }

    public void onClickSearchBtn(View v)
    {
        Toast.makeText(this, "검색기능 구현 예정", Toast.LENGTH_SHORT).show();
    }

    //초기화면에서 보여지는 15개의 레피시 사진의 클릭 이벤트를 처리하기 위한 OnClickListener
    View.OnClickListener onClickRecipeImage =  new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //Toast.makeText(getApplicationContext(), recipes[v.getId()].toString(), Toast.LENGTH_LONG).show();


            Intent intent = new Intent(getApplicationContext(), RecipeInfoActivity.class);

            Recipe sendRecipe = recipes[v.getId()];

            intent.putExtra("recipeInfo", sendRecipe);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        ProgressDialog progressDlg;

        @Override
        //누나가 알려준 있어보이는 방법(XMl 파싱하는 동안 프로그래스바를 돌려서 다운받는 동안 행동 제한하기)
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected Document doInBackground(String... urls) {
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
            progressDlg.dismiss();

            //row태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("row");
            //row 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

            for(int i = 0; i< nodeList.getLength(); i++)
            {
                recipes[i] = new Recipe();

                //row(레시피)데이터에서 원하는 데이터를 추출하는 과정
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node; //row(레시피)엘리먼트 노드

                //음식이름\\
                NodeList nameList  = fstElmnt.getElementsByTagName("RCP_NM");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                recipes[i].setName(((Node) nameList.item(0)).getNodeValue());

                NodeList temp; //레시피에 대한 각종 데이터를 참조할 NodeList 선언

                //조리방법\\
                temp = fstElmnt.getElementsByTagName("RCP_WAY2");
                recipes[i].setWay(temp.item(0).getChildNodes().item(0).getNodeValue());

                //종류\\
                temp = fstElmnt.getElementsByTagName("RCP_PAT2");
                recipes[i].setCategory(temp.item(0).getChildNodes().item(0).getNodeValue());

                //재료\\
                temp = fstElmnt.getElementsByTagName("RCP_PARTS_DTLS");
                //<RCP_PARTS_DTLS>식재료 나열</RCP_PARTS_DTLS> => <RCP_PARTS_DTLS> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 나열된 식재료 data의 string 값
                recipes[i].setFoodIngredients(temp.item(0).getChildNodes().item(0).getNodeValue());

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
                recipes[i].setManual(manual);
                recipes[i].setManualImages(imgUrls.split("\n"));

                //열량\\
                temp = fstElmnt.getElementsByTagName("INFO_ENG");
                double calorie = Double.parseDouble(temp.item(0).getChildNodes().item(0).getNodeValue());
                recipes[i].setCalorie(calorie);

                //이미지 파일 셋팅\\
                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MAIN"); // 이미지경로(소)
                if(temp.item(0).getChildNodes().item(0) != null)
                {
                    String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                    recipes[i].setImageMain(imageAddress);
                    new GetImageTask().execute(imageAddress); //이미지를 화면에 출력하기 위한 AsyncTask호출
                }

                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MK"); // 이미지경로(대)
                if(temp.item(0).getChildNodes().item(0) != null)
                {
                    String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                    recipes[i].setImageSub(imageAddress);
                }
            }

           // System.out.println(s); // 값 출력(추후에 다음 Activity로 넘어 가게끔 수정)

            super.onPostExecute(doc);
        }
    }

    private class GetImageTask extends AsyncTask<String,Void, Bitmap> {
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
            if(result == null) {images[imagesIdx].setImageResource(R.drawable.no_img);} //이미지가 없을 경우를 처리
            else {images[imagesIdx].setImageBitmap(result);}
            recipes[imagesIdx++].setBitmapMain(result);
        }
    }
}