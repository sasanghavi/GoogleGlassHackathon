package glass.remindme;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReminderDB extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "remindme";
	private static final int DATABASE_VERSION = 1;
	
	public String createTable = "create table reminder (id integer, descr text, time text,path text)";
	
	public String getRemind = "select descr";

	public ReminderDB(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists reminder");
		onCreate(db);
	}

	public void add(Reminder r){
		ContentValues values = new ContentValues();
		values.put("id", r.getId());
		values.put("descr", r.getDescr());
		values.put("time", r.getTime());
		values.put("path", r.getPath());
		long insertid = this.getWritableDatabase().insert("reminder", null, values);
		
	}
	
	public String get(int id){
		
		//select name from reminder where id = 5
		Cursor c = this.getWritableDatabase().query(
			    "reminder" /* table */,
			    new String[] { "descr" } /* columns */,
			    "id = ?" /* where or selection */,
			    new String[] { ""+id } /* selectionArgs i.e. value to replace ? */,
			    null /* groupBy */,
			    null /* having */,
			    null /* orderBy */
			);
		
		
		
		return c.getString(c.getColumnIndex("descr"));
		
		
	}

}
