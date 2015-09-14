package com.example.theproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.zip.Inflater;


/**
 * Created by alexgao on 7/10/2014.
 * this is the show content class
 */
public class ContentShow extends Activity {
	// declaration of all widgets and variables.
    MediaPlayer mediaPlayer;
    MediaRecorder recorder;
    String audioFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contene_show);
        String fileName = getIntent().getStringExtra("FileName"); // this is the name of both Bitmap file and .3gpp file

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(getImage(fileName));

        audioFilePath = TakePicture.FILE_FOLDER + "/" + fileName + ".3gpp"; // Audio file path
        File f = new File(audioFilePath);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) findViewById(R.id.contentShowButtonPosition);

        if (f.exists()) {
            // If file exist, show content_show_audio_found.xml
            ll.addView(inflater.inflate(R.layout.content_show_audio_found, null));
            // get buttons start play audio, stop play audio

            Button btnStartPlayback = (Button) findViewById(R.id.btnPlay);
            btnStartPlayback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ContentShow.this, "from content_show_audio_found start playback", Toast.LENGTH_LONG).show();
                    startPlayback();
                }
            });

            Button btnStopPlayback = (Button) findViewById(R.id.btnStop);
            btnStopPlayback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ContentShow.this, "from content_show_audio_found stop playback", Toast.LENGTH_LONG).show();
                    stopPlayback();
                }
            });
        } else {
            // If there is no such file, show content_show_no_audio_found.xml
            ll.addView(inflater.inflate(R.layout.content_show_no_audio_found, null));

            // get buttons start record, stop record
            Button btnStartRecord = (Button) findViewById(R.id.btnStartRecord);
            btnStartRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ContentShow.this, "from content_show_no_audio_found start record", Toast.LENGTH_LONG).show();
                    startRecord();
                }
            });
            Button btnStopRecord = (Button) findViewById(R.id.btnStopRecord);
            btnStopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ContentShow.this, "from content_show_no_audio_found stop record", Toast.LENGTH_LONG).show();
                    stopRecord();
                }
            });
        }
        Button btnBackToMain = (Button) findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentShow.this, MainList.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_all_pages,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(ContentShow.this, TakePicture.class);
        startActivity(intent);
        return true;
    }

    private void startRecord() {
        if (recorder != null) recorder.release();

        File outputFolder = new File(TakePicture.FILE_FOLDER);
        if (!outputFolder.isDirectory())
            outputFolder.mkdir();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(audioFilePath);
        //Toast.makeText(TakePicture.this, outputFile.getPath(), Toast.LENGTH_LONG).show();
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            String me = e.getMessage();
            Log.d(me, "");
        }

    }

    private void stopRecord() {
        recorder.stop();
        recorder.release();
    }

    private void startPlayback() {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            //Toast.makeText(TakePicture.this, FILE_FOLDER + "/" + fileName + ".3gpp", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayback() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }


    private Bitmap getImage(String imageName) {
        Bitmap img = null;
        MyDataBase mdb = new MyDataBase(ContentShow.this, TakePicture.DB_NAME, null, TakePicture.DB_VERSION);
        SQLiteDatabase db = mdb.getReadableDatabase();

        final String QUERY_STRING = "SELECT * FROM " + TakePicture.TABLE_NAME + " where IMAGES_NAME = '" + imageName + "'";
        Cursor cursor = db.rawQuery(QUERY_STRING, null);
        if (cursor.moveToFirst()) {
            byte[] byteArray = cursor.getBlob(1);
            img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        return img;
    }
}