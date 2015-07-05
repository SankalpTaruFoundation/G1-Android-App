package org.sankalptaru.www.framework;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public class FetchRequestAsynTask extends AsyncTask<String, Integer, String> {
	StartActivity studentActivity;
	public FetchRequestAsynTask(Context context) {
		super();
		// TODO Auto-generated constructor stub
		studentActivity=(StartActivity)context;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		((ProgressBar)studentActivity.findViewById(R.id.progress)).setVisibility(View.VISIBLE);
//		AppUtil.initializeProgressDialog(studentActivity, "Fetching Data...");
	}
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		return AppUtil.getResponse(params[0]);
	}
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
//		AppUtil.cancelProgressDialog();
		studentActivity.updateData(result);
	}
}
