# Cooking-King-CooKing
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

#### 추후 개발 내용
+ MainActivity.java의 소스가 너무 더러워서 나중에 머지를 위해 최적화가 필요해 보임
+ 오늘 구현해서 표시한 Image에 대한 onClick 이벤트 처리부분 구현
+ 검색 기능 구현