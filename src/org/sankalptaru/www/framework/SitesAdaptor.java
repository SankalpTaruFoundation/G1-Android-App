package org.sankalptaru.www.framework;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SitesAdaptor extends BaseExpandableListAdapter {

	private PlantationActivity activity;
	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> parentItems, child;


	public SitesAdaptor(ArrayList<String> parents, ArrayList<Object> childern, PlantationActivity plantationActivity) {
		this.parentItems = parents;
		this.childtems = childern;
		activity=plantationActivity;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
		this.activity = (PlantationActivity) activity;
	}


	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		child = (ArrayList<String>) childtems.get(groupPosition);
		final int groupPos=groupPosition;
		TextView textView = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group, null);
		}

		textView = (TextView) convertView.findViewById(R.id.textView1);
		textView.setText(child.get(childPosition));


		((Button)convertView.findViewById(R.id.selectSiteGroupButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.goToTaggingStep(groupPos);
			}
		});

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row, null);
		}

		//		((TextView) convertView).setText(parentItems.get(groupPosition));
		ImageView image = null;
		TextView text=null;
		text=(TextView)convertView.findViewById(R.id.expandableText);
		text.setText(parentItems.get(groupPosition));
		image = (ImageView) convertView.findViewById(R.id.expandableImage);
		
		int imageResourceId = isExpanded ? android.R.drawable.arrow_down_float :android.R.drawable.arrow_up_float;
		image.setImageResource(imageResourceId);

		image.setVisibility(View.VISIBLE);
		
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}



	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
