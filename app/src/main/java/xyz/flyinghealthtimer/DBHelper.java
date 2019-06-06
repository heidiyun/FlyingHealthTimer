package xyz.flyinghealthtimer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "spheregeom.db";
    private static final int DATABASE_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // invoke super constructor.

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SSS", "onCreate");
        // create two tables, vertices and triangles


        db.execSQL("CREATE TABLE records ( _id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, name TEXT, count INT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop the two tables, vertices and triangles
        db.execSQL("DROP TABLE IF EXISTS records");
        onCreate(db);
    }

    public List<Record> getList() {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT _ID, NAME, COUNT FROM RECORDS");

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);

        List<Record> records = new ArrayList<>();
        Record record = null;


        while (cursor.moveToNext()) {
            String name =  cursor.getString(2);
            int count = cursor.getInt(3);
            Log.i("SSS", "ddddd"+count);
            record = new Record(name, count);
            records.add(record);

        }

        return records;
    }
    public  List<Record> selectColumns(String queryDate) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("records", null, null, null, null, null, null);
        List<Record> records = new ArrayList<>();
        Record record = null;
        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            if (date.equals( queryDate)) {
                String name = cursor.getString(2);
                int count = cursor.getInt(3);
                record = new Record(name, count);
                records.add(record);
            }
        }

        return records;

    }


}

