/*
Writer: 조상연
File Name: SearchResult.java
Function: MainActivity에서 SearchActivity로 넘어갈때 Intent에 담겨 같이 넘어가는 데이터형
        Naver search api를 통해 받은 검색 결과로 title, description, blogLink를 가지고 있음
        Acitivity 사이를 넘어다녀야 하므로 Parcelable을 구현하였다. Parcelable을 구현하기 위한 기본 메소드(생성자, writeToParcel, CREATOR, describeContents)를
        제외하면 모두 기본으로 작성되어야 하는 get, set 메소드가 전부이다. 그리고 디버그 용으로 편하게 출력하기 위해 toString() 메소드가 잇다.
 */

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
