package com.acme.latestsongs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class Songs  extends AsyncTask<String, Void, List<String>>{

	@Override
	protected List<String> doInBackground( String... url ) {	    
	    ArrayList<String> htmlString = new ArrayList<String>();
	    
		try {			
			URL mUrl = new URL( url[0] );
		    URLConnection uc = mUrl.openConnection();

		    InputStreamReader input = new InputStreamReader(uc.getInputStream());
		    BufferedReader in = new BufferedReader(input);
		 
		    String inputLine; 
	
		    while ((inputLine = in.readLine()) != null) {
		        htmlString.add( inputLine );
		    }
		    in.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return htmlString; 
	}
}
