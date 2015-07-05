package org.sankalptaru.www.framework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ZodiacAdaptor extends BaseAdapter {
	int []zodiacDrawable;
	String []zodiacTreeType;
	private LayoutInflater inflater;
	public ZodiacAdaptor(int[] zodiacDrawable, String[] zodiacTreeType, STProductDetails stProductDetails) {
		// TODO Auto-generated constructor stub
		this.zodiacDrawable=zodiacDrawable;
		this.zodiacTreeType=zodiacTreeType;
		inflater=(LayoutInflater)stProductDetails.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return zodiacDrawable.length;
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
			vi = inflater.inflate(R.layout.zodiac_grid, null);
		ImageView im=(ImageView)vi.findViewById(R.id.zodiacImage);
		LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT,AppUtil.getScreenWidth()*1/2);
		im.setLayoutParams(lp);
		im.setBackgroundResource(zodiacDrawable[position]);
		TextView tv=(TextView)vi.findViewById(R.id.zodiac_tree_type);
		tv.setText(zodiacTreeType[position]);
		return vi;
	}

}
