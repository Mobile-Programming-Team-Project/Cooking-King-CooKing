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

        recipes = new LinearLayout[]{(LinearLayout) rootView.findViewById(R.id.recipe1), (LinearLayout) rootView.findViewById(R.id.recipe2)};

        for(int i=0; i<5; i++)
        {
            makeImageView(recipes[0], i);
        }

        return rootView;
    }

    private void makeImageView(LinearLayout root, int id)
    {
        ImageView iv = new ImageView(getContext());
        iv.setImageResource(R.drawable.ic_launcher_foreground);
        iv.setId(id);
        iv.setMaxWidth(100);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);
        iv.setLayoutParams(lp);

        root.addView(iv);
    }
}