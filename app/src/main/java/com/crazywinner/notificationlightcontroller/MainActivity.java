package com.crazywinner.notificationlightcontroller;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends PreferenceActivity {
    RecyclerView recyclerView;
    SqlController SQL;
    InterstitialAd mInterstitialAd;
    boolean adshowed=false;

    boolean isHiding=false;
    ViewPager pager;
    SharedPreferences preferences;
    FloatingActionButton fab;
    MyRecylerAdapter adapter;
    private void showAd(){
        //create an adRequest
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        mInterstitialAd.loadAd(adRequest);
    }
    @Override
    public void onDestroy(){
        // we need to destroy entire process onDestroy
        // if we dont cached things wont destroy and service will use over 30-40 mb ram
        android.os.Process.killProcess(android.os.Process.myPid());

    }
    void setBottomBar(int i ){
        //show an animation for bottom bar if i = 0 notification page is selected else settings page is selected
        ImageView settings=(ImageView)findViewById(R.id.imageViewSet);
        ImageView notifi=(ImageView)findViewById(R.id.imageViewNoti);
        notifi.clearAnimation();
        settings.clearAnimation();
        TextView textset=(TextView)findViewById(R.id.textViewSet);
        TextView textNoti=(TextView) findViewById(R.id.textViewNoti);
        if(i==0){
           notifi.animate().translationY(0).setDuration(50).start();
            notifi.setColorFilter(getResources().getColor(android.R.color.white));

            textNoti.animate().alpha(1).setDuration(50).start();
            settings.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            settings.animate().translationY(18).setDuration(50).start();
            textset.animate().alpha(0).setDuration(50).start();


        }else{
            notifi.animate().translationY(18).setDuration(50).start();
            textNoti.animate().alpha(0).setDuration(50).start();
            settings.setColorFilter(getResources().getColor(android.R.color.white));
            notifi.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            settings.animate().translationY(0).setDuration(50).start();
            textset.animate().alpha(1).setDuration(50).start();

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQL=new SqlController(this);
        pager=(ViewPager)findViewById(R.id.viewPager);
        pager.setAdapter(new myPager());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //set bottom bar position same as page position
                setBottomBar(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        preferences=getSharedPreferences(getPackageName(),MODE_PRIVATE);
        if(!preferences.getBoolean("yapildi",false)){
            preferences.edit().putBoolean("yapildi",true).apply();
            //Ask for defaults
            new AlertDialog.Builder(this)
                    .setMessage(R.string.addMessage)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsyncTask<Integer, Integer, Integer>() {
                                ProgressDialog progress;
                                @Override
                                protected void onPreExecute(){
                                   progress=ProgressDialog.show(MainActivity.this,getString(R.string.loading),getString(R.string.loading));



                                }
                                @Override
                                protected Integer doInBackground(Integer... params) {
                                    PackageManager pm=getPackageManager();
                                    //add default color automaticly
                                    HashMap<String,Integer> defaults=defaultValues.getDefaultValues();
                                    for(PackageInfo info :pm.getInstalledPackages(0)){
                                    if(defaults.containsKey(info.packageName)){
                                        ColorPackage packagex=new ColorPackage();
                                        packagex.color=defaults.get(info.packageName);
                                        packagex.duration=800;
                                        packagex.packageName=info.packageName;
                                        SQL.addColor(packagex);

                                    }

                                    }
                                    return null;
                                }
                                @Override
                                protected void onPostExecute(Integer x){
                                    reloadAdapter();
                                    progress.dismiss();

                                }
                            }.execute();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .show();

        }
        recyclerView=(RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new SlideInRightAnimator());
        ArrayList<ColorPackage> packages=SQL.getAllColors();
         recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
             @Override
             public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                 super.onScrollStateChanged(recyclerView, newState);
             }
             @Override
             public void onScrolled(RecyclerView c,int dx,int dy){
                if(dy>0){
                    //if scrolled down hide fab
                    hideFab();
                }else{
                    //if scrolled up show fab again
                    showFab();
                }

             }
         });

        adapter=new MyRecylerAdapter(this,packages);
        recyclerView.setAdapter(adapter);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start editColorActivity with editmode=false(create new mode)
                Intent i=new Intent(MainActivity.this,EditColorActivity.class);
                i.putExtra("edit",false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivityForResult(i, 1,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,fab,"fabTrans").toBundle());
                }else{startActivityForResult(i,1);}
            }
        });
        setBottomBar(0);
        LinearLayout bottom1=(LinearLayout)findViewById(R.id.bottombar1);
        LinearLayout bottom2=(LinearLayout)findViewById(R.id.bottombar2);
        bottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0,false);
            }
        });
        bottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1,false);
            }
        });
        //create a showcase for floating action button
        if(!preferences.getBoolean("showedfirsthint",false)){
            new MaterialTapTargetPrompt.Builder(this)

                    .setTarget(fab)
                    .setPrimaryText(getString(R.string.createfirst))
                    .setSecondaryText(getString(R.string.createfirst_desc))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                    {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                        {

                            //Do something such as storing a value so that this prompt is never shown again
                        }

                        @Override
                        public void onHidePromptComplete()
                        {
                            preferences.edit().putBoolean("showedfirsthint",true).apply();

                        }
                    })
                    .show();
        }
        getPreferenceManager().setSharedPreferencesName(getPackageName());
        addPreferencesFromResource(R.xml.preferences);
        //initalize ad
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_fullscreen));


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(){
                mInterstitialAd.show();
            }

        });

    }
    public void hideFab(){
        //animate fab to bottom of screen
        if(!isHiding){
        fab.clearAnimation();
        fab.animate().translationY(fab.getBottom()).start();
        isHiding=true;}

    }
    @Override
    public void onResume(){
        super.onResume();
        showFab();
    }
    public void showFab(){
        //animate fab to its default place
        if(isHiding){
        fab.clearAnimation();
        fab.animate().translationY(0).start();
        isHiding=false;
        }
    }
    public void onActivityResult(int requestCode,int resultCode,Intent i){
        //listen for data coming from editcoloractivity
      if(requestCode==1&&resultCode==RESULT_OK){
          ColorPackage pac=new ColorPackage();
          pac.packageName=i.getStringExtra("package");
          pac.color=i.getIntExtra("RGB",0);
          pac.duration=i.getIntExtra("duration",0);
          if(SQL.contains(pac.packageName)){refreshPackage(pac);}else{
          addPackage(pac);}

      }
        if(!adshowed){
            showAd();
            adshowed=true;
        }
    }
    public void addPackage(ColorPackage packagee){
        //add package to sql and recylerview
        SQL.addColor(packagee);
        adapter.addPackage(packagee);

    }
    private void refreshPackage(ColorPackage packagee){
        //refresh one package from sql and recylerview
        SQL.addColor(packagee);
        adapter.refreshColor(packagee);

    }
    private void reloadAdapter(){
        //refresh all packages
        ArrayList<ColorPackage> packages=SQL.getAllColors();
        adapter.setPackages(packages);


    }

}
