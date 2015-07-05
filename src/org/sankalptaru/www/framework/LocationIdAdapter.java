package org.sankalptaru.www.framework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocationIdAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater=null;
	private ArrayList<String> locationTitleList;
	private ArrayList<Integer> locationUnplantedList;
	private ArrayList<Integer> locationPopulatedTreeCount;
	private ArrayList<String> zodiacTreeType;
	public LocationIdAdapter(MenuActivity activity2, ArrayList<String> locationTitleList, ArrayList<Integer> locationUnplantedList, ArrayList<Integer> locationPopulatedTreeCount, ArrayList<String> zodiacTreeType) {
		// TODO Auto-generated constructor stub
		super();
		this.activity = activity2;
		inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.locationTitleList=locationTitleList;
		this.locationUnplantedList=locationUnplantedList;
		this.locationPopulatedTreeCount=locationPopulatedTreeCount;
		this.zodiacTreeType=zodiacTreeType;
	}
	public int getCount() {
		return locationTitleList.size();
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
			vi = inflater.inflate(R.layout.location_list_item, null);
		TextView title = (TextView)vi.findViewById(R.id.groundLocationIdTextView);
		if(zodiacTreeType.get(position).length()<=0)
			title.setText(locationTitleList.get(position)+"\nUnplanted Count:"+locationUnplantedList.get(position)+"\nPlanted Count:"+locationPopulatedTreeCount.get(position));
		else
			title.setText(locationTitleList.get(position)+"\nTree Type: "+zodiacTreeType.get(position)+"\nUnplanted Count:"+locationUnplantedList.get(position)+"\nPlanted Count:"+locationPopulatedTreeCount.get(position));		
		return vi;
	}
}