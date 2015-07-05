package org.sankalptaru.www.framework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigateListAdapter extends BaseAdapter {
	private ArrayList<String> treePlanterNameList;
	private ArrayList<String> treeImageUrlList;
	private LayoutInflater inflater;
	public ImageLoader imageLoader; 
	private Activity act;
	public NavigateListAdapter(NavigateActivity navigateActivity, ArrayList<String> tempList, ArrayList<String> treeImageUrl2) {
		// TODO Auto-generated constructor stub
		this.treePlanterNameList=tempList;
		inflater=(LayoutInflater)navigateActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(navigateActivity);
		act=navigateActivity;
		treeImageUrlList=treeImageUrl2;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return treePlanterNameList.size();
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
			vi = inflater.inflate(R.layout.navigate_list_item, null);
		TextView title = (TextView)vi.findViewById(R.id.title); // title
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
		//		thumb_image.setBackgroundResource(R.drawable.tree_grid);
		if(treeImageUrlList.get(position)!=null) {
			thumb_image.setTag(treeImageUrlList.get(position));
			imageLoader.DisplayImage(treeImageUrlList.get(position), act, thumb_image);
		}
		else
			thumb_image.setBackgroundResource(R.drawable.tree_grid);

		title.setText("Navigate to "+treePlanterNameList.get(position)+" location");
		return vi;
	}

}
