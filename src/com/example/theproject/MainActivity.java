package com.example.theproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
/*
 * Create by Runli Song 212535404
 * this is the main menu class
 * */
public class MainActivity extends Activity {
	// declaration of all buttons
	Button photo;
	Button list;
	Button about;
	Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //link variables with buttons by id. 
        photo = (Button) findViewById(R.id.btnTakePhoto);
        list = (Button) findViewById(R.id.btnRecordList);
        about = (Button) findViewById(R.id.btnAbout);
        exit = (Button) findViewById(R.id.btnExit);
        
        //set button actions by using OnClickListener().
        photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//this button is to open the TakePicture activity.
				Intent newintent = new Intent(MainActivity.this, TakePicture.class);
        		startActivity(newintent);
				
			}
		});
        
        list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//this button is to open the MainList activity.
				Intent newintent = new Intent(MainActivity.this, MainList.class);
        		startActivity(newintent);
				
			}
		});
        
        about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//this button is to pup up the about message by calling the show method in AboutBox activity
				AboutBox.Show(MainActivity.this);
			}
		});
        exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//this button is to close the application.
				finish();
			}
		});
    }
}
