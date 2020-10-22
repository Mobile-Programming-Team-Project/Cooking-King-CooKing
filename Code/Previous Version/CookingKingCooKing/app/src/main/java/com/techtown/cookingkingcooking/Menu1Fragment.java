package com.techtown.cookingkingcooking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Menu1Fragment extends Fragment
{
    ViewGroup rootView;
    LinearLayout[] recipes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu1, container, false);;

        recipes = new LinearLayout[]{rootView.findViewById(R.id.recipe1),
                rootView.findViewById(R.id.recipe2),
                rootView.findViewById(R.id.recipe3)};

        for(int i=0; i<recipes.length; i++)
        {
            for(int k=0; k<5; k++)
            {
                makeImageView(recipes[i], i+k);
            }
        }

        return rootView;
    }

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
}