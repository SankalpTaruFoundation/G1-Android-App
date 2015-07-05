package org.sankalptaru.www.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteMapActivity extends FragmentActivity implements LocationListener,OnItemClickListener{
	private ArrayList<LatLng> markerPoints;
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	private ArrayList<String>distanceList,durationList,textDirectionList;
	private ArrayList<LatLng> startLoactionList;
	private String highwayName,totalDistance,totalTime="";
	private AlertDialog alertDialog;
	private Dialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(RouteMapActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		openMap();
	}
	public String getHighwayName() {
		return highwayName;
	}
	public void setHighwayName(String highwayName) {
		this.highwayName = highwayName;
	}
	public String getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		System.out.println("inside create dialog : " + id);
		Dialog pickAlphabetDialog = new Dialog(this);
		pickAlphabetDialog.setCancelable(false);
		pickAlphabetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		final View pickAlphabetView = getLayoutInflater().inflate(R.layout.direction_list, null);
		pickAlphabetDialog.setContentView(pickAlphabetView);
		TextView highwayName=(TextView)pickAlphabetView.findViewById(R.id.highwayText);
		highwayName.setText(getHighwayName());
		TextView distTimeView=(TextView)pickAlphabetView.findViewById(R.id.distTimeText);
		distTimeView.setText(getTotalDistance()+" "+getTotalTime());
		ListView listView = (ListView) pickAlphabetView.findViewById(R.id.directionsList);
		listView.setAdapter(new DirectionAdapter(RouteMapActivity.this,getDistanceList(),getDurationList(),getTextDirectionList()));
		listView.setOnItemClickListener(this);
		return pickAlphabetDialog;
	}

	public void closeDirectionLayout(View v){
		dismissDialog(100);
	}
	public ArrayList<String> getDistanceList() {
		return distanceList;
	}
	public void setDistanceList(ArrayList<String> distanceList) {
		this.distanceList = distanceList;
	}
	public ArrayList<String> getDurationList() {
		return durationList;
	}
	public void setDurationList(ArrayList<String> durationList) {
		this.durationList = durationList;
	}
	public ArrayList<String> getTextDirectionList() {
		return textDirectionList;
	}
	public void setTextDirectionList(ArrayList<String> textDirectionList) {
		this.textDirectionList = textDirectionList;
	}

	public ArrayList<LatLng> getStartLoactionList() {
		return startLoactionList;
	}
	public void setStartLoactionList(ArrayList<LatLng> startLoactionList) {
		this.startLoactionList = startLoactionList;
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.options, menu);

		return(super.onCreateOptionsMenu(menu));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.camera) {
			showDialog(100);
		}

		return(super.onOptionsItemSelected(item));
	}*/
	
	public void showDirections(View v){
		showDialog(100);
	}
	private void openMap() {
		// TODO Auto-generated method stub
		setContentView(R.layout.navigate_map_layout);
		if(null==distanceList)
			distanceList=new ArrayList<String>();
		else
			distanceList.clear();
		if(null==durationList)
			durationList=new ArrayList<String>();
		else
			durationList.clear();
		if(null==textDirectionList)
			textDirectionList=new ArrayList<String>();
		else
			textDirectionList.clear();
		if(null==startLoactionList)
			startLoactionList=new ArrayList<LatLng>();
		else
			startLoactionList.clear();
		double treeLat=getIntent().getExtras().getDouble("treeLatitude");
		double treeLng=getIntent().getExtras().getDouble("treeLongitude");
		markerPoints=new ArrayList<LatLng>();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		AppUtil.initializeProgressDialog(RouteMapActivity.this, "",progressDialog);
		FragmentManager fragmentManager = getSupportFragmentManager();
		//		map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		SupportMapFragment mapFragment =  (SupportMapFragment)
				fragmentManager.findFragmentById(R.id.map);
		map = mapFragment.getMap();

		if(map!=null){
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			Location location = locationManager.getLastKnownLocation(provider);
			// Initialize the location fields
			if (location != null) {
				markerPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
				markerPoints.add(new LatLng(treeLat, treeLng));

				MarkerOptions options = null ;
				// Setting the position of the marker
				for (int i = 0; i < markerPoints.size(); i++) {

					options = new MarkerOptions();
					if(i==0){
						options.position(markerPoints.get(i));
						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
					}
					else{
						options.position(markerPoints.get(i));
						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.trees_planted));
					}
					map.addMarker(options);
				}
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(markerPoints.get(0))      
				.zoom(2)               
				.bearing(0)                
				.tilt(0)                   
				.build();               
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				String url = getDirectionsUrl(markerPoints.get(0), markerPoints.get(1));				

				DownloadTask downloadTask = new DownloadTask();

				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
			}
		}		
	}
	private String getDirectionsUrl(LatLng origin,LatLng dest){

		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;

		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		


		// Sensor enabled
		String sensor = "sensor=false";			

		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url 
			urlConnection = (HttpURLConnection) url.openConnection();

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
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}



	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
			}
			return data;		
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}		
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

		// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
			try{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser(RouteMapActivity.this);

				// Starts parsing data
				routes = parser.parse(jObject);    
			}catch(Exception e){
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	

					points.add(position);						
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(4);
				lineOptions.color(getResources().getColor(R.color.dividerColor));	
			}

			// Drawing polyline in the Google Map for the i-th route
			if(lineOptions!=null)
				map.addPolyline(lineOptions);	
			else
				createNullAlert("Failed to Get Directions.","Info", "Back");
			AppUtil.cancelProgressDialog();
		}			
	}
	public void createNullAlert(String textToShow,String title,String neutralButtonText) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(RouteMapActivity.this);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Warning");
		localTextView2.setText(textToShow);
		localButton1.setText(neutralButtonText);
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
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
	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.catchExceptionsInActivity(RouteMapActivity.this.getClass().getCanonicalName());
		locationManager.requestLocationUpdates(provider, 500, 1, this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}
	@Override
	public void onLocationChanged(Location location) {
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// TODO Auto-generated method stub
		//			super.onBackPressed();
		Intent	in;
		String callingClass=getIntent().getStringExtra("callingClass");
		if(callingClass.equals("Menu")){
			in=new Intent(RouteMapActivity.this, MenuActivity.class);
		/*else
			in=new Intent(RouteMapActivity.this, ForestActivity.class);*/
		startActivity(in);
		}
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(startLoactionList.get(arg2))      
		.zoom(16)               
		.bearing(0)                
		.tilt(50)                   
		.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		MarkerOptions options=new MarkerOptions();
		options.position(startLoactionList.get(arg2));
		options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
		map.addMarker(options);
		dismissDialog(100);
	}
}
