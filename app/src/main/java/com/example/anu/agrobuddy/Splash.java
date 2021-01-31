package com.example.anu.agrobuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private ImageView iv;
    int x=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv=findViewById(R.id.logof);
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(myanim);
        final Intent i=new Intent(this,MainActivity.class);
        i.putExtra("x",1);
        Thread timer=new Thread()
        {
            public void run() {
                try {
                      sleep(3000) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();

                }
            }
        };
           timer.start();
    }
}
