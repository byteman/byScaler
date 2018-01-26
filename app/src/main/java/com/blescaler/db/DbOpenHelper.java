/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blescaler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 

public class DbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static DbOpenHelper instance;
	 
	//计重表	
	private static final String WEIGHT_TABLE_CREATE = "CREATE TABLE "
			+ WeightDao.TABLE_NAME + " ("
			+ WeightDao.COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WeightDao.COLUMN_GROSS  + " TEXT, "
			+ WeightDao.COLUMN_TARE + " TEXT, "
			+ WeightDao.COLUMN_NET  + " TEXT, "
			+ WeightDao.COLUMN_TIME  + " TEXT); ";
	//计数表		
	private static final String COUNT_TABLE_CREATE = "CREATE TABLE "
			+ CountDao.TABLE_NAME + " ("
			+ CountDao.COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CountDao.COLUMN_COUNT + " TEXT, "
			+ CountDao.COLUMN_PER_WEIGHT + " TEXT, "
			+ CountDao.COLUMN_TOTAL_WEIGHT  + " TEXT, "
			+ CountDao.COLUMN_TIME  + " TEXT); ";
					
			
	
	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}
	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	private static String getUserDatabaseName() {
        return  "blescaler.db";
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(WEIGHT_TABLE_CREATE); 
		db.execSQL(COUNT_TABLE_CREATE); 
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	
}
