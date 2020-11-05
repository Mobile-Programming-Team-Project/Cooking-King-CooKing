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
