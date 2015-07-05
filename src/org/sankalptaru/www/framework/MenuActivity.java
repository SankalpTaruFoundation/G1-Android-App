package org.sankalptaru.www.framework;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

public class MenuActivity extends Activity implements OnClickListener,OnItemClickListener {
	private static final int UPLOAD_PROGRESS_DIALOG = 100;
	private static final int SHOW_LOCATION_CHOOSER_DIALOG = 101;
	private static final int UPLOAD_POPULATE_DIALOG = 102;
	private static final int SHOW_TREEID_DIALOG = 103;
	private static final int OPEN_DISCLAIMER_DIALOG = 104;
	private static final int PLANTATION_BUTTON_ID = 105;
	private int currentBatchUploadCounter=0;
	LayoutParams params;
	RelativeLayout parentLayout;
	Intent activityIntent;
	private int uid;
	Dialog viewDialogFirst,viewDialogSecond,viewDialogThird = null;
	private ArrayList<String>groundUserLocationIdList;
	ProgressDialog mprogressDialog,mProgressDialogPopulate;
	SharedPreferences myPrefs;
	String utilityImagesPath;
	private ArrayList<String> imagesNameList;
	private AlertDialog alertDialog;
	private ArrayList<String> listOfServiceFailImageName;
	private ArrayList<String> locationIdList;
	private ArrayList<String> locationTitleList;
	private ArrayList<Integer> locationUnplantedList;
	private ArrayList<Integer> locationPopulatedTreeCountList;
	private String currentUploadBatchReportFileName;
	private File reportTextFile=null;
	private ArrayList<String> zodiacTreeType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normalbg);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(MenuActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		Display d=getWindowManager().getDefaultDisplay();
		int screenWidth=d.getWidth();
		int screnHeight=d.getHeight();
		AppUtil.setScreenHeight(screnHeight);
		AppUtil.setScreenWidth(screenWidth);
		populateNormalUserLayout(screenWidth,screnHeight);
		if(null==groundUserLocationIdList)
			groundUserLocationIdList=new ArrayList<String>();
		else
			groundUserLocationIdList.clear();
	}

	public void populateNormalUserLayout(int screenWidth, int screnHeight){
		parentLayout=(RelativeLayout)findViewById(R.id.menuLayout);
		parentLayout.removeAllViews();
		/*int iconCommonHeight = 0;
		parentLayout.addView(AppUtil.getHeader(MenuActivity.this, params));
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				plantIconBmp=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.plant_trees), screenWidth/3, screnHeight/5, true);
				iconCommonHeight=plantIconBmp.getHeight()/4;
				params.setMargins(10, (screnHeight/10)+iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(plantIconBmp);	
			}
			else {
				loginIconBmp=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.login), screenWidth/3, screnHeight/5, true);
				params.setMargins(screenWidth-loginIconBmp.getWidth()-10, (screnHeight/10)+iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(loginIconBmp);	
			}
			dynamicImageButton.setId(i);
			dynamicImageButton.setTag(i);
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				buyEcode=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buy_ecode), screenWidth/3, screnHeight/5, true);
				params.setMargins(10, (screnHeight/2)-iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(buyEcode);	
			}
			else {
				myForest=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_forest), screenWidth/3, screnHeight/5, true);
				params.setMargins(screenWidth-loginIconBmp.getWidth()-10, (screnHeight/2)-iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(myForest);	
			}
			dynamicImageButton.setId(i+2);
			dynamicImageButton.setTag(i+2);
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 1; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				aboutUs=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.about_us), screenWidth/3, screnHeight/5, true);
				params.setMargins(10, (screnHeight-screnHeight/5)-iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(aboutUs);		
			}

			dynamicImageButton.setId(i+4);
			dynamicImageButton.setTag(i+4);
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}*/
		Bitmap bm=BitmapFactory.decodeResource(getResources(), R.drawable.about_us_initial);
		parentLayout.addView(AppUtil.getHeader(MenuActivity.this, params));
		int iconCommonHeight = bm.getHeight();
		int marginLeftRight=(screenWidth/5)-bm.getWidth()/5;
		//		int topMarginForMiddleIcon=(int) ((screnHeight+AppUtil.getHeaderLayoutParams().height)/4.2+iconCommonHeight/4.2);
		//		int topMarginForthirdRowIcon=(int) ((screnHeight+AppUtil.getHeaderLayoutParams().height)/3.2+iconCommonHeight/3.2);

		//		addViralIcon(screnHeight);
		//		addFootPrintIcon(screnHeight);

		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.BELOW, AppUtil.headerImageID);
				params.setMargins(marginLeftRight, 0, 0, 0);
				dynamicImageButton.setId(PLANTATION_BUTTON_ID);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.plant_trees);	
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.BELOW, AppUtil.headerImageID);
				params.setMargins(0,0,marginLeftRight, 0);
				dynamicImageButton.setId(i);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.co_cal);	
			}

			dynamicImageButton.setTag(i);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.setMargins(marginLeftRight, 0, 0, 0);
				params.addRule(RelativeLayout.BELOW, PLANTATION_BUTTON_ID);
				dynamicImageButton.setBackgroundResource(R.drawable.buy_ecode);	
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.BELOW, 1);
				params.setMargins(0, 0, marginLeftRight, 0);
				dynamicImageButton.setBackgroundResource(R.drawable.my_forest);	
			}
			dynamicImageButton.setLayoutParams(params);
			dynamicImageButton.setId(i+2);
			dynamicImageButton.setTag(i+2);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.setMargins(marginLeftRight, 0, 0, 0);
				params.addRule(RelativeLayout.BELOW, 2);
				dynamicImageButton.setBackgroundResource(R.drawable.refer_friend);		
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 0, marginLeftRight, 0);
				params.addRule(RelativeLayout.BELOW, 3);
				dynamicImageButton.setBackgroundResource(R.drawable.sites);	
			}
			dynamicImageButton.setId(i+4);
			dynamicImageButton.setTag(i+4);
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 2; i++) {

			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.setMargins(marginLeftRight, 0, 0, 0);
				params.addRule(RelativeLayout.BELOW, 4);
				dynamicImageButton.setBackgroundResource(R.drawable.about_us);
				dynamicImageButton.setId(11);
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 0,marginLeftRight, 0);
				params.addRule(RelativeLayout.BELOW, 5);
				dynamicImageButton.setBackgroundResource(R.drawable.login);		
				dynamicImageButton.setId(8);				
			}
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		/*LayoutInflater inflater = (LayoutInflater)      this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(R.layout.sliding_drawer,(ViewGroup) findViewById(R.layout.normalbg));
		parentLayout.addView(childLayout);*/
	}
	/*	private void addFootPrintIcon(int screnHeight) {
		ImageView footPrintIcon=new ImageView(MenuActivity.this);
		footPrintIcon.setId(AppUtil.FOOT_PRINT_ICON_ID);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT,screnHeight/10);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_BOTTOM,AppUtil.headerImageID);
		footPrintIcon.setBackgroundResource(R.drawable.footprint_calculator);
		footPrintIcon.setLayoutParams(params);
		parentLayout.addView(footPrintIcon);
		footPrintIcon.setOnClickListener(this);
	}*/

	/*	private void addViralIcon(int screnHeight) {
		ImageView viralIcon=new ImageView(MenuActivity.this);
		viralIcon.setId(AppUtil.VIRAL_ICON_ID);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT,screnHeight/10);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_BOTTOM,AppUtil.headerImageID);
		viralIcon.setBackgroundResource(R.drawable.update);
		viralIcon.setLayoutParams(params);
		parentLayout.addView(viralIcon);
		viralIcon.setOnClickListener(this);
	}*/
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch(id){
		case SHOW_LOCATION_CHOOSER_DIALOG:
			final View pickAlphabetView = getLayoutInflater().inflate(
					R.layout.pick_location_ground_operations, null);
			viewDialogFirst.setContentView(pickAlphabetView);
			ListView listView = (ListView) pickAlphabetView.findViewById(R.id.locationIdlist);
			TextView tv=(TextView)pickAlphabetView.findViewById(R.id.dialogText);
			tv.setText(getResources().getString(R.string.locationIdDialogText));
			myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
			listView.setAdapter(new LocationIdAdapter(MenuActivity.this,locationTitleList,locationUnplantedList,locationPopulatedTreeCountList,zodiacTreeType));
			listView.setOnItemClickListener(this);
			ImageView closedialog=(ImageView)pickAlphabetView.findViewById(R.id.cancelTreeDialog);
			closedialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogFirst.dismiss();
				}
			});
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		System.out.println("inside create dialog : " + id);

		switch (id) {
		case SHOW_LOCATION_CHOOSER_DIALOG:
			viewDialogFirst = new Dialog(this);
			viewDialogFirst.setCancelable(false);
			viewDialogFirst.requestWindowFeature(Window.FEATURE_NO_TITLE);
			viewDialogFirst.setContentView(R.layout.picklogintype);

			return viewDialogFirst;	

		case OPEN_DISCLAIMER_DIALOG:
			viewDialogThird = new Dialog(this);
			viewDialogThird.setCancelable(false);
			viewDialogThird.requestWindowFeature(Window.FEATURE_NO_TITLE);
			viewDialogThird.setContentView(R.layout.picklogintype);

			final View tempView = getLayoutInflater().inflate(
					R.layout.disclaimer_layout, null);
			viewDialogThird.setContentView(tempView);
			TextView disc=(TextView)tempView.findViewById(R.id.disclaimer);
			disc.setText(getResources().getString(R.string.carbonCalculatordisclaimer));
			Button openFootPrint=(Button)tempView.findViewById(R.id.openFootPrint);
			openFootPrint.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogThird.dismiss();
					activityIntent=new Intent(MenuActivity.this, FootPrintCalActivity.class);
					startActivity(activityIntent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
			});
			ImageView close=(ImageView)tempView.findViewById(R.id.close);
			close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogThird.dismiss();
				}
			});
			return viewDialogThird;

		case UPLOAD_PROGRESS_DIALOG:
			mprogressDialog = new ProgressDialog(this);
			mprogressDialog.setMessage("Uploading Images...");
			mprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mprogressDialog.setCancelable(false);
			mprogressDialog.show();
			return mprogressDialog;

		case UPLOAD_POPULATE_DIALOG:
			mProgressDialogPopulate = new ProgressDialog(this);
			mProgressDialogPopulate.setMessage("Populating Images...");
			mProgressDialogPopulate.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialogPopulate.setCancelable(false);
			mProgressDialogPopulate.show();
			return mProgressDialogPopulate;
		case SHOW_TREEID_DIALOG:
			viewDialogSecond = new Dialog(this);
			viewDialogSecond.setCancelable(false);
			viewDialogSecond.requestWindowFeature(Window.FEATURE_NO_TITLE);

			final View dialogView = getLayoutInflater().inflate(
					R.layout.enter_treeid, null);
			viewDialogSecond.setContentView(dialogView);
			final Button b=(Button)dialogView.findViewById(R.id.submitTreeId);
			Button reset=(Button)dialogView.findViewById(R.id.resetTreeId);
			final ProgressBar pb=(ProgressBar)dialogView.findViewById(R.id.loginprogress);
			final EditText e=(EditText)dialogView.findViewById(R.id.treeIDEditText);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					e.setText("");
				}
			});
			if(b.isEnabled())
				b.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						String tid=e.getText().toString().trim();
						if(tid!=null&&tid.length()>0){
							pb.setVisibility(View.VISIBLE);
							b.setEnabled(false);
							new GetTreeDetails(tid,pb,b).execute(AppUtil.getAppProperty().getProperty("MY_TREE_URL")+tid);
						}
					}
				});
			ImageView closeDialog=(ImageView)dialogView.findViewById(R.id.cancelTreeDialog);
			closeDialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogSecond.dismiss();
				}
			});
			return viewDialogSecond;		

		default:
			break;
		}
		return null;


	}
	private class GetTreeDetails extends AsyncTask<String, Integer, JSONObject>{
		String tid;
		ProgressBar pb;
		Button b;
		public GetTreeDetails(String tid, ProgressBar pb, Button b) {
			// TODO Auto-generated constructor stub
			this.tid=tid;
			this.pb=pb;
			this.b=b;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsResponse = null;
			try {
				jsResponse=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//				this.cancel(true);
			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pb.setVisibility(View.INVISIBLE);
			viewDialogSecond.dismiss();
			String locationName="null";
			String treeLatString = "null",treeLngString="null";
			if(result!=null){
				try {
					locationName=result.getString("location_name");
					if(!locationName.equals("null")){
						treeLatString=result.getString("latitude");
						treeLngString=result.getString("longitude");

						if(treeLatString.equals("null")||treeLngString.equals("null"))
							createAlertMessageDialogAndBackToMenu("Map Unavailable.",MenuActivity.this);
						else{
							Intent actIntent=new Intent(MenuActivity.this, RouteMapActivity.class);
							actIntent.putExtra("treeLatitude",Double.parseDouble(treeLatString));
							actIntent.putExtra("treeLongitude", Double.parseDouble(treeLngString));
							actIntent.putExtra("callingClass", "Menu");
							startActivity(actIntent);
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						}

					}
					else{
						createAlertMessageDialogAndBackToMenu("Oops "+tid+" tree ID entered is invalid.",MenuActivity.this);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				createAlertMessageDialogAndBackToMenu("Map Unavailable.",MenuActivity.this);
			}
			b.setEnabled(true);
		}
	}
	private void createAlertMessageDialogAndBackToMenu(String textToShow, MenuActivity menuActivity) {
		// TODO Auto-generated method stub
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(menuActivity);
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
				alertDialog.dismiss();
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppUtil.catchExceptionsInActivity(MenuActivity.this.getClass().getCanonicalName());
		utilityImagesPath=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_UTIL_DIRECTORY");
		populateNormalUserLayout(AppUtil.getScreenWidth(), AppUtil.getScreenHeight());
		myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		uid=myPrefs.getInt("uid", -1);
		if(uid!=-1&&uid!=-2){
			if(myPrefs.getString("role", "normal").equals("normal")){
				populateNormalUserLayout(AppUtil.getScreenWidth(), AppUtil.getScreenHeight());
			}
			else if(myPrefs.getString("role", "normal").equals("ground")){
				populateGroundUserLayout(AppUtil.getScreenWidth(), AppUtil.getScreenHeight());
			}
			ImageButton imageButton=(ImageButton)findViewById(8);
			imageButton.setBackgroundResource(R.drawable.logout);
		}
		else if(uid==-2){
			populateGroundUserLayout(AppUtil.getScreenWidth(), AppUtil.getScreenHeight());
			SharedPreferences.Editor editor = myPrefs.edit();
			editor.putInt("uid",-1);
			editor.putString("username", "null");
			editor.putString("role", "normal");
			editor.commit();
		}
		else {
			ImageButton imageButton=(ImageButton)findViewById(8);
			imageButton.setBackgroundResource(R.drawable.login);
		}
		AppUtil.setEcodePurchased(false);
	}
	private void populateGroundUserLayout(int screenWidth, int screnHeight) {
		// TODO Auto-generated method stub
		parentLayout=(RelativeLayout)findViewById(R.id.menuLayout);
		parentLayout.removeAllViews();
		int iconCommonHeight = 0;
		parentLayout.addView(AppUtil.getHeader(MenuActivity.this, params));
		Bitmap bm=BitmapFactory.decodeResource(getResources(), R.drawable.about_us_initial);
		int marginLeftRight=(screenWidth/4)-bm.getWidth()/4;

		/*for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				plantIconBmp=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tag), screenWidth/3, screnHeight/5, true);
				iconCommonHeight=plantIconBmp.getHeight()/4;
				params.setMargins(10, (screnHeight/10)+iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(plantIconBmp);
				dynamicImageButton.setId(5+i);
				dynamicImageButton.setTag(5+i);
			}
			else {
				loginIconBmp=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.login), screenWidth/3, screnHeight/5, true);
				params.setMargins(screenWidth-loginIconBmp.getWidth()-10, (screnHeight/10)+iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(loginIconBmp);	
				dynamicImageButton.setId(1);
				dynamicImageButton.setTag(1);
			}

			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 1; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				buyEcode=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.upload), screenWidth/3, screnHeight/5, true);
				params.setMargins(10, (screnHeight/2)-iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(buyEcode);	
			}
			else {
				myForest=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_forest), screenWidth/3, screnHeight/5, true);
				params.setMargins(screenWidth-loginIconBmp.getWidth()-10, (screnHeight/2)-iconCommonHeight, 0, 0);
				dynamicImageButton.setImageBitmap(myForest);	
			}
			dynamicImageButton.setId(6);
			dynamicImageButton.setTag(6);
			dynamicImageButton.setLayoutParams(params);
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}*/
		parentLayout.addView(AppUtil.getHeader(MenuActivity.this, params));
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);

			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.BELOW, AppUtil.headerImageID);
				params.setMargins(marginLeftRight, 0, 0, 0);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.tag);	
				dynamicImageButton.setId(6+i);
				dynamicImageButton.setTag(6+i);
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.BELOW, AppUtil.headerImageID);
				params.setMargins(0, 0, marginLeftRight, 0);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.login);	
				dynamicImageButton.setId(8);
				dynamicImageButton.setTag(8);
			}
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.setMargins(marginLeftRight, 0, 0, 0);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.upload);
				dynamicImageButton.setId(7);
				dynamicImageButton.setTag(7);
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.setMargins(0, 0, marginLeftRight, 0);
				dynamicImageButton.setBackgroundResource(R.drawable.navigate);	
				dynamicImageButton.setId(9);
				dynamicImageButton.setTag(9);
				dynamicImageButton.setLayoutParams(params);
			}
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		
		for (int i = 0; i < 2; i++) {
			ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
			params=new LayoutParams(screenWidth/3, screnHeight/5);
			dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
			if(i==0){
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.BELOW,7);
				params.setMargins(marginLeftRight, marginLeftRight, 0, 0);
				dynamicImageButton.setLayoutParams(params);
				dynamicImageButton.setBackgroundResource(R.drawable.survey);
				dynamicImageButton.setId(10);
				dynamicImageButton.setTag(10);
			}
			else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.BELOW,9);
				params.setMargins(0, marginLeftRight, marginLeftRight, 0);
				dynamicImageButton.setBackgroundResource(R.drawable.log);	
				dynamicImageButton.setId(12);
				dynamicImageButton.setTag(12);
				dynamicImageButton.setLayoutParams(params);
			}
			parentLayout.addView(dynamicImageButton);
			dynamicImageButton.setOnClickListener(this);
		}
		
		/*ImageButton dynamicImageButton=new ImageButton(MenuActivity.this);
		params=new LayoutParams(screenWidth/3, screnHeight/5);
		dynamicImageButton.setBackgroundColor(Color.TRANSPARENT);
		params.addRule(RelativeLayout.BELOW,9);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		dynamicImageButton.setLayoutParams(params);
		dynamicImageButton.setBackgroundResource(R.drawable.survey);
		dynamicImageButton.setId(10);
		dynamicImageButton.setTag(10);
		dynamicImageButton.setOnClickListener(this);
		parentLayout.addView(dynamicImageButton);*/
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.e("v.getId()",""+ v.getId());
		switch (v.getId()) {
		case PLANTATION_BUTTON_ID:
			validateUser(v.getId());
			break;
		case 1:
			validateUser(v.getId());
			break;
		case 2:
			validateUser(v.getId());
			break;
		case 3:
			validateUser(v.getId());
			break;
		case 4:
			activityIntent=new Intent(MenuActivity.this, ReferFriendActivity.class);
			/*activityIntent=new Intent(MenuActivity.this, ReferFriendActivity.class);*/
			startActivity(activityIntent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);		
			break;
		case 5:
			validateUser(v.getId());
			break;
		case 6:
			validateUser(v.getId());
			break;
		case 7:
			validateUser(v.getId());
			break;
		case 8:
			goToLoginPage();
			break;
		case 9:
			validateUser(v.getId());
			break;
		case 10:
			validateUser(v.getId());
			break;
		case 11:
			activityIntent=new Intent(MenuActivity.this, Aboutus.class);
			startActivity(activityIntent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			break;
		case 12:
			activityIntent=new Intent(MenuActivity.this, AttendanceLogActivity.class);
			startActivity(activityIntent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			break;
		case AppUtil.VIRAL_ICON_ID:
			validateUser(v.getId());
			break;
		case AppUtil.FOOT_PRINT_ICON_ID:
			validateUser(v.getId());
			break;
		default:
			break;
		}
	}
	private boolean isGPSAndInternetEnabled() {
		// TODO Auto-generated method stub
		boolean isEnabled=false;
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if(!isOnline()){
			buildAlertMessageNoInternet();
		}
		else{
			if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
				buildAlertMessageNoGps("Your GPS seems to be disabled, do you want to enable it?","Warning",0);
			}
		}
		if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER )&&isOnline())
		{
			isEnabled=true;
		}
		return isEnabled;
	}
	private void buildAlertMessageNoInternet() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(MenuActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localTextView2.setText("Your internet or Wi-Fi is not connected.");
		localButton1.setText("     OK      ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

				if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
					buildAlertMessageNoGps("Your GPS seems to be disabled, do you want to enable it?","Warning",0);
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
	public void createAlertMessageDialog(String textToShow,Activity ac){
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(ac);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)ac.getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localTextView2.setText(textToShow);
		localButton1.setText("     OK      ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
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
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	private void buildAlertMessageNoGps(String textToShow, String title, final int i) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(MenuActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		localTextView2.setText(textToShow);
		localButton1.setText("     Yes      ");
		localButton2.setText("     No      ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.VISIBLE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				if(i==0){
					alertDialog.dismiss();
					startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

				}
				else if(i==1) {
					alertDialog.dismiss();
					finish();
				}
				else if (i==3) {
					alertDialog.dismiss();
					ArrayList<String> tempList=new ArrayList<String>();
					File sdCardRoot =new File(utilityImagesPath);
					for (File f : sdCardRoot.listFiles()) {
						if (f.isFile()){
							tempList.add(f.getName());
						}
					}
					if(tempList.size()>0){
						checkForCorruptImages(tempList);
						uploadTaskReportGeneration();
					}
					doUploadActions();
				}
				else{
					alertDialog.dismiss();
					myPrefs = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("uid",-1);
					editor.putString("username", "null");
					editor.putString("role", "normal");
					editor.commit();
					Toast.makeText(MenuActivity.this, "Successfully Logged out.", 1000).show();
					ImageButton imageButton=(ImageButton)findViewById(8);
					imageButton.setBackgroundResource(R.drawable.login);
				}
			}
		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
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
	protected void checkForCorruptImages(ArrayList<String> tempList) {
		// TODO Auto-generated method stub
		for (int j = 0; j < tempList.size(); j++) {
			String filepath=utilityImagesPath+tempList.get(j);
			Bitmap bm = BitmapFactory.decodeFile(filepath);
			if(null==bm){
				File corruptFile=new File(filepath);
				if(corruptFile.exists())
					corruptFile.delete();
			}
		}
	}

	private void doUploadActions() {
		// TODO Auto-generated method stub
		imagesNameList=new ArrayList<String>();
		File sdCardRoot =new File(utilityImagesPath);
		for (File f : sdCardRoot.listFiles()) {
			if (f.isFile()){
				imagesNameList.add(f.getName());
			}
		}
		listOfServiceFailImageName=new ArrayList<String>();
		for (int i = 0; i < imagesNameList.size(); i++) {
			String str=imagesNameList.get(i);
			if(str.contains("@")){
				listOfServiceFailImageName.add(str);
			}
		}

		if(imagesNameList.size()>0){
			if(listOfServiceFailImageName.size()<=0)
				uploadImageTask(imagesNameList);	
			else{
				doLockAndTagFirst();
				populateServiceFailedImages();
			}
		}
		else{
			createAlertMessageDialog("No Photo to Upload.",MenuActivity.this);
		}
	}


	private void uploadTaskReportGeneration(){
		File direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_REPORT_DIRECTORY"));

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				Log.e("created", "created");
			}

		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd@HH_mm_ss");
		Calendar cal = Calendar.getInstance();
		if(currentUploadBatchReportFileName==null)
			currentUploadBatchReportFileName=dateFormat.format(cal.getTime())+"_UploadReport.txt";
		File uploadReportTextFile=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_REPORT_DIRECTORY")+currentUploadBatchReportFileName);
		try {
			if(!uploadReportTextFile.exists()){
				uploadReportTextFile.createNewFile();
				//				BufferedWriter output = new BufferedWriter(new FileWriter(uploadReportTextFile));
				//				output.write("Testing");
				//				output.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void doLockAndTagFirst() {
		// TODO Auto-generated method stub
		acquireLock();
	}
	private void acquireLock() {
		// TODO Auto-generated method stub
		JSONObject js=new JSONObject();
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		Log.e("uid", ""+myPrefs.getInt("uid", -1));
		try {
			js.put("action", "lock");
			js.put("uid", myPrefs.getInt("uid", -1));
			Log.e("json", js.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void populateServiceFailedImages() {
		// TODO Auto-generated method stub
		showDialog(UPLOAD_POPULATE_DIALOG);
		int size=(listOfServiceFailImageName.size()<10)?listOfServiceFailImageName.size():10;
		for (int i = 0; i < size; i++) {
			String imageName=listOfServiceFailImageName.get(i);
			int uid=Integer.parseInt(imageName.split("-")[0]);
			double lat=Double.parseDouble(imageName.split("-")[1]);
			double lng=Double.parseDouble((imageName.split("-")[2]).split("_")[0]);
			int location=Integer.parseInt(((imageName.split("-")[2]).split("_")[1]).split("@")[0]);
			Log.e("data", "uid: "+uid+" lat: "+lat+" lng: "+lng+" location: "+location);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("action", "lock");
				jsonObject.put("uid", myPrefs.getInt("uid", -1));
			}
			catch(JSONException e){
				e.printStackTrace();
			}
			new PopulateCall(jsonObject,i,size,imageName,location,lat,lng,uid).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+location);
		}
	}
	class PopulateCall extends AsyncTask<String, Integer, JSONObject>{
		JSONObject jsonObject;
		int counter,listSize,location;
		String imageName;

		double latitude;
		double longitude;
		int uid;

		public PopulateCall(JSONObject jsonObject, int i, int size, String imageName, int location, double lat, double lng, int uid) {
			// TODO Auto-generated constructor stub
			this.jsonObject=jsonObject;
			this.counter=i;
			this.listSize=size;
			this.imageName=imageName;
			this.location=location;
			this.latitude=lat;
			this.longitude=lng;
			this.uid=uid;
		}
		protected void onProgressUpdate(Integer... progress) {
			mProgressDialogPopulate.setProgress(progress[0]);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			publishProgress((int) ((counter / (float) listSize) * 100));
			JSONObject jsResponse = null;
			try {
				jsResponse = new JSONObject(AppUtil.doPutJsonObject(params[0], jsonObject));
			} catch (Exception e) {

			}


			String treeId=jsResponse.optString("tid");


			if(treeId.equals("-1")){
				try {
					jsResponse=null;

					JSONObject jsPopulate=new JSONObject();

					jsPopulate.put("long", longitude);
					jsPopulate.put("lat", latitude);
					jsPopulate.put("action", "populate");
					jsPopulate.put("uid", ""+uid);
					jsResponse = new JSONObject(AppUtil.doPutJsonObject(params[0], jsPopulate));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{

				JSONObject jsLockAndTag=new JSONObject();
				jsResponse=null;
				try {
					jsLockAndTag.put("lat", latitude);
					jsLockAndTag.put("long", longitude);
					jsLockAndTag.put("action", "tag");
					jsLockAndTag.put("tid",treeId);
					jsLockAndTag.put("uid", uid);
					jsResponse = new JSONObject(AppUtil.doPutJsonObject(params[0], jsLockAndTag));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

//			Log.e("onPostexecute", ""+result.optString("tid"));

			listOfServiceFailImageName.remove(imageName);
			File photo=new File(utilityImagesPath+imageName);
			photo.renameTo(new File(utilityImagesPath+result.optString("tid")+"_"+location+".jpg"));
			if(counter==listSize-1){
				//				dismissDialog(UPLOAD_POPULATE_DIALOG);
				mProgressDialogPopulate.dismiss();
				Log.e("listOfServiceFailImageName", ""+listOfServiceFailImageName.size());
				if(listOfServiceFailImageName.size()>0){
					populateServiceFailedImages();
				}
				else{
					doUploadActions();
				}
			}	
		}
	}
	private void uploadImageTask(ArrayList<String> imagesNameList) {
		// TODO Auto-generated method stub
		showDialog(UPLOAD_PROGRESS_DIALOG);
		int size=(imagesNameList.size()<10)?imagesNameList.size():10;
		Log.e("size",""+size+" imagelist size: "+imagesNameList.size());
		for (int i = 0; i < size; i++) {
			Bitmap bm = BitmapFactory.decodeFile(utilityImagesPath+imagesNameList.get(i));
			if(bm==null){
				Toast.makeText(MenuActivity.this, "Image at location "+utilityImagesPath+imagesNameList.get(i)+" is corrupt.", 2000).show();
				continue;
			}
			Matrix mtx = new Matrix();
			mtx.postRotate(90);
			// Rotating Bitmap
			Bitmap rotatedbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
			bm.recycle();
			
			File f=new File(utilityImagesPath+imagesNameList.get(i));
			int imageSize=(int)(f.length()/1000);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			if(imageSize<50)
				rotatedbm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
			else
				rotatedbm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object   

			rotatedbm.recycle();
			
			byte[] b = baos.toByteArray(); 
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			JSONObject js=new JSONObject();
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

			int uid=myPrefs.getInt("uid", -1);
			try {
				js.put("image", encodedImage);
				js.put("action", "upload");
				js.put("tid",Integer.parseInt(imagesNameList.get(i).split("_")[0]));
				js.put("uid", uid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			new UploadImageAsyncTask(js, MenuActivity.this,encodedImage,i,size,imagesNameList,imagesNameList.get(i)).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+imagesNameList.get(i).split("_")[1].replace(".jpg", ""));
		}
	}
	/*@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case UPLOAD_DOWNLOAD_PROGRESS
		default:
			return null;
		}
	}*/

	private void goToLoginPage() {
		myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		uid=myPrefs.getInt("uid", -1);
		if(uid==-1){
			Toast.makeText(MenuActivity.this, "Login to proceed.", 1000).show();
			activityIntent=new Intent(MenuActivity.this,LoginActivity.class);
			startActivity(activityIntent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		else {
			buildAlertMessageNoGps("Are you sure you want to logout from the app?","Logout Confirmation", 2);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void validateUser(int i) {
		// TODO Auto-generated method stub
		myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		uid=myPrefs.getInt("uid", -1);
		if(uid!=-1&&i==PLANTATION_BUTTON_ID){
			goToPlantationPage();
		}
		else if (uid!=-1&&i==2) {
			goToPaymentPage();
		}
		else if (uid!=-1&&i==3) {
			goToForestPage();
		}
		else if (uid!=-1&&i==1) {
			//			Toast.makeText(MenuActivity.this, "This feature can be accesed soon in next update.", 1000).show();
			showDialog(OPEN_DISCLAIMER_DIALOG);
		}
		else if (i==6) {

			createTagModuleSelectioneDialog("Select the type of mode.", MenuActivity.this,"Online Mode", "Offline Mode",uid);
		}
		else if (uid!=-1&&i==7) {
			if(isGPSAndInternetEnabled()){
				buildAlertMessageNoGps("Note: Do not manually terminate this process untill finished as crucial data may be lost.\n\nAre you sure you want to start Upload Process?","Upload Confirmation", 3);
			}
		}
		else if (uid!=-1&&i==9) {
			goToRoutePage();
		}
		else if (uid!=-1&&i==10) {
			if(isGPSAndInternetEnabled()){
				goToSurveyPage();
			}
		}
		else if (uid!=-1&&i==5) {
			goToLocationSitesPage();
		}
		else if (uid!=-1&&i==AppUtil.VIRAL_ICON_ID) {
			goToViralUpdatesView();
		}
		else if (uid!=-1&&i==AppUtil.FOOT_PRINT_ICON_ID) {
			goToFootPrintCalView();
		}
		else{
			goToLoginPage();
		}
	}
	public void createTagModuleSelectioneDialog(String textToShow,Activity ac,String btn1Label,String btn2Label, final int uid){
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(ac);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)ac.getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localButton1.setTextSize(12);
		localButton2.setTextSize(12);
		localTextView2.setText(textToShow);
		localButton1.setText(btn1Label);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.VISIBLE);
		localButton2.setText(btn2Label);
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				if(uid!=-1){
					if(isGPSAndInternetEnabled()){
						new FetchPlanterLocations().execute(AppUtil.getAppProperty().getProperty("MY_FOREST_URL")); 
					}
				}
				else
					goToLoginPage();
			}
		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
				if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
					activityIntent=new Intent(MenuActivity.this, CameraOffline.class);
					startActivity(activityIntent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
				else{
					buildAlertMessageNoGps("Your GPS seems to be disabled, do you want to enable it?","Warning",0);

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
	public class FetchPlanterLocations extends AsyncTask<String,Integer, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(mprogressDialog!=null)
				mprogressDialog=null;
			AppUtil.initializeProgressDialog(MenuActivity.this, "Field Locations...", mprogressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsArray = null;
			try {
				jsArray=new JSONObject(AppUtil.getResponse(params[0]+uid));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsArray;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			makeLocationSpecificLists(result);
		}		
	}
	private void goToSurveyPage() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,SurveyActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void makeLocationSpecificLists(JSONObject result) {
		// TODO Auto-generated method stub
		try {
			String userPlanterRole=result.optJSONObject("user").optJSONObject("field_manual_planter").optJSONArray("und").getJSONObject(0).optString("value");
			SharedPreferences.Editor editor = myPrefs.edit();

			if(userPlanterRole.equals("1")){
				editor.putBoolean("isAutoPlanter", false);
			}
			else{
				editor.putBoolean("isAutoPlanter", true);
			}
			editor.commit();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONArray jsArray=result.optJSONArray("locations");
		Log.e("ahgv", ""+jsArray);
		if(jsArray.length()>0){
			zodiacTreeType=new ArrayList<String>();
			locationIdList=new ArrayList<String>();
			locationTitleList=new ArrayList<String>();
			locationUnplantedList=new ArrayList<Integer>();
			locationPopulatedTreeCountList=new ArrayList<Integer>();
			for (int j = 0; j < jsArray.length(); j++) {
				JSONObject js;
				try {
					js = jsArray.getJSONObject(j);
					locationIdList.add(js.getString("id"));
					locationUnplantedList.add(js.getInt("count"));
					locationPopulatedTreeCountList.add(js.getInt("populate_count"));
					locationTitleList.add(js.getString("title"));
					zodiacTreeType.add(js.getString("type"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		showDialog(SHOW_LOCATION_CHOOSER_DIALOG);
	}

	private void goToRoutePage() {
		// TODO Auto-generated method stub
		showDialog(SHOW_TREEID_DIALOG);
	}

	private void goToFootPrintCalView() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,FootPrintCalActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void goToViralUpdatesView() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,UpdateActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void goToPaymentPage() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,PaymentActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	private void goToLocationSitesPage(){
		activityIntent=new Intent(MenuActivity.this, STProductDetails.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	private void goToForestPage() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,ForestActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	private void goToPlantationPage() {
		// TODO Auto-generated method stub
		activityIntent=new Intent(MenuActivity.this,PlantationActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		dismissDialog(SHOW_LOCATION_CHOOSER_DIALOG);
		activityIntent=new Intent(MenuActivity.this, CameraActivity.class);
		myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);

		activityIntent.putExtra("SelectedlocationId", locationIdList.get(arg2).trim());
		activityIntent.putExtra("locationName", locationTitleList.get(arg2));
		activityIntent.putExtra("isAuto", myPrefs.getBoolean("isAutoPlanter", false));
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	public class UploadImageAsyncTask extends AsyncTask<String, Integer, JSONObject>{
		Activity ac;
		JSONObject js;
		int counter;
		int numberOfImages;
		ArrayList<String> imageList;
		String imageName;
		//		RelativeLayout loadingLayout;
		public UploadImageAsyncTask(JSONObject js, MenuActivity menuActivity,String encodedString, int i, int j, ArrayList<String> imagesNameList, String imageName) {
			// TODO Auto-generated constructor stub
			ac=menuActivity;
			this.js=js;
			counter=i;
			numberOfImages=j;
			this.imageList=imagesNameList;
			this.imageName=imageName;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//						loadingLayout.setVisibility(View.VISIBLE);
			Log.e("doUploadActions","doUploadActions"+" "+counter);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jsonObject = null;
			publishProgress((int) ((counter / (float) numberOfImages) * 100));
			try {
				jsonObject = new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return jsonObject;
		}
		protected void onProgressUpdate(Integer... progress) {
			mprogressDialog.setProgress(progress[0]);
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.e("hsb",""+ result);
			Log.e("doUploadActions", "Uploading of Image String Done");
			imagesNameList.remove(imageName);
			File photo=new File(utilityImagesPath+imageName);
			if(photo.exists())
				photo.delete();
			try {
				addUploadedTreeIDOnReportFile(js.getString("tid"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(counter==numberOfImages-1){
				//				dismissDialog(UPLOAD_PROGRESS_DIALOG);
				mprogressDialog.dismiss();
				//				mprogressDialog=null;
				Log.e("imagesNameList", ""+imagesNameList.size());
				if(imagesNameList.size()>0){
					uploadImageTask(imagesNameList);
				}
				else{	
					doReportUpload();
					if(null!=mprogressDialog)
						mprogressDialog.dismiss();
					if(null!=mProgressDialogPopulate)
						mProgressDialogPopulate.dismiss();
					reportTextFile=null;
					currentUploadBatchReportFileName=null;
					currentBatchUploadCounter=0;
				}
			}
		}

	}
	private void doReportUpload() {
		// TODO Auto-generated method stub
		JSONObject js=new JSONObject();
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

		int uid=myPrefs.getInt("uid", -1);
		try {

			js.put("text", convertTextFileToBase64());
			js.put("filename", currentUploadBatchReportFileName);
			js.put("action", "report");
			js.put("uid", uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new UploadReport(js).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+"23");
	}
	private class UploadReport extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js;
		public UploadReport(JSONObject js) {
			// TODO Auto-generated constructor stub
			this.js=js;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(mprogressDialog!=null)
				mprogressDialog=null;
			AppUtil.initializeProgressDialog(MenuActivity.this, "Field Locations...", mprogressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsonObject;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			Log.e("report upload result", ""+result);
		}
	}
	private String convertTextFileToBase64(){
		byte[] bytes = null;
		try {
			bytes = FileUtils.readFileToByteArray(reportTextFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String encoded = Base64.encodeToString(bytes, 0);  
		return encoded;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		buildAlertMessageNoGps("Are you sure you want to close the app?","Exit", 1);
	}

	private void addUploadedTreeIDOnReportFile(String tid) {
		// TODO Auto-generated method stub

		if(null==reportTextFile)
			reportTextFile=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_REPORT_DIRECTORY")+currentUploadBatchReportFileName);

		BufferedWriter output;
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd@HH_mm_ss");
		Calendar cal = Calendar.getInstance();

		try {
			output = new BufferedWriter(new FileWriter(reportTextFile,true));
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

			int uid=myPrefs.getInt("uid", -1);
			String username=myPrefs.getString("username", "null");
			try {
				if(currentBatchUploadCounter==0){
					output.write("Physical Plantation Report:  "+"\n \nUpload process initiated by Ground User:"+username+" User ID:"+uid+" at "+currentUploadBatchReportFileName.replaceAll("_", "-").replaceAll("@", " ").split("-UploadReport")[0]+"\n");
					output.write("\n");
					output.write("Note: Uploaded images can be seen by visiting the link: http://sankalptaru.org/pictures/hires/(treeid)_001.jpg"+"\n");
					output.write("\n");
					output.write("Index "+"  Tree ID:   "+"      Upload Time(yyyy-MM-dd HH-mm-ss)"+"\n");
				}

				currentBatchUploadCounter++;
				output.write("\n"+currentBatchUploadCounter+"."+"  Tree ID:--"+tid+"-Uploaded at:-"+dateFormat.format(cal.getTime()).replaceAll("_", "-").replaceAll("@", " "));
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
