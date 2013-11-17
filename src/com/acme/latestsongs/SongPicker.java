package com.acme.latestsongs;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SongPicker extends ListActivity{
	
	Context mContext = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate( savedInstanceState );
	    setContentView( R.layout.activity_songs_menu );
	    
	    mContext = this;
	    
	    Bundle mExtra = this.getIntent().getExtras();
	    Data mData = (Data) mExtra.get( "data" );
	    
	    Drawable image = null;
	    
	    this.setTitle( mData.movieName );
	    String url = mData.movieUrl;
	    
	    image = Drawable.createFromPath(Environment.getExternalStorageDirectory().getPath() + "/com.apploft.songs/" + mData.movieName + ".png");
		
	    final HashMap<String, String> mMovieData = new HashMap<String, String>();
	    
	    try {
			List<String> htmlString = new Songs().execute( url ).get();
			
			for( String inputLine : htmlString )
			{
				if( inputLine.contains( "<div class=\"song-title\"><p><b><a href=\"" ) )
				{
					String tempString = inputLine.substring( inputLine.indexOf( "<div class=\"song-title\"><p><b><a href=\"" ) + 39 );
					MovieData temData = new MovieData();
					int urlEndIndex = tempString.indexOf( "\"" );
					temData.url = tempString.substring( 0, urlEndIndex );
					tempString = tempString.substring( urlEndIndex + 2 );
					temData.title = tempString.substring( 0, tempString.indexOf( "</a>"));
					mMovieData.put( temData.title, temData.url );
				}
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	    
	    int size = mMovieData.size();
	    Drawable[] images = new Drawable[size];
	    
	    List<Data> lData = new ArrayList<Data>();
	    
	    for( int i = 0; i  < size; i ++ ){
	    	images[i] = image;
	    	lData.add(mData);
	    }
	    String mTitles[] = getTitles( mMovieData );
		//this.setListAdapter(new ArrayAdapter<String>( this, R.layout.sub_list_layout, R.id.subtextName, mTitles ));
		
		//CircularListAdapter circularAdapter = new CircularListAdapter((BaseAdapter) this.getListAdapter());
		
		

        CustomArrayAdapter mAdapter = new CustomArrayAdapter( this, R.layout.sub_list_layout, mTitles, images, lData );
		
		setListAdapter( mAdapter );
		
		ListView mListView = getListView();
		//mListView.setAdapter(circularAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> mAdapter, View mView, int pos,
					long longint ) {
				String mItem = (String) mAdapter.getItemAtPosition( pos );
				
				String url = mMovieData.get( mItem );
				new DownloadFileFromURL().execute( url );				
			}
		});
	}

	private String[] getTitles(HashMap<String, String> mMovieData) {
		Set<String> titleSet = mMovieData.keySet();
		
		return titleSet.toArray( new String[titleSet.size()]);
	}
	
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

	    /**
	     * Before starting background thread Show Progress Bar Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        

	        Toast mToast = Toast.makeText( mContext, "Downloading" , Toast.LENGTH_LONG );
	        mToast.show();
	    }

	    /**
	     * Downloading file in background thread
	     * */
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        try {
	            String filename = f_url[0].substring( f_url[0].lastIndexOf( "/" ) );
	            
	            URL url = new URL(f_url[0].replaceAll(" ", "%20") );
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            
	            

	            // this will be useful so that you can show a tipical 0-100%
	            // progress bar
	            int lenghtOfFile = conection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream() );

	            // Output stream
	            OutputStream output = new FileOutputStream(Environment
	                    .getExternalStorageDirectory().toString()
	                    + "/download/" + filename );

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
	                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

	                // writing data to file
	                output.write(data, 0, count);
	            }

	            // flushing output
	            output.flush();

	            // closing streams
	            output.close();
	            input.close();

	        } catch (Exception e) {

	            String filename = e.getMessage().substring( e.getMessage().lastIndexOf( "/" ) );
	            
	            URL url;
				try {
					url = new URL(e.getMessage().replaceAll(" ", "%20") );
		            URLConnection conection = url.openConnection();
		            conection.connect();
		            
		            InputStream input = new BufferedInputStream(url.openStream() );
		
		            // Output stream
		            OutputStream output = new FileOutputStream(Environment
		                    .getExternalStorageDirectory().toString()
		                    + "/download/" + filename );
		
		            byte data[] = new byte[1024];
		
		            //long total = 0;
		
		            while ((count = input.read(data)) != -1) {
		                //total += count;

		                // writing data to file
		                output.write(data, 0, count);
		            }
		
		            // flushing output
		            output.flush();
		
		            // closing streams
		            output.close();
		            input.close();
				}catch( Exception ex )
				{
					System.out.println( ex.getMessage() );
				}
	        }
	        return null;
	    }

	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    @Override
	    protected void onPostExecute(String file_url) {
	        Toast mToast = Toast.makeText( mContext, "File downloaded" , Toast.LENGTH_SHORT );
	        mToast.show();

	    }
	    
	    
	}
	

}

class MovieData
{
	String title = null;
	String url = null;
}


