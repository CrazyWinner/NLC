package com.crazywinner.notificationlightcontroller;

import android.animation.Animator;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class BeginActivity extends AppCompatActivity {
    boolean verildi=false;
    public void animate(){
        verildi=true;
        Button next =(Button)findViewById(R.id.button_next);
        //set button's text to next
        next.setText(getString(R.string.begin_next));
        //animate between two imageview one is gonna be invisible other one is gonna be visible
        final ImageView eski=(ImageView)findViewById(R.id.imageView3);
        ImageView yeni=(ImageView)findViewById(R.id.imageViewOk);
        ViewPropertyAnimator anim=eski.animate();
        anim.setDuration(800).alpha(0).start();
        anim.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                eski.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        yeni.setVisibility(View.VISIBLE);
        yeni.setAlpha(0f);
        yeni.animate().alpha(1)
                .setDuration(1200).start();
        TextView grantPerm=(TextView)findViewById(R.id.grantpermText);
        grantPerm.setText(getString(R.string.begin_granted));
        TextView text3=(TextView)findViewById(R.id.textView3);
        text3.setVisibility(View.INVISIBLE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        if(isMyServiceRunning(MyService.class)){
            animate();

        }
    }
    public void clickedExit(View v){
        finish();

    }
    private void killMyself(){
        //go to next activity
        SharedPreferences s=getSharedPreferences(getPackageName(),MODE_PRIVATE);
        s.edit().putBoolean("firstTime",false).apply();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onResume(){
        if(isMyServiceRunning(MyService.class)){
            //if service is running animate and let user pass the screen
           animate();

        }
        super.onResume();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        //check if notificationlistenerservice is running
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void clickedSettings(View v){
        //if service is running go to next activity else go to settings activity
        if(!verildi){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);}else{
            killMyself();

        }

    }
}

