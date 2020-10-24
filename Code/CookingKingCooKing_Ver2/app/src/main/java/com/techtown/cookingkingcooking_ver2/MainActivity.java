package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    LinearLayout[] recipes;
    ImageView[] images;
    Document doc = null;

    String address = "http://openapi.foodsafetykorea.go.kr/api/b205d0f499cf47098c8e/COOKRCP01/xml/1/5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("요리왕 쿠킹");

        recipes = new LinearLayout[]{findViewById(R.id.recipe1),
                findViewById(R.id.recipe2),
                findViewById(R.id.recipe3)};
        images = new ImageView[15];

        for (int i = 0; i < recipes.length; i++) {
            for (int k = 0; k < 5; k++) {
                makeImageView(recipes[i], i + k);
            }
        }
    }

    private void makeImageView(LinearLayout root, int id) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.ic_launcher_foreground); //사진을 추후에 추가(지금은 기본 내장된 사진으로 대체)
        iv.setId(id);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(lp);

        root.addView(iv);
        images[id] = iv;
    }

    public void onClickSearchBtn(View v){
        Toast.makeText(this, "검색기능 구현 예정", Toast.LENGTH_SHORT).show();
        GetXMLTask task = new GetXMLTask();
        task.execute("http://openapi.foodsafetykorea.go.kr/api/b205d0f499cf47098c8e/COOKRCP01/xml/1/3/");
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {

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

            String s = "";
            //row태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("row");
            //row 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

            for(int i = 0; i< nodeList.getLength(); i++)
            {
                //row(레시피)데이터에서 원하는 데이터를 추출하는 과정
                s += "레시피 "+(i+1)+" \n";
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node; //row(레시피)엘리먼트 노드

                NodeList nameList  = fstElmnt.getElementsByTagName("RCP_NM");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                s += "이름: "+ ((Node) nameList.item(0)).getNodeValue() +"\n";

                NodeList temp; //레시피에 대한 각종

                //재료
                temp = fstElmnt.getElementsByTagName("RCP_PARTS_DTLS");
                //<RCP_PARTS_DTLS>식재료 나열</RCP_PARTS_DTLS> => <RCP_PARTS_DTLS> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 나열된 식재료 data의 string 값
                s += "재료 \n"+  temp.item(0).getChildNodes().item(0).getNodeValue() +"\n";

                //종류
                temp = fstElmnt.getElementsByTagName("RCP_PAT2");
                s += "요리종류: "+  temp.item(0).getChildNodes().item(0).getNodeValue() +"\n";

                //조리순서
                s += "조리순서\n";
                for(int k=1; k<=20; k++)
                {
                    String ManualTag = "MANUAL"; //레시피의 조리순서를 담고있는 테그이름(순서는 1~20까지 존재)
                    if(k < 10) {ManualTag += "0"+k;} //순서에 따라 순서값을 추가
                    else {ManualTag += k;}

                    temp = fstElmnt.getElementsByTagName(ManualTag);
                    if(temp.item(0).getChildNodes().item(0) != null)
                    {s += temp.item(0).getChildNodes().item(0).getNodeValue() +"\n";}
                    else {break;}
                }

                s += "\n"; //다음 레시피 줄바꿈
            }

            //textview.setText(s);
            System.out.println(s);

            super.onPostExecute(doc);
        }
    }
}