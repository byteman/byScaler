 
package com.example.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.db.WeightRecord;

@SuppressLint("DefaultLocale")
public class WeightDao {
	public static final String TABLE_NAME = "weight_table";
	public static final String COLUMN_ID = "wtid";
	public static final String COLUMN_GROSS= "gross";
    public static final String COLUMN_TARE = "tare";
    public static final String COLUMN_NET= "net";
    public static final String COLUMN_TIME = "wt_time";
 

	private DbOpenHelper dbHelper;
	private int maxid = 0;
	public WeightDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
		maxid = 0;//getMaxID();
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveWeightList(List<WeightRecord> wtList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (WeightRecord item : wtList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_GROSS, item.getGross());				
                values.put(COLUMN_TARE, item.getTare());
                values.put(COLUMN_NET, item.getNet());
                values.put(COLUMN_TIME, item.getTime());
                
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public List<WeightRecord> getWeightList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<WeightRecord> items = new ArrayList<WeightRecord>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String gross = cursor.getString(cursor.getColumnIndex(COLUMN_GROSS));
				String tare = cursor.getString(cursor.getColumnIndex(COLUMN_TARE));
				String net = cursor.getString(cursor.getColumnIndex(COLUMN_NET));
				long   times = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
				String wetid = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
		        
				WeightRecord item = new WeightRecord();
				item.setGross(gross);
				item.setTare(tare);
				item.setNet(net);
				item.setTime(times);
				item.setID(wetid);
				
				//maxid = Integer.parseInt(wetid);
				items.add(item);
			}
			cursor.close();
		}
		return items;
	}
	public  boolean getWeightRecord(WeightRecord item) {
		boolean ok = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if (db.isOpen()) {
			
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME   , null);
			
			//Cursor cursor = db.rawQuery("select * from table", null);
			
			if(cursor != null && cursor.getCount() > 0){
				if (cursor.moveToLast()) {
				    // 该cursor是最后一条数据
					String gross = cursor.getString(cursor.getColumnIndex(COLUMN_GROSS));
					String tare = cursor.getString(cursor.getColumnIndex(COLUMN_TARE));
					String net = cursor.getString(cursor.getColumnIndex(COLUMN_NET));
					long   times = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
					String wetid = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
			        
					
					item.setGross(gross);
					item.setTare(tare);
					item.setNet(net);
					item.setTime(times);
					item.setID(wetid);
					ok = true;
				}
				
			}
			cursor.close();	
		}
		return ok;
	}
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteWeight(String wt_id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{wt_id});
		}
	}
	
	public int getMaxID(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cur = db.rawQuery("selct max(wt_id) from weight_record",null);
			String id = cur.getString(cur.getColumnIndex(COLUMN_ID));
			return Integer.parseInt(id);
			//db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal)(TABLE_NAME, COLUMN_ID + " = ?", new String[]{wt_id});
		}
		return -1;
	}
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveWeight(WeightRecord item){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_GROSS, item.getGross());
		values.put(COLUMN_TARE, item.getTare());
		values.put(COLUMN_NET, item.getNet());
		//item.setID(String.valueOf(++maxid));
		long time=System.currentTimeMillis();
		values.put(COLUMN_TIME, time);
		item.setTime(time);
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
			
		}
	}
}
