package com.acme.latestsongs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class SongsMenu extends Activity {
	
	public static final String _TARGET_URL = "http://www.songspk.pk/";
	
	private HashMap<String, Data> movieMap = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs_menu);
		
		readFileFromSDCard();
	}
	
	private void readFileFromSDCard() {
		  String extstorage = Environment.getExternalStorageDirectory().getPath();
		  
		  File file = null;
		  
		  extstorage += "/com.apploft.songs";
		  
		  file = new File( extstorage );
		  
		  if( !file.exists( ))
		  {
			  file.mkdir();
		  }
		  
		  extstorage += "/1.cache";
		  file = new File( extstorage );
		  
		  if (!file.exists()) {
		    try {
				file.createNewFile();
				refreshCache( file );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		  
		  createMap(file);
		  updateMapAndCache(file);
		  
		  
	}

	private void updateMapAndCache(File file) {
		List<Data> mData = null;
		try {
			List<String> htmlString = new Songs().execute( _TARGET_URL ).get();
			Vector<String> inputLines = new Vector<String>();
			for( String inputLine: htmlString )
			{
				if( inputLine.contains( "leftrightslide" ) && inputLine.contains( "='<a href=\"" ))
				{
					inputLines.add( inputLine );
				}
			}
			mData = processList( inputLines );
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		if( mData == null )
			return;
		
		try {
			FileWriter fw = new FileWriter(file, true);
		
			for( int inx = 0; inx < mData.size(); inx++ )
			{
				if( !movieMap.containsKey(mData.get(inx).movieName))
				{
					String filePush = mData.get(inx).movieName;
					filePush += "||" + mData.get(inx).ImageUrl;
					filePush += "||" + mData.get(inx).movieUrl + "\n";
					
					fw.write(filePush);
					
					movieMap.put(mData.get(inx).movieName, mData.get(inx));
					
					try {
						new RetreiveImage().execute( mData.get(inx) ).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createMap(File file) {
		movieMap = new HashMap<String, Data>();
		
		try {
			FileReader reader = new FileReader( file );
			BufferedReader breader = new BufferedReader( reader );
			
			String readline = null;

	         while ((readline = breader.readLine()) != null) 
	         {
	        	Data mData = null;
				mData = tokenize( readline );
				movieMap.put( mData.movieName, mData );
	         }
	         
	         breader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Data tokenize(String readline) {
		Data mData = new Data(); 
		StringTokenizer mTokenizer = new StringTokenizer( readline, "||");
		
		mData.movieName = mTokenizer.nextToken();
		mData.movieUrl = mTokenizer.nextToken();
		mData.ImageUrl = mTokenizer.nextToken();
		
		return mData;
	}

	private void refreshCache(File file) {
		List<Data> mData = null;
		try {
			List<String> htmlString = new Songs().execute( _TARGET_URL ).get();
			Vector<String> inputLines = new Vector<String>();
			for( String inputLine: htmlString )
			{
				if( inputLine.contains( "leftrightslide" ) && inputLine.contains( "='<a href=\"" ))
				{
					inputLines.add( inputLine );
				}
			}
			mData = processList( inputLines );
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		if( mData == null )
			return;
		try {
			FileWriter fw = new FileWriter(file);
		
			for( int inx = 0; inx < mData.size(); inx++ )
			{
				String filePush = mData.get(inx).movieName;
				filePush += "||" + mData.get(inx).ImageUrl;
				filePush += "||" + mData.get(inx).movieUrl + "\n";
				
				fw.write(filePush);
				
				try {
					new RetreiveImage().execute( mData.get(inx) ).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	private static List<Data> processList(Vector<String> leftrightslide) {
		int size = leftrightslide.size();
		ArrayList<Data> songsdata = new ArrayList<Data>( );
		
		for( int inx = 0; inx < size; inx++ )
		{
			String line = leftrightslide.get( inx );
			Data mData = new Data();
			
			mData.movieName = line.substring( line.lastIndexOf( "alt=" ) + 5, line.length() - 16 ).trim();
			mData.ImageUrl = line.substring( line.lastIndexOf( "<img src=\"" ) + 10, line.length() - 16 );
			mData.ImageUrl = mData.ImageUrl.substring(0, mData.ImageUrl.indexOf('\"'));
			
			mData.movieUrl = line.substring( line.lastIndexOf( "<a href=\"" ) + 9, line.length() - 16 );
			mData.movieUrl = mData.movieUrl.substring( 0, mData.movieUrl.indexOf('\"'));
			mData.movieUrl = SongsMenu._TARGET_URL + mData.movieUrl ;
			
			songsdata.add( mData );
		}
		return songsdata;
	}

}

class Data implements Serializable
{
	private static final long serialVersionUID = 6195232489233661992L;
	String movieName = null;
	String movieUrl = null;
	String ImageUrl = null;
}
