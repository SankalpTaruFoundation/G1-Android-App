package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Aboutus extends Activity {
	private ProgressBar aboutUsProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(Aboutus.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		aboutUsProgress=(ProgressBar)findViewById(R.id.aboutusprogress);
		WebView webView=(WebView)findViewById(R.id.aboutUsWv);
		webView.getSettings().setUserAgentString("ST_APP"); 
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(AppUtil.getAppProperty().getProperty("ABOUT_US_URL"));
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				aboutUsProgress.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				aboutUsProgress.setVisibility(View.VISIBLE);
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
		AppUtil.catchExceptionsInActivity(Aboutus.this.getClass().getCanonicalName());
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		Intent	in=new Intent(Aboutus.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
