package com.acme.latestsongs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Environment;

class RetreiveImage extends AsyncTask<Data, Void, Void> {


    protected Void doInBackground(Data ...mData) {
    	//Drawable image = null;
    	int count;
    	//File mFile = new File(Environment.getExternalStorageDirectory().getPath() + "/com.apploft.songs" );
        try {
        	InputStream URLcontent;
        	byte data[] = new byte[1024];
			URLcontent = (InputStream) new URL(mData[0].ImageUrl).getContent();
			
			FileOutputStream fos = new FileOutputStream(
					Environment.getExternalStorageDirectory().getPath() + "/com.apploft.songs/" + mData[0].movieName + ".png");
			
			while ((count = URLcontent.read(data)) != -1) {
                fos.write(data, 0, count);
            }

            fos.flush();

            fos.close();
            URLcontent.close();

		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();       
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
