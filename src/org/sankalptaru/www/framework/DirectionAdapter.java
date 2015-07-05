package org.sankalptaru.www.framework;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class DirectionAdapter extends BaseAdapter {
	ArrayList<String> distanceList,durationList,textDirectionList;
	private LayoutInflater inflater;
	public DirectionAdapter(RouteMapActivity routeMapActivity,
			ArrayList<String> distanceList, ArrayList<String> durationList,
			ArrayList<String> textDirectionList) {
		// TODO Auto-generated constructor stub
		inflater=(LayoutInflater)routeMapActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.distanceList=distanceList;
		this.durationList=durationList;
		this.textDirectionList=textDirectionList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return distanceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.direct_list_item, null);

		TextView direction = (TextView)vi.findViewById(R.id.listItemDirection); // title
		TextView duration = (TextView)vi.findViewById(R.id.listItemDuration);
		TextView distance = (TextView)vi.findViewById(R.id.listItemDistance);
		direction.setText(stripHtml(textDirectionList.get(position)));
		duration.setText(durationList.get(position));
		distance.setText(distanceList.get(position));
		return vi;
	}
	public String stripHtml(String html) {
		return Html.fromHtml(html).toString();
	}

}
