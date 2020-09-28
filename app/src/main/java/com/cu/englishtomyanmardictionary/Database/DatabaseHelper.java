package com.cu.englishtomyanmardictionary.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cu.englishtomyanmardictionary.BuildConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    String DB_PATH;
    private static final String DATABASE_NAME= "Dictionary.db";
    private static final int DATABASE_VERSION=1;
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    @SuppressLint("SdCardPath")
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext=context;
        this.DB_PATH="/data/data/"+ BuildConfig.APPLICATION_ID+"/databases/";
        Log.e("Path 1",DB_PATH);
    }

    public void createDatabase() throws IOException{

        /*
        boolean dbExist = checkDataBase();
        if (dbExist) {
            this.getWritableDatabase();
            this.close();
        }else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

         */
        if(isExit()){
            this.getWritableDatabase();
            this.close();
        }else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
            exit(true);
        }

    }
    public void exit(Boolean result){
        SharedPreferences sharedPreferences=myContext.getSharedPreferences("Database", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Database",result).apply();
    }

    public Boolean isExit(){
        SharedPreferences sharedPreferences=myContext.getSharedPreferences("Database", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Database",false);
    }
/*
    private boolean checkDataBase() throws IOException{
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        Log.e("db", String.valueOf(checkDB));
        return checkDB != null;
    }
 */
    private void copyDataBase() throws IOException {
        InputStream inputStream=myContext.getAssets().open(DATABASE_NAME);
        String outFileName=DB_PATH+DATABASE_NAME;
        OutputStream myOutput=new FileOutputStream(outFileName);
        byte[] buffer=new byte[10];
        int length;
        while ((length=inputStream.read(buffer))>0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        inputStream.close();
    }
    public void openDatabase() throws SQLException{
        String myPath=DB_PATH+DATABASE_NAME;
        myDatabase=SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
    }
    public synchronized void close(){
        if(myDatabase!=null){
            myDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Recent (ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,STATE TEXT,DEF TEXT,LAN TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE AddParse (ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,STATE TEXT,DEF TEXT,LAN TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


        if(i1>i){
            try{
                copyDataBase();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Recent");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS AddParse");

    }

    public Cursor query(String table){
        return myDatabase.query(table,null,null,null,null,null,null);
    }
    public boolean insertRecent(String word,String state,String def,String lan){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("WORD",word);
        contentValues.put("STATE",state);
        contentValues.put("DEF",def);
        contentValues.put("LAN",lan);

        long result=db.insert("Recent",null,contentValues);
        db.close();

        return result != -1;

    }

    public Cursor getRecent(){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.rawQuery("Select * from Recent",null);
    }
    public int deleteRecent(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("Recent","ID=?",new String[]{id});
    }

    public boolean deleteRecentAll(String lan){
        SQLiteDatabase db=this.getReadableDatabase();
        int affectedRows=db.delete("Recent","LAN=?",new String[]{lan});
        return affectedRows>0;
    }

    public boolean insertParse(String word,String state,String def,String lan){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("WORD",word);
        contentValues.put("STATE",state);
        contentValues.put("DEF",def);
        contentValues.put("LAN",lan);

        long result=db.insert("AddParse",null,contentValues);
        db.close();

        if(result==-1){
            return false;
        }else {
            return true;
        }

    }

    public Cursor getParse(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("Select * from AddParse",null);
        return res;
    }
    public int deleteParse(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        int res=db.delete("AddParse","ID=?",new String[]{id});
        return res;
    }

    public boolean deleteParseAll(String lan){
        SQLiteDatabase db=this.getReadableDatabase();
        int affectedRows=db.delete("AddParse","LAN=?",new String[]{lan});
        return affectedRows>0;
    }

}
