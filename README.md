# 모바일프로그래밍 팀 프로젝트

[TOC]

국민대학교 소프트웨어학과 모바일프로그래밍 팀 프로젝트입니다.<br>	팀원<br>		* 20171706 조상연<br>		* 20171710 최근표

---

## 개발환경

Android Studio로 개발하며 29버전의 API를 사용하였습니다. 확인용 AVD는 pixel 2입니다.

<img src="https://user-images.githubusercontent.com/28241676/96371107-1aabc580-119b-11eb-8cf4-3269e11f696e.PNG" alt="roqkfghksrud" style="zoom:80%;" />

---

## 개발일지

### 2020.10.17, JSY

뼈대 작업, 최초화면을 디자인 하였음, 기본적인 뼈대 작업이 마무리되면 기능별로 branch하여 개발할 예정<br>최초화면이 로딩되면 레시피를 알맞게 표시할 예정

<img src="https://user-images.githubusercontent.com/28241676/96371225-9dcd1b80-119b-11eb-8b63-f07b3ebac8d8.png" alt="Screenshot_1602769796" style="zoom: 25%;" />



### 2020.10.18, JSY

추가적인 뼈대 작업, xml에서 생성하였던 레시피 목록을 java 코드에서 동적 생성하게끔 코드를 수정함.<br>지금은 MenuFragment1.java파일에만 "makeImageView()" 메소드가 구현되어있음.(추후에 뼈대 작업 마무리에 추가할 예정)

```java
private void makeImageView(LinearLayout root, int id)
{
    ImageView iv = new ImageView(getContext());
    iv.setImageResource(R.drawable.ic_launcher_foreground); //사진을 추후에 추가(지금은 기본 내장된 사진으로 대체)
    iv.setId(id);

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lp.setMargins(10,10,10,10);
    iv.setLayoutParams(lp);

    root.addView(iv);
}
```

<img src="https://user-images.githubusercontent.com/28241676/96371295-f69cb400-119b-11eb-86e5-66ca390cde0a.png" alt="Screenshot_1603031920" style="zoom:25%;" />



### 2020.10.22, JSY
Open_API_Parsing 브랜치 생성<br>
브랜치 내용은 해당 브랜치에서 상세설명

Main 브랜치 뼈대작업 초기화 후 새로운 main 코드 생성(CookingKingCooKing_Ver2)<br>API 연동 브랜치도 삭제 후 다시 생성 할 예정<br>코드 자체는 변한게 없고 mainActivity에서 화면을 구성하게 끔 함

<img src="https://user-images.githubusercontent.com/28241676/96895242-2d8a0700-14c7-11eb-9816-3b583f7180f1.png" alt="Screenshot_1603380695" style="zoom:25%;" />



### 2020.10.24, JSY

API_Interworking_Function 브랜치 생성 기존의 Open_API_Parsing 브랜치삭제

#### AsyscTask를 통한 API(XML) 파싱기능 완료.

![open_API 파싱](https://user-images.githubusercontent.com/28241676/97084180-17f41900-1650-11eb-92ed-411022a1e0f7.PNG)

MainActivity.java의 MainActivity AsyncTask를 상속하는 GetXMLTask클래스 선언

```java
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
```

해당 class는 MainActivity클래스에 private로 선언되어있다. MainActivity.java의 76번째 줄에서 사용자가 돋보기 imageBtn을 클릭하면 적용된 API 해석을 시작함(아래 참고)

```java
        GetXMLTask task = new GetXMLTask();
        task.execute("http://openapi.foodsafetykorea.go.kr/api/b205d0f499cf47098c8e/COOKRCP01/xml/1/3/");
```

해당 api주소의 끝부분 1/3은 표시하고자 하는 row의 번호들이가 즉 1~3번 row를 해석함(최대 1000개 까지 가능)<br>



#### Open API

* 해당 API는 http://www.foodsafetykorea.go.kr/api/openApiInfo.do?menu_grp=MENU_GRP31&menu_no=661&show_cnt=10&start_idx=1&svc_no=COOKRCP01 에서 받아왔음.
* 추후에는 네이버나 Google의 검색 API를 받아와서 표시하는 방안을 생각중임.



#### 해당 브랜치에서 추가할 기능

* 초기화면에서 그림을 클릭하면 레시피에 대한 자세한 정보를 볼 수 있는 Activity 출력
* 검색 시 관련된 정보를 보이는 Activity 출력



### 2020.10.25, JSY

#### 구현 내용

GetImageTask class를 통한 레시피 image 로딩 기능 구현(최초 화면에서 보여지는 15개의 사진을 음식 사진으로)<br>현재는 돋보기 모양의 searchBtn을 클릭했을 경우에만 출력되도록 함 -> 추후에 onCreate() 메소드 실행시에 출력되게끔 변경 예정

<img src="https://user-images.githubusercontent.com/28241676/97104514-c0a98380-16f7-11eb-9156-bf4e7b850abc.png" alt="Screenshot_1603621109" style="zoom:25%;" />

코드는 MainActivity.java에서 GetImageTask class를 선언해 생성

해당코드는 앞서 생성된 GetXMLTask class의 onPostExecute() 메소드 속 내용<br>Key = "ATT_FILE_NO_MAIN"은  이미지경로(소) 해당 이미지의 주소를(imageAddress) GetImageTask로 넘겨줌

```java
                //이미지 파일 셋팅\\
                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MAIN");
                String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                new GetImageTask().execute(imageAddress);
```



위에서 전달받은 값을 다시 url로 해석해서 그 값을 bitmap으로 변경해 각각의 imageView에 매칭하는 class

```java
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
            if(imagesIdx > 14) {return;} //최초 화면에 표시되는 15개의(0~14) 사진을 모두 채우면 리턴
            else {images[imagesIdx++].setImageBitmap(result);}
        }
    }
```

#### 수정

코드를 전체적으로 최적화함

+ 레시피 이미지를 클릭했을때, 레시피 정보를 보여주는 Activity로 넘어가는 OnClickListener생성 

+ ```java
  iv.setOnClickListener(onClickRecipeImage); //해당 line은 73번 line으로 최초 ImageView 위젯 생성시에 setOnClickListener를 설정해주는 부분     
  
  //초기화면에서 보여지는 15개의 레피시 사진의 클릭 이벤트를 처리하기 위한 OnClickListener
      View.OnClickListener onClickRecipeImage =  new View.OnClickListener()
      {
          @Override
          public void onClick(View v)
          {
              Toast.makeText(getApplicationContext(),"해당 레시피 출력 기능은 구현 예정", Toast.LENGTH_SHORT).show();
          }
      };
  // 추후에 이 코드를 작성할 예정
  ```

+ 이미지 생성을 돋보기 모양 버튼 클릭이 아닌 OnCreate() 메소드 호출시로 변경

+ 이미지 생성중에 사용자의 행동 제한을 위한 프로그래스 바 출력 추가

+ ```java
      private class GetXMLTask extends AsyncTask<String, Void, Document> {
          ProgressDialog progressDlg; // 추가
          @Override
          //누나가 알려준 있어보이는 방법(XMl 파싱하는 동안 프로그래스바를 돌려서 다운받는 동안 행동 제한하기)
          protected void onPreExecute()
          {
              super.onPreExecute();
              progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
          }
  ```

  <img src="https://user-images.githubusercontent.com/28241676/97110644-aa162300-171d-11eb-9645-ac2012c69b6a.png" alt="Screenshot_1603637722" style="zoom:25%;" />

  

#### 추후 개발 내용

+ MainActivity.java의 소스가 너무 더러워서 나중에 머지를 위해 최적화가 필요해 보임
+ 오늘 구현해서 표시한 Image에 대한 onClick 이벤트 처리부분 구현
+ 검색 기능 구현



### 2020.10.26, JSY

#### API에서 받은 레시피를 데이터 베이스화(== Recipe 클래스 작성)

##### Recipe.java

```java
package com.techtown.cookingkingcooking_ver2;

import android.graphics.Bitmap;

public class Recipe
{
    private String name; //메뉴명 "RCP_NM"
    private String category; // 요리종류 "RCP_PAT2"
    private String way; //조리방법 "RCP_WAY2"
    private String foodIngredients; // 요리재료 "RCP_PARTS_DTLS"
    private String imageMain; // 이미지경로(소) "ATT_FILE_NO_MAIN"
    private String imageSub; // 이미지경로(대) "ATT_FILE_NO_MK"
    private String manual; //만드는 법(1~20) "MANUAL01~20"
    private String[] manualImages; //만드는법에 대한 Image URL
    private double calorie; //열량 "INFO_ENG"
    private Bitmap bitmapMain; //이미지경로(소)를 통해 만들어진 비트맵
    private Bitmap bitmapSub; //이미지경로(소)를 통해 만들어진 비트맵

    public Recipe() {}
    public Recipe(String name, String category, String way, String foodIngredients, String imageMain, String imageSub, String manual, double calorie)
    {
        this.name = name;
        this.category = category;
        this.way = way;
        this.foodIngredients = foodIngredients;
        this.imageMain = imageMain;
        this.imageSub = imageSub;
        this.manual = manual;
        this.calorie = calorie;
    }

    public void setName(String name) {this.name = name;}
    public void setCategory(String category) {this.category = category;}
    public void setWay(String way) {this.way = way;}
    public void setFoodIngredients(String foodIngredients) {this.foodIngredients = foodIngredients;}
    public void setImageMain(String imageMain) {this.imageMain = imageMain;}
    public void setImageSub(String imageSub) {this.imageSub = imageSub;}
    public void setManual(String manual) {this.manual = manual;}
    public void setManualImages(String[] manualImages) {this.manualImages = manualImages;}
    public void setCalorie(double calorie) {this.calorie = calorie;}
    public void setBitmapMain(Bitmap bitmapMain) {this.bitmapMain = bitmapMain;}
    public void setBitmapSub(Bitmap bitmapSub) {this.bitmapSub = bitmapSub;}

    public String getName() {return this.name;}
    public String getCategory() {return this.category;}
    public String getWay() {return this.way;}
    public String getFoodIngredients() {return this.foodIngredients;}
    public String getImageMain() {return this.imageMain;}
    public String getImageSub() {return this.imageSub;}
    public String getManual() {return this.manual;}
    public double getCalorie() {return this.calorie;}
    public Bitmap getBitmapMain() {return this.bitmapMain;}
    public Bitmap getBitmapSub() {return this.bitmapSub;}

    @Override
    public String toString()
    {
        String output = "이름: "+ this.name + "\n\n";
        output += "요리 분류: " + this.category + "\n\n";
        output += "조리 방법: " + this.way + "\n\n";
        output += "칼로리(1인분): " + this.calorie + "Kcal" + "\n\n";
        output += "재료\n" + this.foodIngredients + "\n\n";
        output += "조리 순서\n" + this.manual;

        return output;
    }
}
```

해당 클래스를 작성하고 MainActivity.java에서 api의 내용을 받아올때 set***() 메소드를 통해 레시피에 대한 정보를 하나씩 추가하는 방법으로 작성 아래 코드는 MainActivity.java의 GetXMLTask의 API 파싱 부분

```java
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
                String imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                recipes[i].setImageMain(imageAddress);

                temp = fstElmnt.getElementsByTagName("ATT_FILE_NO_MK"); // 이미지경로(대)
                imageAddress = temp.item(0).getChildNodes().item(0).getNodeValue();
                recipes[i].setImageSub(imageAddress);

                new GetImageTask().execute(imageAddress); //이미지를 화면에 출력하기 위한 AsyncTask호출
            }

           // System.out.println(s); // 값 출력(추후에 다음 Activity로 넘어 가게끔 수정)

            super.onPostExecute(doc);
        }
```

이런식으로 데이터베이스화 한 레시피들을 15개의 배열의 형태로 저장한다음 화면에서 사용자가 클릭했을 경우의 코드를 추가

```java
    //초기화면에서 보여지는 15개의 레피시 사진의 클릭 이벤트를 처리하기 위한 OnClickListener
    View.OnClickListener onClickRecipeImage =  new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //Toast.makeText(getApplicationContext(),"해당 레시피 출력 기능은 구현 예정", Toast.LENGTH_SHORT).show();
            //System.out.println(recipes[v.getId()]);
            Toast.makeText(getApplicationContext(), recipes[v.getId()].toString(), Toast.LENGTH_LONG).show();
        }
    };
```



아래 사진은 초기 화면 15개의 레시피 사진을 클릭했을 경우의 이벤트 사진

<img src="https://user-images.githubusercontent.com/28241676/97188062-7226e280-17e6-11eb-8789-a82d5b08eb40.png" alt="Screenshot_1603723435" style="zoom:25%;" />

#### 없는 이미지에 대해서

이미지가 없는 경우에는 drawable파일에 no_img.png파일로 대체함

<img src="https://user-images.githubusercontent.com/28241676/97189572-165d5900-17e8-11eb-9a46-5e2afef56779.png" alt="no_img" style="zoom:25%;" /> <img src="https://user-images.githubusercontent.com/28241676/97190031-9be10900-17e8-11eb-84dc-04d62883a224.png" alt="Screenshot_1603724898" style="zoom:25%;" />

#### 예상치 못한 오류
+ 여러번 코드를 실행해본 결과 null 포인트 에러가 발생함 어디서 발생했는지 찾지 못했음.

+ 분명 이미지에 대한 URL이 null값인데 참조하려 해서 그런거 같은데, 해당 부분은 예외처리 되어있음

+ 근데 왜 났을까.... 이건 코드 최적화랑 발생했을때 케이스를 제대로 확인해서 처리해야 될듯.

+ ```java
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
  ```

+ 현재 위 처럼 예외처리를 했는데, read_me 쓰면서 또 오류나길래 한번 바꿔봄 이걸로도 또 에러나면 제대로 고쳐봐야 될듯...

+ 또한 아래 같은 오류도 나타남(img 파일의 서버가 사라져서(?) 서버로부터 이미지를 받지 못하는 경우 에러가 남)

  ```xml
  2020-10-27 00:08:06.792 32043-32085/com.techtown.cookingkingcooking_ver2 W/System.err: java.io.FileNotFoundException: http://www.foodsafetykorea.go.kr/uploadimg/20200317/20200317113150_1584412310801.jpg
  ```

#### 추후 개발

+ 위의 사진을 다음 Activity에 띄우는 것을 구현
+ 그에 맞는 XML파일 제작
+ 검색 기능 구현(이거는.... 너무 무섭다....)

### 2020.10.29, 근표상연

이미지 클릭했을때, 다음 Activity로 넘어가는 코드 작성

Recipe.java에서 Recipe 클래스가 Parcelable를 구현함

```java
public class Recipe implements Parcelable
{
    private String name; //메뉴명 "RCP_NM"
    private String category; // 요리종류 "RCP_PAT2"
    private String way; //조리방법 "RCP_WAY2"
    private String foodIngredients; // 요리재료 "RCP_PARTS_DTLS"
    private String imageMain; // 이미지경로(소) "ATT_FILE_NO_MAIN"
    private String imageSub; // 이미지경로(대) "ATT_FILE_NO_MK"
    private String manual; //만드는 법(1~20) "MANUAL01~20"
    private String[] manualImages; //만드는법에 대한 Image URL
    private double calorie; //열량 "INFO_ENG"
    private Bitmap bitmapMain; //이미지경로(소)를 통해 만들어진 비트맵
    private Bitmap bitmapSub; //이미지경로(소)를 통해 만들어진 비트맵

    public Recipe() {}

    public Recipe(Parcel src)
    {
        this.name = src.readString();
        this.category = src.readString();
        this.way = src.readString();
        this.foodIngredients = src.readString();
        this.imageMain = src.readString();
        this.imageSub = src.readString();
        this.manual = src.readString();
        //this.manualImages = src.readStringArray();
        this.calorie = src.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(way);
        dest.writeString(foodIngredients);
        dest.writeString(imageMain);
        dest.writeString(imageSub);
        dest.writeString(manual);
        //dest.writeStringArray(manualImages);
        dest.writeDouble(calorie);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {return 0;}

    public void setName(String name) {this.name = name;}
    public void setCategory(String category) {this.category = category;}
    public void setWay(String way) {this.way = way;}
    public void setFoodIngredients(String foodIngredients) {this.foodIngredients = foodIngredients;}
    public void setImageMain(String imageMain) {this.imageMain = imageMain;}
    public void setImageSub(String imageSub) {this.imageSub = imageSub;}
    public void setManual(String manual) {this.manual = manual;}
    public void setManualImages(String[] manualImages) {this.manualImages = manualImages;}
    public void setCalorie(double calorie) {this.calorie = calorie;}
    public void setBitmapMain(Bitmap bitmapMain) {this.bitmapMain = bitmapMain;}
    public void setBitmapSub(Bitmap bitmapSub) {this.bitmapSub = bitmapSub;}

    public String getName() {return this.name;}
    public String getCategory() {return this.category;}
    public String getWay() {return this.way;}
    public String getFoodIngredients() {return this.foodIngredients;}
    public String getImageMain() {return this.imageMain;}
    public String getImageSub() {return this.imageSub;}
    public String getManual() {return this.manual;}
    public double getCalorie() {return this.calorie;}
    public Bitmap getBitmapMain() {return this.bitmapMain;}
    public Bitmap getBitmapSub() {return this.bitmapSub;}

    @Override
    public String toString()
    {
        String output = "이름: "+ this.name + "\n\n";
        output += "요리 분류: " + this.category + "\n\n";
        output += "조리 방법: " + this.way + "\n\n";
        output += "칼로리(1인분): " + this.calorie + "Kcal" + "\n\n";
        output += "재료\n" + this.foodIngredients + "\n\n";
        output += "조리 순서\n" + this.manual;

        return output;
    }
}
```



MainActivity.java에서

```java
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
```

다음 액티비티로 넘기는 작업을 함



또한 두번째 RecipeInfoActivity.java에서

```java
package com.techtown.cookingkingcooking_ver2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class RecipeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        Intent recipeInfo = getIntent();
        printRecipeInfo(recipeInfo);
    }

    private void printRecipeInfo(Intent intent)
    {
        Recipe info = intent.getParcelableExtra("recipeInfo");
        Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
    }
}
```

printRecipeInfo() 메소드로 간단하게 넘어온 데이터를 출력하는 것으로 구현

해당 Activity에서의 사진

<img src="https://user-images.githubusercontent.com/28241676/97580330-7ea17a00-1a36-11eb-854b-176d68b314e0.png" alt="Screenshot_1603977763" style="zoom:25%;" />

### 브랜치 마무리

+ 현재 여기 까지 시점으로 API_Interworking_Function 브랜치를 main과 머지함
+ 추후 개발 기능 2가지
  + 검색 기능(네이버 검색 API 활용, firebase 데이터베이스 두개를 통해 검색 알고리즘 구현)
  + 레시피 공유기능(우리들 만의 레시피 양식을 만들고 그것을 XML 파일로 만들어서 Firebase에 올리는(?) 형태로 구현)
+ 가보자~!

### 2020.11.03, JSY

검색 api를 활용한 검색 기능 구현 브랜치

네이버 검색 api를 통해 검색 데이터를 추출하는데 까지는 성공

<img src="https://user-images.githubusercontent.com/28241676/97992594-3911f200-1e26-11eb-9704-9777617b558f.PNG" alt="naver search api" style="zoom:75%;" />

위 그림처럼 xml파일의 형태로 검색 결과를 받아오고 System.out.println()로 출력하였다. 그러나 검색 결과의 link가 삭제되거나 없는 게시물이라고 나와서 도저히 사용 할 수 없는 api라고 판단(https://codingcoding.tistory.com/864) 따라서 해당 과정만 커밋하고 구글 검색 api로 갈아탈 예정

<img src="https://user-images.githubusercontent.com/28241676/97992773-7d9d8d80-1e26-11eb-930f-e89e36563ac8.PNG" alt="naver search api2" style="zoom:50%;" />



코드는 ApiSearch.java를 작성하고 MainActivity에 AysncTask class를 상속하는 SearchNaverTask 클래스를 생성함.

#### ApiSearch.java
```java
package com.techtown.cookingkingcooking_ver2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ApiSearch
{
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String get(String apiUrl, Map<String, String> requestHeaders)
    {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

        public HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readBody(InputStream body)
    {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}

```

#### MainActivity.java 273~312 line
```java
    private class SearchNaverTask extends AsyncTask<String,Void, String>
    {
        String clientId = "7Yl7TBQh7e_RX5MbFN6c"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "qY_AlwOwz_"; //애플리케이션 클라이언트 시크릿값"
        String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="; // xml 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // json 결과

        @Override
        protected String doInBackground(String... strings)
        {
            String text = null;
            try {
                System.out.println(strings[0]);
                text = URLEncoder.encode(strings[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패",e);
            }
            apiURL += text+"&sort=sim&display=100&start=100";

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);

            ApiSearch apiSearch = new ApiSearch();
            String responseBody = apiSearch.get(apiURL, requestHeaders);

            return responseBody;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result)
        {
            System.out.println(result);
        }
    }
```

일단 여기까지 커밋하고 구글 검색 api로 갈아타야 겠음....



### 2020.11.05, JSY
#### 검색 api 보완

기존의 네이버 검색 api를 좀더 찾아본 결과: Json 형태로 api를 파싱하면 링크가 잘 나옴 그리고 xml 파일로 받았을 경우에는 링크의 주소를 조금 수정해줘야 함 근데 후자(xml 파싱)보다 전자(json 파싱)이 후작업이 없어서 코드 수정함

MainActivity.java의 Asysnc Task가 너무 많아서 구글의 힘을 빌려 Thread를 상속하는 ApiSearch.java를 작성함

#### apiSearch.java

```java
package com.techtown.cookingkingcooking_ver2;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ApiSearch extends Thread
{
    public static String clientId = "7Yl7TBQh7e_RX5MbFN6c"; //애플리케이션 클라이언트 아이디값"
    public static String clientSecret = "qY_AlwOwz_"; //애플리케이션 클라이언트 시크릿값"
    public static String searchData; // 검색 키워드
    public static String searchResult = ""; //검색 결과를 참조하기 위해 선언

    public ApiSearch(String searchData)
    {this.searchData = searchData;}

    @Override // Thread를 상속하여 실행 함 main 메소드를 실행함으로서, 네이버 검색 api 기능 수행
    public void run()
    {
        super.run();
        this.main();
    }

    public static void main()
    {
        String text = null;
        try {
            text = URLEncoder.encode(searchData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="; // xml 결과
        String apiURL = "https://openapi.naver.com/v1/search/blog?query=";    // json 결과

        apiURL += text+"&sort=sim&display=5&start=10";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = get(apiURL, requestHeaders);

        searchResult = parseData(responseBody);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String get(String apiUrl, Map<String, String> requestHeaders)
    {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

        public static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readBody(InputStream body)
    {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    // json형태의 데이터를 파싱하는 메소드
    // 여러 index들 중 title과 description, link를 뽑아옴
    private static String parseData(String responseBody)
    {
        String title;
        String description;
        String link;

        String result = "";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseBody.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                title = item.getString("title");
                result += "TITLE : " + title +"\n";

                description = item.getString("description");
                result += "DESCRIPTION: "+description + "\n";

                link = item.getString("link");
                result += "LINK: "+link + "\n\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
```

추후에 뽑아온 데이터는 검색 결과를 출력하는 Activity를 만들어서 대충 5~6개 정도를 보여주고 해당 위젯을 누르면 해당 링크로 이동하는 위젯을 만들 예정 검색 기능을 좀던 있어 보이게 만들고 싶은데,,, 현재로선 지금이 최선인듯 나중에 유튜브 검색 api나 우리만의 데이터 베이스도 출력하는 방식도 바램사항

위 코드를 어디서 실행하는가

#### MainActivity.java 113~129 line

````java
    public void onClickSearchBtn(View v)
    {
        String searchData = searchEditText.getText().toString();

        if(searchData.getBytes().length > 0)
        {
            Thread thread = new ApiSearch(searchData);
            thread.start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}

            String searchResult = ApiSearch.searchResult;
            Toast.makeText(this, searchResult, Toast.LENGTH_LONG).show();
        }
        else {return;}
    }
````

해당 onClick 이벤트 메소드는 돋보기 모양의 Imagebutton을 클릭 했을 때를 다룸.<br>사용자가 검색하고자 하는 데이터를 입력하면 해당 데이터를 받아 Thread에서 ApiSearch 클래스를 실행함.<br>이후 1초를 기다리고 출력결과를 일단 Toast로 출력하게끔 작성함

#### 아래는 출력 결과를 보여주는 스크린샷

<img src="https://user-images.githubusercontent.com/28241676/98131984-39ca8700-1eff-11eb-8003-023b78db46b0.png" alt="Screenshot_1604503585" style="zoom:25%;" />

<img src="https://user-images.githubusercontent.com/28241676/98131990-3b944a80-1eff-11eb-94bf-b2e836655388.png" alt="Screenshot_1604503594" style="zoom:25%;" />



#### 추후에
추후에 검색 결과를 이쁘게 정리하는 Activity를 만들고 각각의 표시되는 위젯을 클릭하면 인터넷창을 띄어 사용자에게 보여주는 기능을 구현하고 검색 기능 브랜치는 종료 예정



### 2020.11.06, JSY

#### 구현내용

+ 지난 개발 내용에서 검색기능을 온전하게 구현함
+ SearchActivity.java를 작성함 해당 Activity는 MainActivity에서 넘어온 검색 결과를 넘겨 받아서 동적으로 검색 결과를 출력하여 사용자가 검색 결과를 확인하고 마음에 드는 내용을 클릭하면 해당 링크를 통해 인터넷창을 띄어주는 역할을 함(자세한 코드는 후술)
+ 그리고 초기화면에서 그림을 클릭했을때, 해당 레시피의 정보가 나오는 것 처럼, 검색을 했을때, 검색 결과를 다음 Activity로 넘겨주기 위해 SearchResult.java를 작성함 해당 클래스는 json파일에서 파싱한 데이터를(title, description, blogLink)를 멤버변수로 갖고 있고 Parcelable를 구현해서 intent를 통해 Activity를 넘어다닐 수 잇도록 설계함(자세한 코드는 후술)

#### SearchActivity.java

```java
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
```

코드 자체는 복잡하지 않다. 다만 id값 참조를 위해 dummyId값을 추가해줬음(겹치는걸 막기 위해) 추후에 각종 효과나 UI를 동적으로 보완해야 한다면 복잡해 질 수도 있을거 같음....

#### SearchResult.java

```java
package com.techtown.cookingkingcooking_ver2;

import android.os.Parcel;
import android.os.Parcelable;

// MainActivity에서 검색한 결과를 담는 class
public class SearchResult implements Parcelable
{
    private String title;
    private String description;
    private String blogLink;

    public SearchResult(String title, String description, String blogLink)
    {
        this.title = title;
        this.description = description;
        this.blogLink = blogLink;
    }

    public SearchResult(Parcel src)
    {
        this.title = src.readString();
        this.description = src.readString();
        this.blogLink = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(blogLink);
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel source) {
            return new SearchResult(source);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    @Override
    public int describeContents() {return 0;}

    @Override
    public String toString()
    {
        return "TITLE: " + this.title + "\n" +
                "DESCRIPTION: " + this.description + "\n" +
                "BlogLink: " + this.blogLink + "\n";
    }

    public String getTitle() {return this.title;}
    public String getDescription() {return this.description;}
    public String getBlogLink() {return this.blogLink;}

    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setBlogLink(String blogLink) {this.blogLink = blogLink;}
}
```

전체적으로 Recipe.java와 형태는 비슷하다. 멤버변수를 갖고 있고 Parcelable를 구현해 필요한 메소드를 재정의하고 set,get함수 구현

#### 실행화면

<img src="https://user-images.githubusercontent.com/28241676/98261172-8083b400-1fc7-11eb-9bcf-dd0a42c14777.png" alt="Screenshot_1604590291" style="zoom:25%;" />

검색 버튼 클릭후 화면 (검색 키워드: "탕수육 레시피")

<img src="https://user-images.githubusercontent.com/28241676/98261242-9a24fb80-1fc7-11eb-9852-439b6796d57e.png" alt="Screenshot_1604589725" style="zoom:25%;" />

해당하는 Link 클릭시 internet창으로 넘어감

<img src="https://user-images.githubusercontent.com/28241676/98261321-add06200-1fc7-11eb-9cc2-3df951e9db3d.png" alt="Screenshot_1604589736" style="zoom:25%;" />

클릭 후 하이퍼링크 효과를 줘서 보라색으로 바뀐 모습

-> 사실 실제로 하이퍼 링크는 아니고 그냥 setTextColor()메소드로 인위적으로 바꾼거...ㅋㅋ


#### 추후에

+ 여기까지 검색 기능 구현 브랜치 마무리하고 main과 머지 예정
+ 이제 며칠 안남아서 전체적으로 UI 다듬고 해야될 듯

### 2020.11.06, 근표, 상연

#### 근표

+ fireBase 앱과 연동
+ Recipe.java, RecipeInfoActivity.java, activity_recipe_info.xml  <=> 파이어베이스 적용(?)
+ 레시피 공유 기능

#### 상연

+ 본인이 만든부분 UI 깔끔하게 정리
+ 내가짠 코드 최적화
+ Firebase 공부
+ Search_Fuction 브랜치 main과 머지

### 2020.11.08, JSY

#### 한 것

+ 파이어 베이스 공부

  관련 URL: https://medium.com/wasd/android-firebase-realtimedb-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-4f65b360d06e

  위의 URL을 통해서 실습해보고 적용해보면 될듯

+ 코드 최적화는 하지 못함

+ UI 부분

  + 검색 결과 부분에 수평선으로 구분지어 놓음
    + <img src="https://user-images.githubusercontent.com/28241676/98467541-401b7480-2219-11eb-9ab2-adf97acb7150.png" alt="Screenshot_1604845321" style="zoom:25%;" />
  + 앱 아이콘을 변경함
    + <img src="https://user-images.githubusercontent.com/28241676/98467545-41e53800-2219-11eb-8b35-37bdfc65e303.png" alt="Screenshot_1604845041" style="zoom:25%;" />

  

  
#### 추후에
  + 코드 최적화는 주석을 달면서 최대한 깔끔하게 할 예정
  + 파이어 베이스 연동도 지금 공부 중인데 주중에 회의하면서 같이 작성도 나쁘지 않아 보임


### 2020.11.13, JSY
#### 주석

+ MainActivity.java 주석 추가
+ SearchActivity.java 주석 추가
+ SearchResult.java 주석 추가
+ ApiSearch.java 주석 추가
+ 개발 로그를 달아야 되는데 github commit 이력 보고 일일이 입력 할 생각하니까 가슴이 웅장해 짐...(귀찮아서 못하겠어...)


#### 오류?
<img src="https://user-images.githubusercontent.com/28241676/99078950-9108df80-2602-11eb-8a47-c235a7a5759b.png" alt="image" style="zoom:25%;" />

+ 현재 api 서비스 점검 중이라 실행이 안됌.... 저거 만약에 발표때까지 점검 계속 되면 우리 풋고추 되는 거...

### 2020.11.15, 근표 상연
+ 근표(파이어베이스) + 기존 main 수동으로 머지
+ 코드 최종적으로 마무리했다.
  + FireBase 데이터 읽기/쓰기 구현(근표)
  + 사용자간 레시피 공유 기능(근표)
  + api에서 받아오는 레시피 상세히 보여주는 xml, java 완료(상연)

#### 내일 (11.16) 주석 달고 이후에 할일

+ 근표
  + user_Recipe.java
  + share_Recipe.java
  + mainAcitivity.java
  + FirebasePost.java
+ 상연
  + RecipeInfoActivity.java
  + mainAcitivity.java
  + 전체적으로 내가 작성한거 다시한번 확인
+ 주석 달기 완료 되는 내일 이후에(11.17 부터) PPT 빠르게 작성
  + 움짤로 작성하면 가독성 x100
  + 화요일에 공동으로 작성할 부분 만들기
  + 수요일에 개인이 발표할 부분 만들기
  + 목요일에 최종적으로 확인 후 합치기
  + 금요일 최종확인 후 제출
  + 발표 전까지 간간히 발표 연습하기

### 2020.11.16, 근표 상연

개발 완료

+ 이제 ppt 만들고 read me 작성하고 하면서 마무리 하면 될듯