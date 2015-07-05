package org.sankalptaru.www.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalptaru.www.framework.helper.DatabaseHelper;
import org.sankalptaru.www.framework.model.Census;
import org.sankalptaru.www.framework.model.Forest_Census;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AttendanceLogActivity extends FragmentActivity implements LocationListener {
	private ProgressBar pBar;
	private LocationDialog locDialog;
	private LocationManager locationManager;
	private Size globalSelectedSize;
	private Camera camera=null;
	private String forestImagesPath;
	private MediaPlayer shutterSound;
	private Location globalLocation = null;
	private SurfaceHolder previewHolder;
	private boolean cameraConfigured=false;
	private boolean inPreview=false;
	private SurfaceView preview = null;
	private String forestName;
	private long forest_id;
	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(AttendanceLogActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		setContentView(R.layout.camera_layout);
		this.locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
		setContentView(R.layout.census);

		db = new DatabaseHelper(AttendanceLogActivity.this);

		selectResolutionAndStartActions();
		/*setContentView(R.layout.attendance);
		WebView wv=(WebView)findViewById(R.id.attendanceWv);
		wv.loadUrl("http://acct.sankalptaru.org/eform/submit/attendance-log");
		pBar=(ProgressBar)findViewById(R.id.loginprogress);

		wv.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				pBar.setVisibility(View.VISIBLE);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				pBar.setVisibility(View.INVISIBLE);
			}
		});*/

	}
	private void selectResolutionAndStartActions() {
		if(null==camera)
			camera=Camera.open();
		final Parameters parameters=camera.getParameters();
		// TODO Auto-generated method stub
		final RadioGroup resolutionGroup=(RadioGroup)findViewById(R.id.resolutionRadio);
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		resolutionGroup.setOrientation(1);
		for (int i = 0; i < parameters.getSupportedPictureSizes().size(); i++) {
			Camera.Size currentSize=parameters.getSupportedPictureSizes().get(i);
			//			Log.e("data", "w: "+currentSize.width+" h: "+currentSize.height);
			RadioButton r=new RadioButton(AttendanceLogActivity.this);
			r.setText(currentSize.width+" x "+currentSize.height);
			r.setId(i);
			r.setTextColor(getResources().getColor(R.color.StTheme));
			r.setTypeface(Typeface.DEFAULT_BOLD);
			r.setLayoutParams(params);
			resolutionGroup.addView(r);
		}

		final EditText locName =(EditText)findViewById(R.id.forestTxt);

		Button startSessionBtn = (Button)findViewById(R.id.startSessionBtn);
		startSessionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int i = resolutionGroup.getCheckedRadioButtonId();
				String forestStr = locName.getText().toString().trim();
				if(i == -1 || forestStr.length() < 0){
					Toast.makeText(AttendanceLogActivity.this, "Enter forest name and camera resolution to proceed.", 2000).show();
				}
				else{

					int id = db.getForest(forestStr);
					if(id == 0)		
					{
						forestName = forestStr;
						DatabaseHelper db = new DatabaseHelper(AttendanceLogActivity.this);
						forestName = forestStr;
						globalSelectedSize=parameters.getSupportedPictureSizes().get(i);
						((ScrollView)findViewById(R.id.resolutionScrollLayout)).setVisibility(View.GONE);
						db =new DatabaseHelper(AttendanceLogActivity.this);
						forest_id = db.createForest(new Forest_Census(forestName));
						InputMethodManager imm = (InputMethodManager)getSystemService(
								Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						doCameraActivity();
					}else{
						Toast.makeText(AttendanceLogActivity.this, "Forest with name "+forestStr+" already exists.Enter another forest name.", 2000).show();
					}
				}
			}
		});

		resolutionGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				globalSelectedSize=parameters.getSupportedPictureSizes().get(checkedId);

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (this.locationManager != null){
			this.locationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
		}
	}
	private void doCameraActivity() {
		// TODO Auto-generated method stub

		forestImagesPath=Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKALP_TARU_CENSUS_DIRECTORY")+forestName;

		this.locationManager = ((LocationManager)getSystemService("location"));
		shutterSound= MediaPlayer.create(AttendanceLogActivity.this, R.raw.camera_click);
		createFolderInSDCard(forestName,true);
		initiateCamera();
		showCameraLayout();
	}
	private void showCameraLayout() {
		// TODO Auto-generated method stub
		RelativeLayout takePictureLayout=(RelativeLayout)findViewById(R.id.takePictureLayout);

		takePictureLayout.setVisibility(View.VISIBLE);
		preview.setVisibility(View.VISIBLE);
	}

	private void initiateCamera() {
		// TODO Auto-generated method stub
		preview  = (SurfaceView)findViewById(R.id.preview);
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
	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(AttendanceLogActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters=camera.getParameters();
				Camera.Size size=AppUtil.getBestPreviewSize(width, height, parameters);
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


	private void createFolderInSDCard(String folderName,boolean isForestPath) {
		File direct = null;

		if(isForestPath)
			direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_CENSUS_DIRECTORY")+folderName);
		else
			direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_CENSUS_REPORT_DIRECTORY")+folderName);

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				Log.e("created", "created");
			}
		}
	}

	public void doUploadReport(View v){

		db = new DatabaseHelper(AttendanceLogActivity.this);
		List<Forest_Census> list = db.getAllForest();

		if(list.size()>0){
			UploadCensusDialog uploadCensusDialog = new UploadCensusDialog(list);
			uploadCensusDialog.setCancelable(true);
			uploadCensusDialog.show(getSupportFragmentManager(), "open");
		}
		else{
			Toast.makeText(AttendanceLogActivity.this, "No forest census to upload.", 2000).show();
		}
	}

	public void goToMenu(View v){
		Intent	in=new Intent(AttendanceLogActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	public void onBackPressed() {
		Intent	in=new Intent(AttendanceLogActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	private class UploadCensusDialog extends DialogFragment{
		List<Forest_Census> list;
		public UploadCensusDialog(List<Forest_Census> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.direction_list, container,
					false);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);


			TextView distTimeText =(TextView)rootView.findViewById(R.id.distTimeText);
			distTimeText.setVisibility(View.GONE);

			ImageView closeDirectionLayout = (ImageView)rootView.findViewById(R.id.closeDirectionLayout);
			closeDirectionLayout.setVisibility(View.GONE);

			TextView highwayText = (TextView)rootView.findViewById(R.id.highwayText);
			highwayText.setText("Select forest to upload its census sheet and kml.");



			ListView directionsList = (ListView)rootView.findViewById(R.id.directionsList);
			directionsList.setAdapter(new ForestAdaptor(list));
			directionsList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String fName = list.get(arg2).getForest_name();
					getDialog().dismiss();
					List<Census> census_list = db.getAllCensus(list.get(arg2).getId());
					/*for (int i = 0; i < census_list.size(); i++) {
						Log.e("census date", fName+" "+census_list.get(i).getTreename());
					}*/
					SendReportDialog sendReportDialog = new SendReportDialog(census_list,fName);
					sendReportDialog.setCancelable(false);
					sendReportDialog.show(getSupportFragmentManager(), "open");
				}
			});

			return rootView;
		}
	}


	private class SendReportDialog extends DialogFragment{

		private List<Census> census_list;
		private String frstName;
		private ArrayList<String> treeImageNameList;
		private Dialog progressDialog;

		public SendReportDialog(List<Census> census_list, String forestName) {
			// TODO Auto-generated constructor stub
			this.census_list = census_list;
			this.frstName = forestName;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.census_upload_conf, container,
					false);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

			Button cancelCensusBtn = (Button)rootView.findViewById(R.id.cancelCensusBtn);
			cancelCensusBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getDialog().dismiss();	
				}
			});


			final EditText emails = (EditText)rootView.findViewById(R.id.census_report_reciepient);

			Button uploadCensusBtn = (Button)rootView.findViewById(R.id.uploadCensusBtn);
			uploadCensusBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getDialog().dismiss();

					String emailString = emails.getText().toString().trim();

					if(emailString.length() > 0)
						generateForestExcelAndKml(emailString);
					else
						Toast.makeText(AttendanceLogActivity.this, "Enter reciepient email.", 2000).show();
				}
			});


			return rootView;
		}

		private void generateForestExcelAndKml(String emailString) {
			// TODO Auto-generated method stub
			createFolderInSDCard(frstName, false);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar cal = Calendar.getInstance();
			String folderName = dateFormat.format(cal.getTime())+"/"+frstName.replaceAll(" ", "");
			uploadImagesToServer(folderName,emailString);

			processExcelAndKml();
		}

		private void uploadImagesToServer(String folderName, String emailString) {
			// TODO Auto-generated method stub
			treeImageNameList = checkImage();
			if(treeImageNameList.size()>0){
				doUploadActions(folderName,emailString);
			}else{
				Toast.makeText(AttendanceLogActivity.this, "App cannot find tree images for forest: "+frstName+". Only sheet and kml is uploaded", 2000).show();
			}
		}

		private void doUploadActions(String folderName, String emailString) {
			// TODO Auto-generated method stub

			int size=(treeImageNameList.size()<10)?treeImageNameList.size():10;
			AppUtil.initializeProgressDialog(AttendanceLogActivity.this, "Uploading Images", progressDialog);
			for (int i = 0; i < size; i++) {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKALP_TARU_CENSUS_DIRECTORY")+frstName+"/"+treeImageNameList.get(i);
				File file = new File(path);
				Log.e("data",""+ file.exists());
				Bitmap bm = BitmapFactory.decodeFile(path);
				if(bm==null){
					Toast.makeText(AttendanceLogActivity.this, "Image at location "+path+" is corrupt.", 2000).show();
					continue;
				}
				Matrix mtx = new Matrix();
				mtx.postRotate(90);
				// Rotating Bitmap
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);

				File f=new File(path);
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
					js.put("action", "upload-census-img");
					js.put("foldername", folderName);
					js.put("filename", treeImageNameList.get(i));
					js.put("uid", uid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				new UploadCensusImage(js,i,size,treeImageNameList.get(i),folderName,emailString).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+23);				
			}
		}

		private String convertFileToBase64(String path){
			byte[] bytes = null;
			try {
				bytes = FileUtils.readFileToByteArray(new File(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String encoded = Base64.encodeToString(bytes, 0);  
			return encoded;
		}

		private class UploadCensusImage extends AsyncTask<String, Integer, JSONObject>{

			JSONObject json;
			int counter;
			int size;
			String imageName;
			String folderName;
			String emailString;


			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			public UploadCensusImage(JSONObject js, int i, int size, String imageName, String folderName, String emailString) {
				// TODO Auto-generated constructor stub
				this.json = js;
				this.counter = i;
				this.size = size;
				this.imageName = imageName;
				this.folderName = folderName;
				this.emailString = emailString;
			}

			@Override
			protected JSONObject doInBackground(String... params) {
				// TODO Auto-generated method stub
				JSONObject jsonObject=null;
				try {
					jsonObject = new JSONObject(AppUtil.doPutJsonObject(params[0], json));
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
				treeImageNameList.remove(imageName);
				if(treeImageNameList.size()==0){
					AppUtil.cancelProgressDialog();
					uploadKmlAndExcel(folderName,emailString);
				}else if((counter == size-1) &&  treeImageNameList.size()>0){
					AppUtil.cancelProgressDialog();
					doUploadActions(folderName,emailString);
				}
			}
		}

		private void uploadKmlAndExcel(String serverFoldername, String emailString) {
			// TODO Auto-generated method stub

			ArrayList<String> mimeTempList=new ArrayList<String>();
			String path =Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKLAP_TARU_CENSUS_REPORT_DIRECTORY")+frstName;
			File f=new File(path);
			if(f.exists()){
				for (File tempF : f.listFiles()) {
					if (tempF.isFile()){
						mimeTempList.add(tempF.getName());
					}
				}
			}

			AppUtil.initializeProgressDialog(AttendanceLogActivity.this, "Uploading Kml and Excel sheet", progressDialog);

			int listSize = mimeTempList.size();

			for (int i = 0; i < listSize; i++) {
				JSONObject js=new JSONObject();
				SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
				int uid=myPrefs.getInt("uid", -1);
				try {
					js.put("image", convertFileToBase64(path+"/"+mimeTempList.get(i)));
					js.put("action", "upload-census-img");
					js.put("foldername", serverFoldername);
					js.put("filename", mimeTempList.get(i));
					js.put("uid", uid);
					js.put("isImage", "No");
					if(i==1){
						js.put("sendEmail", "Yes");
						js.put("forestName", serverFoldername.split("/")[1]);
						js.put("email",emailString);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				new UploadKmlAndExcel(i,listSize,js,serverFoldername,mimeTempList).execute(AppUtil.getAppProperty().getProperty("TREE_ID_FETCH_URL")+23);
			}
		}
		private class UploadKmlAndExcel extends AsyncTask<String, Integer, JSONObject>{

			int i;
			int listSize;
			JSONObject js;
			String serverFoldername;
			ArrayList<String> mimeTempList;

			public UploadKmlAndExcel(int i, int listSize, JSONObject js, String serverFoldername, ArrayList<String> mimeTempList) {
				this.i = i;
				this.listSize = listSize;
				this.js = js;
				this.serverFoldername = serverFoldername;
				this.mimeTempList = mimeTempList;
			}
			@Override
			protected JSONObject doInBackground(String... params) {
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
				if(i == (listSize-1)){
					AppUtil.cancelProgressDialog();
					Toast.makeText(AttendanceLogActivity.this, frstName+ " census details has been processed successfully.", 2000).show();
				}
			}
		}

		private ArrayList<String> checkImage() {
			// TODO Auto-generated method stub
			ArrayList<String> imagesTempList=new ArrayList<String>();
			File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+AppUtil.getAppProperty().getProperty("SANKALP_TARU_CENSUS_DIRECTORY")+frstName);
			if(f.exists()){
				for (File tempF : f.listFiles()) {
					if (tempF.isFile()){
						imagesTempList.add(tempF.getName());
					}
				}
			}
			return imagesTempList;
		}

		private void processExcelAndKml() {
			// TODO Auto-generated method stub
			File recordReportFile=new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_CENSUS_REPORT_DIRECTORY")+frstName+"/"+frstName.replaceAll(" ", "")+".xls");
			File recordKmlFile =new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_CENSUS_REPORT_DIRECTORY")+frstName+"/"+frstName.replaceAll(" ", "")+".kml");

			try {
				if(recordReportFile.exists())
					recordReportFile.delete();

				if(recordKmlFile.exists())
					recordKmlFile.delete();

				recordReportFile.createNewFile();
				recordKmlFile.createNewFile();

				processCustomRow(recordReportFile);
				processCustomKml(recordKmlFile);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void processCustomKml(File recordKmlFile) {
			// TODO Auto-generated method stub
			String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
					"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"+"<Document>\n";

			String kmlelement = "";
			for (int i = 0; i < census_list.size(); i++) {
				kmlelement = kmlelement + "\t<Placemark>\n" +
						"\t<name>"+census_list.get(i).getTreename()+"</name>\n" +
						"\t<description>"+"Tree Height:"+census_list.get(i).getHeight()+"\nTree Girth:"+census_list.get(i).getGirth()+"\nCanopy Spread:"+census_list.get(i).getCanopy_size()+"\n"+"</description>\n" +
						"\t<Point>\n" +
						"\t\t<coordinates>"+census_list.get(i).getLongitude()+","+census_list.get(i).getLatitude()+"</coordinates>\n" +
						"\t</Point>\n" +
						"\t</Placemark>\n";
			}

			String kmlend = "</Document>\n"+"</kml>";

			ArrayList<String> content = new ArrayList<String>();
			content.add(0,kmlstart);
			content.add(1,kmlelement);
			content.add(2,kmlend);

			String kmltest = content.get(0) + content.get(1) + content.get(2);


			Writer fwriter;
			try {

				fwriter = new FileWriter(recordKmlFile);
				fwriter.write(kmltest);
				fwriter.flush();
				fwriter.close();
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   
		}

		private void processCustomRow(File recordReportFile){
			Workbook wb = new HSSFWorkbook();

			Cell c = null;

			//Cell style for header row
			CellStyle cs = wb.createCellStyle();
			cs.setFillForegroundColor(HSSFColor.WHITE.index);
			cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			//New Sheet
			Sheet sheet1 = null;
			sheet1 = wb.createSheet("Census");

			// Generate column headings
			Row row = sheet1.createRow(0);
			c = row.createCell(0);
			c.setCellValue("Tree Name");
			c.setCellStyle(cs);

			c = row.createCell(1);
			c.setCellValue("Latitude");
			c.setCellStyle(cs);

			c = row.createCell(2);
			c.setCellValue("Longitude");
			c.setCellStyle(cs);

			c = row.createCell(3);
			c.setCellValue("Tree Height");
			c.setCellStyle(cs);

			c = row.createCell(4);
			c.setCellValue("Tree Girth");
			c.setCellStyle(cs);

			c = row.createCell(5);
			c.setCellValue("Canopy Spread");
			c.setCellStyle(cs);

			c = row.createCell(6);
			c.setCellValue("Remarks");
			c.setCellStyle(cs);

			c = row.createCell(7);
			c.setCellValue("Created On");
			c.setCellStyle(cs);

			sheet1.setColumnWidth(0, (15 * 500));
			sheet1.setColumnWidth(1, (15 * 500));
			sheet1.setColumnWidth(2, (15 * 500));
			sheet1.setColumnWidth(4, (15 * 500));
			sheet1.setColumnWidth(5, (15 * 500));
			sheet1.setColumnWidth(6, (15 * 500));
			sheet1.setColumnWidth(7, (15 * 500));

			for (int i = 0; i < census_list.size(); i++) {
				Row rowData = sheet1.createRow(i+1);
				c = rowData.createCell(0);
				c.setCellValue(census_list.get(i).getTreename());
				c.setCellStyle(cs);

				c = rowData.createCell(1);
				c.setCellValue(census_list.get(i).getLatitude());
				c.setCellStyle(cs);

				c = rowData.createCell(2);
				c.setCellValue(census_list.get(i).getLongitude());
				c.setCellStyle(cs);

				c = rowData.createCell(3);
				c.setCellValue(census_list.get(i).getHeight());
				c.setCellStyle(cs);

				c = rowData.createCell(4);
				c.setCellValue(census_list.get(i).getGirth());
				c.setCellStyle(cs);

				c = rowData.createCell(5);
				c.setCellValue(census_list.get(i).getCanopy_size());
				c.setCellStyle(cs);

				c = rowData.createCell(6);
				c.setCellValue(census_list.get(i).getRemarks());
				c.setCellStyle(cs);

				c = rowData.createCell(7);
				c.setCellValue(census_list.get(i).getCreated_at());
				c.setCellStyle(cs);
				sheet1.setColumnWidth(0, (15 * 500));
				sheet1.setColumnWidth(1, (15 * 500));
				sheet1.setColumnWidth(2, (15 * 500));
				sheet1.setColumnWidth(4, (15 * 500));
				sheet1.setColumnWidth(5, (15 * 500));
				sheet1.setColumnWidth(6, (15 * 500));
				sheet1.setColumnWidth(7, (15 * 500));
			}


			// Create a path where we will place our List of objects on external storage 
			FileOutputStream os = null; 

			try { 
				os = new FileOutputStream(recordReportFile);
				wb.write(os);
				Log.w("FileUtils", "Writing file" + recordReportFile); 
			} catch (IOException e) { 
				Log.w("FileUtils", "Error writing " + recordReportFile, e); 
			} catch (Exception e) { 
				Log.w("FileUtils", "Failed to save file", e); 
			} finally { 
				try { 
					if (null != os) 
						os.close(); 
				} catch (Exception ex) { 
				} 
			} 
		}
	}

	private class ForestAdaptor extends BaseAdapter{

		List<Forest_Census> forestList;
		private LayoutInflater inflater;
		public ForestAdaptor(List<Forest_Census> list){
			forestList = list;
			inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return forestList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			View vi=convertView;
			if(convertView==null)
				vi = inflater.inflate(R.layout.direct_list_item, null);

			TextView listItemDuration = (TextView)vi.findViewById(R.id.listItemDuration);
			listItemDuration.setVisibility(View.GONE);

			TextView listItemDirection = (TextView)vi.findViewById(R.id.listItemDirection);
			listItemDirection.setText((arg0+1)+". "+forestList.get(arg0).getForest_name());

			TextView listItemDistance = (TextView)vi.findViewById(R.id.listItemDistance);
			listItemDistance.setTextColor(getResources().getColor(R.color.StTheme));
			listItemDistance.setText("Created On: "+forestList.get(arg0).getCreated_at());			

			return vi;
		}

	}

	private class LocationDialog extends DialogFragment{

		int counter;
		double lat;
		double lng;

		public LocationDialog(int counter, double lat, double lng) {
			// TODO Auto-generated constructor stub

			this.counter =counter;
			this.lat = lat;
			this.lng = lng;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub

			View rootView = inflater.inflate(R.layout.census_forest_dialog, container,
					false);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

			locationLayoutHandle(rootView,counter,lat,lng);

			return rootView;
		}
	}

	public void doCapturePhoto(View v){
		if (inPreview) {
			Log.e("doCapturePhoto", "doCapturePhoto");
			shutterSound.start();
			camera.takePicture(null, null, photoCallback);
			inPreview=false;
		}
	}

	private Bitmap original=null;
	private Bitmap resizedBitmap=null;

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
				new SavePhotoInServiceFailMode(lat,lng).execute(resized);
			}
			else{
				locationNullPrompt("Unfortunately device current Location can not be retrieved from any sources i.e. GPS,Network or last location.\n\n Go to phone location settings and check all option to access your location is ON like Access to my location,Google apps access your location,GPS satellites, and Google Location services etc.", "Warning", "", "OK");	
			}
			camera.startPreview();
			inPreview=true;
		}
	};

	class SavePhotoInServiceFailMode extends AsyncTask<byte[], String, String>{
		private double lat,lng;
		private int counter = 0;
		private Dialog progressDialog;
		public SavePhotoInServiceFailMode(double lat, double lng) {
			// TODO Auto-generated constructor stub
			this.lat=lat;
			this.lng=lng;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(AttendanceLogActivity.this, "", progressDialog);
		}
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File directory=new File(forestImagesPath);
			File[] list = directory.listFiles();
			counter = list.length+1;
			File photo=
					new File(forestImagesPath+"/"+(counter)+".jpg");
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
			AppUtil.cancelProgressDialog();
			openDetailsDialog(counter,lat,lng);
		}
	}

	private AlertDialog alertDialog = null;

	public void locationNullPrompt(String textToShow,final String title,String neutralButtonText,String positiveButtonText){

		AlertDialog.Builder localBuilder = new AlertDialog.Builder(AttendanceLogActivity.this);
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
				Intent	in=new Intent(AttendanceLogActivity.this, MenuActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
		});

		localButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				alertDialog .dismiss();
				Intent	in=new Intent(AttendanceLogActivity.this, MenuActivity.class);
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

	public void openDetailsDialog(int counter, double lat, double lng) {
		// TODO Auto-generated method stub
		locDialog = new LocationDialog(counter,lat,lng);
		locDialog.setCancelable(false);

		locDialog.show(getSupportFragmentManager(), "open");
	}
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


	public void locationLayoutHandle(View rootView, final int counter, final double lat, final double lng) {
		// TODO Auto-generated method stub

		ImageView pic = (ImageView)rootView.findViewById(R.id.censusTreeImg);
		Matrix mtx = new Matrix();
		mtx.postRotate(90);
		// Rotating Bitmap
		Bitmap normalBmp = BitmapFactory.decodeFile(forestImagesPath+"/"+counter+".jpg");
		Bitmap rotatedbm = Bitmap.createBitmap(normalBmp, 0, 0, normalBmp.getWidth(), normalBmp.getHeight(), mtx, true);
		normalBmp.recycle();
		pic.setImageBitmap(rotatedbm);

		final EditText treeEditText = (EditText)rootView.findViewById(R.id.treeEditText);
		final EditText treeHeightEditText = (EditText)rootView.findViewById(R.id.treeHeightEditText);
		final EditText treeGirthEditText = (EditText)rootView.findViewById(R.id.treeGirthEditText);
		final EditText treeCanopyEditText = (EditText)rootView.findViewById(R.id.treeCanopyEditText);
		final EditText remarksEditText = (EditText)rootView.findViewById(R.id.remarksEditText);



		Button proceedBtn = (Button)rootView.findViewById(R.id.proceedBtn);
		proceedBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("data of pic", ""+counter+" "+lat+" "+lng);

				String treeName = treeEditText.getText().toString().trim();
				String treeGirth = treeHeightEditText.getText().toString().trim();
				String treeHeight = treeGirthEditText.getText().toString().trim();
				String treeCanopy = treeCanopyEditText.getText().toString().trim();
				String remarks = remarksEditText.getText().toString().trim();

				if(treeName.length() > 0 && treeGirth.length() > 0 && treeHeight.length() >0 && treeCanopy.length() >0){
					//					generateReportOnFile(counter,lat,lng,treeName,treeGirth,treeHeight,treeCanopy,remarks);
					createRowInDBTable(counter,treeName,lat,lng,treeHeight,treeGirth,treeCanopy,remarks);
					locDialog.dismiss();
				}else{
					Toast.makeText(AttendanceLogActivity.this, "Please fill mandatory fields before submit.", 1000).show();
				}
			}
		});
	}


	private void createRowInDBTable(int counter, String treeName, double lat, double lng, String treeHeight,
			String treeGirth, String treeCanopy, String remarks) {
		// TODO Auto-generated method stub
		db = new DatabaseHelper(AttendanceLogActivity.this);
		long census_id= db.createCensus(new Census(treeName, ""+lat, ""+lng, ""+treeHeight, ""+treeGirth,""+Double.parseDouble(treeCanopy), remarks, (int)forest_id,forestName+"/"+counter+".jpg"));
		Log.e("census_id", ""+census_id+" "+forest_id);
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
		globalLocation =location;
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
}
