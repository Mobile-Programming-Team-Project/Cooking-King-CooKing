package com.techtown.cookingkingcooking_ver2;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

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
