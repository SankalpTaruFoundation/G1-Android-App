package org.sankalptaru.www.framework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdaptor extends BaseAdapter{
	ArrayList<String> nameList;
	Activity forestActivity;
	private LayoutInflater inflater;
	public GridAdaptor(ForestActivity forestActivity, ArrayList<String> tempList) {
		inflater=(LayoutInflater)forestActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		nameList=tempList;
		this.forestActivity=forestActivity;
	}
	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.forest_grid_item, null);
		vi.setPadding(5, 5, 5, 5);
		TextView title=((TextView)vi.findViewById(R.id.treeUsername));
		title.setText(nameList.get(arg0));
		return vi;
	}
}
