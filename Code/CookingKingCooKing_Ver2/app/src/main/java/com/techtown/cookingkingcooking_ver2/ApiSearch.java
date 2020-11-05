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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ApiSearch extends Thread
{
    private static String clientId = "7Yl7TBQh7e_RX5MbFN6c"; //애플리케이션 클라이언트 아이디값"
    private static String clientSecret = "qY_AlwOwz_"; //애플리케이션 클라이언트 시크릿값"
    public static String searchKeyword; // 검색 키워드
    public static ArrayList<SearchResult> searchResults; //검색 결과를 참조하기 위해 선언

    public ApiSearch(String searchData)
    {
        this.searchKeyword = searchData;
        searchResults = new ArrayList<SearchResult>();
    }

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
            text = URLEncoder.encode(searchKeyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="; // xml 결과
        String apiURL = "https://openapi.naver.com/v1/search/blog?query=";    // json 결과

        apiURL += text+"&sort=sim&display=10";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = get(apiURL, requestHeaders);

        parseData(responseBody);
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
    private static void parseData(String responseBody)
    {
        String title;
        String description;
        String link;

        JSONObject jsonObject = null;
        searchResults.clear(); // 검색 결과 초기화
        try {
            jsonObject = new JSONObject(responseBody.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                title = item.getString("title");
                title = dataProcessing(title);

                description = item.getString("description");
                description = dataProcessing(description);

                link = item.getString("link");

                searchResults.add(new SearchResult(title, description, link));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String dataProcessing(String temp)
    {
        String result = "";
        String[] sliceTemp = temp.split("");
        Boolean flag = false;

        for(int i=0; i<sliceTemp.length; i++)
        {
            if(sliceTemp[i].equals("<")) {flag = true; continue;}
            else if(sliceTemp[i].equals(">")) {flag = false; continue;}

            if(flag) {continue;}
            else {result += sliceTemp[i];}
        }
        return result;
    }
}
