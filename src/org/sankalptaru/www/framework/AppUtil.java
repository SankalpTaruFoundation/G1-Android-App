package org.sankalptaru.www.framework;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AppUtil {

	static String data=null;
	private static Properties appProperty;
	private static String reasonToPlant;
	private static String ecode;
	private static int locationId;
	private static String userName;
	public static final int headerImageID=100;
	public static final int USERNAME_TEXTVIEW_ID=101;
	public static final int GRID_VIEW_ID = 102;
	public static final int CAPTION_ID = 103;
	public static final int LINE_ID = 104;
	public static final int USERNAME_ID = 105;
	public static final int DISPLAY_NAME_EDITTEXT_ID = 106;
	public static final int WISHLIST_EDITTEXT_ID = 107;
	public static final int OTHER_OCASSION_EDITTEXT_ID = 108;
	public static final int SITE_CHANGE_ID = 109;
	public static final int TAG_MY_TREE_BUTTON = 110;
	private static PrintWriter exceptionLogWriter = null;
	private static int screenWidth,screenHeight;
	public static boolean isEcodePurchased=false;
	public static final String FB_APP_ID = "171428516292751";
	public static final String FB_TOKEN = "fb_token";
	public static final int VIRAL_ICON_ID = 111;
	public static final String TEST_TAG = "Digest Class";
	public static final int FOOT_PRINT_ICON_ID = 112;
	public static final int FOREST_BACK_TO_MENU = 113; 
	public static String fb_prpoertis =   "{\"Download it from\":{\"href\":\"https://play.google.com/\",\"text\":\"Google PLay Store\"},\"Download it from:\":{\"href\":\"http://itunes.apple.com/\",\"text\":\"iTunes AppStore\"}}";
	public static Dialog activityDialog;
	public static boolean isEcodePurchased() {
		return isEcodePurchased;
	}
	public static void setEcodePurchased(boolean isEcodePurchased) {
		AppUtil.isEcodePurchased = isEcodePurchased;
	}

	private static LayoutParams headerLayoutParams;
	private static Bitmap headerBitmap;
	private static AnimationDrawable animationDrawable;
	public static int getScreenWidth() {
		return screenWidth;
	}
	public static int getScreenHeight() {
		return screenHeight;
	}

	public static void initializeProgressDialog(Activity ac,String textToShow,Dialog progressDialog){
		/*progressDialog = new ProgressDialog(ac);
		Window window = progressDialog.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		//		progressDialog.setProgressStyle(ProgressDialog.);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setIndeterminateDrawable(ac.getResources().getDrawable(R.anim.frame));
		progressDialog.show();*/

		if (progressDialog == null)
		{
			progressDialog = new Dialog(ac, R.style.Theme_Dialog_Translucent);
			progressDialog.requestWindowFeature(1);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			activityDialog=progressDialog;
		}
		progressDialog.setContentView(R.layout.loading_progress);
		progressDialog.setCancelable(false);
		if ((progressDialog != null) && (progressDialog.isShowing()))
			progressDialog.dismiss();
		progressDialog.show();
		ImageView localImageView = (ImageView)progressDialog.findViewById(R.id.imgeView);
		TextView localTextView = (TextView)progressDialog.findViewById(R.id.tvLoading);
		/* if (!this.strrMsg.equalsIgnoreCase(""))
	          localTextView.setText(this.strrMsg);*/
		localImageView.setBackgroundResource(R.anim.frame);
		animationDrawable = ((AnimationDrawable)localImageView.getBackground());
		if (animationDrawable != null)
			animationDrawable.start();

	}

	public static void catchExceptionsInActivity(final String activityName){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				logExceptionToFile(ex,activityName);
			}
		});
	}
	public static void logExceptionToFile(String extraMessage,Throwable exceptionToLog,String activityName){
		try{
			if(null == exceptionLogWriter)
				initializeExceptionLogFile(activityName);

			if(null != exceptionLogWriter){
				StringBuffer logBuffer = new StringBuffer();
				logBuffer.append("------------------ " + new Date() +  " ------------------\n");
				logBuffer.append("Activity Name: " + activityName);
				logBuffer.append("\n*******************************\n");
				if(null != extraMessage){
					logBuffer.append(extraMessage + "\n");
				}
				exceptionLogWriter.println(logBuffer.toString());
				if(null != exceptionToLog)
					exceptionToLog.printStackTrace(exceptionLogWriter);
				exceptionLogWriter.println();
			}

			if(null == extraMessage)
				extraMessage = "extra message";
			if(null != exceptionToLog){
				Log.e("AppUtil", extraMessage,exceptionToLog);
			}else{
				Log.e("AppUtil", extraMessage);
			}

		}catch (Throwable e) {
			Log.e("AppUtil", "exception in writing to exception log file." ,e);
		}
	}
	private static void initializeExceptionLogFile(String activityName){
		try{
			if(null != exceptionLogWriter)
				return;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yy");
			Date date=new Date();
			String logFileName = "log.txt"; 
			logFileName = activityName + "_" + date.getDate()+ ".txt";
			File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+getAppProperty().getProperty("SANKLAP_TARU_LOG_DIRECTORY") + logFileName);
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			exceptionLogWriter = new PrintWriter(new FileOutputStream(logFile,true), true);

		}catch (Throwable e) {
			Log.e("AppUtil", "initializing exception log file",e);
		}
	}

	public static void logExceptionToFile(Throwable exceptionToLog,String activityName){
		logExceptionToFile(null, exceptionToLog,activityName);
	}


	public static Document getDomElement(String xml){
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is); 

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}

	public static void cancelProgressDialog(){
		activityDialog.cancel();
	}
	public static String getUserName() {
		return userName;
	}
	public static void setUserName(String userName) {
		AppUtil.userName = userName;
	}
	public static int getLocationId() {
		return locationId;
	}
	public static void setLocationId(int locationId) {
		AppUtil.locationId = locationId;
	}
	public static String getEcode() {
		return ecode;
	}
	public static void setEcode(String ecode) {
		AppUtil.ecode = ecode;
	}
	public static String getReasonToPlant() {
		return reasonToPlant;
	}
	public static void setReasonToPlant(String reasonToPlant) {
		AppUtil.reasonToPlant = reasonToPlant;
	}
	public static Typeface getFont(Activity ac){
		return Typeface.createFromAsset(ac.getAssets(), "DS-DIGI.TTF");
	}
	public static void sendRequestAndUpdate(String path){
		String s = null;
		try{
			URL url= new URL(path);
			URLConnection fc=url.openConnection();
			fc.connect();
			InputStream jsonStream = fc.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int pos = jsonStream.read(buffer);
			while(pos != -1)
			{
				baos.write(buffer, 0, pos);
				pos = jsonStream.read(buffer);
			}
			baos.flush();
			s=new String(baos.toByteArray());
		}
		catch(MalformedURLException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static ImageView getHeader(Activity ac,LayoutParams params){
		Activity tempActivity=ac;
		Display d=tempActivity.getWindowManager().getDefaultDisplay();
		int screenWidth=d.getWidth();
		int screnHeight=d.getHeight();
		ImageView header=new ImageView(tempActivity);
		header.setId(headerImageID);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, screnHeight/10);
		params.setMargins(0, 0, 0, 0);
		Bitmap headerBitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(tempActivity.getResources(), R.drawable.header), screenWidth, screnHeight/10, true);
		headerLayoutParams=params;
		AppUtil.headerBitmap=headerBitmap;
		header.setLayoutParams(params);
		header.setImageBitmap(headerBitmap);
		return header;
	}

	public static int getHeaderimageid() {
		return headerImageID;
	}
	public static LayoutParams getHeaderLayoutParams() {
		return headerLayoutParams;
	}
	public static void setHeaderLayoutParams(LayoutParams headerLayoutParams) {
		AppUtil.headerLayoutParams = headerLayoutParams;
	}
	public static Bitmap getHeaderBitmap() {
		return headerBitmap;
	}
	public static void setHeaderBitmap(Bitmap headerBitmap) {
		AppUtil.headerBitmap = headerBitmap;
	}
	public static String getResponse(String url){
		String downloadedData = null;
		Log.e("getResponse", url);
		try {
			URL downloadURL = new URL(url);
			InputStream inputStream = (InputStream) downloadURL.getContent();
			if (null != inputStream) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int readCounter = inputStream.read(buffer);
				while (readCounter != -1) {
					byteArrayOutputStream.write(buffer, 0, readCounter);
					readCounter = inputStream.read(buffer);
				}
				downloadedData = new String(
						byteArrayOutputStream.toByteArray());
				/*if (null != downloadedData && !"".equals(downloadedData)) {
					downloadedJson = new JSONObject(downloadedData);
				}*/
			}else{
				Log.e("getResponse", "Response is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadedData;
	}
	public static String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
        }

        return data;
    }
	public static String doPostJsonObject(String url,JSONObject jsonObject){
		HttpClient httpclient = new DefaultHttpClient();   
		HttpPost httppost = new HttpPost(url);  
		String responseString = null;
		try{ 

			// json.put("api_token",settings.getString("api_token", "")); 
			StringEntity se = new StringEntity(jsonObject.toString()); 
			httppost.setEntity(se); 
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 


			HttpResponse response = httpclient.execute(httppost);   
			HttpEntity responseEntity =response.getEntity(); 
			responseString= EntityUtils.toString(responseEntity).trim();

			Log.d("Response", responseString); 
		} catch (UnsupportedEncodingException UnsupportedEncodingException) { 
			// TODO Auto-generated catch block 
			Log.e("UnsupportedEncodingException",UnsupportedEncodingException.getMessage()); 
		} catch (ClientProtocolException ClientProtocolException) { 
			// TODO Auto-generated catch block 
			Log.e("ClientProtocolException", ClientProtocolException.getMessage()); 
		} catch (IOException IOException) { 
			// TODO Auto-generated catch block 
			Log.e("IOException", IOException.getMessage()); 
		}
		return responseString; 

	}
	public static String doPutJsonObject(String url,JSONObject jsonObject){
		Log.e("doPutJsonObject", "doPutJsonObject");
		HttpClient httpclient = new DefaultHttpClient();   
		HttpPut httpput = new HttpPut(url);  
		String responseString = null;
		try{ 

			// json.put("api_token",settings.getString("api_token", "")); 
			StringEntity se = new StringEntity(jsonObject.toString()); 
			httpput.setEntity(se); 
			httpput.setHeader("Accept", "application/json");
			httpput.setHeader("Content-type", "application/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 


			HttpResponse response = httpclient.execute(httpput);   
			HttpEntity responseEntity =response.getEntity(); 
			responseString= EntityUtils.toString(responseEntity).trim();

			Log.d("Response", responseString); 
		} catch (UnsupportedEncodingException UnsupportedEncodingException) { 
			// TODO Auto-generated catch block 
			Log.e("UnsupportedEncodingException",UnsupportedEncodingException.getMessage()); 
		} catch (ClientProtocolException ClientProtocolException) { 
			// TODO Auto-generated catch block 
			Log.e("ClientProtocolException", ClientProtocolException.getMessage()); 
		} catch (IOException IOException) { 
			// TODO Auto-generated catch block 
			Log.e("IOException", IOException.getMessage()); 
		}
		return responseString; 

	}
	public static boolean isEmailValid(String email) { 
		boolean isValid = false; 

		String expression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"; 
		CharSequence inputStr = email; 

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE); 
		Matcher matcher = pattern.matcher(inputStr); 
		if (matcher.matches()) { 
			isValid = true; 
		} 
		return isValid; 
	}
	public static void loadProperties(Properties prop) {
		appProperty=prop;
	}
	public static Properties getAppProperty() {
		return appProperty;
	}
	public static void setScreenHeight(int screnHeight) {
		// TODO Auto-generated method stub
		AppUtil.screenHeight=screnHeight;
	}
	public static void setScreenWidth(int screenWidth) {
		// TODO Auto-generated method stub
		AppUtil.screenWidth=screenWidth;
	}
	public static Properties loadPropties(Activity ac) throws IOException {
		String[] fileList = { "data.properties" };
		Properties prop = new Properties();
		for (int i = fileList.length - 1; i >= 0; i--) {
			String file = fileList[i];
			try {
				InputStream fileStream = ac.getAssets().open(file);
				prop.load(fileStream);
				fileStream.close();
			} catch (FileNotFoundException e) {
				Log.d("Data", "Ignoring missing property file " + file);
			}
		}
		return prop;
	}
	public static void getHttpGetDigestStatus(String url, IHttpDigestCompletionInterface iHttpInterface){
		HttpDigestGetThread httpDigestThread=new HttpDigestGetThread(iHttpInterface);
		httpDigestThread.execute(url,"stmobile","$S$DR0hsGzHq8BJnn0JEZN1lO8mHGp/fTwZ4SOrPGTrIkC6HnfE5Lb1");
	}
	public static void getHttpPutDigestStatus(String url, IHttpDigestCompletionInterface iHttpInterface,JSONObject js){
		HttpPutDigest httpDigestThread=new HttpPutDigest(iHttpInterface, js);
		httpDigestThread.execute(url,"stmobile","$S$DR0hsGzHq8BJnn0JEZN1lO8mHGp/fTwZ4SOrPGTrIkC6HnfE5Lb1");
	}
	public static IHttpDigestCompletionInterface getClassObject(Activity startActivity){
		IHttpDigestCompletionInterface interfaceObject;
		interfaceObject=(IHttpDigestCompletionInterface) startActivity;
		return interfaceObject;
	}
	public static void getHttpPostDigestStatus(String url,IHttpDigestCompletionInterface iHttpInterface, JSONObject js) {
		HttpPostDigest httpDigestThread=new HttpPostDigest(iHttpInterface, js);
		httpDigestThread.execute(url,"stmobile","$S$DR0hsGzHq8BJnn0JEZN1lO8mHGp/fTwZ4SOrPGTrIkC6HnfE5Lb1");
	}
	public static void setCookie(CookieManager cookieMngr, String url, int uid) {
		// TODO Auto-generated method stub
		cookieMngr.setCookie(url,"uid="+uid);
		cookieMngr.setCookie(url,"api_user=stmobile");

		try {
			MessageDigest mDigest;
			mDigest = MessageDigest.getInstance("MD5");
			String md5ActualString=uid+":$S$DR0hsGzHq8BJnn0JEZN1lO8mHGp/fTwZ4SOrPGTrIkC6HnfE5Lb1";
			mDigest.update(md5ActualString.getBytes());

			byte d[]=mDigest.digest();
			StringBuffer hash=new StringBuffer();

			for (int i=0; i<d.length; i++) {
				hash.append(Integer.toHexString(0xFF & d[i]));
			}
			cookieMngr.setCookie(url, "api_verify="+hash.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getBase64StringOfFile(File fileName){
		String encodedString=null;
		try {
			InputStream inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
			byte[] bytes;
			byte[] buffer = new byte[8192];
			int bytesRead;
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			bytes = output.toByteArray();
			encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encodedString;
	}
	
	
	
	public static Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result=size;
				}
				else {
					int resultArea=result.width * result.height;
					int newArea=size.width * size.height;

					if (newArea > resultArea) {
						result=size;
					}
				}
			}
		}

		return(result);
	}
}
