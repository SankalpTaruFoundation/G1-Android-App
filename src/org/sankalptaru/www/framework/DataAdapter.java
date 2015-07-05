package org.sankalptaru.www.framework;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	private  HashMap<String, Integer> nameDrawableMap;
	private ArrayList<String> tempList;

	public DataAdapter(Activity activity, HashMap<String, Integer> nameDrawableMap, ArrayList<String> listOfLoginOrderedList) {
		super();
		this.activity = activity;
		inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.nameDrawableMap=nameDrawableMap;
		tempList=listOfLoginOrderedList;
	}
	public int getCount() {
		return nameDrawableMap.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	} 
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.list_item, null);
		TextView title = (TextView)vi.findViewById(R.id.title); // title
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
		thumb_image.setBackgroundResource(nameDrawableMap.get(tempList.get(position)));
		title.setText(tempList.get(position));
		return vi;
	}

}