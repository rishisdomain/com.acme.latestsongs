package com.acme.latestsongs;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String>
{
	private final Activity context;
	private final String[] names;
	private final List<Data> mData;
	private final Drawable[] images;
	private final int layout;
	
	
	public CustomArrayAdapter(Activity context,	int layout, String[] lists, Drawable[] images, List<Data> mData)
	{
		super(context, R.layout.main_list_layout, lists);
		this.context = context;
		this.names = lists;
		this.images = images;
		this.mData = mData;
		this.layout = layout;
	}
	

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		Log.d("CustomArrayAdapter",String.valueOf(position));
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate( layout, null, true);
		
		TextView txtTitle = null;
		if( layout == R.layout.main_list_layout)
		{
			txtTitle = (TextView) rowView.findViewById(R.id.textName);
			//rowView.setRotation(90);
		}
		else
		{
			txtTitle = (TextView) rowView.findViewById(R.id.subtextName);
		}
			
		rowView.setTag( mData.get( position) );
//		view.setTag( position,  mData.get( position));
		
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		txtTitle.setText( names[position] );
//		imageView.setImageResource(imageIds[position]);
		
		ImageView imageView = null;
		if( layout == R.layout.main_list_layout)
			imageView  = (ImageView) rowView.findViewById(R.id.icon);
		else
			imageView = (ImageView) rowView.findViewById(R.id.subicon);
			
		imageView.setImageDrawable(images[position]);
		
		return rowView;
	}
	
}
