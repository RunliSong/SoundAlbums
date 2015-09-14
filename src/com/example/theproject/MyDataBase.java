package com.example.theproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by alexgao on 6/10/2014.
 * this is the database helper class
 */
public class MyDataBase extends SQLiteOpenHelper {
	//create sql query and store in a string variable.
    private final String CREATE_DB = "CREATE TABLE " + TakePicture.TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, IMAGES BLOB, IMAGES_NAME TEXT)";

    public MyDataBase(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	//execute query to create database
        db.execSQL(CREATE_DB);
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	//this method is to update the database.
        String syntax = "DROP TABLE IF EXISTS " + TakePicture.TABLE_NAME;
        Log.d("Syntax: ", syntax);
        db.execSQL(syntax);

        onCreate(db);
    }
}
