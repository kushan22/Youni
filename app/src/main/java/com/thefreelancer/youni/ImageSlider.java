package com.thefreelancer.youni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ViewFlipper;

public class ImageSlider extends AppCompatActivity implements View.OnClickListener {

    ViewFlipper vf;
    Button btNext;
    private int count;
    SharedPreferences sp1;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_slider);
        vf = (ViewFlipper) findViewById(R.id.vf);
        btNext = (Button) findViewById(R.id.btNext);
        btNext.setOnClickListener(this);

        sp1 = getPreferences(Context.MODE_PRIVATE);
        editor = sp1.edit();
        count = sp1.getInt("totalCount", 0);
        count++;
        editor.putInt("totalCount", count);
        editor.commit();

        if (count != 1) {

            Intent goToNext = new Intent(ImageSlider.this, Signin.class);
            startActivity(goToNext);
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        int position = vf.getDisplayedChild();
        if (position == 3) {
            Intent i = new Intent(ImageSlider.this, Signin.class);
            startActivity(i);
            finish();
        } else {
            vf.showNext();
        }
    }
}
