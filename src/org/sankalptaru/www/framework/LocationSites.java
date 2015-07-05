package org.sankalptaru.www.framework;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LocationSites extends BaseAdapter {
	ArrayList<JSONObject> locationsJsonObjectList;
	private LayoutInflater inflater;
	private int screenSize;
	private STProductDetails stProductDetails;
	public LocationSites(ArrayList<JSONObject> locationsJsonObjectList, STProductDetails stProductDetails, int i) {
		// TODO Auto-generated constructor stub
		this.locationsJsonObjectList=locationsJsonObjectList;
		inflater=(LayoutInflater)stProductDetails.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screenSize=i;
		this.stProductDetails=stProductDetails;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return locationsJsonObjectList.size();
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
		// TODO Auto-generated method stub
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.location_grid_item, null);
		TextView title=(TextView)vi.findViewById(R.id.locTitle);
		String locName=locationsJsonObjectList.get(position).optString("location");
		title.setText(Html.fromHtml((position+1)+". "+"<u>"+locName+"</u>"));
		
		TextView desc=(TextView)vi.findViewById(R.id.locDesc);
		desc.setText(locationsJsonObjectList.get(position).optString("description"));
		ViewFlipper vf=(ViewFlipper)vi.findViewById(R.id.siteFlipper);
		vf.setAutoStart(true);
		vf.setFlipInterval(3000);
		vf.removeAllViews();
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		File dir = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY"));

		File[] files = dir.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			if(locName.contains(files[i].getName())){
				File[] innerFiles = new File(Environment.getExternalStorageDirectory(), AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY")+"/"+files[i].getName()).listFiles();
				for (int j = 0; j < innerFiles.length; j++) {
					ImageView im=new ImageView(stProductDetails);
					LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,AppUtil.getScreenWidth()*1/2);
					im.setLayoutParams(lp);
					BitmapFactory.Options bitopt=new BitmapFactory.Options();
					bitopt.inJustDecodeBounds=false;
					bitopt.inSampleSize=1;
					Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY")+files[i].getName()+"/"+innerFiles[j].getName(),bitopt), AppUtil.getScreenWidth(),  AppUtil.getScreenWidth()*1/2, true);
					im.setImageBitmap(bmp);
					vf.addView(im);
				}
				break;
			}
		}
		vf.startFlipping();
		return vi;
	}


}
