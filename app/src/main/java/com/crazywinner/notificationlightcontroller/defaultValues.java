package com.crazywinner.notificationlightcontroller;


import java.util.HashMap;

/**
 * mertcan tarafindan 16.01.2017 tarihinde olusturuldu.
 */


class defaultValues {
   //default values
  static HashMap<String,Integer> getDefaultValues(){
      HashMap<String,Integer> don=new HashMap<>();
      don.put("com.facebook.mlite",0xff0000ff);
      don.put("com.whatsapp",0xff00ff00);
      don.put("com.google.android.youtube",0xffff0000);
      don.put("com.google.android.gm",0xffe2203d);
      don.put("com.facebook.orca",0xff0000ff);
      don.put("com.instagram.android",0xfff77737);
      don.put("com.facebook.katana",0xff0000ff);
      don.put("com.snapchat.android",0xffffff00);
      don.put("com.facebook.lite",0xff0000ff);

       return don;
    }

}
