package com.example.theproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;



public class TakePicture extends Activity {
    /**
     * Called when the activity is first created.
     * this is the take picture class
     */
	// declaration of all widgets and variables.
    ImageView img;
    MediaRecorder recorder;
    String fileName = "";
    Button btnCam, btnCancel, btnSave, btnRecord, btnPlayback, btnStop;
    ImageButton btnGo;
    public static final String TABLE_NAME = "images";
    public static final String DB_NAME = "finalProject.db";
    public static final int DB_VERSION = 2;

    public static final String FILE_FOLDER = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "voice";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takepicture);

        //link variables with widgets by id. 
        img = (ImageView) findViewById(R.id.imgResult);

        btnCam = (Button) findViewById(R.id.btnOpenCamera);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnGo = (ImageButton) findViewById(R.id.btnGo);
        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnPlayback = (Button) findViewById(R.id.btnPlayback);
        btnStop = (Button) findViewById(R.id.btnStop);

      //set button actions by using OnClickListener().
        btnCam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//set all other buttons to unable before the take photo button has been clicked. 
                btnCancel.setEnabled(false);
                btnSave.setEnabled(false);
                btnRecord.setEnabled(false);
                btnStop.setEnabled(false);
                btnPlayback.setEnabled(false);
                // open system camera
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // this method will return result in same activity
                startActivityForResult(openCameraIntent, 0);

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//this button is for user if user don't want to save photo.
            	//clear image view, and reset cancel and save button to unable.
            	
                img.setImageDrawable(null);
                btnCancel.setEnabled(false);
                btnSave.setEnabled(false);
               
                
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//this button is to save the photo into database
                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                Calendar c = new GregorianCalendar();//create system calendar
                DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //set file name format.eg:20141006155033381
                fileName = formatter.format(c.getTime());//get file name by calling the calendar.getTime().
                boolean flag = saveImageToDataBase(bitmap, fileName);//this method is declare down the bottom
                if (flag) {
                    img.setImageDrawable(null);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnRecord.setEnabled(true);
                    //clear image view, reset cancel and save button to unable, set record button to enable allow user to add sound.  
                }

            }
        });

        btnRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//this button is to let user start recording the sound 
                btnCancel.setEnabled(false);
                btnSave.setEnabled(false);
                btnCam.setEnabled(false);
                btnStop.setEnabled(true);
                //set stop button to enable allow user stop recording any time.
                if (recorder != null) recorder.release();

                File outputFolder = new File(FILE_FOLDER);
                if (!outputFolder.isDirectory())
                    outputFolder.mkdir();
                //create sound file with same name as photo
                File outputFile = new File(outputFolder, fileName + ".3gpp");
                //start mediaRecorder service.
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setOutputFile(outputFile.getPath());
                //Toast.makeText(TakePicture.this, outputFile.getPath(), Toast.LENGTH_LONG).show();
                try {
                    recorder.prepare();
                    recorder.start();
                } catch (Exception e) {
                    String me = e.getMessage();
                    Log.d(me, "");
                }
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//this button is to let user stop recording at any time.
                try {
                    recorder.stop();
                    btnCam.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnPlayback.setEnabled(true);
                    //set play button to enable to let user play the sound just recorded
                } catch (Exception ex) {
                    Log.d("Here I am", ex.getLocalizedMessage());
                }
            }
        });

       btnGo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePicture.this, MainList.class);
                startActivity(intent);
            }
        });

        btnPlayback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//this button is to let user play the sound that just been record.
                MediaPlayer mediaPlayer = new MediaPlayer();//start the media player service.
                try {
                    mediaPlayer.setDataSource(FILE_FOLDER + "/" + fileName + ".3gpp");
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    //Toast.makeText(TakePicture.this, FILE_FOLDER + "/" + fileName + ".3gpp", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = null;
        try {
            // if didn't take a picture and return, there will be an exception.
            if (data.getExtras().get("data") != null)
                bp = (Bitmap) data.getExtras().get("data");
            if (bp != null) {
                img.setImageBitmap(bp);
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
            }
            else {
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
            }

        } catch (Exception e) {
            //e.printStackTrace(); // do not need to do anything.
        }

    }

    private boolean saveImageToDataBase(Bitmap bitmapImage, String fileName) {
    	//this method is to save the photo and photo name into database return true if success return false if fall.
        boolean flag = true;

        MyDataBase mdb = new MyDataBase(TakePicture.this, DB_NAME, null, DB_VERSION);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();
        SQLiteDatabase db = mdb.getWritableDatabase();
        try {

            ContentValues cv = new ContentValues();
            cv.put("IMAGES", img);
            cv.put("IMAGES_NAME", fileName);

            db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            flag = false;
        } finally {
            db.close();
        }

        return flag;
    }
}
