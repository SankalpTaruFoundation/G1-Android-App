package org.sankalptaru.www.framework;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

class HttpPutDigest extends AsyncTask<String, Void, String>{
	IHttpDigestCompletionInterface interfaceHttpDigest;
	private JSONObject jsonObject;
	public HttpPutDigest(IHttpDigestCompletionInterface interfaceHttpDigest,JSONObject jsonObject) {
		this.interfaceHttpDigest=interfaceHttpDigest;
		this.jsonObject=jsonObject;
	}
	@Override
	protected String doInBackground(String... params) {

		String responseString = null;
		if(params.length > 0){
			String url = params[0];
			String username = params[1];
			String password = params[2];

			try
			{
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("test user agent");

				URL urlObj = new URL(url);
				HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());
				AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
				UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

				CredentialsProvider cp = new BasicCredentialsProvider();
				cp.setCredentials(scope, creds);
				HttpContext credContext = new BasicHttpContext();
				credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);



				HttpPut httpput = new HttpPut(url);  

				// json.put("api_token",settings.getString("api_token", "")); 
				StringEntity se = new StringEntity(jsonObject.toString()); 
				httpput.setEntity(se); 
				httpput.setHeader("Accept", "application/json");
				httpput.setHeader("Content-type", "application/json");
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 
				HttpResponse response = httpClient.execute(host,httpput,credContext);
				HttpEntity responseEntity =response.getEntity(); 
				responseString = EntityUtils.toString(responseEntity).trim();
				StatusLine status = response.getStatusLine();
				String statusLine=status.toString();
				Log.e(AppUtil.TEST_TAG, status.toString()+" responseString "+responseString);
				httpClient.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}

		}
		return responseString;

	}
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		interfaceHttpDigest.onTaskComplete(result);	
	}
}