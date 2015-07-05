package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class NavigateActivity extends Activity implements OnItemClickListener{
	//	private static final int OPEN_MAP_DIALOG = 103;
	ArrayList<String>treeIdList;
	ArrayList<String> treeImageUrl;
	private RelativeLayout loadingLayout;
	private ArrayList<LatLng> treesLatLngList;
	private AlertDialog alertDialog;
	private Dialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		onCreateContent();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppUtil.catchExceptionsInActivity(NavigateActivity.this.getClass().getCanonicalName());
	}

	public void goToMenu(View v){
		Intent	in=new Intent(NavigateActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	private void onCreateContent(){
		setContentView(R.layout.navigate_layout);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(NavigateActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		loadingLayout=(RelativeLayout)findViewById(R.id.mainLoadingInclude);
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		int uid=myPrefs.getInt("uid", -1);
		new FetchForest().execute(AppUtil.getAppProperty().getProperty("MY_FOREST_URL")+""+uid);
		if(treeImageUrl==null)
			treeImageUrl=new ArrayList<String>();
		else {
			treeImageUrl.clear();
		}
		treesLatLngList=new ArrayList<LatLng>();
	}
	private class FetchForest extends AsyncTask<String, Integer, JSONObject>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AppUtil.initializeProgressDialog(NavigateActivity.this,"Processing your Forest.",progressDialog);
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
			for (int i = 0; i < result.optJSONArray("results").length(); i++) {
				try {
					tempList.add(result.optJSONArray("results").getJSONObject(i).optString("displayname"));
					treeIdList.add(result.optJSONArray("results").getJSONObject(i).optString("tid"));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			AppUtil.cancelProgressDialog();
			if(treeIdList.size()>0){
				loadingLayout.setVisibility(View.VISIBLE);
				ArrayList<String> allFieldsOkPlanterNameList=new ArrayList<String>();
				for (int i = 0; i < treeIdList.size(); i++) {
					new FetchTreeDetails(i,treeIdList.size(),tempList,allFieldsOkPlanterNameList).execute(AppUtil.getAppProperty().getProperty("MY_TREE_URL")+""+treeIdList.get(i));
				}
			}
			else{
				createAlertMessageDialogAndBackToMenu("Plant trees to navigate..!!", NavigateActivity.this);
			}
		}
	}
	private void createAlertMessageDialogAndBackToMenu(String textToShow,final NavigateActivity ac) {
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
				Intent activityIntent=new Intent(ac, MenuActivity.class);	
				startActivity(activityIntent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);	
				alertDialog.dismiss();
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
	public void addViewsOnParentContainer(ArrayList<String> tempList, ArrayList<String> treeImageUrl2) {
		// TODO Auto-generated method stub
		ListView treeList=(ListView)findViewById(R.id.navigateList);
		treeList.setAdapter(new NavigateListAdapter(NavigateActivity.this,tempList,treeImageUrl2));
		loadingLayout.setVisibility(View.INVISIBLE);
		treeList.setOnItemClickListener(this);
	}
	private class FetchTreeDetails extends AsyncTask<String, Integer, JSONObject>{
		int counter,listSize;
		ArrayList<String>tempList,allFieldsOkPlanterNameList;
		public FetchTreeDetails(int i, int size, ArrayList<String> tempList, ArrayList<String> allFieldsOkPlanterNameList) {
			// TODO Auto-generated constructor stub
			counter=i;
			listSize=size;
			this.tempList=tempList;
			this.allFieldsOkPlanterNameList=allFieldsOkPlanterNameList;
		}
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
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
			JSONArray imageArray=result.optJSONArray("images");
			try {
				if(!result.getString("latitude").equals("null")&&!result.getString("longitude").equals("null")&&imageArray.length()>0){
					treesLatLngList.add(new LatLng(Double.parseDouble(result.getString("latitude")), Double.parseDouble(result.getString("longitude"))));
					treeImageUrl.add(AppUtil.getAppProperty().getProperty("TREE_IMAGE_URL")+imageArray.getString(0));
					allFieldsOkPlanterNameList.add(tempList.get(counter));
				}
			}catch (JSONException e) {

				e.printStackTrace();
			}
			if((listSize-1)==counter){
				if(treesLatLngList.size()>0){
					addViewsOnParentContainer(allFieldsOkPlanterNameList,treeImageUrl);
				}
				else{
					Toast.makeText(NavigateActivity.this, "Failed to get navigation details.", 1000).show();
					finish();
				}
			}

		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.e("clicked", "out side clicked");

		Intent actIntent=new Intent(NavigateActivity.this, RouteMapActivity.class);
		actIntent.putExtra("treeLatitude", treesLatLngList.get(arg2).latitude);
		actIntent.putExtra("treeLongitude", treesLatLngList.get(arg2).longitude);
		startActivity(actIntent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		Intent	in=new Intent(NavigateActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
}
