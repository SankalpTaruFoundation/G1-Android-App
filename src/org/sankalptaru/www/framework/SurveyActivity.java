package org.sankalptaru.www.framework;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyActivity extends Activity implements LocationListener,OnItemClickListener{
	private static final int LOCATION_LABEL_ID = 1;
	private static final int LOCATION_NAME_EDITTEXT_ID = 2;
	private static final int CAMERA_PREVIEW_ID = 3;
	private static final int UPDATE_LOCATION_LABEL_ID = 4;
	private static final int RADIO_GROUP_ID = 5;
	private static final int SCROLL_VIEW_ID = 6;
	private static final int OPEN_CAMERA_BTN_ID = 7;
	private static final int SPONSORS_DIALOG_ID = 100;
	private static final int LOCATION_BTN_ID = 8;
	private static final int CAPTURE_BTN_ID = 9;
	private static final int EMAIL_LABEL_ID = 10;
	private static final int EMAIL_EDITTEXT_ID = 11;
	private static final int SHARE_BTN_ID = 12;
	private static final int COMMENT_LABEL_ID = 13;
	private static final int COMMENT_EDITTEXT_ID = 14;
	LocationManager locationManager = null;
	private AlertDialog alertDialog;
	private Dialog progressDialog;
	private RelativeLayout surveyParentLayout;
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	private Camera camera=null;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
	private MediaPlayer shutterSound;
	private boolean isCameraShown=false;
	private String 		lid;
	private String currentSponsor="";
	private boolean isRecordModuleEnabled=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_layout);
		surveyParentLayout=(RelativeLayout)findViewById(R.id.sureveyParentLayout);
		initiatePropertyFiles();

		createAlertMessageDialog("Which type of survey you want to do?", SurveyActivity.this,getCurrentLocationOfDevice(),0,"Sponsors Survey", "Record Locations",null);
	}
	private Location getCurrentLocationOfDevice(){
		this.locationManager = ((LocationManager)getSystemService("location"));
		if (this.locationManager != null)
			this.locationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location =null;
		Location gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location networkLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(null!=gpslocation){
			location=gpslocation;
		}
		else if(null!=networkLocation){
			location=networkLocation;
		}
		else{
			location=locationManager.getLastKnownLocation(provider);
		}
		return location;
	}
	private void initiatePropertyFiles() {
		// TODO Auto-generated method stub
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(SurveyActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (this.locationManager != null)
			this.locationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
	}
	public void createDialog(String textToShow,final String title,String neutralButtonText,String positiveButtonText,final boolean doExit, boolean showSecodButton,final boolean takePicture, final String locationId,boolean manipulateString,String optionalButtonText, final ArrayList<String> locationNameList, final ArrayList<String> locationIdList){

		AlertDialog.Builder localBuilder = new AlertDialog.Builder(SurveyActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		Button localButton3 = (Button)localLinearLayout.findViewById(R.id.btnOptional);
		ImageView orImage = (ImageView)localLinearLayout.findViewById(R.id.orImage);
		orImage.setVisibility(View.GONE);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		if(manipulateString){
			orImage.setVisibility(View.VISIBLE);
			localButton3.setVisibility(View.VISIBLE);
			textToShow=textToShow.replaceAll("\n", "<br>");
			localTextView2.setText(Html.fromHtml("<b><font color=#000000>"+textToShow+"</font></b>"));
		}else{
			localButton3.setVisibility(View.GONE);
			localTextView2.setText(textToShow);
		}
		localButton1.setText("     "+positiveButtonText+"     ");
		localButton2.setText("     "+neutralButtonText+"     ");
		localButton3.setText("     "+optionalButtonText+"     ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		if (showSecodButton) {
			localButton2.setVisibility(View.VISIBLE);
		}
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				if(doExit){
					backToMenu();
				}
				else if(!doExit&&!takePicture){
					populateCustomLocationForm();
				}
				else{
					showCamera(locationId);
				}
			}
		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				backToMenu();
			}
		});
		localButton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				populateExistingLocationLayout(locationNameList,locationIdList);
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
	protected void populateExistingLocationLayout(ArrayList<String> locationNameList, ArrayList<String> locationIdList) {
		// TODO Auto-generated method stub
		LayoutParams params;
		surveyParentLayout.removeAllViews();

		TextView label=new TextView(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		label.setId(UPDATE_LOCATION_LABEL_ID);
		label.setLayoutParams(params);
		label.setText("Select Location:");
		label.setTypeface(Typeface.DEFAULT_BOLD);
		label.setTextColor(getResources().getColor(R.color.StTheme));
		label.setTextSize(16);
		surveyParentLayout.addView(label);



		Button createLocBtn=new Button(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//		params.addRule(RelativeLayout.BELOW, SCROLL_VIEW_ID);
		params.setMargins(0, 10, 0,0);
		createLocBtn.setId(OPEN_CAMERA_BTN_ID);
		createLocBtn.setLayoutParams(params);
		createLocBtn.setBackgroundResource(R.drawable.redbutton);
		createLocBtn.setText("Open Camera");
		createLocBtn.setTextColor(Color.WHITE);
		createLocBtn.setTypeface(Typeface.DEFAULT_BOLD);
		surveyParentLayout.addView(createLocBtn);
		createLocBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((RadioButton)findViewById(((RadioGroup)findViewById(RADIO_GROUP_ID)).getCheckedRadioButtonId()))!=null){
					String locID=((RadioButton)findViewById(((RadioGroup)findViewById(RADIO_GROUP_ID)).getCheckedRadioButtonId())).getTag().toString();
					showCamera(locID);
				}
				else{
					Toast.makeText(SurveyActivity.this, "Select a location to continue.", 1000).show();
				}

			}
		});

		ScrollView scrlView=new ScrollView(SurveyActivity.this,null,R.style.goodList);
		scrlView.setScrollBarStyle(R.style.goodList);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, UPDATE_LOCATION_LABEL_ID);
		params.addRule(RelativeLayout.ABOVE, OPEN_CAMERA_BTN_ID);
		scrlView.setLayoutParams(params);
		scrlView.setId(SCROLL_VIEW_ID);
		surveyParentLayout.addView(scrlView);

		RadioGroup rdGrp=new RadioGroup(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		rdGrp.setId(RADIO_GROUP_ID);
		scrlView.addView(rdGrp);

		for (int i = 0; i < locationNameList.size(); i++) {
			RadioButton rdBtn=new RadioButton(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rdBtn.setLayoutParams(params);
			rdBtn.setId(Integer.parseInt(locationIdList.get(i)));
			rdBtn.setText(locationNameList.get(i));
			rdBtn.setTag(locationIdList.get(i));
			rdBtn.setTypeface(Typeface.DEFAULT_BOLD);
			rdBtn.setTextColor(Color.BLACK);
			rdGrp.addView(rdBtn);
		}

	}
	protected void showCamera(String locationId) {
		// TODO Auto-generated method stub
		isCameraShown=true;
		createLocationFolderInSDCard();
		surveyParentLayout.removeAllViews();
		shutterSound= MediaPlayer.create(SurveyActivity.this, R.raw.camera_click);
		lid=locationId;
		initiateCamera(locationId);
	}

	private void doCaptureRecordedPhoto() {
		// TODO Auto-generated method stub
		testIfTextFileExists();
		if (inPreview) {
			shutterSound.start();
			camera.takePicture(null, null, photoCallbackSecond);
			inPreview=false;
		}
	}

	Camera.PictureCallback photoCallbackSecond=new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			String path=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY")+"Images/";
			File dir=new File(path);
			if(!dir.exists()){
				if(dir.mkdirs()){
					//					Log.e("image direc created", "image direc created");
				}
			}
			new SaveRecordedImageToSDCard(path).execute(data);
			camera.startPreview();
			inPreview=true;
			capture.setVisibility(View.INVISIBLE);
			//			populateRecordLayout();
		}
	};

	public void doCapturePhoto(){
		if (inPreview) {
			shutterSound.start();
			camera.takePicture(null, null, photoCallback);
			inPreview=false;
		}
	}
	Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			String utilityImagesPath=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_LOCATION_DIRECTORY");
			new SavePhoto(utilityImagesPath).execute(data);
			camera.startPreview();
			inPreview=true;
		}
	};
	private Dialog viewDialogFirst;
	private HashMap<String, String> sponsorsIdNameMapList;
	private ArrayList<String> tempList;
	private boolean isRecordFolderNotCreated=true;
	private String currentUploadBatchReportFileName=null;
	private int currentBatchUploadCounter=0;
	private String recordedImageName=null;
	private String localTextFileName;
	private ImageView capture;
	private ArrayList<String> imagesTempList;
	private TextView emailLabel;
	private EditText emailEditText;
	private TextView label;
	private EditText editText;
	private Button createLocBtn;
	private Button shareBtn;
	private Button sendBtn;
	private TextView commentLabel;
	private EditText commentEditText;
	private boolean normalSurveyEnabled=false;
	private class SaveRecordedImageToSDCard extends AsyncTask<byte[], String, String>{
		String path;
		public SaveRecordedImageToSDCard(String path){
			this.path=path;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected String doInBackground(byte[]... params) {
			
			File photo=
					new File(path+"/"+recordedImageName+".jpg");
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());
				writeTextOnPicture(params[0],recordedImageName).compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			}
			catch (java.io.IOException e) {
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			addDataToTextFile(true,false);
		}
	}
	
	Bitmap writeTextOnPicture(byte[] data, String imageName){
		BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
        
		normalSurveyEnabled= true;
		
        Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        Matrix mtx = new Matrix();
		mtx.postRotate(90);
		// Rotating Bitmap
        mBitmap = mBitmap.copy(Bitmap.Config.RGB_565, true);
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mtx, true);
       
        try{
        
        Canvas mCanvas = new Canvas(mBitmap);
        
        Size mSize = camera.getParameters().getPictureSize();
        
        if(options.inSampleSize == 2){
        	mSize.width = mSize.width/2;
        	mSize.height = mSize.height/2;
        }
        
        int infoWindowOffsetX =  5;
        
        RectF infoWindowRect = new RectF(0, infoWindowOffsetX, mSize.height-1, 30);
        
        Paint textBkgPaint = new Paint();
        textBkgPaint.setARGB(225, 75, 75, 75); //gray
        textBkgPaint.setAntiAlias(true);
        
        // Draw inner info window
		mCanvas.drawRoundRect(infoWindowRect, 5, 5, textBkgPaint);
		
		Paint windowBorder = new Paint();
		windowBorder.setARGB(255, 255, 255, 255);
		windowBorder.setAntiAlias(true);
		windowBorder.setStyle(Paint.Style.STROKE);
		windowBorder.setStrokeWidth(2);
		
	    // Draw border for info window
		mCanvas.drawRoundRect(infoWindowRect, 5, 5, windowBorder);
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(12);
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.LEFT);
		
		
		//Draw trip name, text Size = 12
		String imgName = "Location Name : "+ imageName;
		mCanvas.drawText(imgName, 0, imgName.length(), 2, infoWindowOffsetX+3+12, textPaint);
		
		
		mBitmap = mBitmap.copy(Bitmap.Config.RGB_565, false);
		
//		this.viewImage = mBitmap;
//		storePicture(mBitmap);
		
        }catch(Exception e){
        	Log.e("Survey Activity", "Capture Picture : Exception while drawing text on img" + e.toString());
        	mBitmap = mBitmap.copy(Bitmap.Config.RGB_565, false);
//        	this.viewImage = mBitmap;
//        	storePicture(mBitmap);
        }
        
        return mBitmap;
	}

	private void addDataToTextFile(boolean isImageCaptured,boolean isComments) {
		// TODO Auto-generated method stub
		File reportTextFile=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY")+currentUploadBatchReportFileName);

		BufferedWriter output;
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd@HH_mm_ss");
		Calendar cal = Calendar.getInstance();

		try {
			output = new BufferedWriter(new FileWriter(reportTextFile,true));
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
			String currentCapturedString=dateFormat.format(cal.getTime()).replaceAll("_", "-").replaceAll("@", " ")+"_Coordinates.txt";
			int uid=myPrefs.getInt("uid", -1);
			String username=myPrefs.getString("username", "null");
			if(isComments){
				output.write("\n");
				output.write("Comments: "+commentEditText.getText().toString()+"\n");
				output.write("\n");
			}else{
				try {
					if(currentBatchUploadCounter==0){
						output.write("\n");
						output.write("\n");
						output.write("Coordinates Capture Report:  "+"\n \nThis process was initiated by Ground User:"+username+" User ID:"+uid+" at "+currentCapturedString.replaceAll("_", "-").replaceAll("@", " ").split("-Coordinates")[0]+"\n");
						output.write("\n");
						//					output.write("Note: Uploaded images can be seen by visiting the link: http://sankalptaru.org/pictures/hires/(treeid)_001.jpg"+"\n");
						output.write("\n");
						output.write("Index "+"  Location Name:   "+"           Latitude           "+"           Longitude           "+"           ImageUrl           "+"\n");
					}
					Location loc=getCurrentLocationOfDevice();
					currentBatchUploadCounter++;
					if(isImageCaptured){
						/*String stServerFolderName=null;
						if(null!=localTextFileName)
							stServerFolderName=localTextFileName.replaceAll("Coordinates.txt", "").replaceAll("@", "_").replaceAll("_", "");
						else
							testIfTextFileExists();*/
						String stServerFolderName=localTextFileName.replaceAll("Coordinates.txt", "").replaceAll("@", "_").replaceAll("_", "");
						output.write("\n"+currentBatchUploadCounter+"."+"   "+recordedImageName+" -- "+loc.getLatitude()+" -- "+loc.getLongitude()+" -- "+"http://sankalptaru.org/coordinate_reports/"+stServerFolderName+"/"+recordedImageName+".jpg");
						//					Log.e("vsah", ""+stServerFolderName);
					}
					else
						output.write("\n"+currentBatchUploadCounter+"."+"   "+recordedImageName+" -- "+loc.getLatitude()+" -- "+loc.getLongitude());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			output.write("\n");
			output.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private class SavePhoto extends AsyncTask<byte[], String, String>{
		private String utilityImagesPath;
		public SavePhoto(String utilityImagesPath) {
			// TODO Auto-generated constructor stub
			this.utilityImagesPath=utilityImagesPath;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(utilityImagesPath+"/"+lid+".jpg");
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			}
			catch (java.io.IOException e) {
			}

			return(null);
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			doPreUploadActions(utilityImagesPath+"/"+lid+".jpg");
		}
	}
	private class UploadImage extends AsyncTask<String, Integer, JSONObject>{
		JSONObject json;
		public UploadImage(JSONObject js) {
			// TODO Auto-generated constructor stub
			this.json=js;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject js=null;
			try {
				js=new JSONObject(AppUtil.doPutJsonObject(params[0],json));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			createDialog("Location image successfully uploaded to server.", "Information", "", "OK", true, false, false, "",false,"",null,null);
		}
	}
	private void initiateCamera(String locationId) {
		// TODO Auto-generated method stub
		surveyParentLayout.removeAllViews();
		LayoutParams params;

		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.surveyHeader);
		params.setMargins(0, 0, 0, 0);
		surveyParentLayout.setLayoutParams(params);

		preview=new SurfaceView(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		preview.setId(CAMERA_PREVIEW_ID);
		preview.setLayoutParams(params);
		surveyParentLayout.addView(preview);

		RelativeLayout clickLayout=new RelativeLayout(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		clickLayout.setLayoutParams(params);
		clickLayout.setBackgroundColor(Color.parseColor("#40000000"));

		capture=new ImageView(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		capture.setBackgroundResource(R.drawable.ic_menu_camera);
		capture.setLayoutParams(params);
		capture.setId(CAPTURE_BTN_ID);
		clickLayout.addView(capture);
		capture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isRecordModuleEnabled){
					doCaptureRecordedPhoto();
				}else{
					doCapturePhoto();
				}
			}
		});
		surveyParentLayout.addView(clickLayout);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if (camera == null) {
			camera=Camera.open();
		}

		startPreview();
		if(isRecordModuleEnabled){

			label=new TextView(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			label.setId(LOCATION_LABEL_ID);
			label.setLayoutParams(params);
			label.setText("Enter Location Name:");
			label.setTypeface(Typeface.DEFAULT_BOLD);
			label.setTextColor(getResources().getColor(R.color.StTheme));
			label.setBackgroundColor(getResources().getColor(R.color.white_color));
			label.setTextSize(14);
			surveyParentLayout.addView(label);

			editText=new EditText(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, LOCATION_LABEL_ID);
			params.setMargins(0, 5, 0,0);
			editText.setLayoutParams(params);
			editText.requestFocus();
			editText.setTextSize(14);
			editText.setId(LOCATION_NAME_EDITTEXT_ID);
			surveyParentLayout.addView(editText);


			emailLabel=new TextView(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.BELOW, LOCATION_NAME_EDITTEXT_ID);
			emailLabel.setId(EMAIL_LABEL_ID);
			emailLabel.setLayoutParams(params);
			emailLabel.setText("Enter Email Address:");
			emailLabel.setTypeface(Typeface.DEFAULT_BOLD);
			emailLabel.setTextColor(getResources().getColor(R.color.StTheme));
			emailLabel.setBackgroundColor(getResources().getColor(R.color.white_color));
			emailLabel.setTextSize(14);
			surveyParentLayout.addView(emailLabel);

			emailEditText=new EditText(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, EMAIL_LABEL_ID);
			params.setMargins(0, 5, 0,0);
			emailEditText.setLayoutParams(params);
			emailEditText.setId(EMAIL_EDITTEXT_ID);
			emailEditText.setHint("Enter email sperated by (,) if more than one to send them the survey details mail.");
			emailEditText.setHintTextColor(getResources().getColor(R.color.black_color));
			emailEditText.setTextSize(14);
			surveyParentLayout.addView(emailEditText);

			
			commentLabel=new TextView(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.BELOW, EMAIL_EDITTEXT_ID);
			commentLabel.setId(COMMENT_LABEL_ID);
			commentLabel.setLayoutParams(params);
			commentLabel.setText("Enter Comments:");
			commentLabel.setTypeface(Typeface.DEFAULT_BOLD);
			commentLabel.setTextColor(getResources().getColor(R.color.StTheme));
			commentLabel.setBackgroundColor(getResources().getColor(R.color.white_color));
			commentLabel.setTextSize(14);
			surveyParentLayout.addView(commentLabel);

			commentEditText=new EditText(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, COMMENT_LABEL_ID);
			params.setMargins(0, 5, 0,0);
			commentEditText.setLayoutParams(params);
			commentEditText.setId(COMMENT_EDITTEXT_ID);
			commentEditText.setHint("Add Comments");
			commentEditText.setHintTextColor(getResources().getColor(R.color.black_color));
			commentEditText.setTextSize(14);
			commentEditText.setLines(4);
			surveyParentLayout.addView(commentEditText);

			
			
			createLocBtn=new Button(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.BELOW, COMMENT_EDITTEXT_ID);
			createLocBtn.setId(LOCATION_BTN_ID);
			params.setMargins(0, 10, 0,0);
			createLocBtn.setLayoutParams(params);
			createLocBtn.setBackgroundResource(R.drawable.redbutton);
			createLocBtn.setText("Record");
			createLocBtn.setTextColor(Color.WHITE);
			createLocBtn.setTypeface(Typeface.DEFAULT_BOLD);
			surveyParentLayout.addView(createLocBtn);
			createLocBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String str=editText.getText().toString();
					if(str.length()>0){
						if(isRecordFolderNotCreated)
							createRecordFolderInSDCard();
						addCoordinatesToRecordFile(str,editText);
					}
					else{
						Toast.makeText(SurveyActivity.this, "Enter location name.", 4000).show();
					}
				}
			});

			shareBtn=new Button(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.BELOW, LOCATION_BTN_ID);
			params.setMargins(0, 10, 0,0);
			shareBtn.setLayoutParams(params);
			shareBtn.setBackgroundResource(R.drawable.greenbutton);
			shareBtn.setText("Upload To Server");
			shareBtn.setTextColor(Color.WHITE);
			shareBtn.setId(SHARE_BTN_ID);
			shareBtn.setTypeface(Typeface.DEFAULT_BOLD);

			shareBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(testIfTextFileExists())
						uploadConfirmation();
					else
						Toast.makeText(SurveyActivity.this, "No content to upload.", 2000).show();
				}
			});

			surveyParentLayout.addView(shareBtn);

			sendBtn=new Button(SurveyActivity.this);
			params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.BELOW, SHARE_BTN_ID);
			params.setMargins(0, 10, 0,0);
			sendBtn.setLayoutParams(params);
			sendBtn.setBackgroundResource(R.drawable.redbutton);
			sendBtn.setText("Finish");
			sendBtn.setTextColor(Color.WHITE);
			sendBtn.setTypeface(Typeface.DEFAULT_BOLD);

			sendBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(commentEditText.getText().toString().length()>0){
						addDataToTextFile(false,true);
					}
					doUploadReport();
				}
			});

			surveyParentLayout.addView(sendBtn);
			capture.setVisibility(View.INVISIBLE);
			emailEditText.setVisibility(View.GONE);
			emailLabel.setVisibility(View.GONE);
			commentLabel.setVisibility(View.GONE);
			commentEditText.setVisibility(View.GONE);
			sendBtn.setVisibility(View.GONE);

		}
	}

	private void uploadConfirmation() {
		// TODO Auto-generated method stub
		createAlertMessageDialog("Are you sure to initiate report email process ?", SurveyActivity.this, null, 3, "Yes", "No", null);
	}

	private class UploadReport extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js;
		String serverFolderName;
		public UploadReport(JSONObject js, String stServerFolderName) {
			// TODO Auto-generated constructor stub
			this.js=js;
			this.serverFolderName=stServerFolderName;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
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
			//			Log.e("report upload result", ""+result);
			checkIfImagesAreThereAndUpload(serverFolderName);
		}
	}

	private void checkIfImagesAreThereAndUpload(String serverFolderName) {
		// TODO Auto-generated method stub
		String pth=Environment.getExternalStorageDirectory()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY")+"Images/";
		File f=new File(pth);
		if(f.exists()){
			if(f.list().length>0){
				imagesTempList=new ArrayList<String>();
				for (File tempF : f.listFiles()) {
					if (tempF.isFile()){
						imagesTempList.add(tempF.getName());
					}
				}
				uploadSurveyImages(imagesTempList,serverFolderName,pth);
			}
			else{
				deleteCoordDirect(true);
			}
		}
	}

	private void uploadSurveyImages(ArrayList<String> imagesNameList, String serverFolderName, String pth) {
		// TODO Auto-generated method stub
		int size=(imagesNameList.size()<10)?imagesNameList.size():10;
		//		Log.e("size",""+size+" imagelist size: "+imagesNameList.size());
		for (int i = 0; i < size; i++) {
			Bitmap bm = BitmapFactory.decodeFile(pth+imagesNameList.get(i));
			//			Log.e("pth+imagesNameList.get(i)",""+pth+imagesNameList.get(i));
			if(bm==null){
				Toast.makeText(SurveyActivity.this, "Image at location "+pth+imagesNameList.get(i)+" is corrupt.", 2000).show();
				continue;
			}
			if(!normalSurveyEnabled){
			Matrix mtx = new Matrix();
			mtx.postRotate(90);
			// Rotating Bitmap
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
			}
			File f=new File(pth+imagesNameList.get(i));
			int imageSize=(int)(f.length()/1000);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			if(imageSize<50)
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
			else
				bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object   

			byte[] b = baos.toByteArray(); 
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			JSONObject js=new JSONObject();
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

			int uid=myPrefs.getInt("uid", -1);
			try {
				js.put("image", encodedImage);
				js.put("action", "upload-survey-img");
				js.put("foldername", serverFolderName);
				js.put("filename", imagesNameList.get(i).replaceAll(".jpg",""));
				js.put("uid", uid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			new UploadSurveyImage(js,i,size,imagesNameList,imagesNameList.get(i),serverFolderName,pth).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+23);
		}
	}
	private class UploadSurveyImage extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js;
		int counter;
		int numberOfImages;
		ArrayList<String> imageList;
		String imageName;
		String fldrName;
		String pth;
		public UploadSurveyImage(JSONObject js, int i, int size, ArrayList<String> imagesNameList, String imageName, String serverFolderName, String pth) {
			// TODO Auto-generated constructor stub
			this.js=js;
			counter=i;
			numberOfImages=size;
			this.imageList=imagesNameList;
			this.imageName=imageName;
			this.pth=pth;
			this.fldrName=serverFolderName;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObject=null;
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
			imagesTempList.remove(imageName);
			File photo=new File(pth+imageName);
			if(photo.exists())
				photo.delete();
			if(counter==numberOfImages-1){
				AppUtil.cancelProgressDialog();
				//				Log.e("imagesNameList", ""+imagesTempList.size());
				if(imagesTempList.size()>0){
					uploadSurveyImages(imagesTempList, fldrName, pth);
				}
				else{	
					deleteCoordDirect(true);
				}
			}
		}
	}

	private void deleteCoordDirect(boolean b) {
		// TODO Auto-generated method stub
		File dir=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY"));
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				new File(dir, children[i]).delete();
			}
		}
		if(b){
			backToMenu();
		}
		else{
			Toast.makeText(SurveyActivity.this, "Fresh survey session started.", 2000).show();
			showCamera(null);
		}
	}

	private String convertTextFileToBase64(){
		byte[] bytes = null;
		try {
			bytes = FileUtils.readFileToByteArray(new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY")+localTextFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String encoded = Base64.encodeToString(bytes, 0);  
		return encoded;
	}


	public void doPreUploadActions(String imagePath) {
		// TODO Auto-generated method stub
		Bitmap bm = BitmapFactory.decodeFile(imagePath);
		if(bm==null){
			Toast.makeText(SurveyActivity.this, "Image at location "+lid+".jpg"+" is corrupt.", 2000).show();
			backToMenu();
		}
		else{
			Matrix mtx = new Matrix();
			mtx.postRotate(90);
			// Rotating Bitmap
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
			File f=new File(imagePath);
			int imageSize=(int)(f.length()/1000);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			if(imageSize<50)
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
			else
				bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object   

			byte[] b = baos.toByteArray(); 
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			JSONObject js=new JSONObject();
			try {
				js.put("image", encodedImage);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			new UploadImage(js).execute(AppUtil.getAppProperty().getProperty("LOCATION_IMAGE_UPLOAD_URL")+lid);
		}
	}
	private void createLocationFolderInSDCard() {
		File direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_LOCATION_DIRECTORY"));

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
			}

		}
	}
	public void goToMenu(View v){
		backToMenu();
	}
	private void backToMenu(){
		Intent	in=new Intent(SurveyActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	private class GetNearByLocations extends AsyncTask<String, Integer, JSONArray>{
		private double lat;
		private double lng;
		public GetNearByLocations(double lat, double lng) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONArray jsResponse=null;
			try {
				jsResponse=new JSONArray(AppUtil.getResponse(params[0]+"?lat="+lat+"&long="+lng));
				//								jsResponse=new JSONArray(AppUtil.getResponse(params[0]+"?lat="+14.239136390482129+"&long="+78.25975812764739));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			doPostGetLocationActions(result,lat,lng);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//		super.onBackPressed();
		backToMenu();
	}
	public void doPostGetLocationActions(JSONArray result, double lat, double lng) {
		// TODO Auto-generated method stub
		if(result!=null){
			String str = "";
			if(result.length()>0){
				ArrayList<String> locationNameList=new ArrayList<String>();
				ArrayList<String> locationIdList=new ArrayList<String>();
				String tempString="Locations found nearby are:\n\n";
				for (int i = 0; i < result.length(); i++) {
					try {
						str = str+(i+1)+". "+result.getJSONObject(i).getString("location")+"\n\n";
						locationNameList.add(result.getJSONObject(i).getString("location"));
						locationIdList.add(result.getJSONObject(i).getString("lid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(i==result.length()-1)
						tempString=tempString+str;
				}
				tempString=tempString+"Do you want to create new location?";
				createDialog(tempString, "Information", "No", "Yes", false,true,false,"",true,"Update Existing Locations",locationNameList,locationIdList);
			}
			else{
				populateCustomLocationForm();
			}
		}
		else{
			createDialog("Retrieve nearby location service failed.", "Warning", "", "OK",true,false,false,"",false,"",null,null);
		}
	}
	private void populateCustomLocationForm() {
		// TODO Auto-generated method stub
		double lat,lng=0;
		Location location=getCurrentLocationOfDevice();
		if (location != null) {
			lat = (location.getLatitude());
			lng = (location.getLongitude());
			addViewsToParent(lat,lng);
		}
		else {
			createDialog("Current Location can not be retrieved", "Warning", "", "OK",true,false,false,"",false,"",null,null);
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SPONSORS_DIALOG_ID:
			viewDialogFirst = new Dialog(this);
			viewDialogFirst.setCancelable(false);
			viewDialogFirst.requestWindowFeature(Window.FEATURE_NO_TITLE);
			viewDialogFirst.setContentView(R.layout.picklogintype);

			final View pickAlphabetView = getLayoutInflater().inflate(
					R.layout.pick_location_ground_operations, null);
			viewDialogFirst.setContentView(pickAlphabetView);
			ListView listView = (ListView) pickAlphabetView.findViewById(R.id.locationIdlist);
			TextView tv=(TextView)pickAlphabetView.findViewById(R.id.dialogText);
			tv.setText(getResources().getString(R.string.sponsorsDialogText));
			Object[] array=sponsorsIdNameMapList.keySet().toArray();
			tempList=new ArrayList<String>();
			for (int i = 0; i < array.length; i++) {
				tempList.add(array[i].toString());
			}
			listView.setAdapter(new SponsorsAdaptor(SurveyActivity.this,tempList));
			listView.setOnItemClickListener(this);
			ImageView closedialog=(ImageView)pickAlphabetView.findViewById(R.id.cancelTreeDialog);
			closedialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogFirst.dismiss();
					backToMenu();
				}
			});
			return viewDialogFirst;	

		default:
			break;
		}
		return null;


	}
	private class SponsorsAdaptor extends BaseAdapter{
		ArrayList<String> tempList;
		private LayoutInflater inflater;

		public SponsorsAdaptor(SurveyActivity surveyActivity,ArrayList<String> tempList) {
			// TODO Auto-generated constructor stub
			this.tempList=tempList;
			inflater=(LayoutInflater)surveyActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tempList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi=convertView;
			if(convertView==null)
				vi = inflater.inflate(R.layout.location_list_item, null);
			TextView title = (TextView)vi.findViewById(R.id.groundLocationIdTextView);
			title.setText((position+1)+".   "+tempList.get(position)+"\n");
			return vi;
		}

	}
	private class GetSponsors extends AsyncTask<String, Integer, JSONArray>{
		double lat,lng;
		public GetSponsors(double lat, double lng) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONArray jsArray=null;
			try {
				jsArray=new JSONArray(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsArray;
		}
		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			addSponsorDataToList(result,lat,lng);
			AppUtil.cancelProgressDialog();
		}
	}
	private void addViewsToParent(final double lat,final double lng) {
		// TODO Auto-generated method stub
		new GetSponsors(lat,lng).execute(AppUtil.getAppProperty().getProperty("SPONSORS_LIST_URL"));
	}
	public void addSponsorDataToList(JSONArray result,final double lat, final double lng) {
		LayoutParams params;
		// TODO Auto-generated method stub
		if(null==sponsorsIdNameMapList)
			sponsorsIdNameMapList=new HashMap<String, String>();
		else
			sponsorsIdNameMapList.clear();

		for (int i = 0; i < result.length(); i++) {
			JSONObject js=result.optJSONObject(i);
			sponsorsIdNameMapList.put(js.optString("sponsor_text"),js.optString("sid"));
		}

		surveyParentLayout.removeAllViews();
		showDialog(SPONSORS_DIALOG_ID);
		TextView label=new TextView(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		label.setId(LOCATION_LABEL_ID);
		label.setLayoutParams(params);
		label.setText("Location Name for "+currentSponsor+": ");
		label.setTypeface(Typeface.DEFAULT_BOLD);
		label.setTextColor(getResources().getColor(R.color.StTheme));
		label.setTextSize(16);
		surveyParentLayout.addView(label);

		final EditText editText=new EditText(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, LOCATION_LABEL_ID);
		params.setMargins(0, 5, 0,0);
		editText.setLayoutParams(params);
		editText.requestFocus();
		editText.setId(LOCATION_NAME_EDITTEXT_ID);
		surveyParentLayout.addView(editText);

		Button createLocBtn=new Button(SurveyActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, LOCATION_NAME_EDITTEXT_ID);
		params.setMargins(0, 10, 0,0);
		createLocBtn.setLayoutParams(params);
		createLocBtn.setBackgroundResource(R.drawable.redbutton);
		createLocBtn.setText("Create Location");
		createLocBtn.setTextColor(Color.WHITE);
		createLocBtn.setTypeface(Typeface.DEFAULT_BOLD);
		surveyParentLayout.addView(createLocBtn);
		createLocBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str=editText.getText().toString();
				if(str.length()==0){
					Toast.makeText(SurveyActivity.this, "Location name cannot be empty.", 1000).show();
				}
				else{
					JSONObject js=new JSONObject();
					try {
						js.put("location", str);
						js.put("latitude", lat);
						js.put("longitude", lng);
						js.put("sid", sponsorsIdNameMapList.get(currentSponsor));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new CreateLocation(js).execute(AppUtil.getAppProperty().getProperty("CREATE_LOCATION_URL"));
				}
			}
		});
	}
	public void createAlertMessageDialog(String textToShow,Activity ac, final Location location,final int dialogCallIndex,String btn1Label,String btn2Label, final String locationName){
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
				if(dialogCallIndex==0){
					if (location != null) {
						isRecordModuleEnabled=false;
						double lat = (location.getLatitude());
						double lng = (location.getLongitude());
						new GetNearByLocations(lat,lng).execute(AppUtil.getAppProperty().getProperty("NEARBY_LOCATIONS_URL"));
					}
					else{
						createDialog("Current Location can not be retrieved, Go to phone location settings and check Google Location Services and GPS is active or not.", "Warning", "", "OK",true,false,false,"",false,"",null,null);
					}
				}
				else if(dialogCallIndex==1){
					Toast.makeText(SurveyActivity.this, "Previous survey session continued.", 2000).show();
					openCamera();
				}
				else if(dialogCallIndex==2){
					enableCaptureButton(locationName,true);
				}
				else{
					handleLayout();
				}
			}


		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				if(dialogCallIndex==0){
					initiateRecordCoord();
				}
				else if(dialogCallIndex==1){
					deleteCoordDirect(false);
				}
				else if(dialogCallIndex==2){
					capture.setVisibility(View.INVISIBLE);
					recordedImageName=locationName;
					addDataToTextFile(false,false);
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
	private void handleLayout() {
		// TODO Auto-generated method stub
		label.setVisibility(View.GONE);
		editText.setVisibility(View.GONE);
		capture.setVisibility(View.GONE);
		createLocBtn.setVisibility(View.GONE);
		shareBtn.setVisibility(View.GONE);

		preview.setVisibility(View.GONE);
		emailLabel.setVisibility(View.VISIBLE);
		emailEditText.setVisibility(View.VISIBLE);
		sendBtn.setVisibility(View.VISIBLE);
		commentLabel.setVisibility(View.VISIBLE);
		commentEditText.setVisibility(View.VISIBLE);
	}

	public void doUploadReport(){
		if(testIfTextFileExists()){
			JSONObject js=new JSONObject();
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
			String stServerFolderName=localTextFileName.replaceAll("Coordinates.txt", "").replaceAll("@", "_").replaceAll("_", "");
			int uid=myPrefs.getInt("uid", -1);
			try {
				js.put("text", convertTextFileToBase64());
				js.put("filename", "Coordinates.txt");
				js.put("foldername", stServerFolderName);
				js.put("action", "coordinates");
				js.put("uid", uid);
				js.put("email", emailEditText.getText().toString().trim());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			new UploadReport(js,stServerFolderName).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+"23");
		}
		else{
			Toast.makeText(SurveyActivity.this, "No content to upload.", 2000).show();
		}
	}

	private void enableCaptureButton(String locationName, boolean b) {
		// TODO Auto-generated method stub
		recordedImageName=locationName;
		capture.setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	private void openCamera() {
		showCamera(null);
	}

	private void initiateRecordCoord(){
		boolean isThereFiles=testIfTextFileExists();
		if(isThereFiles)
			createAlertMessageDialog("Previous survey session records still exists.\n Do you want to continue or start a new survey session?.", SurveyActivity.this, null, 1, "Continue", "New Survey",null);
		else{
			Toast.makeText(SurveyActivity.this, "Fresh survey session started.", 2000).show();
			showCamera(null);
		}
	}
	private void addCoordinatesToRecordFile(String str, EditText editText) {
		// TODO Auto-generated method stub
		editText.setText("");
		createAlertMessageDialog("Coordinates has been captured successfuly.\nDo you want to capture image?", SurveyActivity.this, null, 2, "Yes", "No",str);
	}
	protected void createTextFileInRecordFolder() {
		if(testIfTextFileExists()){
			currentUploadBatchReportFileName=localTextFileName;
		}
		else{
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd@HH_mm_ss");
			Calendar cal = Calendar.getInstance();
			//			currentUploadBatchReportFileName=dateFormat.format(cal.getTime()).replaceAll("_", "-").replaceAll("@", " ")+"_Coordinates.txt";
			if(currentUploadBatchReportFileName==null)
				currentUploadBatchReportFileName=dateFormat.format(cal.getTime())+"_Coordinates.txt";
			File recordReportTextFile=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY")+currentUploadBatchReportFileName);
			try {
				if(!recordReportTextFile.exists()){
					recordReportTextFile.createNewFile();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private boolean testIfTextFileExists() {
		// TODO Auto-generated method stub
		File sdCardRoot =new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY"));
		boolean value =false;
		if(sdCardRoot.exists()){
			for (File f : sdCardRoot.listFiles()) {
				if (f.isFile()){
					if(f.getName().contains(".txt")){
						localTextFileName=f.getName();
						value=true;
						break;
					}
				}
			}
		}
		return value;
	}
	private void createRecordFolderInSDCard(){
		File direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_RECORD_DIRECTORY"));
		//		Log.e("record clicked1", "record clicked1");
		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				//				Log.e("created", "created");
			}

		}
		createTextFileInRecordFolder();
		isRecordFolderNotCreated=false;
	}
	private class CreateLocation extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js;
		public CreateLocation(JSONObject js) {
			// TODO Auto-generated constructor stub
			this.js=js;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(SurveyActivity.this, "", progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsObject=null;
			try {
				jsObject=new JSONObject(AppUtil.doPostJsonObject(params[0],js ));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsObject;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppUtil.cancelProgressDialog();
			//			Log.e("nsn", ""+result);
			if (result!=null) {

				try {
					String locName=js.getString("location");
					String locationId=result.getString("lid");
					createDialog("Location "+locName+" successfully created.\nDo you want to capture and upload image of the location "+js.getString("location")+"?", "Information", "No", "Yes", false, true,true,locationId,false,"",null,null);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				createDialog("Location cannot be created.", "Warning", "", "OK",true,false,false,"",false,"",null,null);
			}
		}		
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isCameraShown){
			if (inPreview) {
				camera.stopPreview();
			}
			if(camera!=null){
				camera.release();
				camera=null;
			}
			inPreview=false;
			shutterSound.release();
		}
	}
	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};
	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				//				Log.e("PreviewDemo-surfaceCallback","Exception in setPreviewDisplay()", t);
				Toast.makeText(SurveyActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters=camera.getParameters();
				Camera.Size size=getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize=getSmallestPictureSize(parameters);

				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					camera.setDisplayOrientation(90);
					camera.setParameters(parameters);
					cameraConfigured=true;
				}
			}
		}
	}
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result=size;
				}
				else {
					int resultArea=result.width * result.height;
					int newArea=size.width * size.height;

					if (newArea > resultArea) {
						result=size;
					}
				}
			}
		}

		return(result);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result=size;
			}
			else {
				int resultArea=result.width * result.height;
				int newArea=size.width * size.height;

				if (newArea < resultArea) {
					result=size;
				}
			}
		}

		return(result);
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview=true;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		currentSponsor=tempList.get(arg2);
		TextView tv=(TextView)findViewById(LOCATION_LABEL_ID);
		tv.setText("Location Name for "+currentSponsor+" :");
		dismissDialog(SPONSORS_DIALOG_ID);
	}
}
