package org.sankalptaru.www.framework.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.sankalptaru.www.framework.model.Census;
import org.sankalptaru.www.framework.model.Forest_Census;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat user
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "sankalptaru";

	// Table Names
	private static final String TABLE_FOREST_CENSUS = "forest_census";
	private static final String TABLE_CENSUS = "census";
	private static final String TABLE_INVITES = "invites";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// NOTES Table - column nmaes
	private static final String KEY_TREENAME = "treename";
	private static final String KEY_LATITUDE = "latitude";

	// TAGS Table - column names
	private static final String KEY_FOREST_NAME = "forest_name";


	private static final String KEY_LONGITUDE = "longitude";

	private static final String KEY_TREEHEIGHT = "height";

	private static final String KEY_TREEGIRTH = "girth";

	private static final String KEY_TREECANOPY = "canopy_size";

	private static final String KEY_REMARKS = "remarks";

	private static final String KEY_TREEURL = "tree_url";

	private static final String KEY_FORESTKEY = "forest_id";

	private static final String CREATE_TABLE_CENSUS = "CREATE TABLE "
			+ TABLE_CENSUS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TREENAME
			+ " TEXT," + KEY_LATITUDE + " TEXT,"+ KEY_TREEURL + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_TREEHEIGHT + " INTEGER,"+ KEY_TREEGIRTH + " INTEGER,"+ KEY_TREECANOPY + " INTEGER,"
			+ KEY_REMARKS + " TEXT,"+ KEY_CREATED_AT
			+ " DATETIME" +" TEXT," + KEY_FORESTKEY + " INTEGER"+")";

	// User table create statement
	private static final String CREATE_FOREST_CENSUS = "CREATE TABLE " + TABLE_FOREST_CENSUS
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FOREST_NAME + " TEXT not null unique,"+ KEY_CREATED_AT + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_CENSUS);
		db.execSQL(CREATE_FOREST_CENSUS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOREST_CENSUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CENSUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVITES);

		// create new tables
		onCreate(db);
	}



	public long createCensus(Census census) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TREENAME, census.getTreename());
		values.put(KEY_LATITUDE, census.getLatitude());
		values.put(KEY_LONGITUDE, census.getLongitude());
		values.put(KEY_TREEHEIGHT, census.getHeight());
		values.put(KEY_TREEGIRTH, census.getGirth());
		values.put(KEY_TREECANOPY, census.getCanopy_size());
		values.put(KEY_REMARKS, census.getRemarks());
		values.put(KEY_FORESTKEY, census.getForest_id());
		values.put(KEY_CREATED_AT, getDateTime());
		values.put(KEY_TREEURL, census.getImageUrl());

		// insert row
		long census_id = db.insert(TABLE_CENSUS, null, values);

		return census_id;
	}

	public List<Census> getAllCensus(long l) {
		List<Census> census = new ArrayList<Census>();
		String selectQuery = "SELECT  * FROM " + TABLE_CENSUS + " WHERE "
				+ KEY_FORESTKEY + " = " + l;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Census td = new Census();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setTreename((c.getString(c.getColumnIndex(KEY_TREENAME))));
				td.setCreated_at(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
				td.setCanopy_size(c.getString((c.getColumnIndex(KEY_TREECANOPY))));
				td.setForest_id(c.getInt((c.getColumnIndex(KEY_FORESTKEY))));
				td.setGirth(c.getString((c.getColumnIndex(KEY_TREEGIRTH))));
				td.setHeight(c.getString((c.getColumnIndex(KEY_TREEHEIGHT))));
				td.setLatitude(c.getString(c.getColumnIndex(KEY_LATITUDE)));
				td.setLongitude(c.getString(c.getColumnIndex(KEY_LONGITUDE)));
				td.setRemarks(c.getString(c.getColumnIndex(KEY_REMARKS)));
				td.setImageUrl(c.getString(c.getColumnIndex(KEY_TREEURL)));

				// adding to topic list
				census.add(td);
			} while (c.moveToNext());
		}

		return census;
	}

	public long createForest(Forest_Census forest) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FOREST_NAME, forest.getForest_name());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long forest_id = db.insert(TABLE_FOREST_CENSUS, null, values);

		return forest_id;
	}

	public List<Forest_Census> getAllForest() {
		List<Forest_Census> forest_list = new ArrayList<Forest_Census>();
		String selectQuery = "SELECT  * FROM " + TABLE_FOREST_CENSUS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Forest_Census td = new Forest_Census();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setForest_name((c.getString(c.getColumnIndex(KEY_FOREST_NAME))));
				td.setCreated_at(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				// adding to topic list
				forest_list.add(td);
			} while (c.moveToNext());
		}

		return forest_list;
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public int getForest(String forestStr) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_FOREST_CENSUS + " WHERE "
				+ KEY_FOREST_NAME + " LIKE '" + forestStr+"'";


		Cursor c = db.rawQuery(selectQuery, null);

		return c.getCount();
	}
}
