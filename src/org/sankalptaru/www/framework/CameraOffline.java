package org.sankalptaru.www.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.inputmethod.InputMethodManager;
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

public class CameraOffline extends Activity implements LocationListener {
	private Size globalSelectedSize;
	private Camera camera=null;
	private AlertDialog alertDialog;
	private RelativeLayout userDetailslyt;
	private String userid;
	private String locationid;
	LocationManager locationManager = null;
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
	private String utilityImagesPath;
	private TextView loadingText;
	private RelativeLayout takePictureLayout;
	private ImageView dummyTageImageview;
	private RelativeLayout loadingLayout;
	private MediaPlayer shutterSound;
	private int plantationCount=0;
	@Override
	protected void onResume() {
		super.onResume();

		if (this.locationManager != null){
			this.locationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(CameraOffline.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
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
			RadioButton r=new RadioButton(CameraOffline.this);
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
				Toast.makeText(CameraOffline.this, globalSelectedSize.width+" x "+globalSelectedSize.height+" resolution has been selected", 2500).show();
				populateUserDetailsLayout();
			}
		});
	}
	private void populateUserDetailsLayout() {
		// TODO Auto-generated method stub
		userDetailslyt=(RelativeLayout)findViewById(R.id.userdetailsLayout);
		userDetailslyt.setVisibility(View.VISIBLE);
	}
	public void doInitiateOfflineCamera(View v){
		InputMethodManager imm = (InputMethodManager) getSystemService(
				INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		EditText userId=(EditText)findViewById(R.id.detailstextUsername);
		EditText locID=(EditText)findViewById(R.id.detailstextLocation);
		String a=locID.getText().toString();
		String b=userId.getText().toString();
		if(b.length()>0&&a.length()>0){
			setUserid(b);
			setLocationid(a);
			doCameraActivity();
		}else{
			Toast.makeText(CameraOffline.this, "User ID and Location ID cannot be empty.", 2000).show();
		}
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getLocationid() {
		return locationid;
	}
	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}
	private void doCameraActivity() {
		// TODO Auto-generated method stub
		userDetailslyt.setVisibility(View.GONE);
		utilityImagesPath=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_UTIL_DIRECTORY");
		loadingText=(TextView)findViewById(R.id.loadingText);
		takePictureLayout=(RelativeLayout)findViewById(R.id.takePictureLayout);
		dummyTageImageview=(ImageView)findViewById(R.id.dummytagImageView);
		loadingLayout=(RelativeLayout)findViewById(R.id.mainLoadingInclude);
		this.locationManager = ((LocationManager)getSystemService("location"));
		shutterSound= MediaPlayer.create(CameraOffline.this, R.raw.camera_click);
		createFolderInSDCard();
		initiateCamera();
		showCameraLayout();
	}

	private void showCameraLayout() {
		// TODO Auto-generated method stub

		((TextView)findViewById(R.id.treeCountText)).setText("Count:"+"\n"+plantationCount);
		takePictureLayout.setVisibility(View.VISIBLE);
		dummyTageImageview.setVisibility(View.VISIBLE);
		preview.setVisibility(View.VISIBLE);
	}
	private void initiateCamera() {
		// TODO Auto-generated method stub
		preview = (SurfaceView)findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if (camera == null) {
			camera=Camera.open();
		}

		startPreview();
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
	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraOffline.this, t.getMessage(),
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
	public void doCapturePhoto(View v){
		if (inPreview) {
			Log.e("doCapturePhoto", "doCapturePhoto");
			shutterSound.start();
			camera.takePicture(null, null, photoCallback);
			inPreview=false;
		}
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

			Location location = getCurrentLocationOfDevice();

			if(null==location)
				location=globalLocation;

			if(null!=location){
				double lat = location.getLatitude();
				double lng=location.getLongitude();
				int numberOfPhotoClickedInServiceFailedMode=Integer.parseInt(myPrefs.getString("serviceFailedCounter", "0"));
				numberOfPhotoClickedInServiceFailedMode++;
				updatePhotoClickedCounter(""+numberOfPhotoClickedInServiceFailedMode);
				new SavePhotoInServiceFailMode(lat,lng,numberOfPhotoClickedInServiceFailedMode).execute(resized);
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


	public void locationNullPrompt(String textToShow,final String title,String neutralButtonText,String positiveButtonText){

		AlertDialog.Builder localBuilder = new AlertDialog.Builder(CameraOffline.this);
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
				Intent	in=new Intent(CameraOffline.this, MenuActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
		});

		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				alertDialog.dismiss();
				Intent	in=new Intent(CameraOffline.this, MenuActivity.class);
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

	class SavePhotoInServiceFailMode extends AsyncTask<byte[], String, String>{
		private double lat,lng;
		private int counter;
		public SavePhotoInServiceFailMode(double lat, double lng, int numberOfPhotoClickedInServiceFailedMode) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
			counter=numberOfPhotoClickedInServiceFailedMode;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			plantationCount++;
			loadingLayout.setVisibility(View.VISIBLE);
			loadingText.setText("Saving Picture...");
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo=
					new File(utilityImagesPath+"/"+getUserid()+"-"+lat+"-"+lng+"_"+getLocationid()+"@"+counter+".jpg");
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
			((TextView)findViewById(R.id.treeCountText)).setText("Picture Clicked:"+"\n"+plantationCount);
		}
	}

	protected void updatePhotoClickedCounter(String counter) {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("serviceFailedCounter", counter);
		editor.commit();
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
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		globalLocation=location;
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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		backToMenu();
	}
	public void goToMenu(View v){
		backToMenu();
	}
	private void backToMenu(){
		Intent	in=new Intent(CameraOffline.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

}
