package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlantationActivity extends Activity implements OnItemClickListener,OnClickListener,IHttpDigestCompletionInterface,OnChildClickListener{

	private ArrayList<JSONObject> locationsJsonObjectList;
	private SitesAdaptor adaptor;
	JSONObject tempJSObject;
	private int idOfCurrentLayout;
	private EditText wishlistBx;
	private EditText displayNameBx;
	private Dialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Properties prop = new Properties();
		try {
			prop = AppUtil.loadPropties(PlantationActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}
		//		setContentView(R.layout.reasontoplant);
		//		((ImageView)findViewById(R.id.reasonHeader)).setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, AppUtil.getHeaderBitmap().getHeight()));
		//		idOfCurrentLayout=R.layout.reasontoplant;
		showEnterEcodeLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.catchExceptionsInActivity(PlantationActivity.this.getClass().getCanonicalName());
		if(AppUtil.isEcodePurchased()){
			showEnterEcodeLayout();
		}
	}

	public void doCollectReasonAndContinue(View v){
		showEnterEcodeLayout();
	}

	public void showEnterEcodeLayout(){
		setContentView(R.layout.enter_ecode);
		//		resizeImagesInEnterEcodeLayout(R.id.enterEcodeHeader,R.id.enterEcode);
		EditText ecodeBx=(EditText)findViewById(R.id.ecodeEditText);
		if(null!=AppUtil.getEcode()&&AppUtil.getEcode().length()>0)
			ecodeBx.setText(AppUtil.getEcode());
		idOfCurrentLayout=R.layout.enter_ecode;
	}
	public void doEnterEcode(View v){
		//showEnterEcodeLayout();
	}
	/*private void resizeImagesInEnterEcodeLayout(int headerId,int belowHeaderId) {
		// TODO Auto-generated method stub
		((ImageView)findViewById(headerId)).setLayoutParams(AppUtil.getHeaderLayoutParams());
		((ImageView)findViewById(headerId)).setImageBitmap(AppUtil.getHeaderBitmap());
		android.widget.RelativeLayout.LayoutParams params=new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, AppUtil.getHeaderLayoutParams().height);
		params.addRule(RelativeLayout.BELOW, headerId);
		((ImageView)findViewById(belowHeaderId)).setLayoutParams(params);
	}*/
	public void goToMenu(View v){
		Intent	in=new Intent(PlantationActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
	public void doSaveEcodeAndContinue(View v){
		String enteredCode=((EditText)findViewById(R.id.ecodeEditText)).getText().toString();
		tempJSObject=null;
		if(enteredCode!=null&&enteredCode.length()>0&&!enteredCode.contains(" ")){
			new GetResponseAsync(enteredCode).execute(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+enteredCode);
		}
		else if (enteredCode==null||!(enteredCode.length()>0)) {
			Toast.makeText(PlantationActivity.this, "Please Enter Valid ECode.", 1000).show();
		}
	}
	public void doPlantation(){
		JSONObject jsonObject = new JSONObject();
		JSONObject jsResponse = null;
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",Context.MODE_PRIVATE); 
		int uid=myPrefs.getInt("uid", -1);
		String displayText= displayNameBx.getText().toString();
		String wishlistText=wishlistBx.getText().toString();
		try {
			jsonObject.put("displayname",displayText);
			jsonObject.put("uid",uid );
			jsonObject.put("wishlist",wishlistText );
			jsonObject.put("locations", AppUtil.getLocationId());
			jsonObject.put("ecode", AppUtil.getEcode());
			if(displayText!=null&&displayText.length()>0&&wishlistText!=null&&wishlistText.length()>0)
				new FetchRequestAsynTask(jsonObject).execute(AppUtil.getAppProperty().getProperty("SANKAIP_TARU_PLANT_A_TREE_URL"));
			//			{
			//				AppUtil.getHttpPostDigestStatus(AppUtil.getAppProperty().getProperty("SANKAIP_TARU_PLANT_A_TREE_URL"),AppUtil.getClassObject(PlantationActivity.this),jsonObject);
			//
			//			}
			else
				Toast.makeText(PlantationActivity.this, "Fields cannot be empty.", 1000).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int pos, long id) {
		// TODO Auto-generated method stub
		setContentView(R.layout.plantation);

		//		resizeImagesInEnterEcodeLayout(R.id.dialogHeader, R.id.step3Imageview);
		idOfCurrentLayout=R.layout.plantation;
		try {
			AppUtil.setLocationId(Integer.parseInt(locationsJsonObjectList.get(pos).getString("lid")));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		populateTagMyTreeCompleteLayout();
	}
	private void populateTagMyTreeCompleteLayout(String site, String description) {
		// TODO Auto-generated method stub
		TextView siteTitle=(TextView)findViewById(R.id.siteTitle);
		//		TextView sitedescription=(TextView)findViewById(R.id.siteDecription);
		Spinner visibilitySpinner=(Spinner)findViewById(R.id.visibilitySpinner);
		displayNameBx=(EditText)findViewById(R.id.displayNameBx);
		wishlistBx=(EditText)findViewById(R.id.wishlistBx);
		Spinner ocassionSpinner=(Spinner)findViewById(R.id.ocassionSpinner);
		EditText otherOcassionBx=(EditText)findViewById(R.id.otherOccassionBx);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.visibilityOptions, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		visibilitySpinner.setAdapter(adapter);

		ArrayAdapter<CharSequence> adapterOther = ArrayAdapter.createFromResource(
				this, R.array.ocassionOptions, android.R.layout.simple_spinner_item);
		adapterOther.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ocassionSpinner.setAdapter(adapterOther);

		siteTitle.setText(Html.fromHtml("<b><font color=#3a8300>Plantation Site: </font></b>"+ "<b><font color=\"#000000\">" + site + "</font></b>"));
		//		sitedescription.setText(description);
	}
	public void doTagTree(View v){
		doPlantation();
	}
	public void doChangeSite(View v){
		new GetResponseAsync(AppUtil.getEcode()).execute(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+AppUtil.getEcode());

	}
	/*	private void populateRightLayout(LinearLayout parentContainer, android.widget.FrameLayout.LayoutParams params, LayoutParams linearParams, int screenHeight, int screnWidth) {
		// TODO Auto-generated method stub
		ScrollView scroll = new ScrollView(PlantationActivity.this);
		scroll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		parentContainer.addView(scroll);

		LinearLayout rightOfblackLineLayout=new LinearLayout(PlantationActivity.this);
		linearParams=new LayoutParams((screnWidth-(screnWidth/3))-20,screenHeight);
		rightOfblackLineLayout.setLayoutParams(linearParams);
		rightOfblackLineLayout.setOrientation(LinearLayout.VERTICAL);
		scroll.addView(rightOfblackLineLayout);


		TextView visibilityTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		visibilityTextView.setLayoutParams(linearParams);
		visibilityTextView.setTextColor(Color.BLACK);
		visibilityTextView.setText("Your Tree is visible to:");
		visibilityTextView.setTextSize(10);
		rightOfblackLineLayout.addView(visibilityTextView);

		Spinner visibilityCheckSpinner=new Spinner(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		visibilityCheckSpinner.setLayoutParams(linearParams);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.visibilityOptions, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		visibilityCheckSpinner.setAdapter(adapter);
		visibilityCheckSpinner.setBackgroundResource(R.drawable.green);
		rightOfblackLineLayout.addView(visibilityCheckSpinner);

		View whiteSpace=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace);


		TextView displayTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		displayTextView.setLayoutParams(linearParams);
		displayTextView.setTextColor(Color.BLACK);
		displayTextView.setText("Display Name:");
		displayTextView.setTextSize(10);
		rightOfblackLineLayout.addView(displayTextView);

		EditText displayEditText=new EditText(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		displayEditText.setLayoutParams(linearParams);
		displayEditText.setBackgroundResource(R.drawable.green);
		displayEditText.setTextSize(12);
		displayEditText.setId(AppUtil.DISPLAY_NAME_EDITTEXT_ID);
		rightOfblackLineLayout.addView(displayEditText);

		TextView displayNoteTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		displayNoteTextView.setLayoutParams(linearParams);
		displayNoteTextView.setText("*This name is displayed on the Tree");
		displayNoteTextView.setTextSize(10);
		rightOfblackLineLayout.addView(displayNoteTextView);

		View whiteSpace1=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace1.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace1);

		TextView wishlistTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		wishlistTextView.setLayoutParams(linearParams);
		wishlistTextView.setTextColor(Color.BLACK);
		wishlistTextView.setText("My Wishlist:");
		wishlistTextView.setTextSize(10);
		rightOfblackLineLayout.addView(wishlistTextView);

		EditText wishlistEditText=new EditText(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		wishlistEditText.setLayoutParams(linearParams);
		wishlistEditText.setBackgroundResource(R.drawable.green);
		wishlistEditText.setId(AppUtil.WISHLIST_EDITTEXT_ID);
		wishlistEditText.setTextSize(12);
		rightOfblackLineLayout.addView(wishlistEditText);

		TextView wishlistNoteTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		wishlistNoteTextView.setLayoutParams(linearParams);
		wishlistNoteTextView.setText("*Add a wish and see a thread wrapped on the virtual Tree");
		wishlistNoteTextView.setTextSize(10);
		rightOfblackLineLayout.addView(wishlistNoteTextView);

		View whiteSpace2=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace2.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace2);

		TextView ocassionTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ocassionTextView.setLayoutParams(linearParams);
		ocassionTextView.setTextColor(Color.BLACK);
		ocassionTextView.setText("Select an Ocassion:");
		ocassionTextView.setTextSize(10);
		rightOfblackLineLayout.addView(ocassionTextView);

		Spinner ocassionSpinner=new Spinner(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ocassionSpinner.setLayoutParams(linearParams);
		ArrayAdapter<CharSequence> adapterOther = ArrayAdapter.createFromResource(
				this, R.array.ocassionOptions, android.R.layout.simple_spinner_item);
		adapterOther.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ocassionSpinner.setAdapter(adapterOther);
		ocassionSpinner.setBackgroundResource(R.drawable.green);
		rightOfblackLineLayout.addView(ocassionSpinner);

		View whiteSpace3=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace3.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace3);

		TextView orTextView=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		orTextView.setLayoutParams(linearParams);
		orTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		orTextView.setTextColor(Color.rgb(58, 131,0));
		orTextView.setText("-OR-");
		orTextView.setTextSize(10);
		rightOfblackLineLayout.addView(orTextView);


		EditText otherOcassionEditText=new EditText(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		otherOcassionEditText.setLayoutParams(linearParams);
		otherOcassionEditText.setBackgroundResource(R.drawable.green);
		otherOcassionEditText.setId(AppUtil.OTHER_OCASSION_EDITTEXT_ID);
		otherOcassionEditText.setTextSize(12);
		otherOcassionEditText.setHint("Please write your additional ocassions/Causes/Wish. Explain your wish");
		rightOfblackLineLayout.addView(otherOcassionEditText);

		View whiteSpace4=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace4.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace4);

		LinearLayout buttonLinearLayout=new LinearLayout(PlantationActivity.this);
		buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLinearLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		rightOfblackLineLayout.addView(buttonLinearLayout);

		View whiteSpace5=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.FILL_PARENT, 5);
		whiteSpace5.setLayoutParams(params);
		rightOfblackLineLayout.addView(whiteSpace5);


		Button siteChangeButton=new Button(PlantationActivity.this);
		siteChangeButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		siteChangeButton.setBackgroundColor(Color.rgb(162, 32, 41));
		siteChangeButton.setText("Site Change");
		siteChangeButton.setId(AppUtil.SITE_CHANGE_ID);
		siteChangeButton.setTextColor(Color.WHITE);
		siteChangeButton.setOnClickListener(this);
		buttonLinearLayout.addView(siteChangeButton);

		View whiteSpace6=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(5,android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
		whiteSpace6.setLayoutParams(params);
		buttonLinearLayout.addView(whiteSpace6);


		Button tagButton=new Button(PlantationActivity.this);
		tagButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tagButton.setBackgroundColor(Color.rgb(58, 131,0));
		tagButton.setText("Tag Tree");
		tagButton.setId(AppUtil.TAG_MY_TREE_BUTTON);
		tagButton.setTextColor(Color.WHITE);
		tagButton.setOnClickListener(this);
		buttonLinearLayout.addView(tagButton);

	}

	private void showLine(LinearLayout parentContainer, android.widget.FrameLayout.LayoutParams params, int screenHeight) {
		// TODO Auto-generated method stub

		View whiteLine=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(5, screenHeight);
		whiteLine.setLayoutParams(params);
		parentContainer.addView(whiteLine);

		View blackLine=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(5, screenHeight);
		blackLine.setLayoutParams(params);
		blackLine.setBackgroundColor(Color.BLACK);
		parentContainer.addView(blackLine);

		View whiteLine1=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(5, screenHeight);
		whiteLine1.setLayoutParams(params);
		parentContainer.addView(whiteLine1);
	}

	private void populateLeftLayout(LayoutParams linearParams, LinearLayout indexOneSubLayoutOfParent, int screnWidth, int screenHeight, android.widget.FrameLayout.LayoutParams params) {
		// TODO Auto-generated method stub
		ImageView sampletagImageView=new ImageView(PlantationActivity.this);
		linearParams=new LayoutParams(screnWidth/3, screenHeight/4);
		sampletagImageView.setLayoutParams(linearParams);
		indexOneSubLayoutOfParent.addView(sampletagImageView);
		Bitmap bm=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tree_grid), screnWidth/3, screenHeight/4, true);
		sampletagImageView.setImageBitmap(bm);


		TextView sampleTagDescription=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sampleTagDescription.setLayoutParams(linearParams);
		sampleTagDescription.setText("Sample tag Photograph description");
		indexOneSubLayoutOfParent.addView(sampleTagDescription);

		View v=new View(PlantationActivity.this);
		params=new android.widget.FrameLayout.LayoutParams(screnWidth/3, 5);
		v.setLayoutParams(params);
		indexOneSubLayoutOfParent.addView(v);

		ImageView projectSiteImageView=new ImageView(PlantationActivity.this);
		linearParams=new LayoutParams(screnWidth/3, screenHeight/4);
		projectSiteImageView.setLayoutParams(linearParams);
		indexOneSubLayoutOfParent.addView(projectSiteImageView);
		bm=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tree_grid), screnWidth/3, screenHeight/4, true);
		projectSiteImageView.setImageBitmap(bm);

		TextView projectSiteDescription=new TextView(PlantationActivity.this);
		linearParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		projectSiteDescription.setLayoutParams(linearParams);
		projectSiteDescription.setText("Project Site description");
		indexOneSubLayoutOfParent.addView(projectSiteDescription);

	}
	 */
	private class FetchRequestAsynTask extends AsyncTask<String, Integer, JSONObject>{
		JSONObject jsResponse=null;
		public FetchRequestAsynTask(JSONObject jsonObject) {
			// TODO Auto-generated constructor stub
			jsResponse=jsonObject;
			AppUtil.initializeProgressDialog(PlantationActivity.this, "Updating data...",progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject js = null;
			try {
				js = new JSONObject(
						AppUtil.doPostJsonObject(params[0],jsResponse));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			Log.e("result", ""+result);
			if(null!=result&&result.optString("status").equals("0")){
				setContentView(R.layout.congrats);
				doCongratsZoom();
				//				resizeImagesInEnterEcodeLayout(R.id.congratsdialogHeader, R.id.congratsStepImage);
				idOfCurrentLayout=R.layout.congrats;
			}
			else if (null!=result&&result.optString("status").equals("1")) {
				Toast.makeText(PlantationActivity.this,result.optString("reason"), 1000).show();
			}
			AppUtil.cancelProgressDialog();
		}
	}
	public void showForest(View v){
		finish();
		Intent in =new Intent(PlantationActivity.this,ForestActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	public void doCongratsZoom() {
		// TODO Auto-generated method stub
		Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		((ImageView)findViewById(R.id.congratsMiddleLayout)).startAnimation(hyperspaceJump);
	}

	class GetResponseAsync extends AsyncTask<String, Integer,JSONObject>{
		String enteredCode;
		public GetResponseAsync(String enteredCode) {
			// TODO Auto-generated constructor stub
			this.enteredCode=enteredCode;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(PlantationActivity.this, "Fetching sites available...",progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject js=null;
			try {
				js=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return js;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			//			tempJSObject=new JSONObject(AppUtil.getResponse(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+enteredCode));
		
			String couponName=result.optString("coupon_code");
			Log.e("data",""+ result+"  "+couponName);
			if(!couponName.equals("null")){
				int uses=Integer.parseInt(result.optString("uses"));
				if(uses>0){
					setContentView(R.layout.selectsite);
					//				resizeImagesInEnterEcodeLayout(R.id.selectSiteHeader,R.id.stepTwoImage);
					idOfCurrentLayout=R.layout.selectsite;
					if(null==locationsJsonObjectList)
						locationsJsonObjectList=new ArrayList<JSONObject>();
					else
						locationsJsonObjectList.clear();
					for (int i = 0; i < result.optJSONArray("locations").length(); i++) {
						try {
							locationsJsonObjectList.add(result.optJSONArray("locations").getJSONObject(i));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					AppUtil.setEcode(enteredCode);
					doListActions(uses,enteredCode);
					AppUtil.cancelProgressDialog();
				}
				else  {
					AppUtil.cancelProgressDialog();
					Toast.makeText(PlantationActivity.this, "ECode already used.", 1000).show();
				}
			}
			else{
				AppUtil.cancelProgressDialog();
				Toast.makeText(PlantationActivity.this, "Invalid ECode", 1000).show();
			}
		}
	}
	private void doListActions(int uses, String enteredCode) {
		// TODO Auto-generated method stub
		String times="time only";
		if(uses>1)
			times="times";
		Toast.makeText(PlantationActivity.this, "You can use ecode "+enteredCode.toUpperCase()+" "+uses+" "+times+" to plant trees.", Toast.LENGTH_LONG).show();
		ExpandableListView sitesList=(ExpandableListView)findViewById(R.id.selectSiteList);
		sitesList.setGroupIndicator(null);
		sitesList.setClickable(true);
		ArrayList<String> locationList=new ArrayList<String>();
		ArrayList<Object> childItem=new ArrayList<Object>();
		for (int i = 0; i < locationsJsonObjectList.size(); i++) {
			locationList.add(locationsJsonObjectList.get(i).optString("location"));
		}
		for (int i = 0; i < locationList.size(); i++) {
			ArrayList<String> tempList=new ArrayList<String>();
			tempList.add(locationsJsonObjectList.get(i).optString("description"));
			childItem.add(tempList);
		}
		adaptor=new SitesAdaptor(locationList, childItem,PlantationActivity.this);
		adaptor.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		sitesList.setAdapter(adaptor);
		sitesList.setOnChildClickListener(this);
	}
	public void onBackPressed() {
		if(idOfCurrentLayout==R.layout.reasontoplant)
		{
			//			finish();
			// TODO Auto-generated method stub
			//				super.onBackPressed();
			Intent	in=new Intent(PlantationActivity.this, MenuActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			finish();
		}
		/*else if(idOfCurrentLayout==R.layout.ecodeoptions){
			setContentView(R.layout.reasontoplant);
			((ImageView)findViewById(R.id.reasonHeader)).setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, AppUtil.getHeaderBitmap().getHeight()));
			idOfCurrentLayout=R.layout.reasontoplant;
		}*/
		else if(idOfCurrentLayout==R.layout.enter_ecode){
			/*	setContentView(R.layout.ecodeoptions);
			((ImageView)findViewById(R.id.ecodeOptionsHeader)).setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, AppUtil.getHeaderBitmap().getHeight()));

			idOfCurrentLayout=R.layout.ecodeoptions;*/
			//			setContentView(R.layout.reasontoplant);
			//			((ImageView)findViewById(R.id.reasonHeader)).setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, AppUtil.getHeaderBitmap().getHeight()));
			//			idOfCurrentLayout=R.layout.reasontoplant;
			Intent	in=new Intent(PlantationActivity.this, MenuActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			finish();
		}
		else if(idOfCurrentLayout==R.layout.selectsite)
		{
			setContentView(R.layout.enter_ecode);
			//			resizeImagesInEnterEcodeLayout(R.id.enterEcodeHeader,R.id.enterEcode);
			if(AppUtil.getEcode()!=null&&AppUtil.getEcode().length()!=0){
				((EditText)findViewById(R.id.ecodeEditText)).setText(AppUtil.getEcode());
			}
			idOfCurrentLayout=R.layout.enter_ecode;
		}
		else if(idOfCurrentLayout==R.layout.plantation){
			new GetResponseAsync(AppUtil.getEcode()).execute(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+AppUtil.getEcode());
		}
		else if(idOfCurrentLayout==R.layout.congrats)
		{
			//			setContentView(R.layout.plantation);
			//			resizeImagesInEnterEcodeLayout(R.id.dialogHeader, R.id.step3Imageview);
			//			idOfCurrentLayout=R.layout.plantation;
			//			populateTagMyTreeCompleteLayout();
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case AppUtil.TAG_MY_TREE_BUTTON:

			doPlantation();
			break;
		case AppUtil.SITE_CHANGE_ID:
			new GetResponseAsync(AppUtil.getEcode()).execute(AppUtil.getAppProperty().getProperty("ECODE_STATUS_DESCRIPTION")+AppUtil.getEcode());

			break;

		default:
			break;
		}
	}

	@Override
	public void onTaskComplete(String result) {
		// TODO Auto-generated method stub
		Log.e("onTaskComplete", ""+result);
	}
	public void goToTaggingStep(int groupPosition){
		setContentView(R.layout.plantation);
		//		resizeImagesInEnterEcodeLayout(R.id.dialogHeader, R.id.step3Imageview);
		idOfCurrentLayout=R.layout.plantation;
		try {
			AppUtil.setLocationId(Integer.parseInt(locationsJsonObjectList.get(groupPosition).getString("lid")));
			String site=locationsJsonObjectList.get(groupPosition).optString("location");
			String description=locationsJsonObjectList.get(groupPosition).optString("description");
			populateTagMyTreeCompleteLayout(site,description);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
