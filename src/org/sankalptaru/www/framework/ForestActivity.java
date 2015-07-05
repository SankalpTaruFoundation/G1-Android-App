package org.sankalptaru.www.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class ForestActivity extends Activity implements OnItemClickListener,LocationListener{
	private static final int SHOW_TREE_DIALOG = 200;
	private GeoPoint TreeGeoPoint = null;
	private AlertDialog alertDialog;
	ArrayList<String>treeIdList;
	private JSONObject treeProfileJsonObject;
	private String masterUsername;
	Random rand=new Random();
	private ArrayList<Integer> randomNumberRecordList;
	private Bitmap treeBitmap;
	private LocationManager locationManager;
	private String provider;
	private String numberOfTressPlanted;
	private Dialog progressDialog;
	private double treeLat;
	private double treeLng;
	private Dialog pickAlphabetDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.forest);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(ForestActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		randomNumberRecordList=new ArrayList<Integer>();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			double lat =  (location.getLatitude());
			double lng =  (location.getLongitude());
		}
		else {
		}
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		int uid=myPrefs.getInt("uid", -1);
		new FetchForest().execute(AppUtil.getAppProperty().getProperty("MY_FOREST_URL")+""+uid);
	}
	@Override
	protected void onResume() {
		locationManager.requestLocationUpdates(provider, 500, 1, this);
		super.onResume();
		AppUtil.catchExceptionsInActivity(ForestActivity.this.getClass().getCanonicalName());

	}
	@Override
	protected void onPause() {

		super.onPause();
		locationManager.removeUpdates(this);
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch(id){
		case SHOW_TREE_DIALOG:
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// Define the criteria how to select the locatioin provider -> use
			// default
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);
			// Initialize the location fields
			String directionOftree = null;
			treeLat = 0;
			treeLng=0;
			int distanceOfTreeFromCurrentLocation = 0;
			if (location != null) {
				int lat = (int) (location.getLatitude());
				int lng = (int) (location.getLongitude());
				try {
					treeLat=Double.parseDouble(treeProfileJsonObject.getString("latitude"));
					treeLng=Double.parseDouble(treeProfileJsonObject.getString("longitude"));
					distanceOfTreeFromCurrentLocation=(int)CalculationByDistance(new GeoPoint((int)(lat * 1E6), (int)(lng * 1E6)), new GeoPoint((int)((Double.parseDouble(treeProfileJsonObject.getString("longitude"))) * 1E6), (int)(Double.parseDouble(treeProfileJsonObject.getString("longitude")) * 1E6)));
					if (location != null)
					{
						float[] arrayOfFloat = new float[3];
						TreeGeoPoint=new GeoPoint((int)(treeLat* 1E6),(int) (treeLng * 1E6));
						Location.distanceBetween(location.getLatitude(), location.getLongitude(), TreeGeoPoint.getLatitudeE6() / 1000000.0D, TreeGeoPoint.getLongitudeE6() / 1000000.0D, arrayOfFloat);
						directionOftree=headingToString2(arrayOfFloat[1]);
					}
				} catch (NumberFormatException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}
			} else {
			}

			final View pickAlphabetView = getLayoutInflater().inflate(
					R.layout.pick_tree_profile, null);
			pickAlphabetView.setBackgroundResource(R.drawable.green);
			pickAlphabetDialog.setContentView(pickAlphabetView);

			ImageView closeDialog=(ImageView)pickAlphabetView.findViewById(R.id.cancelTreeDialog);
			closeDialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pickAlphabetDialog.dismiss();
				}
			});

			Button viewMap=(Button)pickAlphabetView.findViewById(R.id.navigateFromTreeBtn);
			viewMap.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(treeLat==0||treeLng==0)
						createAlertMessageDialogAndBackToMenu("Map Unavailable.", ForestActivity.this,false);
					else{
						Intent actIntent=new Intent(ForestActivity.this, RouteMapActivity.class);
						actIntent.putExtra("treeLatitude",treeLat);
						actIntent.putExtra("treeLongitude", treeLng);
						actIntent.putExtra("callingClass", "Forest");
						startActivity(actIntent);
						overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						//					finish();
					}
				}
			});

			TextView username=(TextView)pickAlphabetView.findViewById(R.id.treeProfileUsername);
			username.setText(Html.fromHtml("<b><font color=#000000>Username</font></b>:"+ "<font color=\"#ffffff\">" + masterUsername + "</font>"));

			TextView captions=(TextView)pickAlphabetView.findViewById(R.id.treePlantedText);
			captions.setText(Html.fromHtml(		"<b><font color=#61210B>Trees Planted</font></b>:"+ "<font color=\"#61210B\">" + numberOfTressPlanted + "</font>"+"<b><font color=#000000> v/s </font></b>"+ "<font color=\"#ffffff\">" +(Integer.parseInt(numberOfTressPlanted)*1.5)+" Ton co2 Absorbed"+ "</font>"));

			TextView treeUsername=(TextView)pickAlphabetView.findViewById(R.id.treeUsername);
			//		TextView mainTreeUsername=(TextView)pickAlphabetView.findViewById(R.id.mainTreeUser);
			TextView plantedBy=(TextView)pickAlphabetView.findViewById(R.id.plantedBy);
			TextView plantedAt=(TextView)pickAlphabetView.findViewById(R.id.plantedAt);
			TextView plantedOn=(TextView)pickAlphabetView.findViewById(R.id.plantedOn);
			TextView wishlist=(TextView)pickAlphabetView.findViewById(R.id.treeWishlist);
			ImageView treeImage=(ImageView)pickAlphabetView.findViewById(R.id.treeIDImage);
			/*LocationManager manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	Location location=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//

	Log.d("data", ""+location.getLatitude()+" data  "+		location.getLongitude());*/
			try {

				if(null!=treeBitmap){

					treeImage.setImageBitmap(treeBitmap);
					//				treeImage.setVisibility(View.INVISIBLE);
					//				((RelativeLayout)pickAlphabetView.findViewById(R.id.treeContent)).setBackgroundDrawable(new BitmapDrawable(treeBitmap));
					if(distanceOfTreeFromCurrentLocation!=0&&directionOftree!=null)
						treeUsername.setText("Your plant is "+distanceOfTreeFromCurrentLocation+" Kms away in "+directionOftree+" direction from your current location.");
					//				mainTreeUsername.setText(Html.fromHtml("<b>"+treeProfileJsonObject.getString("displayname")+"</b>"));
					plantedBy.setText(Html.fromHtml("<b><font color=#000000>Planted By </font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("displayname") + "</font>"));
					plantedAt.setText(Html.fromHtml("<b><font color=#000000>Planted At</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("location_name") + "</font>"));
					plantedOn.setText(Html.fromHtml("<b><font color=#000000>Planted On</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("create_date") + "</font>"));
					wishlist.setText(Html.fromHtml("<b><font color=#000000>Tree's Wishlist</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("wishlist") + "</font>"));

				}
				else
				{
					//				treeUsername.setText(treeProfileJsonObject.getString("displayname"));
					//				mainTreeUsername.setText(Html.fromHtml("<b>"+treeProfileJsonObject.getString("displayname")+"</b>"));
					plantedBy.setText(Html.fromHtml("<b><font color=#000000>Planted By </font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("displayname") + "</font>"));
					plantedAt.setText(Html.fromHtml("<b><font color=#000000>Planted At</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("location_name") + "</font>"));
					plantedOn.setText(Html.fromHtml("<b><font color=#000000>Planted On</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("create_date") + "</font>"));
					wishlist.setText(Html.fromHtml("<b><font color=#000000>Tree's Wishlist</font></b>: "+ "<font color=\"#ffffff\">" + treeProfileJsonObject.getString("wishlist") + "</font>"));
					//				treeImage.setVisibility(View.VISIBLE);
					treeImage.setBackgroundResource(R.drawable.tree_grid);
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		System.out.println("inside create dialog : " + id);
		switch(id){
		case SHOW_TREE_DIALOG:
			pickAlphabetDialog = new Dialog(this);
			pickAlphabetDialog.setCancelable(true);
			pickAlphabetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pickAlphabetDialog.setContentView(R.layout.pick_tree_profile);
			pickAlphabetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			return pickAlphabetDialog;
		}
		return null;
	}

	public static String headingToString2(double x)
	{
		String directions[] = {"North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West", "North"};
		int index=((int)Math.round((  ((double)x % 360) / 45)));
		index=(index<0)?(-1*index):index;
		return directions[ index ];
	}

	private class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {
		ImageView treeImageView;
		public GetImageFromServer(){
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AppUtil.initializeProgressDialog(ForestActivity.this, "",progressDialog);
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				treeBitmap = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return treeBitmap; 
			//<<< return Bitmap
		}      
		@Override
		protected void onPostExecute(Bitmap result) { 
			treeBitmap=Bitmap.createScaledBitmap(result, AppUtil.getScreenWidth(),(int) (AppUtil.getScreenHeight()/2.5), true);
			AppUtil.cancelProgressDialog();
			generateRandomTreeDialog();

		}
	}
	public void generateRandomTreeDialog(){

		showDialog(SHOW_TREE_DIALOG);
	}
	private class FetchForest extends AsyncTask<String, Integer, JSONObject>{
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			AppUtil.initializeProgressDialog(ForestActivity.this,"Processing your Forest.",progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject js = null;

			try {
				js=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {

				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {

			super.onProgressUpdate(values);
			setProgress(values[0]);
		}
		@Override
		protected void onPostExecute(JSONObject result) {

			super.onPostExecute(result);

			ArrayList<String> tempList=new ArrayList<String>();
			if(null==treeIdList)
				treeIdList=new ArrayList<String>();
			else
				treeIdList.clear();
			JSONArray js=result.optJSONObject("forest").optJSONArray("results");
			for (int i = 0; i < js.length(); i++) {
				try {
					tempList.add(js.getJSONObject(i).optString("displayname"));
					treeIdList.add(js.getJSONObject(i).optString("tid"));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			addViewsOnParentContainer(tempList);
			AppUtil.cancelProgressDialog();
		}
	}
	private void addViewsOnParentContainer(ArrayList<String> tempList) {
		numberOfTressPlanted=""+tempList.size();
		if(tempList.size()>0){
			RelativeLayout parentLayout=((RelativeLayout)findViewById(R.id.forestLayout));
			parentLayout.removeAllViews();
			LayoutParams 	params= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			parentLayout.addView(AppUtil.getHeader(ForestActivity.this,params));

			ImageView backToMenu=new ImageView(ForestActivity.this);
			params= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT|RelativeLayout.ALIGN_PARENT_TOP);
			int homeIconHeight=(BitmapFactory.decodeResource(getResources(), R.drawable.home)).getHeight();
			params.setMargins(0, (int)(homeIconHeight/2.8),(int)(homeIconHeight/2.8),0);
			backToMenu.setLayoutParams(params);
			backToMenu.setBackgroundResource(R.drawable.home);
			backToMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent	in=new Intent(ForestActivity.this, MenuActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
			});
			parentLayout.addView(backToMenu);

			RelativeLayout containerLayout=new RelativeLayout(ForestActivity.this);
			params= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			containerLayout.setBackgroundColor(Color.parseColor("#70ffffff"));
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			params.addRule(RelativeLayout.BELOW,AppUtil.headerImageID);
			containerLayout.setLayoutParams(params);
			parentLayout.addView(containerLayout);
			TextView username=new TextView(ForestActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			username.setTextColor(Color.BLACK);
			username.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			//		username.setTypeface(AppUtil.getFont(ForestActivity.this));
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
			masterUsername=myPrefs.getString("username", "null");
			username.setText(Html.fromHtml("<b>"+masterUsername+"</b>"));

			username.setLayoutParams(params);
			username.setId(AppUtil.USERNAME_ID);
			username.setLayoutParams(params);
			containerLayout.addView(username);
			TextView captions=new TextView(ForestActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.BELOW, AppUtil.USERNAME_ID);
			captions.setTextColor(Color.parseColor("#61210B"));
			//		captions.setTypeface(AppUtil.getFont(ForestActivity.this));
			captions.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			captions.setText(Html.fromHtml("<b><font color=#61210B>Trees Planted</font></b>:"+ "<b><font color=\"#61210B\">" + numberOfTressPlanted + "</font></b>"+"<b><font color=#000000> v/s </font></b>"+ "<b><font color=\"#ffffff\">" +(Integer.parseInt(numberOfTressPlanted)*1.5)+" Ton co2 Absorbed"+ "</font></b>"));
			//				Html.fromHtml("<b>"+"Trees Planted:"+AppUtil.getNumberOfTreesPlanted()+ " vs "+(Integer.parseInt(AppUtil.getNumberOfTreesPlanted())*1.5)+" Ton co2 Absorbed"+"</b>"));
			captions.setLayoutParams(params);
			captions.setId(AppUtil.CAPTION_ID);
			captions.setLayoutParams(params);
			containerLayout.addView(captions);

			View line=new View(ForestActivity.this);
			line.setBackgroundColor(Color.parseColor("#61210B"));
			line.setId(AppUtil.LINE_ID);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, 4);
			params.addRule(RelativeLayout.BELOW, AppUtil.CAPTION_ID);
			line.setLayoutParams(params);
			containerLayout.addView(line);
			GridView forestGridView =new GridView(ForestActivity.this,null,R.style.goodList);
			forestGridView.setOnItemClickListener(this);
			forestGridView.setCacheColorHint(getResources().getColor(R.color.dividerColor));
			forestGridView.setId(AppUtil.GRID_VIEW_ID);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, AppUtil.LINE_ID);
			forestGridView.setLayoutParams(params);
			params.setMargins(15, 15,15, 15);
			forestGridView.setNumColumns(3);
			containerLayout.addView(forestGridView);
			forestGridView.setAdapter(new GridAdaptor(ForestActivity.this,tempList));
			setProgressBarVisibility(false);
		}
		else{
			createAlertMessageDialogAndBackToMenu("Plant trees to see your forest..!!", ForestActivity.this,true);
		}
	}
	private void createAlertMessageDialogAndBackToMenu(String textToShow,final ForestActivity ac, final boolean b) {
		// TODO Auto-generated method stub
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(ac);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Warning");
		localTextView2.setText(textToShow);
		localButton1.setText("     OK     ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				if(b){
					Intent activityIntent=new Intent(ac, MenuActivity.class);	
					startActivity(activityIntent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);	
					alertDialog.dismiss();
					finish();
				}
				else{
					alertDialog.dismiss();
				}
			}
		});
		try
		{
			this.alertDialog = localBuilder.create();
			this.alertDialog.setView(localLinearLayout, 0, 0, 0, 0);
			this.alertDialog.setInverseBackgroundForced(true);
			this.alertDialog.show();
			return;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}

	}
	@Override
	public void onItemClick(AdapterView<?> parentView, View arg1, int position, long id) {

		if(null!=treeBitmap){
			treeBitmap.recycle();
			treeBitmap=null;
		}
		new FetchTreeDetails().execute(AppUtil.getAppProperty().getProperty("MY_TREE_URL")+""+treeIdList.get(position));
	}
	private class FetchTreeDetails extends AsyncTask<String, Integer, JSONObject>{
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			AppUtil.initializeProgressDialog(ForestActivity.this,"Loading..",progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject js = null;
			try {
				js=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {

				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONObject result) {

			super.onPostExecute(result);
			treeProfileJsonObject=result;
			AppUtil.cancelProgressDialog();
			JSONArray imageArray=result.optJSONArray("images");
			if(imageArray.length()>0){
				try {
					new GetImageFromServer().execute(AppUtil.getAppProperty().getProperty("TREE_IMAGE_URL")+imageArray.getString(0));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			else{
				treeBitmap=null;
				generateRandomTreeDialog();
			}
		}
	}
	public double CalculationByDistance(GeoPoint StartP, GeoPoint EndP) {  
		double lat1 = StartP.getLatitudeE6()/1E6;  
		double lat2 = EndP.getLatitudeE6()/1E6;  
		double lon1 = StartP.getLongitudeE6()/1E6;  
		double lon2 = EndP.getLongitudeE6()/1E6;  
		double dLat = Math.toRadians(lat2-lat1);  
		double dLon = Math.toRadians(lon2-lon1);  
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +  
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *  
				Math.sin(dLon/2) * Math.sin(dLon/2);  
		double c = 2 * Math.asin(Math.sqrt(a));  
		return 6371 * c;  
	}  
	@Override
	public void onBackPressed() {

		super.onBackPressed();
		if(null!=treeBitmap){
			treeBitmap.recycle();
			treeBitmap=null;
		}
		// TODO Auto-generated method stub
		//			super.onBackPressed();
		Intent	in=new Intent(ForestActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
		if(null!=treeBitmap){
			treeBitmap.recycle();
			treeBitmap=null;
		}
	}
	@Override
	public void onLocationChanged(Location location) {

		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
	}
	@Override
	public void onProviderDisabled(String provider) {


	}
	@Override
	public void onProviderEnabled(String provider) {


	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {


	}
}