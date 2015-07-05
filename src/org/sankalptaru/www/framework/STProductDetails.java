package org.sankalptaru.www.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class STProductDetails extends Activity {
	private static final int OPEN_DISCLAIMER_DIALOG = 100;
	private static final int OPEN_PROGRESS_DIALOG = 101;
	private ArrayList<String> productImagesUrlList;
	private Dialog viewDialogThird;
	private ProgressDialog mprogressDialog;
	private Dialog progressDialog;
	private RadioGroup rb;
	private ArrayList<JSONObject> locationsJsonObjectList;
	private String currentDialogSelection;
	private File direct;
	private AlertDialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		showActivityView();
	}
	private void showActivityView(){
		setContentView(R.layout.product_layout);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(STProductDetails.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		if(null==productImagesUrlList)
			productImagesUrlList=new ArrayList<String>();
		else
			productImagesUrlList.clear();
		addPaths();
	}
	private void addPaths() {
		// TODO Auto-generated method stub
		productImagesUrlList.add("@app_sites@Himalayas@1&jpg");
		productImagesUrlList.add("@app_sites@Himalayas@2&jpg");
		productImagesUrlList.add("@app_sites@Ladakh@1&jpg");
		productImagesUrlList.add("@app_sites@Ladakh@2&jpg");
		productImagesUrlList.add("@app_sites@Ladakh@3&jpg");
		productImagesUrlList.add("@app_sites@Biodiversity@1&jpg");
		productImagesUrlList.add("@app_sites@Deccan@1&jpg");
		productImagesUrlList.add("@app_sites@Deccan@2&jpg");
		productImagesUrlList.add("@app_sites@Deccan@3&jpg");
		productImagesUrlList.add("@app_sites@Thar@1&jpg");
		productImagesUrlList.add("@app_sites@Thar@2&jpg");
		productImagesUrlList.add("@app_sites@Thar@3&jpg");
		direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY"));
		if(!direct.exists()){
			currentDialogSelection="normal";
			showDialog(OPEN_DISCLAIMER_DIALOG);
		}
		else
			doGridPopulateActions();
	}
	private void doGridPopulateActions() {
		// TODO Auto-generated method stub
		rb=(RadioGroup)findViewById(R.id.productsGroup);
		showViewAccordingToView();
		rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				showViewAccordingToView();
			}
		});
	}
	private void showViewAccordingToView() {
		// TODO Auto-generated method stub
		if(rb.getCheckedRadioButtonId()==R.id.radioLocation){
			GridView gv=(GridView)findViewById(R.id.productGrid);
			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					createConfirmationDialog("Redirect to Payment page?", "Confirmation");
				}
			});
			gv.setNumColumns(1);
			new GetResponseAsync(gv).execute(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+"OM111111");
		}
		else if (rb.getCheckedRadioButtonId()==R.id.radioProduct) {
			currentDialogSelection="zodiac";
			showDialog(OPEN_DISCLAIMER_DIALOG);
		}
	}
	class GetResponseAsync extends AsyncTask<String, Integer,JSONObject>{
		GridView gv;

		public GetResponseAsync(GridView gv) {
			// TODO Auto-generated constructor stub
			this.gv=gv;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(STProductDetails.this, "Fetching sites available...",progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject js=null;
			try {
				js=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			//			tempJSObject=new JSONObject(AppUtil.getResponse(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+enteredCode));
			Log.e("data",""+ result);
			AppUtil.cancelProgressDialog();
			if(null==locationsJsonObjectList)
				locationsJsonObjectList=new ArrayList<JSONObject>();
			else
				locationsJsonObjectList.clear();
			for (int i = 0; i < result.optJSONArray("locations").length(); i++) {
				try {
					locationsJsonObjectList.add(result.optJSONArray("locations").getJSONObject(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			gv.setAdapter(new LocationSites(locationsJsonObjectList,STProductDetails.this,AppUtil.getScreenWidth()));
		}
	}
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case OPEN_DISCLAIMER_DIALOG:
			final View tempDialogView = getLayoutInflater().inflate(
					R.layout.disclaimer_layout, null);
			viewDialogThird.setContentView(tempDialogView);
			TextView disc=(TextView)tempDialogView.findViewById(R.id.disclaimer);
			if(null!=currentDialogSelection){
			if(currentDialogSelection.equals("normal"))
				disc.setText(getResources().getString(R.string.downloadSitesDisclaimer));
			else
				disc.setText(getResources().getString(R.string.zodiacDisclaimer));
			}
			else{
				currentDialogSelection="zodiac";
				RadioButton rbLocation=(RadioButton)findViewById(R.id.radioProduct);
				rbLocation.setChecked(true);
				disc.setText(getResources().getString(R.string.zodiacDisclaimer));
			}
			Button openFootPrint=(Button)tempDialogView.findViewById(R.id.openFootPrint);
			openFootPrint.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogThird.dismiss();
					if(currentDialogSelection.equals("normal"))
						downloadSupportActions();
					else{
						GridView gv=(GridView)findViewById(R.id.productGrid);
						gv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
								// TODO Auto-generated method stub
								createConfirmationDialog("Redirect to Payment page?", "Confirmation");
							}
						});
						gv.setNumColumns(2);
						int zodiacDrawable[]={R.drawable.zodiac_aquarius,R.drawable.zodiac_aries,R.drawable.zodiac_cancer,R.drawable.zodiac_capricorn,R.drawable.zodiac_gemini,R.drawable.zodiac_leo,R.drawable.zodiac_libra,R.drawable.zodiac_pisces,R.drawable.zodiac_sagittarius,R.drawable.zodiac_scorpio,R.drawable.zodiac_taurus,R.drawable.zodiac_virgo};
						String zodiacTreeType[]={"Shami","Amla","Peepal","Shami","Khair","Banyan","Arjun","Mango","Sarj (Saal) or Jalvetas","Pine","Jamun","Reetha"};
						gv.setAdapter(new ZodiacAdaptor(zodiacDrawable,zodiacTreeType,STProductDetails.this));
					}
				}
			});
			ImageView close1=(ImageView)tempDialogView.findViewById(R.id.close);
			close1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewDialogThird.dismiss();
					if(currentDialogSelection.equals("normal"))
						back();
					else
					{
						RadioButton rbLocation=(RadioButton)findViewById(R.id.radioLocation);
						rbLocation.setChecked(true);
					}
				}
			});

			break;

		default:
			break;
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case OPEN_PROGRESS_DIALOG:
			mprogressDialog = new ProgressDialog(this);
			mprogressDialog.setMessage("Downloading Data...\n\n Thanks for the patience..!!");
			mprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mprogressDialog.setCancelable(false);
			mprogressDialog.show();
			return mprogressDialog;

		case OPEN_DISCLAIMER_DIALOG:
			viewDialogThird = new Dialog(this);
			viewDialogThird.setCancelable(false);
			viewDialogThird.requestWindowFeature(Window.FEATURE_NO_TITLE);
			viewDialogThird.setContentView(R.layout.disclaimer_layout);
			viewDialogThird.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			return viewDialogThird;

		default:
			break;
		}
		return null;
	}
	protected void downloadSupportActions() {
		// TODO Auto-generated method stub
		makeDirectory(AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY"));
		showDialog(OPEN_PROGRESS_DIALOG);
		for (int i = 0; i < productImagesUrlList.size(); i++) {
			new DownloadTask(productImagesUrlList.size(),i,productImagesUrlList.get(i)).execute(AppUtil.getAppProperty().getProperty("SANKALPTARU_DOWNLOAD_ANYIMAGE_URL")+productImagesUrlList.get(i));
		}
	}
	private class DownloadTask extends AsyncTask<String, Integer, JSONObject>{
		int counter,numberOfImages;
		String imageNameUrl;

		public DownloadTask(int size, int i, String imageNameUrl) {
			// TODO Auto-generated constructor stub
			counter=i;
			numberOfImages=size;
			this.imageNameUrl=imageNameUrl;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jsonObject = null;
			publishProgress((int) ((counter / (float) numberOfImages) * 100));
			try {
				jsonObject = new JSONObject(AppUtil.getResponse(params[0]));
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
			//			Log.e("dbsksa", ""+result);
			saveImage(result,imageNameUrl);
			if(counter==numberOfImages-1){
				mprogressDialog.dismiss();
				doGridPopulateActions();
			}
		}
	}
	private void makeDirectory(String path) {
		// TODO Auto-generated method stub
		File direct = new File(Environment.getExternalStorageDirectory(),path);

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				Log.e("created", "created");
			}

		}
	}
	public void saveImage(JSONObject result, String imageNameUrl) {
		// TODO Auto-generated method stub
		String imageName=imageNameUrl.split("@")[3].replaceAll("@","");
		makeDirectory(AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY")+"/"+imageNameUrl.split("@")[2].replaceAll("@",""));
		decodeStringAndSave(imageName.replaceFirst("&", "."),result,AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY")+"/"+imageNameUrl.split("@")[2].replaceAll("@",""));
	}
	private void decodeStringAndSave(String imageName, JSONObject result, String currentDirectory) {
		// TODO Auto-generated method stub
		byte[] pdfAsBytes = Base64.decode(result.optString("decode"), 0);

		File filePath = new File(Environment.getExternalStorageDirectory()+currentDirectory+"/"+imageName);
		FileOutputStream os;
		try {
			os = new FileOutputStream(filePath, true);
			os.write(pdfAsBytes);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKALP_TARU_UPLOAD_SITE_IMAGES_DIRECTORY"));
	}
	public void goToMenu(View v){
		back();
	}
	public void back(){
		Intent	in=new Intent(STProductDetails.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	public void onBackPressed() {
		back();
	}
	private void goToPaymentPage(){
		Intent activityIntent=new Intent(STProductDetails.this,PaymentActivity.class);
		startActivity(activityIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	private void createConfirmationDialog(String textToShow, String title){
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(STProductDetails.this);
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
				goToPaymentPage();
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
}
