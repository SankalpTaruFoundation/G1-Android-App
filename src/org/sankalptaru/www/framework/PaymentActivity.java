package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PaymentActivity extends Activity{
	ProgressBar paymentProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(PaymentActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		paymentProgress=(ProgressBar)findViewById(R.id.paymentprogress);
		WebView webView=(WebView)findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		final CookieManager cookieMngr = CookieManager.getInstance();
		cookieMngr.removeAllCookie();
		cookieMngr.acceptCookie();
		webView.getSettings().setUserAgentString("ST_APP"); 
		final Activity activity = this;
		final SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		webView.loadUrl(AppUtil.getAppProperty().getProperty("PAYMENT_GATEWAY_URL")+myPrefs.getInt("uid", -1));
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				paymentProgress.setVisibility(View.INVISIBLE);
				if(cookieMngr.hasCookies()){
					String cookieString=cookieMngr.getCookie(AppUtil.getAppProperty().getProperty("PAYMENT_GATEWAY_URL")+myPrefs.getInt("uid", -1));
					if(cookieString.contains("ecodes=")){
						AppUtil.setEcode(cookieString.substring(cookieString.indexOf("ecodes="), cookieString.indexOf(";", cookieString.indexOf("ecodes="))).split("=")[1]);
						AppUtil.setEcodePurchased(true);
						Intent in=new Intent(PaymentActivity.this, PlantationActivity.class);
						startActivity(in);
						overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

						finish();
					}
				}
			}

			@Override
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				AppUtil.setCookie(cookieMngr, url, myPrefs.getInt("uid", -1));
				Log.e("cookieMngr.getCookie", ""+cookieMngr.getCookie(url));
				paymentProgress.setVisibility(View.VISIBLE);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView  view, String  url)
			{
				return false;
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppUtil.catchExceptionsInActivity(PaymentActivity.this.getClass().getCanonicalName());
	}
	public void goToMenu(View v){
		Intent	in=new Intent(PaymentActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		Intent	in=new Intent(PaymentActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
}
