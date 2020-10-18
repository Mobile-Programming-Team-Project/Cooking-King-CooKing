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

추가적인 뼈대 작업, xml에서 생성하였던 레시피 목록을 java 코드에서 동적 생성하게끔 코드를 수정함.<br>지금은 MenuFragment.java파일에만 "makeImageView()" 메소드가 구현되어있음.(추후에 뼈대 작업 마무리에 추가할 예정)

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