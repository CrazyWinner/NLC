package com.crazywinner.notificationlightcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * mertcan tarafindan 17.01.2017 tarihinde olusturuldu.
 */

class SqlController extends SQLiteOpenHelper {
    private String TABLE_NAME="table_colors";
    SqlController(Context context) {
        super(context, "my_database", null, 1);
    }
    //basic SQL control unit
    @Override
    public void onCreate(SQLiteDatabase db) {
     String table= "CREATE TABLE "+TABLE_NAME+"("+
             "id "+"INTEGER PRIMARY KEY AUTOINCREMENT,"+
             "package "+"TEXT,"+
             "color "+"INTEGER,"+
             "duration "+ "INTEGER"+")";
        db.execSQL(table);
    }
    synchronized void addColor(ColorPackage c){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues val=new ContentValues();
        String query="SELECT * FROM "+TABLE_NAME+ " WHERE package = "+"'"+c.packageName+"'";
        Cursor cursor=db.rawQuery(query,null);


        if(cursor.getCount()>0){
            val.put("package",c.packageName);
            val.put("color",c.color);
            val.put("duration",c.duration);
            db.update(TABLE_NAME,val,"package = ?",new String[]{c.packageName});

        }else{

            val.put("package",c.packageName);
            val.put("color",c.color);
            val.put("duration",c.duration);
            db.insert(TABLE_NAME,null,val);}

        cursor.close();
        db.close();

    }
    synchronized boolean contains(String packageName){
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME+ " WHERE package = "+"'"+packageName+"'";
        Cursor c=db.rawQuery(query,null);

        boolean don=c.getCount()>0;
        db.close();
        c.close();
        return don;
    }
    synchronized void deleteColor(String packageName){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME,"package = ?",new String[]{packageName});
        db.close();
    }

    synchronized ColorPackage getColor(String packageName){
        ColorPackage don=new ColorPackage();
        String query="SELECT * FROM "+TABLE_NAME+ " WHERE package = "+"'"+packageName+"'";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            don.packageName=cursor.getString(1);
            don.color=cursor.getInt(2);
            don.duration=cursor.getInt(3);

        }else{don.packageName="";
        don.color=0;
        don.duration=0;}
        cursor.close();
        db.close();
        return don;

    }
    synchronized ArrayList<ColorPackage> getAllColors(){
        ArrayList<ColorPackage> colors=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME;
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
        do{
            ColorPackage ekle=new ColorPackage();
            ekle.packageName=cursor.getString(1);
            ekle.color=cursor.getInt(2);
            ekle.duration=cursor.getInt(3);
            colors.add(ekle);
        }while(cursor.moveToNext());}
        cursor.close();
        db.close();
        return colors;



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
