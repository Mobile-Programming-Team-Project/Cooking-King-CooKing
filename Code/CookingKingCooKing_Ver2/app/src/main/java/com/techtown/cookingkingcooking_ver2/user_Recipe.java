package com.techtown.cookingkingcooking_ver2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class user_Recipe extends AppCompatActivity {

    DatabaseReference mDatabase;

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recipe);

        text = (TextView) findViewById(R.id.textView3);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //text = mDatabase.getDatabase();
    }
}
