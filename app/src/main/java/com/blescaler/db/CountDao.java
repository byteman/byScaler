 
package com.blescaler.db;

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


@SuppressLint("DefaultLocale")
public class CountDao {
	public static final String TABLE_NAME = "count_table";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_COUNT= "count";
    public static final String COLUMN_PER_WEIGHT = "per_weight";
    public static final String COLUMN_TOTAL_WEIGHT= "total_weight";
    public static final String COLUMN_TIME = "time";
 

	private DbOpenHelper dbHelper;
	private int maxid = 0;
	public CountDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
		maxid = 0;//getMaxID();
	}

	/**
	 * 保存计数记录列表
	 * 
	 * @param contactList
	 */
	public void saveWeightList(List<CountRecord> wtList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (CountRecord item : wtList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_COUNT, item.getCount());				
                values.put(COLUMN_PER_WEIGHT, item.getPerWeight());
                values.put(COLUMN_TOTAL_WEIGHT, item.getTotalWeight());
                values.put(COLUMN_TIME, item.getTime());
                
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取所有的称重记录
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public List<CountRecord> getCountList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<CountRecord> items = new ArrayList<CountRecord>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String count = cursor.getString(cursor.getColumnIndex(COLUMN_COUNT));
				String perw = cursor.getString(cursor.getColumnIndex(COLUMN_PER_WEIGHT));
				String totalw = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_WEIGHT));
				long   times = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
				String wetid = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
		        
				CountRecord item = new CountRecord();
				item.setCount(count);
				item.setPerWeight(perw);
				item.setTotalWeight(totalw);
				item.setTime(times);
				item.setID(wetid);
				
				//maxid = Integer.parseInt(wetid);
				items.add(item);
			}
			cursor.close();
		}
		return items;
	}
	//获取最近一条过磅记录
	public  boolean getCountRecord(CountRecord item) {
		boolean ok = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if (db.isOpen()) {
			
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME   , null);
			
			//Cursor cursor = db.rawQuery("select * from table", null);
			
			if(cursor != null && cursor.getCount() > 0){
				if (cursor.moveToLast()) {
				    // 该cursor是最后一条数据
					String count = cursor.getString(cursor.getColumnIndex(COLUMN_COUNT));
					String perw = cursor.getString(cursor.getColumnIndex(COLUMN_PER_WEIGHT));
					String totalw = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_WEIGHT));
					long   times = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
					String wetid = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
			        
					
					item.setCount(count);
					item.setPerWeight(perw);
					item.setTotalWeight(totalw);
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
	 * 删除一条过磅记录
	 * @param username
	 */
	public void deleteOne(String id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id});
		}
	}
	
	public int getMaxID(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cur = db.rawQuery("select max(id) from count_table",null);
			String id = cur.getString(cur.getColumnIndex(COLUMN_ID));
			return Integer.parseInt(id);
			//db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal)(TABLE_NAME, COLUMN_ID + " = ?", new String[]{wt_id});
		}
		return -1;
	}
	/**
	 * 保存一条过磅记录
	 * @param user
	 */
	public void saveOne(CountRecord item){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_COUNT, item.getCount());
		values.put(COLUMN_PER_WEIGHT, item.getPerWeight());
		values.put(COLUMN_TOTAL_WEIGHT, item.getTotalWeight());
		//item.setID(String.valueOf(++maxid));
		long time=System.currentTimeMillis();
		values.put(COLUMN_TIME, time);
		item.setTime(time);
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
			
		}
	}
}
