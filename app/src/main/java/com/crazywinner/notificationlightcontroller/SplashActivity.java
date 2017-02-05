package com.crazywinner.notificationlightcontroller;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;

import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * mertcan tarafindan 17.01.2017 tarihinde olusturuldu.
 */

public class SplashActivity extends AwesomeSplash {
    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(android.R.color.transparent); //any color you want form colors.xml
 //int ms
        configSplash.setAnimCircularRevealDuration(0);


        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.mipmap.ic_launcher); //or any other drawable
        configSplash.setAnimLogoSplashDuration(600); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.StandUp); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        configSplash.setTitleSplash(getString(R.string.splash_welcome));
        configSplash.setAnimTitleTechnique(Techniques.FadeInDown);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(300);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void animationsFinished() {
        SharedPreferences sharedPreferences =getSharedPreferences(getPackageName(),MODE_PRIVATE);
        if(sharedPreferences.getBoolean("firstTime",true)|| !isMyServiceRunning(MyService.class)){
            Intent i = new Intent(this,BeginActivity.class);
            startActivity(i);

        }else{
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);

        }

        finish();
    }
}
