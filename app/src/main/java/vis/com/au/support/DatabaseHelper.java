package vis.com.au.support;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static String dbName = "IndividualWallet";
	public static int version=1;
	
	public DatabaseHelper(Context context) {
		super(context, dbName, null, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS tblDocuments(docId INTEGER PRIMARY KEY AUTOINCREMENT,userType VARCHAR,userId INTEGER," +
				"	documentTitle VARCHAR,documentType VARCHAR,addInfo TEXT," +
				"	dateOfIssue VARCHAR,dateOfExpire VARCHAR,suplierName VARCHAR,registratinId VARCHAR,upLoadedDate VARCHAR,empImage blob not null )");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

	public static void insertData(Context context,ContentValues cv,String tblName){
		DatabaseHelper dHepler=new DatabaseHelper(context);
		SQLiteDatabase dt=dHepler.getWritableDatabase();
		Log.e("Error", dt+"");
		dt.insertWithOnConflict(tblName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
	}
	
	public static void upDate(Context context,ContentValues cv,String tblName,String id){
		DatabaseHelper dHelper = new DatabaseHelper(context);
		SQLiteDatabase dt = dHelper.getWritableDatabase();
		dt.update(tblName, cv, "docId"+"='"+id+"'", null);
		
	}
	
	public static Cursor readQuery(Context mcontext,String theQuery){
		DatabaseHelper dHelper = new DatabaseHelper(mcontext);
		SQLiteDatabase sloH = dHelper.getReadableDatabase();
		
		Cursor cursor = sloH.rawQuery(theQuery, new String[]{});
		Log.e("Error", theQuery+"");
		return cursor;
	}
}
