package com.app.bids;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class LinnaeusDatabase extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "bidschart_db";
	public final static String DATABASE_PATH = "/data/data/com.app.bids/databases/";
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase dataBase;
	private final Context dbContext;

	public LinnaeusDatabase(Context context) {
		super(context, SplashScreen.DatabaseName, null, DATABASE_VERSION);
		this.dbContext = context;
		DATABASE_NAME = SplashScreen.DatabaseName;
		// checking database and open it if exists
		if (checkDataBase()) {
			openDataBase();
		} else {
			try {
				this.getReadableDatabase();
				copyDataBase();
				this.close();
				openDataBase();

			} catch (IOException e) {
				throw new Error("Error copying database");
			}
			Toast.makeText(context, "Initial database is created",
					Toast.LENGTH_LONG).show();
		}
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = dbContext.getAssets().open(DATABASE_NAME);
		String outFileName = DATABASE_PATH + DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
			// Toast.makeText(dbContext,
			// "Initial database is created"+outFileName,
			// Toast.LENGTH_LONG).show();
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		String dbPath = DATABASE_PATH + DATABASE_NAME;
		Log.v("db log", "database does't exist");
		dataBase = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		boolean exist = false;
		try {
			String dbPath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.v("db log", "database does't exist");
		}

		if (checkDB != null) {
			exist = true;
			checkDB.close();
		}
		return exist;
	}

	// Select All Data
	public class sCharacter {
		String _ID, _Char;

		// Set Value
		public void sID(String vID) {
			this._ID = vID;
		}

		public void sChar(String vChar) {
			this._Char = vChar;
		}

		// Get Value
		public String gCharID() {
			return _ID;
		}

		public String gChar() {
			return _Char;
		}
	}

	
	// ID : email = 1, facebook = 2, googleplus = 3, twitter = 4
	
	// ------------ Select All Data Login ------------------------------

	public String[][] SelectDataLogin() {
		try {
			String arrData[][] = null;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + "bidschart_login";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					arrData = new String[cursor.getCount()][cursor
							.getColumnCount()];
					/***
					 * [x][x] = id, type, status, user, password
					 */
					int i = 0;
					do {
						arrData[i][0] = cursor.getString(0);
						arrData[i][1] = cursor.getString(1);
						arrData[i][2] = cursor.getString(2);
						arrData[i][3] = cursor.getString(3);
						arrData[i][4] = cursor.getString(4);
						i++;
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return arrData;
		} catch (Exception e) {
			return null;
		}
	}

	// -------------------- update status login ------------------------------

	public long UpdateDataStatusLogin(String strID, String strStatus) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data
			ContentValues Val = new ContentValues();
			Val.put("status", strStatus);
			long rows = db.update("bidschart_login", Val, " id = ?",
					new String[] { String.valueOf(strID) });
			db.close();
			return rows; // return rows updated.
		} catch (Exception e) {
			return -1;
		}
	}

	// -------------------- insert login ------------------------------

		public long InsertDataMemberLogin(String strName, String strEmail,
				String strPassword, String strStatus) {
			try {
				SQLiteDatabase db;
				db = this.getWritableDatabase(); // Write Data

				// insert
				ContentValues Val = new ContentValues();
				Val.put("name", strName);
				Val.put("email", strEmail);
				Val.put("password", strPassword);
				Val.put("status", strStatus);
				long rows = db.insert("bidschart_login", null, Val);
				db.close();
				return rows; // return rows inserted.
			} catch (Exception e) {
				return -1;
			}
		}
		
	// -------------------- update email login ------------------------------

	public long UpdateDataEmailLogin(String strID, String strStatus, String strUser, String strPss) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data
			ContentValues Val = new ContentValues();
			Val.put("status", strStatus);
			Val.put("user", strUser);
			Val.put("password", strPss);
			long rows = db.update("bidschart_login", Val, " id = ?",
					new String[] { String.valueOf(strID) });
			db.close();
			return rows; // return rows updated.
		} catch (Exception e) {
			return -1;
		}
	}

	// ------------ Select All Data Member ------------------------------

	public String[][] SelectDataMember() {
		try {
			String arrData[][] = null;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + "bidschart_member";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					arrData = new String[cursor.getCount()][cursor
							.getColumnCount()];
					/***
					 * [x][x] = id, name, email, password, status
					 */
					int i = 0;
					do {
						arrData[i][0] = cursor.getString(0);
						arrData[i][1] = cursor.getString(1);
						arrData[i][2] = cursor.getString(2);
						arrData[i][3] = cursor.getString(3);
						arrData[i][4] = cursor.getString(4);
						i++;
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return arrData;
		} catch (Exception e) {
			return null;
		}

	}

	// -------------------- update member ------------------------------

	public long UpdateDataMember(String strID, String strStatus) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data
			ContentValues Val = new ContentValues();
			Val.put("status", strStatus);
			long rows = db.update("bidschart_member", Val, " id = ?",
					new String[] { String.valueOf(strID) });
			db.close();
			return rows; // return rows updated.
		} catch (Exception e) {
			return -1;
		}
	}

	// -------------------- insert member ------------------------------

	public long InsertDataMember(String strName, String strEmail,
			String strPassword, String strStatus) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data

			// insert
			ContentValues Val = new ContentValues();
			Val.put("name", strName);
			Val.put("email", strEmail);
			Val.put("password", strPassword);
			Val.put("status", strStatus);
			long rows = db.insert("bidschart_member", null, Val);
			db.close();
			return rows; // return rows inserted.
		} catch (Exception e) {
			return -1;
		}
	}

	// ------------ Select All Data fav ------------------------------

	public String[][] SelectDataFav() {
		try {
			String arrData[][] = null;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + "bidschart_favorite";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					arrData = new String[cursor.getCount()][cursor
							.getColumnCount()];
					/***
					 * [x][x] = id, name
					 */
					int i = 0;
					do {
						arrData[i][0] = cursor.getString(0);
						arrData[i][1] = cursor.getString(1);
						i++;
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return arrData;
		} catch (Exception e) {
			return null;
		}

	}

	// -------------------- update favorite ------------------------------

	public long UpdateDataFav(String strID, String strName) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data
			ContentValues Val = new ContentValues();
			Val.put("name", strName);
			long rows = db.update("bidschart_favorite", Val, " id = ?",
					new String[] { String.valueOf(strID) });
			db.close();
			return rows; // return rows updated.
		} catch (Exception e) {
			return -1;
		}
	}

	// -------------------- insert favorite ------------------------------

	public long InsertDataFav(String strName) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase(); // Write Data

			// insert
			ContentValues Val = new ContentValues();
			Val.put("name", strName);
			long rows = db.insert("bidschart_favorite", null, Val);
			db.close();
			return rows; // return rows inserted.
		} catch (Exception e) {
			return -1;
		}
	}

	// -------------------- delete date ------------------------------

	// public long DeleteDataMember(String strID) {
	// try {
	// SQLiteDatabase db;
	// db = this.getWritableDatabase(); // Write Data
	//
	// long rows = db.delete("schedule", " id = ?",
	// new String[] { String.valueOf(strID) });
	// db.close();
	// return rows; // return rows delete.
	// } catch (Exception e) {
	// return -1;
	// }
	// }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

}