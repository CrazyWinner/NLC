package com.crazywinner.notificationlightcontroller;



import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Build;

import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.CardView;

import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewAnimationUtils;

import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;


import android.widget.LinearLayout;
import android.widget.SeekBar;

import android.widget.TextView;
import android.widget.Toast;


import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;


import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class EditColorActivity extends AppCompatActivity implements ColorPickerDialogListener{
    FloatingActionButton fab;
    TextView textAd;
    boolean showed=false;
    boolean editMi=true;
    ImageView tint;
    SharedPreferences preferences;
    int duration;
    int myColor;
    CardView card;
    TextView textDur;
    List<ApplicationInfo> infos;
    String packageName="";
    //create showcase
    private void createTargerPrompt(View target, String text1, String text2, final Runnable onFinish){
        new MaterialTapTargetPrompt.Builder(EditColorActivity.this)
                .setTarget(target)

                .setPrimaryText(text1)
                .setSecondaryText(text2)
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
                    onFinish.run();
                    }
                })
                .show();

    }
    public void showHints(){
        //show showcase if it's not showed before
        if(!preferences.getBoolean("hintshowed",false)){
            createTargerPrompt(textDur,getString(R.string.durationhint),getString(R.string.durationhint_desc),
                    new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

        }
    }
    @Override
    public void onEnterAnimationComplete(){
        //make an reveal animation if it's not made before
        if(!showed){
            fab.setVisibility(View.INVISIBLE);
             card.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           if(editMi)
                        ViewAnimationUtils.createCircularReveal(fab, fab.getWidth() / 2, fab.getHeight() / 2, 0, (int) Math.hypot(fab.getWidth() / 2, fab.getHeight() / 2)).start();
                        if(!editMi){
                             card.setVisibility(View.VISIBLE);
                             Animation anim= AnimationUtils.loadAnimation(EditColorActivity.this,R.anim.left_slide);

                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        createTargerPrompt(findViewById(R.id.imageView), getString(R.string.selectapp), getString(R.string.selectapp_desc),
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showed=true;
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });



                            card.startAnimation(anim);
                        }else{
                            showHints();
                        }
                    }
                }
            }, 200);



    }
    showed=true;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an enter transition
            getWindow().setEnterTransition(new Fade());
            // set an exit transition
            getWindow().setExitTransition(new Fade());
        }




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);
        //get sharedpreferences
        preferences=getSharedPreferences(getPackageName(),MODE_PRIVATE);
        //find some views
        card=(CardView)findViewById(R.id.cardView);
        textDur=(TextView) findViewById(R.id.textView4);
        editMi=getIntent().getBooleanExtra("edit",false);
        fab =(FloatingActionButton)findViewById(R.id.fab);
        textAd=(TextView)findViewById(R.id.textViewAd);
        //check edit mode if edit mode is on set package name and icon
        if(editMi){
            packageName=getIntent().getStringExtra("package");
            setImage(packageName);
        }else{




        }
        duration=getIntent().getIntExtra("duration",800);
        myColor=getIntent().getIntExtra("RGB",0xFF000000);
        try {
            //set textview to name of our package
            textAd.setText(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,0)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //set the color to imageview by tinting it
        tint=(ImageView)findViewById(R.id.tintle);
        tint.setColorFilter(Color.argb((myColor>>24)&0xFF,(myColor>>16)&0xFF,(myColor>>8)&0xFF,myColor&0xFF));

        textDur.setText(duration+"ms");
        //if user clicks color create a color picker dialog
        tint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setDialogTitle(ColorPickerDialog.TYPE_CUSTOM)
                        .setPresets(new int[]{0xFFFF0000,0xFF00FF00,0xFF0000FF,0xFFFFFF00,0xFF00FFFF,0xFFFF00FF,0xFFFFFFFF})
                        .setShowAlphaSlider(true)
                        .setColor(myColor).show(EditColorActivity.this);

            }
        });

        //create an alertdialog for setting duration
        textDur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin=(LinearLayout) LayoutInflater.from(EditColorActivity.this).inflate(R.layout.dialogres,null);
                final TextView tx=(TextView)lin.findViewById(R.id.textViewDialog);
                final SeekBar sb=(SeekBar)lin.findViewById(R.id.seekbarDialog);
                sb.setMax(250);
                sb.setProgress(duration/8);
                tx.setText(duration+"ms");

                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tx.setText((progress*8)+"ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                new AlertDialog.Builder(EditColorActivity.this).setTitle(getString(R.string.duration))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                duration=sb.getProgress()*8;
                                textDur.setText((sb.getProgress()*8)+"ms");
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!preferences.getBoolean("hintshowed",false))

                        createTargerPrompt(tint, getString(R.string.colorhint), getString(R.string.colorhint_desc), new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }).setView(lin).show();

            }
        });








    }
    public void onButtonClick(View v){
        // if user touch the icon create an alertdialog to select app
        if(editMi){onBackPressed();}else{
            //first get all applications
            infos=getPackageManager().getInstalledApplications(0);
            final String[] packages=new String[infos.size()];
            //then get applications name in background
            new AsyncTask<Integer,Integer,Integer>(){
                ProgressDialog progressDialog;
                @Override
                protected void onPreExecute(){
                    //create a loading dialog
                    progressDialog=ProgressDialog.show(EditColorActivity.this,getString(R.string.loading),getString(R.string.loading));
                }
                @Override
                protected Integer doInBackground(Integer... params) {
                    for(int i =0;i<infos.size();i++){
                        packages[i]=(String)getPackageManager().getApplicationLabel(infos.get(i));
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Integer i){
                    //dismiss loading dialog
                    progressDialog.dismiss();
                    progressDialog=null;
                    //create alertdialog for picking app
                    new AlertDialog.Builder(EditColorActivity.this)
                            .setItems(packages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    packageName=infos.get(which).packageName;
                                    setImage(packageName);
                                    try {
                                        textAd.setText(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,0)));
                                    } catch (PackageManager.NameNotFoundException e) {
                                        textAd.setText(packageName);
                                    }
                                    dialog.dismiss();
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showHints();
                        }
                    }).show();
                }
            }.execute();
        }

    }

    public void setImage(String packageName){
        //set selected applications icon
        ImageView im=(ImageView)findViewById(R.id.imageView);
        Drawable d=null;
        boolean yap=true;
        try {
            d = getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            im.setImageResource(R.mipmap.ic_launcher);
            yap=false;
            e.printStackTrace();
        }
        if(yap){
            im.setImageDrawable(d);
        }
    }
    public void clicked(View v){
        //send data back to main activity
        Intent i = new Intent();
        i.putExtra("RGB",myColor);
        i.putExtra("package",packageName);
        i.putExtra("duration",duration);
        if(packageName.equals("")){setResult(RESULT_CANCELED);
            Toast.makeText(this,R.string.appnotselected,Toast.LENGTH_SHORT).show();
        }else{
        setResult(RESULT_OK,i);}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }else{finish();}


    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {

         myColor=color;
         tint.setColorFilter(Color.argb((myColor>>24)&0xFF,(myColor>>16)&0xFF,(myColor>>8)&0xFF,myColor&0xFF));
        if(!preferences.getBoolean("hintshowed",false))
        createTargerPrompt(fab, getString(R.string.savehint), getString(R.string.savehint_decs), new Runnable() {
            @Override
            public void run() {
                preferences.edit().putBoolean("hintshowed",true).apply();
            }
        });
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
