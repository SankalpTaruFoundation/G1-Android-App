package org.sankalptaru.www.framework;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartActivity extends Activity implements IHttpDigestCompletionInterface {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.st_splash);
		
		TextView skip=(TextView)findViewById(R.id.skip);
		skip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToMenu(1000);
			}
		});
		
		SharedPreferences	myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = pref.edit();
		if(myPrefs.getBoolean("firstTime", true)){
			editor.putBoolean("firstTime", false);
			editor.putString("serviceFailedCounter", "0");
			editor.commit();
		}
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(StartActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		new FetchRequestAsynTask(StartActivity.this).execute(AppUtil.getAppProperty().getProperty("SANKALP_TARU_PLANTCOUNT_URL"));
		createFolderInSDCard();
	}
	@Override
	protected void onResume() {
		super.onResume();
		//		AppUtil.getHttpGetDigestStatus(AppUtil.getAppProperty().getProperty("SANKALP_TARU_PLANTCOUNT_URL"),AppUtil.getClassObject(StartActivity.this));
		
	}

	private void createFolderInSDCard() {
		File direct = new File(Environment.getExternalStorageDirectory(),AppUtil.getAppProperty().getProperty("SANKLAP_TARU_LOG_DIRECTORY"));

		if(!direct.exists())
		{
			if(direct.mkdirs()) 
			{
				Log.e("created", "created");
			}

		}
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();	
	}
	public void updateData(String string) {
		Log.e("tree count", " "+string);
		if(null!=string){
			TextView plantationDatatextTextView=(TextView)findViewById(R.id.plantCountView);
			TextView staticText=(TextView)findViewById(R.id.staticText);
			plantationDatatextTextView.setVisibility(View.VISIBLE);
			staticText.setVisibility(View.VISIBLE);
			plantationDatatextTextView.setTypeface(AppUtil.getFont(StartActivity.this));
			try {
				String treesPlantedNumber=new JSONObject(string).optString("count");
				SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString("TREE_COUNT", treesPlantedNumber);
				editor.commit();
				plantationDatatextTextView.setText(treesPlantedNumber);
				((ProgressBar)findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			goToMenu(3500);
		}
	}

	private void goToMenu(long time) {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			public void run() {

				Intent intent = new Intent();
				intent.setClass(StartActivity.this, MenuActivity.class);

				StartActivity.this.startActivity(intent);
				StartActivity.this.finish();

				// transition from splash to main menu
				overridePendingTransition(R.anim.fadein,
						R.anim.fadeout);

			}
		}, time);
	}
	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
		updateData(result);
	}
}

