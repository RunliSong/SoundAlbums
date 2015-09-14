package com.example.theproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Telephony.Sms.Conversations;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;





/**
 * Created by alexgao on 6/10/2014.
 * this is the record list class
 */
public class MainList extends Activity {
	// declaration of all widgets and variables.
	TextView empty;
    ListView listView;
    String[] fileName;
    List<RowItem> items;
    ImageButton takePhoto;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     	//link variables with widgets by id.     
        empty = (TextView) findViewById(R.id.emptyMessage);
        Bitmap[] pictures = loadImagesFromDataBase();//this method is declare down the bottom
        takePhoto = (ImageButton) findViewById(R.id.GoToPhoto);
        listView = (ListView) findViewById(R.id.contentList);  
        //display empty message if there is no record.
        if (pictures == null)
        	listView.setVisibility(View.INVISIBLE);
        else
        {
    	empty.setVisibility(View.INVISIBLE); 
            	       	
        items = new ArrayList<RowItem>();
        for (int i =0; i < pictures.length; i++) {
            RowItem r = new RowItem(pictures[i],fileName[i]);
            items.add(r);
        }// store all photo into a list<>.       	
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,R.layout.single_row_of_list,items);//put each record into the single row
        listView.setAdapter(adapter);//display all records
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	//this is make all of the record be able to click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = items.get(position).getPath();
                Intent intent = new Intent(MainList.this, ContentShow.class);
                intent.putExtra("FileName", path);
                startActivity(intent);
            }
        });
        }
        
        
        //this is imagebutton to goto the TakePicture activity
        takePhoto.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		Intent newintent = new Intent(MainList.this, TakePicture.class);
        		startActivity(newintent);
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
        Intent intent = new Intent(MainList.this, TakePicture.class);
        startActivity(intent);
        return true;
    }

    private Bitmap[] loadImagesFromDataBase() {
    	//this method is to load all records from database
        MyDataBase mdb = new MyDataBase(MainList.this, TakePicture.DB_NAME, null, TakePicture.DB_VERSION);
        SQLiteDatabase db = mdb.getReadableDatabase();

        final String QUERY_STRING = "SELECT * FROM " + TakePicture.TABLE_NAME;
        Cursor cursor = db.rawQuery(QUERY_STRING, null);
        Bitmap[] images = null;
        // if there is some data in the array, clear it.
        if (fileName != null) fileName = null;

        try {
            if (cursor.moveToFirst()) {
                images = new Bitmap[cursor.getCount()];
                fileName = new String[cursor.getCount()];
                do {
                    byte[] byteArray = cursor.getBlob(1);
                    images[cursor.getPosition()] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    fileName[cursor.getPosition()] = cursor.getString(2);
                }
                while (cursor.moveToNext());
            }
            else
            {
            	images = null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }

        return images;
    }


}

// A cell of a list view. Contains a image and a path.
class RowItem {
    private Bitmap img;
    private String path;

    public RowItem(Bitmap image, String path) {
        this.img = image;
        this.path = path;
    }
    public Bitmap getImage() {
        return img;
    }
    public void setImage(Bitmap image) {
        this.img = image;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public String toString() {
        return path;
    }
}

// Custom ListView adapter, includes a image and its path
class CustomListViewAdapter extends ArrayAdapter<RowItem> {

    Context context;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtPath;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_row_of_list, null);
            holder = new ViewHolder();
            holder.txtPath = (TextView) convertView.findViewById(R.id.info);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        String filePath = TakePicture.FILE_FOLDER + "/" + rowItem.getPath() + ".3gpp";
        File f = new File(filePath);
        if (f.exists()) {
            holder.txtPath.setText(rowItem.getPath());
        }
        else holder.txtPath.setText("No record found");
        holder.imageView.setImageBitmap(rowItem.getImage());

        return convertView;
    }

}

