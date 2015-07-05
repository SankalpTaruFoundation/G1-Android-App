package org.sankalptaru.www.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FootPrintCalActivity extends Activity {
	private AlertDialog alertDialog;
	private ArrayList<ArrayList<JSONObject>>eachSectionOptionsList;
	public Dialog progressDialog;
	private RelativeLayout parentLayout;
	private ArrayList<String> widgetNameList;
	private ArrayList<String> sectionsTitleList;
	private TextView tit;
	private TextView hint;
	private ArrayList<String> sectionQuestionStringList;
	private ArrayList<JSONObject> listOfAnswersToPost;
	private String currentKey;
	private JSONObject jsonToPost;
	private String treesRecommended;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.footprint_cal);
		parentLayout=(RelativeLayout)findViewById(R.id.parentContainer);
		Properties prop = new Properties(); 
		try {
			prop = AppUtil.loadPropties(FootPrintCalActivity.this);
			AppUtil.loadProperties(prop);
		} catch (IOException e) {
			Log.e("StartActivity", "Exception", e);
		}

		if(jsonToPost==null)
			jsonToPost=new JSONObject();

		if(null==eachSectionOptionsList)
			eachSectionOptionsList=new ArrayList<ArrayList<JSONObject>>();
		else
			eachSectionOptionsList.clear();

		if(null==widgetNameList)
			widgetNameList=new ArrayList<String>();
		else
			widgetNameList.clear();

		if(null==sectionsTitleList)
			sectionsTitleList=new ArrayList<String>();
		else
			sectionsTitleList.clear();

		if(null==sectionQuestionStringList)
			sectionQuestionStringList=new ArrayList<String>();
		else
			sectionQuestionStringList.clear();

		if(null==listOfAnswersToPost)
			listOfAnswersToPost=new ArrayList<JSONObject>();
		else
			listOfAnswersToPost.clear();

		widgetNameList.add("button");
		widgetNameList.add("radios");
		widgetNameList.add("checkboxes");

		new GetJsonForCalculator().execute(AppUtil.getAppProperty().getProperty("CALCULATOR_SERVICE_URL"));
	}
	private class GetJsonForCalculator extends AsyncTask<String, Integer, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(FootPrintCalActivity.this, "", progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsResponse = null;
			try {
				jsResponse=new JSONObject(AppUtil.getResponse(params[0]));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						AppUtil.cancelProgressDialog();
						createAlertMessageDialogAndBackToMenu("Service Failed.", FootPrintCalActivity.this);
					}
				});
				this.cancel(true);
			}
			return jsResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				populateViews(result);
			}
			else{
				createAlertMessageDialogAndBackToMenu("Service Failed.", FootPrintCalActivity.this);
			}
		}
	}
	private void createAlertMessageDialogAndBackToMenu(String textToShow,final FootPrintCalActivity footPrintCalActivity) {
		// TODO Auto-generated method stub
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(footPrintCalActivity);
		localBuilder.setCancelable(false);
		LinearLayout localLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_dailog, null);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.tvTitle);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.tvMessage);
		Button localButton1 = (Button)localLinearLayout.findViewById(R.id.btnYes);
		Button localButton2 = (Button)localLinearLayout.findViewById(R.id.btnNo);
		localLinearLayout.findViewById(R.id.viewLine).setVisibility(0);
		localTextView1.setText("Warning");
		localTextView2.setText(textToShow);
		localButton1.setText("     OK     ");
		localButton1.setVisibility(View.VISIBLE);
		localButton2.setVisibility(View.GONE);
		localButton1.setOnClickListener(new View.OnClickListener()
		{


			public void onClick(View paramAnonymousView)
			{
				alertDialog.dismiss();
				back();
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
	public void populateViews(JSONObject jsResponse) {
		// TODO Auto-generated method stub
		ArrayList<JSONObject>sectionsJsonObjectsList =new ArrayList<JSONObject>();
		try {
			for (int i = 0; i <jsResponse.length(); i++) {
				sectionsJsonObjectsList.add(jsResponse.getJSONObject("S"+(i+1)));
			}
			for (int i = 0; i < sectionsJsonObjectsList.size(); i++) {
				ArrayList<JSONObject> tempList=new ArrayList<JSONObject>();
				for (int j = 0; j < sectionsJsonObjectsList.get(i).length(); j++) {
					JSONObject currentJsonObject=sectionsJsonObjectsList.get(i);
					if(currentJsonObject.has("Q"+(j+1))){
						tempList.add(currentJsonObject.getJSONObject("Q"+(j+1)));
						sectionQuestionStringList.add("S"+(i+1)+"_"+"Q"+(j+1)+"_");
					}
					else{
						continue;
					}
				}
				eachSectionOptionsList.add(tempList);
			}

			for (int i = 0; i < sectionsJsonObjectsList.size(); i++) {
				sectionsTitleList.add(sectionsJsonObjectsList.get(i).optString("#title"));
			}
			populateFirstView();
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void populateFirstView() {
		// TODO Auto-generated method stub
		parentLayout.removeAllViews();
		AppUtil.cancelProgressDialog();

		View lineOne=(View)findViewById(R.id.zeroLine);
		View lineTwo=(View)findViewById(R.id.firstLine);
		View lineThree=(View)findViewById(R.id.secondLine);
		lineOne.setVisibility(View.VISIBLE);
		lineTwo.setVisibility(View.VISIBLE);
		lineThree.setVisibility(View.VISIBLE);

		TextView title=(TextView)findViewById(R.id.headerText);
		title.setVisibility(View.VISIBLE);

		addActualWidget(eachSectionOptionsList.get(0).get(0),0);

	}
	private void addActualWidget(JSONObject jsonObject, int sectionIndex) {
		// TODO Auto-generated method stub
		addMainSectionTextViews(sectionIndex);
		tit=(TextView)findViewById(R.id.quesTitle);
		tit.setText("");

		hint=(TextView)findViewById(R.id.quesRef);
		hint.setText("");
		String jsonObjectNameWithinQuestion=jsonObject.optString("#title");
		if(jsonObjectNameWithinQuestion.contains(" ")){
			jsonObjectNameWithinQuestion=jsonObjectNameWithinQuestion.replaceAll(" ", "_");
		}
		JSONObject operationalJsonObject = null;
		Iterator<String> data=jsonObject.keys();
		String jsonObjectKeyClone = null;
		while (data.hasNext()) {
			String type = (String) data.next();
			for (int i = 0; i < sectionQuestionStringList.size(); i++) {
				if(type.contains(sectionQuestionStringList.get(i))){
					jsonObjectKeyClone=sectionQuestionStringList.get(i);
					try {
						currentKey=type;
						operationalJsonObject=jsonObject.getJSONObject(type);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		switch (widgetNameList.indexOf(operationalJsonObject.optString("#type"))) {
		case 0:
			addButtonToLayout(operationalJsonObject,sectionIndex,jsonObjectNameWithinQuestion);
			break;
		case 1:
			addRadioButtonsToLayout(operationalJsonObject,sectionIndex,jsonObjectNameWithinQuestion,currentKey);
			break;
		case 2:
			addChcekBoxesToLayout(operationalJsonObject,sectionIndex,jsonObjectNameWithinQuestion,jsonObject,jsonObjectKeyClone+"next");
			break;
		default:
			break;
		}
	}
	private void addMainSectionTextViews(int sectionIndex) {
		// TODO Auto-generated method stub
		LinearLayout sectionsLayout=(LinearLayout)findViewById(R.id.sectionTitleLayout);
		sectionsLayout.removeAllViews();
		final HorizontalScrollView mScrollView=(HorizontalScrollView)findViewById(R.id.scroll1);
		for (int i = 0; i < sectionsTitleList.size(); i++) {
			android.widget.LinearLayout.LayoutParams lp=new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			final TextView tempText=new TextView(FootPrintCalActivity.this);
			tempText.setTypeface(Typeface.DEFAULT_BOLD);
			tempText.setTextColor(Color.BLACK);
			lp.setMargins(5, 5, 5, 5);
			if(i!=sectionIndex)
				tempText.setBackgroundResource(R.drawable.roundbox);
			else{
				mScrollView.post(new Runnable() { 
					public void run() { 
						mScrollView.scrollTo(tempText.getLeft(), 0);
					} 
				});
				tempText.setBackgroundResource(R.drawable.green_roundbox);
			}
			tempText.setLayoutParams(lp);
			tempText.setText(sectionsTitleList.get(i));
			sectionsLayout.addView(tempText);
		}
	}
	private void addChcekBoxesToLayout(JSONObject jsonObject, final int sectionIndex, String questionName, final JSONObject jsonObject2, final String buttonJSONObjectKey) {
		// TODO Auto-generated method stub
		parentLayout.removeAllViews();


		String title=jsonObject.optString("#value");
		tit.setText(Html.fromHtml("<b><u><font color=#FF0000>Ques</font></u></b>"+": "+title));

		//		hint.setText(jsonObject.optString("#description"));

		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LinearLayout l=new LinearLayout(FootPrintCalActivity.this);
		l.setLayoutParams(params);
		l.setOrientation(1);
		parentLayout.addView(l);
		/*for (int i = 0; i < jsonObject.optJSONObject("#options").length(); i++) {
			CheckBox chkBx=new CheckBox(FootPrintCalActivity.this);
			try {
				chkBx.setTextColor(Color.BLACK);
				chkBx.setText(jsonObject.optJSONObject("#options").getJSONObject(""+(i+1)).optString("#label"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chkBx.setLayoutParams(params);
			l.addView(chkBx);
		}*/
		Iterator<String> chkKeys=jsonObject.optJSONObject("#options").keys();
		while (chkKeys.hasNext()) {
			String string = (String) chkKeys.next();
			CheckBox chkBx=new CheckBox(FootPrintCalActivity.this);
			try {
				chkBx.setTextColor(Color.BLACK);
				chkBx.setText(jsonObject.optJSONObject("#options").getJSONObject(string).optString("#label"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chkBx.setLayoutParams(params);
			l.addView(chkBx);
		}
		showSubSectionTextView(questionName,sectionIndex);
		Button tempButton=new Button(FootPrintCalActivity.this);
		params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		tempButton.setBackgroundResource(R.drawable.redbutton);
		final String buttonText=jsonObject2.optJSONObject(buttonJSONObjectKey).optString("#value");
		tempButton.setText(buttonText);
		tempButton.setTypeface(Typeface.DEFAULT_BOLD);
		tempButton.setTextColor(Color.BLACK);
		params.setMargins(10, 10, 10, 10);
		tempButton.setLayoutParams(params);
		l.addView(tempButton);
		tempButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(buttonText.equals("Calculate>>")){
					new PostAnswersToServer().execute(AppUtil.getAppProperty().getProperty("CALCULATOR_RESULT_URL"));
				}else{
					int []arr= getSectionAndRespectiveQuestionIndex(jsonObject2.optJSONObject(buttonJSONObjectKey),sectionIndex);
					addActualWidget(eachSectionOptionsList.get(arr[0]).get(arr[1]),arr[0]);
				}

			}
		});
	}
	class PostAnswersToServer extends AsyncTask<String, Integer, JSONObject>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppUtil.initializeProgressDialog(FootPrintCalActivity.this, "", progressDialog);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject jsResponse=null;
			try {
				jsResponse=new JSONObject(AppUtil.doPostJsonObject(params[0], jsonToPost));
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
			AppUtil.cancelProgressDialog();
			try {
				createResultTable(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void addRadioButtonsToLayout(final JSONObject jsonObject, final int sectionIndex, String questionName, final String currentKey2) {
		// TODO Auto-generated method stub
		parentLayout.removeAllViews();
		String title=jsonObject.optString("#title");
		tit.setText(Html.fromHtml("<b><u><font color=#FF0000>Ques</font></u></b>"+": "+title));
		hint.setText(Html.fromHtml("<b><u><font color=#3a8300>Note</font></u></b>"+": "+jsonObject.optString("#description")));

		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		RadioGroup rgGroup=new RadioGroup(FootPrintCalActivity.this);
		rgGroup.setLayoutParams(params);
		parentLayout.addView(rgGroup);
		ArrayList<String> keysList=new ArrayList<String>();
		JSONObject optionJsonObject=jsonObject.optJSONObject("#options");

		if(optionJsonObject!=null){
			Iterator<String> keys=optionJsonObject.keys();
			while (keys.hasNext()) {
				String type = (String) keys.next();
				keysList.add(type);
			}
			Collections.sort(keysList);

			for (int i = 0; i < jsonObject.optJSONObject("#options").length(); i++) {
				try {
					RadioButton rb=new RadioButton(FootPrintCalActivity.this);
					rb.setText(jsonObject.optJSONObject("#options").getJSONObject(keysList.get(i)).optString("#label"));
					rb.setTextColor(Color.BLACK);
					rb.setLayoutParams(params);
					rb.setTag(keysList.get(i));
					rgGroup.addView(rb);
					rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							// TODO Auto-generated method stub
							String tag=((RadioButton)findViewById(checkedId)).getTag().toString();
							final int []arr= getSectionAndRespectiveQuestionIndex(jsonObject.optJSONObject("#options").optJSONObject(tag),sectionIndex);
							try {
								jsonToPost.putOpt(currentKey2, tag);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							addActualWidget(eachSectionOptionsList.get(arr[0]).get(arr[1]),arr[0]);
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			JSONArray optionJsonArray=jsonObject.optJSONArray("#options");
			for (int i = 0; i < optionJsonArray.length(); i++) {
				try {
					RadioButton rb=new RadioButton(FootPrintCalActivity.this);
					rb.setText(optionJsonArray.getJSONObject(i).optString("#label"));
					rb.setTextColor(Color.BLACK);
					rb.setLayoutParams(params);
					rb.setTag(optionJsonArray.getJSONObject(i));
					rgGroup.addView(rb);
					rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							// TODO Auto-generated method stub
							JSONObject js;
							try {
								String tag=((RadioButton)findViewById(checkedId)).getTag().toString();
								js = new JSONObject(tag);
								final int []arr= getSectionAndRespectiveQuestionIndex(js,sectionIndex);
								try {
									jsonToPost.putOpt(currentKey2, tag);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								addActualWidget(eachSectionOptionsList.get(arr[0]).get(arr[1]),arr[0]);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		showSubSectionTextView(questionName,sectionIndex);
	}
	public void createResultTable(JSONObject result) throws JSONException {
		// TODO Auto-generated method stub
		parentLayout.removeAllViews();
		tit.setText(Html.fromHtml("<b><font color=#3a8300>Here are your Green Quotient results:</font></b>"));		

		RelativeLayout treesLayout=new RelativeLayout(FootPrintCalActivity.this);
		TableLayout resultTable=new TableLayout(FootPrintCalActivity.this);

		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		treesLayout.setLayoutParams(params);
		parentLayout.addView(treesLayout);

		treesLayout.addView(resultTable);

		resultTable.setLayoutParams(params);

		resultTable.removeAllViews();
		resultTable.requestLayout();
		resultTable.addView(addHeaderRow(), 0);
		resultTable.requestLayout();
		TextView textView;
		TableRow tableRow;
		int columnNumber = 0;
		JSONObject greenQuotientObject = null;
		Iterator<String> iterate=result.keys();
		JSONArray tempJsobject=new JSONArray();
		int index=0;
		while (iterate.hasNext()) {

			String string = (String) iterate.next();
			if(result.optJSONObject(string).has("section")){
				try {
					tempJsobject.put(index, result.optJSONObject(string));
					index++;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				greenQuotientObject=result.optJSONObject(string);
			}
		}
		int numberOfRows=tempJsobject.length();

		for(int i=0;i<numberOfRows;i++){
			columnNumber = 0;

			tableRow = new TableRow(getApplicationContext());
			//			tableRow.setBackgroundColor(Color.DKGRAY);

			tableRow.setPadding(2, 2, 2, (numberOfRows-i)==1?2:0 );

			textView = getTextViewForTableRow(tempJsobject.getJSONObject(i).optString("title"));
			tableRow.addView(textView, columnNumber++);
			tableRow.addView(getSpacerTextView(),columnNumber++);


			textView = getTextViewForTableRow(tempJsobject.getJSONObject(i).optString("sum"));
			tableRow.addView(textView, columnNumber++);
			tableRow.addView(getSpacerTextView(),columnNumber++);

			textView = getTextViewForTableRow(tempJsobject.getJSONObject(i).optString("range"));

			textView.setTextColor(Color.BLACK);
			tableRow.addView(textView, columnNumber++);
			tableRow.addView(getSpacerTextView(),columnNumber++);

			String rating=tempJsobject.getJSONObject(i).optString("rating");
			textView = getTextViewForTableRow(rating);
			tableRow.addView(textView,columnNumber++);
			tableRow.addView(getSpacerTextView(),columnNumber++);

			textView = getTextViewForTableRow("     ");

			if(rating.contains("Average")){
				textView.setBackgroundColor(Color.YELLOW);
			}
			else if (rating.contains("Bad")) {
				textView.setBackgroundColor(Color.RED);
			}
			else if(rating.contains("Good")){
				textView.setBackgroundColor(Color.GREEN);
			}

			tableRow.addView(textView,columnNumber++);

			resultTable.addView(tableRow, i+1);
			resultTable.requestLayout();
		}
		resultTable.addView(addTeacherTabletStatusRow(greenQuotientObject), numberOfRows+1);

		TextView tv=new TextView(FootPrintCalActivity.this);
		tv.setTextColor(this.getResources().getColor(R.color.red_color));
		tv.setTextSize(13);
		tv.setTypeface(Typeface.DEFAULT_BOLD);
		if(Integer.parseInt(treesRecommended)>0)
		  tv.setText("You need to plant "+ treesRecommended +" trees to offset your CO2 footprint.");
		else
			 tv.setText("You need to plant 1 tree to offset your CO2 footprint.");
		tv.setLayoutParams(params);
		resultTable.addView(tv);
		
		Button plantNow=new Button(FootPrintCalActivity.this);
		params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		plantNow.setLayoutParams(params);
		plantNow.setBackgroundResource(R.drawable.greenbutton);
		plantNow.setText("Plant Now!");
		plantNow.setTextColor(this.getResources().getColor(R.color.white_color));
		plantNow.setTypeface(Typeface.DEFAULT_BOLD);
		resultTable.addView(plantNow);
		
		plantNow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent	in=new Intent(FootPrintCalActivity.this, PaymentActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();	
			}
		});
	}
	private TableRow addTeacherTabletStatusRow(JSONObject greenQuotientObject){
		TableRow teacherRow = new TableRow(this);
		//		teacherRow.setBackgroundColor(Color.WHITE);
		teacherRow.setPadding(2, 2, 2, 0 );
		int columnCounter = 0;

		teacherRow.addView(getTextViewForTableRow(greenQuotientObject.optString("title")), columnCounter++);
		teacherRow.addView(getSpacerTextView(), columnCounter++);
		teacherRow.addView(getTextViewForTableRow(greenQuotientObject.optString("sum")), columnCounter++);

		treesRecommended=greenQuotientObject.optString("trees");
		return teacherRow;
	}

	private TableRow addHeaderRow(){
		TableRow headerRow = new TableRow(this);
		//		headerRow.setBackgroundColor(Color.DKGRAY);
		headerRow.setPadding(2, 2, 2, 0 );
		int columnCounter = 0;

		headerRow.addView(getTextViewForHeaderRow("Section"), columnCounter++);
		headerRow.addView(getSpacerTextView(), columnCounter++);
		headerRow.addView(getTextViewForHeaderRow("Score"), columnCounter++);
		headerRow.addView(getSpacerTextView(), columnCounter++);
		headerRow.addView(getTextViewForHeaderRow("Range"), columnCounter++);
		headerRow.addView(getSpacerTextView(), columnCounter++);
		headerRow.addView(getTextViewForHeaderRow("Rating"), columnCounter++);
		headerRow.addView(getSpacerTextView(), columnCounter++);
		headerRow.addView(getTextViewForHeaderRow("Signal"), columnCounter++);

		return headerRow;

	}
	private TextView getTextViewForHeaderRow(String headerText){
		TextView textView = getTextViewForTableRow(headerText);
		textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		//		textView.setTextSize(fontSize);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	private TextView getTextViewForTableRow(String textToDisplay){
		TextView textView = new TextView(this);
		textView.setTextColor(Color.BLACK);
		//		textView.setTextSize(fontSize);
		textView.setTypeface(Typeface.SANS_SERIF);
		textView.setGravity(Gravity.CENTER);
		//		textView.setBackgroundColor(Color.WHITE);
		textView.setPadding(2, 2, 2, 2);
		textView.setText(textToDisplay);
		return textView;

	}

	private TextView getSpacerTextView(){
		TextView textView = new TextView(this);
		textView.setTextColor(Color.BLACK);
		//		textView.setTextSize(fontSize);
		textView.setTypeface(Typeface.SANS_SERIF);
		textView.setGravity(Gravity.CENTER);
		//		textView.setBackgroundColor(Color.WHITE);
		textView.setPadding(2, 2, 2,2);
		textView.setText("|");
		return textView;
	}

	private void addButtonToLayout(final JSONObject jsonObject, final int sectionIndex, String questionName) {
		// TODO Auto-generated method stub
		parentLayout.removeAllViews();
		Button tempButton=new Button(FootPrintCalActivity.this);
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tempButton.setBackgroundResource(R.drawable.redbutton);
		tempButton.setText(jsonObject.optString("#value"));
		tempButton.setTypeface(Typeface.DEFAULT_BOLD);
		tempButton.setTextColor(Color.BLACK);
		params.setMargins(10, 10, 10, 10);
		tempButton.setLayoutParams(params);

		parentLayout.addView(tempButton);



		tempButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int []arr= getSectionAndRespectiveQuestionIndex(jsonObject,sectionIndex);
				addActualWidget(eachSectionOptionsList.get(arr[0]).get(arr[1]),arr[0]);
			}
		});

		showSubSectionTextView(questionName,sectionIndex);
	}
	private int[] getSectionAndRespectiveQuestionIndex(JSONObject jsonObject, int sectionIndex2) {
		// TODO Auto-generated method stub
		int [] sectionQuestionArray =new int[2];
		String sectionIndex=jsonObject.optString("#next_section");
		String questionIndex=jsonObject.optString("#next_question");
		boolean sectionIndexChanged=true;
		boolean questionIndexChanged=true;
		if(sectionIndex.equals("")){
			sectionIndex=""+sectionIndex2;
			sectionIndexChanged=false;
		}


		sectionQuestionArray[0]=Integer.parseInt(sectionIndex);

		if(sectionIndexChanged)
			sectionQuestionArray[0]=sectionQuestionArray[0]-1;

		if(questionIndex.equals("")){
			questionIndex="0";
			questionIndexChanged=false;
		}


		sectionQuestionArray[1]=Integer.parseInt(questionIndex);

		if(questionIndexChanged)
			sectionQuestionArray[1]=sectionQuestionArray[1]-1;

		return sectionQuestionArray;
	}
	private void showSubSectionTextView(String questionName, int sectionIndex) {
		// TODO Auto-generated method stub
		LinearLayout linearLayout=(LinearLayout)findViewById(R.id.subSectionTitleLayout);
		linearLayout.removeAllViews();

		ArrayList<String> tempList=new ArrayList<String>();
		for (int j = 0; j < eachSectionOptionsList.get(sectionIndex).size(); j++) {
			tempList.add(eachSectionOptionsList.get(sectionIndex).get(j).optString("#title"));
		}
		final HorizontalScrollView mScrollView=(HorizontalScrollView)findViewById(R.id.scroll);

		for (int i = 0; i < tempList.size(); i++) {
			android.widget.LinearLayout.LayoutParams lp=new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
			final TextView tempText=new TextView(FootPrintCalActivity.this);
			tempText.setTypeface(Typeface.DEFAULT_BOLD);
			tempText.setTextColor(Color.BLACK);
			lp.setMargins(5, 5, 5, 5);
			String text=tempList.get(i);
			if(text.contains("_"))
				text=text.replaceAll("_", " ");


			if(questionName.contains("_"))
				questionName=questionName.replaceAll("_", " ");

			if(!questionName.equals(tempList.get(i)))
				tempText.setBackgroundResource(R.drawable.roundbox);
			else{
				mScrollView.post(new Runnable() { 
					public void run() { 
						mScrollView.scrollTo(tempText.getLeft(), 0);
					} 
				});
				tempText.setBackgroundResource(R.drawable.green_roundbox);
			}
			tempText.setLayoutParams(lp);
			tempText.setText(text);
			linearLayout.addView(tempText);
		}
		/*TextView subSectTextView=(TextView)findViewById(R.id.subSectionTitle);
		if (questionName.contains("_")) {
			questionName=questionName.replaceAll("_", " ");
		}
		subSectTextView.setText(questionName);
		subSectTextView.setVisibility(View.VISIBLE);*/
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//			super.onBackPressed();
		back();
	}
	public void goToMenu(View v){
		back();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	public void back(){
		Intent	in=new Intent(FootPrintCalActivity.this, MenuActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}
}
