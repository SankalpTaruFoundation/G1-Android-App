package org.sankalptaru.www.framework;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReferFriendActivity extends Activity{
	private EditText email,confirmEmail;
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refer_friend);
		email=(EditText)findViewById(R.id.textUsername);
		confirmEmail=(EditText)findViewById(R.id.textPassword);
	}
	public void doReset(View v){
		email.setText("");
		email.requestFocus();
		confirmEmail.setText("");
	}
	private EditText[] getEditext(){
		EditText[] arrayOfEditText = {(EditText)findViewById(R.id.textUsername),(EditText)findViewById(R.id.textPassword)};
		return arrayOfEditText;
	}
	private boolean allfieldsAreFilled(EditText[] editext) {
		// TODO Auto-generated method stub
		boolean isFilled=true;
		for (int i = 0; i < editext.length; i++) {
			if(!(editext[i].getText().toString().trim().length()>0)){
				isFilled=false;
				break;
			}
		}
		return isFilled;
	}
	public void doReferFriend(View v){
		if(allfieldsAreFilled(getEditext())){
			String email=getEditext()[0].getText().toString();
			if(AppUtil.isEmailValid(email)){
				String initialPassword=getEditext()[0].getText().toString();
				String confirmPassword=getEditext()[1].getText().toString();
				if(initialPassword.equals(confirmPassword)){
					JSONObject js=new JSONObject();
					SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 

					int uid=myPrefs.getInt("uid", -1);
					try {
						js.put("email",email);
						js.put("uid", uid);
						new ReferFriend(js).execute(AppUtil.getAppProperty().getProperty("SANKALP_TARU_REFER_FRIEND"));
						//						AppUtil.getHttpPostDigestStatus(AppUtil.getAppProperty().getProperty("SANKALP_TARU_CREATE_USER"),AppUtil.getClassObject(LoginActivity.this),js);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				else{
					informatrionAlert("Email Address missmatch.",false);
				}
			}
			else{
				informatrionAlert("Please enter email in proper format.",false);
			}
		}
		else {
			informatrionAlert("All fields should be filled.",false);
		}
	}
	private class ReferFriend extends AsyncTask<String, Integer,JSONObject>{
		JSONObject js;
		public ReferFriend(JSONObject js) {
			// TODO Auto-generated constructor stub
			this.js=js;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.VISIBLE);
			((ProgressBar)findViewById(R.id.loginprogress)).bringToFront();
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsResponse = null;

			try {
				jsResponse=new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);
			if(result.optInt("status")==0){
				informatrionAlert("Reference completed succussfully",true);
			}
			else{
				informatrionAlert(result.optString("reason"),false);
			}
		}
	}
	public void informatrionAlert(String textToShow, final boolean b) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(ReferFriendActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localTextView2.setText(textToShow);
		localButton1.setText("     OK     ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				if(b){
					alertDialog.dismiss();
					Intent	in=new Intent(ReferFriendActivity.this, MenuActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
				else{
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
	public void goToMenu(View v){
		Intent	in=new Intent(ReferFriendActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	public void onBackPressed() {
			// TODO Auto-generated method stub
//			super.onBackPressed();
			Intent	in=new Intent(ReferFriendActivity.this, MenuActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			finish();
	}
}
