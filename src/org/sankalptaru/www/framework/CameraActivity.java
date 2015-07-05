package org.sankalptaru.www.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

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
import android.graphics.ImageFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements LocationListener{
	private AlertDialog alertDialog;
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
	private String treeId=null;
	private TextView loadingText;
	private RelativeLayout loadingLayout;
	private boolean isAuto;
	private ArrayList<String> treeIdList;
	private boolean isTagMode=true;
	private boolean isServiceFailedHandleMode=false;
	private String currentMode;
	private int plantationCount=0;
	private int serviceFailCounter=0;
	public String getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(String currentMode) {
		this.currentMode = locationName+"("+currentMode+")";
	}

	public boolean isServiceFailedHandleMode() {
		return isServiceFailedHandleMode;
	}

	public void setServiceFailedHandleMode(boolean isServiceFailedHandleMode) {
		this.isServiceFailedHandleMode = isServiceFailedHandleMode;
	}

	public boolean isTagMode() {
		return isTagMode;
	}

	public void setIsTagMode(boolean plantOrTagMode) {
		this.isTagMode = plantOrTagMode;
	}

	public void goToMenu(View v){
		Intent	in=new Intent(CameraActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	public boolean isAuto() {
		return isAuto;
	}
	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}
	public String getTreeIdLocation() {
		return treeIdLocation;
	}
	public void setTreeIdLocation(String treeIdLocation) {
		this.treeIdLocation = treeIdLocation;
	}

	private String treeIdLocation=null;
	private String locationId;
	private Intent activityIntent;
	private RelativeLayout takePictureLayout;
	private ImageView dummyTageImageview;
	private static final int TREE_ID_DIALOG = 102;
	String utilityImagesPath;
	LocationManager locationManager = null;
	private MediaPlayer shutterSound;
	private String locationName;
	private Size globalSelectedSize;
	public String getTreeId() {
		return treeId;
	}
	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		System.out.println("inside create dialog : " + id);
		Dialog pickAlphabetDialog = null;

		switch (id) {

		case TREE_ID_DIALOG:

			pickAlphabetDialog = new Dialog(this);
			pickAlphabetDialog.setCancelable(false);
			pickAlphabetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			final View pickAlphabetView = getLayoutInflater().inflate(
					R.layout.enter_treeid, null);
			pickAlphabetDialog.setContentView(pickAlphabetView);
			Button b=(Button)pickAlphabetView.findViewById(R.id.submitTreeId);
			Button reset=(Button)pickAlphabetView.findViewById(R.id.resetTreeId);
			final EditText e=(EditText)pickAlphabetView.findViewById(R.id.treeIDEditText);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					e.setText("");
				}
			});
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String tid=e.getText().toString().trim();
					if(tid!=null&&tid.length()>0){
						treeIdList.clear();
						if(tid.split(",").length>0){

							for (int i = 0; i <tid.split(",").length; i++) {
								treeIdList.add(tid.split(",")[i]);
							}

						}
						else {
							/*setTreeId(tid);
							setTreeIdLocation(tid+"_"+loactionId);*/
							treeIdList.add(tid);
						}
						setTreeId(treeIdList.get(0));
						setTreeIdLocation(treeIdList.get(0)+"_"+locationId);
						showCamera();	
						dismissDialog(TREE_ID_DIALOG);
					}
				}
			});
			ImageView close=(ImageView)pickAlphabetView.findViewById(R.id.cancelTreeDialog);
			close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismissDialog(TREE_ID_DIALOG);
					Intent	in=new Intent(CameraActivity.this, MenuActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
			});
			return pickAlphabetDialog;		
		}
		return null;
	}
	protected void proceedAutomaticTagging(ArrayList<String> treeIdList) {

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		this.locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
		selectResolutionAndStartActions();
	}
	private void selectResolutionAndStartActions() {
		if(null==camera)
			camera=Camera.open();
		final Parameters parameters=camera.getParameters();
		// TODO Auto-generated method stub
		RadioGroup resolutionGroup=(RadioGroup)findViewById(R.id.resolutionRadio);
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		resolutionGroup.setOrientation(1);
		for (int i = 0; i < parameters.getSupportedPictureSizes().size(); i++) {
			Camera.Size currentSize=parameters.getSupportedPictureSizes().get(i);
			//			Log.e("data", "w: "+currentSize.width+" h: "+currentSize.height);
			RadioButton r=new RadioButton(CameraActivity.this);
			r.setText(currentSize.width+" x "+currentSize.height);
			r.setId(i);
			r.setTextColor(getResources().getColor(R.color.StTheme));
			r.setTypeface(Typeface.DEFAULT_BOLD);
			r.setLayoutParams(params);
			resolutionGroup.addView(r);
		}
		resolutionGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				globalSelectedSize=parameters.getSupportedPictureSizes().get(checkedId);
				((RelativeLayout)findViewById(R.id.resolutionLayout)).setVisibility(View.GONE);
				Toast.makeText(CameraActivity.this, globalSelectedSize.width+" x "+globalSelectedSize.height+" resolution has been selected", 2500).show();
				actionsAfterSelectingResolution();
			}
		});
	}

	private void actionsAfterSelectingResolution(){
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(CameraActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		utilityImagesPath=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_UTIL_DIRECTORY");
		loadingText=(TextView)findViewById(R.id.loadingText);
		takePictureLayout=(RelativeLayout)findViewById(R.id.takePictureLayout);
		dummyTageImageview=(ImageView)findViewById(R.id.dummytagImageView);
		loadingLayout=(RelativeLayout)findViewById(R.id.mainLoadingInclude);
		this.locationManager = ((LocationManager)getSystemService("location"));
		shutterSound= MediaPlayer.create(CameraActivity.this, R.raw.camera_click);
		createFolderInSDCard();
		initiateCamera();
		if(null==treeIdList)
			treeIdList=new ArrayList<String>();
		else 
			treeIdList.clear();
		if(isAuto())
			acquireLock();
		else
			showEditText();

	}
	private void showEditText() {
		showDialog(TREE_ID_DIALOG);
	}
	private void acquireLock() {
		// TODO Auto-generated method stub
		setServiceFailedHandleMode(false);
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
		new PutAsynctask(CameraActivity.this,js,locationId).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+locationId);
	}
	private void initiateCamera() {
		// TODO Auto-generated method stub
		preview=(SurfaceView)findViewById(R.id.preview);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		locationId=getIntent().getExtras().getString("SelectedlocationId");
		locationName=getIntent().getExtras().getString("locationName");
		Log.e("locationId", ""+locationId);
		setAuto(getIntent().getExtras().getBoolean("isAuto"));

		if (camera == null) {
			camera=Camera.open();
		}

		startPreview();
	}
	private void createFolderInSDCard() {
		File direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_UTIL_DIRECTORY"));

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				Log.e("created", "created");
			}

		}
	}
	private void showCamera(){
		if(isTagMode()){
			if(isAuto()){
				((TextView)findViewById(R.id.treeIdText)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.treeCountText)).setVisibility(View.VISIBLE);
			}
			else {
				((TextView)findViewById(R.id.treeIdText)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.treeIdText)).setText("Tree Id:"+"\n"+getTreeId());
			}
		}
		else{
			((TextView)findViewById(R.id.treeIdText)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.treeCountText)).setText("Count:"+"\n"+plantationCount);
			//			((TextView)findViewById(R.id.treeCountText)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.treeIdText)).setVisibility(View.INVISIBLE);
		}
		((TextView)findViewById(R.id.treeIdText)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.currentMode)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.currentMode)).setText(getCurrentMode());
		takePictureLayout.setVisibility(View.VISIBLE);
		dummyTageImageview.setVisibility(View.VISIBLE);
		preview.setVisibility(View.VISIBLE);
	}
	private void hideCamera(){
		((TextView)findViewById(R.id.treeIdText)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.treeCountText)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.currentMode)).setVisibility(View.INVISIBLE);
		takePictureLayout.setVisibility(View.INVISIBLE);
		dummyTageImageview.setVisibility(View.INVISIBLE);
		preview.setVisibility(View.INVISIBLE);
	}
	@Override
	protected void onResume() {
		super.onResume();
		//		AppUtil.catchExceptionsInActivity(CameraActivity.this.getClass().getCanonicalName());
		if (this.locationManager != null)
			this.locationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (inPreview) {
			camera.stopPreview();
		}
		if(camera!=null){
			camera.release();
			camera=null;
		}
		inPreview=false;
		if(null!=shutterSound)
			shutterSound.release();
	}
	/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean showMenu=false;
		if(getTreeId().equals("-1"))
		{
			new MenuInflater(this).inflate(R.menu.options, menu);
			showMenu=true;
		}

		return showMenu;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.camera) {
			if (inPreview) {
				camera.takePicture(null, null, photoCallback);
				inPreview=false;
			}
		}

		return(super.onOptionsItemSelected(item));
	}
	 */
	public void doCapturePhoto(View v){
		if (inPreview) {
			Log.e("doCapturePhoto", "doCapturePhoto");
			shutterSound.start();
			camera.takePicture(null, null, photoCallback);
			inPreview=false;
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

		Log.e("location object types","locationManager.getLastKnownLocation(provider)"+locationManager.getLastKnownLocation(provider)+"network"+networkLocation+"gps"+gpslocation);

		return location;
	}
	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result=null;
		int resultArea=600*400;
		/*for (Camera.Size size : parameters.getSupportedPictureSizes()) {
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

			int newArea=size.width * size.height;

			if (newArea >= resultArea) {
				result=size;
				break;
			}
		}*/
		/*for (int i = 0; i < parameters.getSupportedPictureSizes().size(); i++) {
			Camera.Size currentSize=parameters.getSupportedPictureSizes().get(i);
			Log.e("data", "w: "+currentSize.width+" h: "+currentSize.height);
			int newArea=currentSize.width * currentSize.height;

			if (newArea >= resultArea) {
				result=currentSize;
				break;
			}
		}
		if(null==result||(result.width*result.height)<resultArea){
			lowResolutionCameraPrompt("Sorry, your Camera resolution is too low to be qualified to take sapling pictures.\n Your camera resolution(width*height):"+parameters.getSupportedPictureSizes().get(0).width+"*"+parameters.getSupportedPictureSizes().get(0).height+"\nMinimum Required(w*h):600*400", "Fatal Warning", "", "OK");
		}*/
		return(result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters=camera.getParameters();
				Camera.Size size=getBestPreviewSize(width, height, parameters);
				//				Camera.Size pictureSize=getSmallestPictureSize(parameters);

				Camera.Size selectedPictureSize=globalSelectedSize;

				if (size != null && selectedPictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(selectedPictureSize.width,
							selectedPictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					camera.setDisplayOrientation(90);
					camera.setParameters(parameters);
					cameraConfigured=true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview=true;
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
	private Location getLocationByProvider(String provider) {
		Location location = null;

		LocationManager locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {

		}
		return location;
	}
	Camera.PictureCallback photoCallback=new Camera.PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
			byte[] resized = resizeImage(data);

			if(null != original){
				original.recycle();
				original=null;
			}

			if(null != resizedBitmap){
				resizedBitmap.recycle();
				original=null;
			}

			int uid=myPrefs.getInt("uid", -1);
			Location location = getCurrentLocationOfDevice();

			if(null==location){
				location=globalLocation;
			}
			
			if(null!=location){

				double lat = location.getLatitude();
				double lng=location.getLongitude();

				if(!isServiceFailedHandleMode()){
					if(isTagMode())
						new SavePhotoTagTask(lat,lng,uid).execute(resized);
					else{
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("long", lng);
							jsonObject.put("lat", lat);
							jsonObject.put("action", "populate");
							jsonObject.put("uid", ""+uid);
							new DoPopulateCall(resized,jsonObject).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+locationId);					
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else{
					int numberOfPhotoClickedInServiceFailedMode=Integer.parseInt(myPrefs.getString("serviceFailedCounter", "0"));
					numberOfPhotoClickedInServiceFailedMode++;
					updatePhotoClickedCounter(""+numberOfPhotoClickedInServiceFailedMode);
					new SavePhotoInServiceFailMode(lat,lng,uid,""+numberOfPhotoClickedInServiceFailedMode).execute(resized);
				}
			}
			else{
				locationNullPrompt("Unfortunately device current Location can not be retrieved from any sources i.e. GPS,Network or last location.\n\n Go to phone location settings and check all option to access your location is ON like Access to my location,Google apps access your location,GPS satellites, and Google Location services etc.", "Warning", "", "OK");	
			}
			camera.startPreview();
			inPreview=true;
		}
	};

	private Bitmap original=null;
	private Bitmap resizedBitmap=null;
	private Location globalLocation=null;
	byte[] resizeImage(byte[] input) {
		ByteArrayOutputStream blob = null;
		try{
			original = BitmapFactory.decodeByteArray(input , 0, input.length);
			resizedBitmap = Bitmap.createScaledBitmap(original, 640, 480, true);
			blob = new ByteArrayOutputStream();
			resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
		}
		catch(OutOfMemoryError e){

		}
		return blob.toByteArray();
	}

	class SavePhotoInServiceFailMode extends AsyncTask<byte[], String, String>{
		private double lat,lng;
		private int uid;
		private String numberOfPhotoClickedInServiceFailedMode;
		public SavePhotoInServiceFailMode(double lat, double lng, int uid, String string) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
			this.uid=uid;
			numberOfPhotoClickedInServiceFailedMode=string;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Saving Picture...");
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(utilityImagesPath+"/"+uid+"-"+lat+"-"+lng+"_"+locationId+"@"+numberOfPhotoClickedInServiceFailedMode+".jpg");
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			}
			catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return(null);
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.INVISIBLE);
			acquireLock();
		}
	}

	class DoPopulateCall extends AsyncTask<String, Integer, JSONObject>{
		byte[] jpeg;
		JSONObject jsonObject;
		public DoPopulateCall(byte[] data, JSONObject jsonObject) {
			// TODO Auto-generated constructor stub
			jpeg=data;
			this.jsonObject=jsonObject;
			Log.e("jsonObject", ""+this.jsonObject);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Planting...");
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			Log.e("params[0]", params[0]);
			JSONObject jsResponse = null;
			try {
				jsResponse = new JSONObject(AppUtil.doPutJsonObject(params[0], jsonObject));
			} catch (Exception e) {
				// TODO: handle exception
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadingLayout.setVisibility(View.INVISIBLE);
						createWebserviceFailedDialog("Service Failed, switching to Offline mode.", "Information", "", "OK");
					}
				});
				setServiceFailedHandleMode(true);
				this.cancel(true);
			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if(!isServiceFailedHandleMode()){
				Log.e("populate response", ""+result);
				loadingLayout.setVisibility(View.INVISIBLE);
				new SavePhotoPlantingTask(result.optString("tid")+"_"+locationId).execute(jpeg);
			}
		}
	}
	class SavePhotoPlantingTask extends AsyncTask<byte[], String, String>{
		String tid;
		public SavePhotoPlantingTask(String tid) {
			// TODO Auto-generated constructor stub
			this.tid=tid;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Saving Picture...");
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(utilityImagesPath+"/"+tid+".jpg");
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			}
			catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return(null);
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.INVISIBLE);
			plantationCount++;
			acquireLock();
		}
	}

	class SavePhotoTagTask extends AsyncTask<byte[], String, String> {
		private double lat;
		private double lng;
		private int uid;
		public SavePhotoTagTask(double lat, double lng, int uid) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
			this.uid=uid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Saving Picture...");
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(utilityImagesPath+"/"+getTreeIdLocation()+".jpg");
			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos=new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			}
			catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return(null);
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.INVISIBLE);

			Log.e("photo exists", ""+(new File(utilityImagesPath+getTreeIdLocation()+".jpg")).exists());

			//			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// Define the criteria how to select the locatioin provider -> use
			// default

			JSONObject js=new JSONObject();
			Log.e("uid", ""+uid);
			try {
				js.put("lat", lat);
				js.put("long", lng);
				js.put("action", "tag");
				js.put("tid",getTreeId());
				js.put("uid", uid);
				Log.e("json", js.toString()+"  "+AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+locationId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new PutTagInfoAsynctask(CameraActivity.this,js).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+locationId);
		}
	}
	public class PutTagInfoAsynctask extends AsyncTask<String, Integer, JSONObject> {
		Activity ac;
		JSONObject js;
		public PutTagInfoAsynctask(CameraActivity startActivity, JSONObject js) {
			// TODO Auto-generated constructor stub
			ac=startActivity;
			this.js=js;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingText.setText("Tagging...");
			loadingLayout.setVisibility(View.VISIBLE);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObject = null;
			try {
				Log.e("json In Tag", ""+js);
				jsonObject = new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			}catch (Exception e) {
				// TODO: handle exception
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadingLayout.setVisibility(View.INVISIBLE);
						createWebserviceFailedDialog("Service Failed, switching to Offline mode.", "Information", "", "OK");
					}
				});
				setServiceFailedHandleMode(true);
				this.cancel(true);
			}
			return jsonObject;

		}
		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!isServiceFailedHandleMode()){
				Log.e("last Tag Result", ""+result);
				loadingLayout.setVisibility(View.INVISIBLE);
				if(isAuto()){
					if(result!=null){
						String tid=result.optString("tid");
						String count=result.optString("count");
						createConfirmationOfTagAlert("Tag Next tree.","Tree Successfully Tagged.","   No   ","   Yes   ",tid,count,true);
					}
					else{
						(new File(utilityImagesPath+getTreeIdLocation()+".jpg")).delete();
						createConfirmationOfTagAlert("Continue Tagging.","Tagging Failed.","   No   ","   Yes   ",null,null,false);
					}
				}
				else{
					treeIdList.remove(getTreeId());
					if(result!=null){
						if(treeIdList.size()>0){
							createManualTagStatusAlert("Tree Id "+getTreeId()+" Successfully Tagged","Tag Status","Tag Tree Id "+treeIdList.get(0));
						}
						else{
							createConfirmationOfTagAlert("Tree Id "+getTreeId()+" Succesfully Tagged."+"\n"+"Enter More TreeId's.","Manual Tagging Finished.","No","Yes",null,null,true);
						}
					}
					else{
						if(treeIdList.size()>0){
							(new File(utilityImagesPath+getTreeIdLocation()+".jpg")).delete();
							createManualTagStatusAlert("Tree Id "+getTreeId()+" Tagging Failed","Tag Status","Tag Tree Id "+treeIdList.get(0));
						}
						else{
							(new File(utilityImagesPath+getTreeIdLocation()+".jpg")).delete();
							createConfirmationOfTagAlert("Tree Id "+getTreeId()+" Tagging Failed."+"\n"+"Enter More TreeId's.","Manual Tagging Finished.","No","Yes",null,null,true);
						}
					}
				}
				/*			setTreeId("-1");
			JSONObject js=new JSONObject();
			try {
				js.put("action", "upload");
				js.put("tid",getTreeId());
				js.put("image", encodedImage);			
				Log.e("json", js.toString());
				Toast.makeText(CameraActivity.this, "Lock Acquired And tree Tagged. Now Uploading Image String", 1000).show();
				setTreeId("-1");
				//				new PutImageAsyncTask(js,CameraActivity.this).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+loactionId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			}
		}
	}
	public void locationNullPrompt(String textToShow,final String title,String neutralButtonText,String positiveButtonText){

		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		localTextView2.setText(textToShow);
		localButton1.setText("     "+positiveButtonText+"     ");
		localButton2.setText("     "+neutralButtonText+"     ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				/*alertDialog.dismiss();
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();*/
				alertDialog.dismiss();
				Intent	in=new Intent(CameraActivity.this, MenuActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
		});

		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				alertDialog.dismiss();
				Intent	in=new Intent(CameraActivity.this, MenuActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
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
	public void createWebserviceFailedDialog(String textToShow,final String title,String neutralButtonText,String positiveButtonText){

		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		localTextView2.setText(textToShow);
		localButton1.setText("     "+positiveButtonText+"     ");
		localButton2.setText(neutralButtonText);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.INVISIBLE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				//				doWebserviceFailActions();
				backToMenu();
			}
		});
		try
		{
			this.alertDialog = localBuilder.create();
			this.alertDialog.setView(localLinearLayout, 0, 0, 0, 0);
			this.alertDialog.setInverseBackgroundForced(true);
			this.alertDialog.show();
			//			hideCamera();
			return;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}

	}
	protected void updatePhotoClickedCounter(String counter) {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("serviceFailedCounter", counter);
		editor.commit();
	}

	protected void doWebserviceFailActions() {
		// TODO Auto-generated method stub
		setCurrentMode("Offline Mode");
		//		showCamera();
		((TextView)findViewById(R.id.treeIdText)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.treeCountText)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.treeCountText)).setText("Count: "+serviceFailCounter);
		serviceFailCounter++;
	}

	public void createConfirmationOfTagAlert(String textToShow,final String title,String neutralButtonText,String positiveButtonText, final String tid, final String count, final boolean b){
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		localTextView2.setText(textToShow);
		localButton1.setText(positiveButtonText);
		localButton2.setText(neutralButtonText);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.VISIBLE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				if(b&&isAuto()){
					if(tid.equals("-1")){
						hideCamera();
						createNoTreeAlert(" No Trees to be Tagged.\n Continue to plant trees?","No","-1","Yes");
					}
					else {
						setTreeId(tid);
						Log.e("loactionId", locationId);
						setTreeIdLocation(tid+"_"+locationId);
						updateTreeIDAndCount(tid, count);
					}
				}
				else if (b&&!isAuto()) {
					showDialog(TREE_ID_DIALOG);
				}
				else if(!b&&isAuto()){
					acquireLock();
				}
				else if(!b&&!isAuto()){
					showDialog(TREE_ID_DIALOG);
				}

			}
		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				finish();
				activityIntent=new Intent(CameraActivity.this, MenuActivity.class);
				startActivity(activityIntent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
	public void createManualTagStatusAlert(String textToShow,String title,String neutralButtonText) {
		// TODO Auto-generated method stub
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText(title);
		localTextView2.setText(textToShow);
		localButton1.setText(neutralButtonText);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				setTreeId(treeIdList.get(0));
				setTreeIdLocation(treeIdList.get(0)+"_"+locationId);
				((TextView)findViewById(R.id.treeIdText)).setText("Tree Id:"+"\n"+getTreeId());
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
	public class PutAsynctask extends AsyncTask<String, Integer, JSONObject> {
		Activity ac;
		JSONObject js;
		String treeIdAsynString=null;
		String locString;
		public PutAsynctask(CameraActivity startActivity, JSONObject js, String loactionId) {
			// TODO Auto-generated constructor stub
			ac=startActivity;
			this.js=js;
			locString=loactionId;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Acquiring Lock...");
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} 
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadingLayout.setVisibility(View.INVISIBLE);
						createWebserviceFailedDialog("Service Failed,Select Offline Plantation from Main Menu.", "Information", "", "OK");
					}
				});
				setServiceFailedHandleMode(true);
				this.cancel(true);
			}
			return jsonObject;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!isServiceFailedHandleMode()){
				Log.e("result", result.toString());
				loadingLayout.setVisibility(View.INVISIBLE);
				treeIdAsynString=result.optString("tid");
				updateTreeIDAndCount(treeIdAsynString,result.optString("count"));
				setTreeId(treeIdAsynString);
				setTreeIdLocation(treeIdAsynString+"_"+locString);
				/*	if(!treeIdAsynString.equals("-1")){
				setTreeId(treeIdAsynString+"_"+locString);

				new SavePhotoTask().execute(cameraByteArray);
				cameraInstance.startPreview();
				inPreview=true;
			}
			else {
				AppUtil.createAlertMessageDialog("No Trees to be Tagged.", CameraActivity.this);
			}*/
				if(getTreeId().equals("-1")){
					setCurrentMode("Plantation Mode");
					createNoTreeAlert(" No Trees to be Tagged.\n Continue to plant trees?","No","-1","Yes");
				}
				else{
					setCurrentMode("Tag Mode");
					setIsTagMode(true);
					showCamera();
				}
			}
		}
	}



	public void createNoTreeAlert(String textToShow,String neutralButtonText,final String treeId,String positiveButtonText){
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localTextView2.setText(textToShow);
		localButton1.setText(positiveButtonText);
		localButton2.setText(neutralButtonText);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.VISIBLE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				setIsTagMode(false);
				showCamera();
			}
		});
		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(treeId.equals("-1")){
					setIsTagMode(true);
					activityIntent=new Intent(CameraActivity.this, MenuActivity.class);
					startActivity(activityIntent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
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
	public void updateTreeIDAndCount(String treeIdAsynString, String count) {
		// TODO Auto-generated method stub
		((TextView)findViewById(R.id.treeIdText)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.treeCountText)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.treeIdText)).setText("Tree Id: "+"\n"+treeIdAsynString);
		((TextView)findViewById(R.id.treeCountText)).setText("Count:"+"\n"+count);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// TODO Auto-generated method stub
		//			super.onBackPressed();
		backToMenu();
	}
	private void backToMenu(){
		Intent	in=new Intent(CameraActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		globalLocation=arg0;
	}

	@Override
	public void onProviderDisabled(String arg0) {
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
}
