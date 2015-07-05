package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnItemClickListener,IHttpDigestCompletionInterface{
	private HashMap<String, Integer> nameDrawableMap;
	private int currentLoginContext;
	private TextView registerTextView;
	private TextView forgotTextView;
	private WebView login;
	private String str;
	private AlertDialog alertDialog;
	private ArrayList<String> listOfLoginOrderedList;
	public String getStr() {
		return str;
	}
	public void setStr(final String str, final LoginActivity la) {
		Log.e("data str", str);
		this.str = str;

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				doPostWebLoginActions(la,str);
			}
		});
	}
	protected void doPostWebLoginActions(LoginActivity la, String stripHtml) {
		// TODO Auto-generated method stub
		if(stripHtml.length()>0){
			Toast.makeText(la, "Login Successful", 1000).show();
			//la.login.freeMemory();
			//la.login.destroy();
			try {
				la.doPostLoginActions(new JSONObject(str), "",false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Toast.makeText(la, "login Failed", 1000).show();
			la.login.freeMemory();
			la.login.destroy();
			la.populateLoginLayout();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateLoginLayout();
	}
	private void populateLoginLayout() {
		setContentView(R.layout.loginlayout);
		goTologinView();
		registerTextView=(TextView)findViewById(R.id.registerTextView);
		forgotTextView=(TextView)findViewById(R.id.forgotTextview);
		registerTextView.setText(Html.fromHtml("<u><b>"+"Register"+"</u></b>"));
		forgotTextView.setText(Html.fromHtml("<u><b>"+"Forgot Password ?"+"</u></b>"));
		forgotTextView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					forgotTextView.setTextColor(Color.BLACK);
					break;
				case MotionEvent.ACTION_UP:
					forgotTextView.setTextColor(Color.parseColor("#3a8300"));
					openForgotPasswordView();
					break;

				default:
					break;
				}

				return true;
			}
		});
		registerTextView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					registerTextView.setTextColor(Color.BLACK);
					break;
				case MotionEvent.ACTION_UP:
					registerTextView.setTextColor(Color.parseColor("#3a8300"));
					openRegisterView();
					break;

				default:
					break;
				}

				return true;
			}


		});
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(LoginActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		nameDrawableMap=new HashMap<String, Integer>();
		//		nameDrawableMap.put("Twitter", R.drawable.twitter);
		//		nameDrawableMap.put("Facebook", R.drawable.fb);
		//		nameDrawableMap.put("Yahoo Mail", R.drawable.yahoo);
		//		nameDrawableMap.put("Gmail", R.drawable.gmail);
		nameDrawableMap.put("Sankalp Taru", R.drawable.st);
		nameDrawableMap.put("Guest Login", R.drawable.guest);
		nameDrawableMap.put("Gmail", R.drawable.gmail);
		nameDrawableMap.put("Facebook", R.drawable.fb);
		if(null==listOfLoginOrderedList)
			listOfLoginOrderedList=new ArrayList<String>();
		else
			listOfLoginOrderedList.clear();

		listOfLoginOrderedList.add("Sankalp Taru");
		//		listOfLoginOrderedList.add("Facebook");
		//		listOfLoginOrderedList.add("Twitter");
		//		listOfLoginOrderedList.add("Gmail");
		//		listOfLoginOrderedList.add("Yahoo Mail");
		listOfLoginOrderedList.add("Guest Login");
		listOfLoginOrderedList.add("Gmail");
		listOfLoginOrderedList.add("Facebook");
		showDialog(100);
	}
	protected void openChangePasswordLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.change_password);
		goTologinView();
	}
	protected void openForgotPasswordView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.forgot_password);
		goTologinView();
	}
	public void doChangePassword(View v){
		if(allfieldsAreFilled(getEditext())){
			if(!getEditext()[1].getText().toString().equals(getEditext()[2].getText().toString())){
				JSONObject js=new JSONObject();
				try {
					js.put("username", getEditext()[0].getText().toString());
					js.put("reset", 1);
					js.put("oldpassword", getEditext()[1].getText().toString());
					js.put("newpassword",  getEditext()[2].getText().toString());
					new ResetActionTask(js).execute(AppUtil.getAppProperty().getProperty("SANKAL_TARU_LOGIN_URL"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				informatrionAlert("Old and new password should not be same.");
			}
		}
	}
	public void doResetPassword(View v){
		EditText emailId=(EditText)findViewById(R.id.textResetEmail);
		String email=emailId.getText().toString();
		if(AppUtil.isEmailValid(email)){
			JSONObject js=new JSONObject();
			try {
				js.put("username", email);
				js.put("reset", 1);
				new ResetActionTask(js).execute(AppUtil.getAppProperty().getProperty("SANKAL_TARU_LOGIN_URL"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			informatrionAlert("Please enter email in proper format.");
		}
	}
	private void goTologinView(){
		final TextView backToLogin=(TextView)findViewById(R.id.backToLogin);
		backToLogin.setVisibility(View.VISIBLE);
		backToLogin.setText(Html.fromHtml("<u><b>"+"More login options"+"</u></b>"));
		backToLogin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					backToLogin.setTextColor(Color.BLACK);
					break;
				case MotionEvent.ACTION_UP:
					backToLogin.setTextColor(Color.parseColor("#3a8300"));
					populateLoginLayout();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	private void openRegisterView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.register);
		goTologinView();
	}

	private EditText[] getEditext(){
		EditText[] arrayOfEditText = {(EditText)findViewById(R.id.textEmail),(EditText)findViewById(R.id.textInitialPassword),(EditText)findViewById(R.id.textConfirmPassword)};
		return arrayOfEditText;
	}
	public void doRegister(View v){
		if(allfieldsAreFilled(getEditext())){
			String email=getEditext()[0].getText().toString();
			if(AppUtil.isEmailValid(email)){
				String initialPassword=getEditext()[1].getText().toString();
				String confirmPassword=getEditext()[2].getText().toString();
				if(initialPassword.equals(confirmPassword)){
					JSONObject js=new JSONObject();
					try {
						js.put("username",email);
						js.put("password",  confirmPassword);
						new CreateUser(js).execute(AppUtil.getAppProperty().getProperty("SANKALP_TARU_CREATE_USER"));
						//						AppUtil.getHttpPostDigestStatus(AppUtil.getAppProperty().getProperty("SANKALP_TARU_CREATE_USER"),AppUtil.getClassObject(LoginActivity.this),js);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				else{
					informatrionAlert("Password missmatch.");
				}
			}
			else{
				informatrionAlert("Please enter email in proper format.");
			}
		}
		else {
			informatrionAlert("All fields should be filled.");
		}
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
	public void doResetRegister(View v){
		for (int i = 0; i < getEditext().length; i++) {
			getEditext()[i].setText("");
		}
		getEditext()[0].requestFocus();
	}
	public void doReset(View v){
		((EditText)findViewById(R.id.textUsername)).setText("");
		((EditText)findViewById(R.id.textUsername)).requestFocus();
		((EditText)findViewById(R.id.textPassword)).setText("");

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppUtil.catchExceptionsInActivity(LoginActivity.this.getClass().getCanonicalName());
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		System.out.println("inside create dialog : " + id);
		final Dialog pickAlphabetDialog = new Dialog(this);
		pickAlphabetDialog.setCancelable(false);
		pickAlphabetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pickAlphabetDialog.setContentView(R.layout.picklogintype);

		final View pickAlphabetView = getLayoutInflater().inflate(
				R.layout.picklogintype, null);
		pickAlphabetDialog.setContentView(pickAlphabetView);

		ListView listView = (ListView) pickAlphabetView.findViewById(R.id.list);
		DataAdapter dataAdapter=new DataAdapter(LoginActivity.this,nameDrawableMap,listOfLoginOrderedList);
		listView.setAdapter(dataAdapter);
		listView.setOnItemClickListener(this);
		ImageView closeDialog=(ImageView)pickAlphabetView.findViewById(R.id.cancelTreeDialog);
		closeDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickAlphabetDialog.dismiss();
			}
		});
		return pickAlphabetDialog;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			currentLoginContext=position;
			dismissDialog(100);
			break;
		case 1:
			dismissDialog(100);
			((TextView)findViewById(R.id.passwordTextView)).setVisibility(View.GONE);
			((EditText)findViewById(R.id.textPassword)).setVisibility(View.GONE);
			registerTextView.setVisibility(View.GONE);
			forgotTextView.setVisibility(View.GONE);
			currentLoginContext=2;
			//			setContentView(R.layout.login_webview);
			//			goTologinView();
			//			loadLoginPage("GMAIL_LOGIN",position);
			//			dismissDialog(100);
			//			setContentView(R.layout.login_webview);
			//			goTologinView();
			//			loadLoginPage("FACEBOOK_LOGIN",position);
			//			dismissDialog(100);
			break;
		case 2:
			setContentView(R.layout.login_webview);
			goTologinView();
			loadLoginPage("GMAIL_LOGIN",position);
			dismissDialog(100);
			//			((TextView)findViewById(R.id.passwordTextView)).setVisibility(View.GONE);
			//			((EditText)findViewById(R.id.textPassword)).setVisibility(View.GONE);
			//			registerTextView.setVisibility(View.GONE);
			//			forgotTextView.setVisibility(View.GONE);
			//			currentLoginContext=position;
			//			setContentView(R.layout.login_webview);
			//			goTologinView();
			//			loadLoginPage("TWITTER_LOGIN",position);
			//			dismissDialog(100);
			break;
		case 3:
			setContentView(R.layout.login_webview);
			goTologinView();
			loadLoginPage("FACEBOOK_LOGIN",position);
			dismissDialog(100);
			//			setContentView(R.layout.login_webview);
			//			goTologinView();
			//			loadLoginPage("GMAIL_LOGIN",position);
			//			dismissDialog(100);
			break;
		case 4:
			//			setContentView(R.layout.login_webview);
			//			goTologinView();
			//			loadLoginPage("YAHOO_LOGIN",position);
			//			dismissDialog(100);
			break;
		case 5:
			//			dismissDialog(100);
			//			((TextView)findViewById(R.id.passwordTextView)).setVisibility(View.GONE);
			//			((EditText)findViewById(R.id.textPassword)).setVisibility(View.GONE);
			//			registerTextView.setVisibility(View.GONE);
			//			forgotTextView.setVisibility(View.GONE);
			//			currentLoginContext=position;
			break;

		default:
			break;
		}
	}
	public void goToMenu(View v){
		Intent	in=new Intent(LoginActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadLoginPage(String urls, int position) {
		Log.e("position", ""+position+" "+urls);
		login=(WebView)findViewById(R.id.loginWebview);
		login.getSettings().setLoadWithOverviewMode(true);
		login.getSettings().setUseWideViewPort(true);
		login.getSettings().setBuiltInZoomControls(true);
		
		String url=AppUtil.getAppProperty().getProperty(urls);
		Log.e("sams", url);
		
		login.loadUrl(url);
		login.getSettings().setJavaScriptEnabled(true);
		final JavascriptInterace iface=new JavascriptInterace();
		login.addJavascriptInterface(iface, "droid");
		final CookieManager cookieMngr = CookieManager.getInstance();
		cookieMngr.removeAllCookie();
		cookieMngr.acceptCookie();

		login.getSettings().setUserAgentString("ST_APP");
		final String finishProbaleUrl=AppUtil.getAppProperty().getProperty(position+"_SUCCESS_FINISH_URL");
		final ProgressBar paymentProgress=(ProgressBar)findViewById(R.id.paymentprogress);
		login.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				paymentProgress.setVisibility(View.INVISIBLE);
				Log.e("url finish", url);
				if(url.length()>finishProbaleUrl.length()&&finishProbaleUrl.equals(url.substring(0, finishProbaleUrl.length()))){
					String uid=cookieMngr.getCookie(url).split("uid=")[1].split(";")[0];
					new DownloadHtml().execute(AppUtil.getAppProperty().getProperty("MY_FOREST_URL")+uid);
				}
			}
			@Override
			public void onPageStarted(WebView view, String url,Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				AppUtil.setCookie(cookieMngr,url,-1);
				paymentProgress.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView  view, String  url)
			{
				return false;
			}
		});
	}
	public class DownloadHtml extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String str=null;
			try {
				str= AppUtil.downloadUrl(params[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return str;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				doPostLoginActions(new JSONObject(result).optJSONObject("user"), "",false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class JavascriptInterace {
		String str;
		LoginActivity la;
		public void print(String data) {
			data ="<html>"+data+"</html>";
			str=stripHtml(data);
			la.setStr(str,getContext());
		}
		public void setContext(LoginActivity loginActivity) {
			// TODO Auto-generated method stub
			la=(LoginActivity)loginActivity;
		}
		private LoginActivity getContext(){
			return la;
		}
	}
	public String stripHtml(String html) {
		return Html.fromHtml(html).toString();
	}
	public void doLogin(View v){
		EditText usernameEditText=(EditText)findViewById(R.id.textUsername);
		EditText passwordEditText=(EditText)findViewById(R.id.textPassword);
		JSONObject js=new JSONObject();
		int uid=-1;
		if(currentLoginContext==0){
			if(usernameEditText.getText().toString().trim()!=null&&usernameEditText.getText().toString().trim().length()>0&&passwordEditText.getText().toString().trim()!=null&&passwordEditText.getText().toString().trim().length()>0){
				((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.VISIBLE);
				((ProgressBar)findViewById(R.id.loginprogress)).bringToFront();

				putLoginContextOne(usernameEditText,js,uid,passwordEditText);
			}
			else {
				if(usernameEditText.getText().toString().trim().length()==0&&passwordEditText.getText().toString().trim().length()==0){
					informatrionAlert("Enter username and password to continue.");
				}
				else{
					if(usernameEditText.getText().toString().trim().length()==0){
						informatrionAlert("Enter email or username to continue.");
					}
					else {
						informatrionAlert("Enter password to continue.");
					}
				}
			}
		}
		else if (currentLoginContext==2) {
			if(usernameEditText.getText().toString().trim()!=null&&usernameEditText.getText().toString().trim().length()>0){
				((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.VISIBLE);
				((ProgressBar)findViewById(R.id.loginprogress)).bringToFront();
				putLoginContextTwo(usernameEditText,js,uid);
			}
			else {
				informatrionAlert("Enter email or username to continue.");
			}
		}
		//		new FetchDetails(currentLoginContext,usernameEditText,passwordEditText,uid,js).execute();
	}
	private void putLoginContextOne(EditText usernameEditText, JSONObject js,int uid, EditText passwordEditText) {
		try {
			js.put("username", usernameEditText.getText().toString());
			js.put("password", passwordEditText.getText().toString());
			//			AppUtil.getHttpPutDigestStatus(AppUtil.getAppProperty().getProperty("SANKAL_TARU_LOGIN_URL"),AppUtil.getClassObject(LoginActivity.this),js);

			new FetchDetails(js,usernameEditText.getText().toString()).execute(AppUtil.getAppProperty().getProperty("SANKAL_TARU_LOGIN_URL"));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	private void putLoginContextTwo(EditText usernameEditText, JSONObject js, int uid) {
		try {
			String emailEntered=usernameEditText.getText().toString();
			if(AppUtil.isEmailValid(emailEntered)){
				if(emailEntered.toLowerCase().equals("none@sankalptaru.org")){
					SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
					SharedPreferences.Editor editor = pref.edit();
					Intent in=new Intent(LoginActivity.this, MenuActivity.class);
					editor.putInt("uid",-2);
					editor.commit();
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
				else{
					js.put("username",emailEntered);
					new FetchDetails(js,emailEntered).execute(AppUtil.getAppProperty().getProperty("GUEST_LOGIN_URL"));
					//				AppUtil.getHttpPutDigestStatus(AppUtil.getAppProperty().getProperty("GUEST_LOGIN_URL"),AppUtil.getClassObject(LoginActivity.this),js);
				}
			}
			else {
				((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);
				informatrionAlert("Please enter email in proper format.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private class FetchDetails extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js=null;
		String username;
		public FetchDetails(JSONObject js, String username) {
			// TODO Auto-generated constructor stub
			this.js=js;
			this.username=username;
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jsonresponse = null;
			try {
				jsonresponse=new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonresponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			Log.e("data doPostLoginActions",""+ result);
			doPostLoginActions(result,username,true);
		}
	}
	public void doPostLoginActions(JSONObject result, String username, boolean isNormalLogin) {
		// TODO Auto-generated method stub
		int uid=result.optInt("uid");
		SharedPreferences pref = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = pref.edit();
		if(uid!=-1){
			try {
				if(isNormalLogin){
					if(result.optString("reason").equals("Logged in as guest contributor")){
						doLoginActions(username,uid,editor);
					}
					else{
						getRoles(result,editor,true);
						SharedPreferences	myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
						if(myPrefs.getString("role", "normal").equals("ground")){
							String fieldResetPasswordFlag=result.optJSONObject("user").optJSONObject("field_reset_password").optJSONArray("und").getJSONObject(0).optString("value");
							Log.e("fieldResetPasswordFlag", ""+fieldResetPasswordFlag);
							if(fieldResetPasswordFlag.equals("1")){
								informatrionAlert("Change password before login.");
								setContentView(R.layout.change_password);
//								getEditext()[0].setText(username);
//								getEditext()[0].setFocusable(false);
//								getEditext()[0].setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//								getEditext()[0].setClickable(false); 
							}
							else{
								doLoginActions(username,uid,editor);
							}
						}
						else{
							String fieldResetPasswordFlag=result.optJSONObject("user").optJSONObject("field_reset_password").optJSONArray("und").getJSONObject(0).optString("value");
							Log.e("fieldResetPasswordFlag", ""+fieldResetPasswordFlag);
							if(fieldResetPasswordFlag.equals("1")){
								informatrionAlert("Change password before login.");
								setContentView(R.layout.change_password);
//								getEditext()[0].setText(username);
//								getEditext()[0].setFocusable(false);
//								getEditext()[0].setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//								getEditext()[0].setClickable(false); 
							}
							else{
								doLoginActions(username,uid,editor);
							}
						}
					}
				}
				else{
					Log.e("id else", ""+uid);
					Log.e("result", ""+result);
					getRoles(result,editor,false);
					Intent in=new Intent(LoginActivity.this, MenuActivity.class);
					editor.putString("username", result.optString("init"));
					editor.putInt("uid",uid);
					editor.commit();
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);
			informatrionAlert("Incorrect username or password");
		}

	}
	private void doLoginActions(String username, int uid, Editor editor) {
		// TODO Auto-generated method stub
		Toast.makeText(LoginActivity.this, "Successfull Login.", 1000).show();
		((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);
		Intent in=new Intent(LoginActivity.this, MenuActivity.class);
		editor.putString("username", username);
		editor.putInt("uid",uid);
		editor.commit();
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	private void getRoles(JSONObject result, Editor editor, boolean isNormalUser) {
		Log.e("login Response", result.toString());
		editor.putString("role", "normal");
		editor.putBoolean("isAutoPlanter", true);
		JSONObject userJsObject=new JSONObject();
		if(isNormalUser){
			userJsObject=result.optJSONObject("user");
			doRolesActions(editor,userJsObject);
		}
		else{
			doRolesActions(editor,result);
		}
	}
	private void doRolesActions(Editor editor, JSONObject userJsObject) {
		// TODO Auto-generated method stub

		String locID;
		try {
			for (int i = 0; i < userJsObject.optJSONObject("roles").names().length(); i++) {
				if(userJsObject.optJSONObject("roles").optString(userJsObject.optJSONObject("roles").names().getString(i)).equals("Ground Operations")){
					editor.putString("role", "ground");
					locID=userJsObject.optJSONObject("field_planting_locations").optJSONArray("und").getJSONObject(0).optString("value");	
					String userPlanterRole=userJsObject.optJSONObject("field_manual_planter").optJSONArray("und").getJSONObject(0).optString("value");
					Log.e("userPlanterRole", ""+userPlanterRole);
					if(userPlanterRole.equals("1")){
						editor.putBoolean("isAutoPlanter", false);
					}
					editor.putString("locationsIdList", locID);
					break;
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.commit();
	}
	private class CreateUser extends AsyncTask<String, Integer, JSONObject>{
		private JSONObject js;
		public CreateUser(JSONObject js) {
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
			JSONObject jsResponse=null;
			try {
				jsResponse=new JSONObject(AppUtil.doPostJsonObject(params[0], js));
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
			Log.e("response", ""+result);

			((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);

			if(result.optInt("status")==1){
				informatrionAlert(result.optString("reason"));
			}
			else{
				doPostLoginActions(result,js.optString("username"),true);
			}
		}
	}
	private class ResetActionTask extends AsyncTask<String, Integer, JSONObject>{
		JSONObject js;
		public ResetActionTask(JSONObject js) {
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
			JSONObject jsResponse=null;
			try {
				jsResponse=new JSONObject(AppUtil.doPutJsonObject(params[0], js));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.e("response", ""+result);
			((ProgressBar)findViewById(R.id.loginprogress)).setVisibility(View.INVISIBLE);
			doPostResetPasswordActions(result);
		}
	}
	public void doPostResetPasswordActions(JSONObject result) {
		if(result.optInt("status")!=2){
			informatrionAlert("Password reset successful.");
			populateLoginLayout();
		}
		else{
			informatrionAlert(result.optString("reason"));
		}
	}

	public void informatrionAlert(String textToShow) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Information");
		localTextView2.setText(textToShow);
		localButton1.setText("     OK      ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
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

	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
		Log.e("onTaskComplete", result);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//		super.onBackPressed();
		Intent	in=new Intent(LoginActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
}