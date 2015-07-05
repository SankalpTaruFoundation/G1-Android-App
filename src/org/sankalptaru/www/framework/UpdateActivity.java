package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateActivity extends Activity {
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppUtil.catchExceptionsInActivity(UpdateActivity.this.getClass().getCanonicalName());
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updates_layout);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(UpdateActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		SharedPreferences prefs=getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

		getUpdates(prefs.getInt("uid", -1));
	}
	private void getUpdates(int uid) {
		// TODO Auto-generated method stub
		new GetUpdates(uid).execute(AppUtil.getAppProperty().getProperty("ST_UPDATES_URL"));
	}
	public void goToMenu(View v){
		Intent	in=new Intent(UpdateActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	private class GetUpdates extends AsyncTask<String, Integer, JSONArray>{
		int uid;
		public GetUpdates(int uid) {
			// TODO Auto-generated constructor stub
			this.uid=uid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONArray js = null;
			try {
				js = new JSONArray(AppUtil.getResponse(params[0]+uid));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			populateList(result);
		}
	}
	private void populateList(JSONArray result) {
		// TODO Auto-generated method stub
		ArrayList<String> updateList=new ArrayList<String>();
		ArrayList<String> dateList=new ArrayList<String>();
		for (int i = 0; i < result.length(); i++) {
			try {
				dateList.add(result.getJSONObject(i).optString("create_date"));
				updateList.add(result.getJSONObject(i).optString("display"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ListView updateListView=(ListView)findViewById(R.id.updatesList);
		updateListView.setAdapter(new UpdateListAdapter(updateList,dateList,UpdateActivity.this));

	}
	private class UpdateListAdapter extends BaseAdapter{
		ArrayList<String>updateList,dateList;
		private LayoutInflater inflater;

		public UpdateListAdapter(ArrayList<String> updateList,ArrayList<String> dateList, UpdateActivity updateActivity) {
			// TODO Auto-generated constructor stub
			this.updateList=updateList;
			this.dateList=dateList;
			inflater=(LayoutInflater)updateActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return updateList.size();
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
			View vi=convertView;
			if(convertView==null)
				vi = inflater.inflate(R.layout.update_list_item, null);
			TextView date = (TextView)vi.findViewById(R.id.dateInfo); 
			TextView update = (TextView)vi.findViewById(R.id.updateInfo); 
			date.setText(dateList.get(position));
			update.setText(updateList.get(position));
			return vi;

		}

	}
	@Override
	public void onBackPressed() {
			// TODO Auto-generated method stub
//			super.onBackPressed();
			Intent	in=new Intent(UpdateActivity.this, MenuActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			finish();
	}
}
