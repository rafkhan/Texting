package com.rafaelkhan.android.texting;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {

	private String dbName = "textingdb";
	private String allThreadTables = "allthreads";
	private SQLiteDatabase db;

	public DatabaseHandler(Context context) {
		DBHelper dbHelper = new DBHelper(context, this.dbName);
		this.db = dbHelper.getWritableDatabase();
		this.createThreadTable();
	}

	/*
	 * Creates the table that holds all unique numbers
	 */
	private void createThreadTable() {
		String newTable = "CREATE TABLE IF NOT EXIST " + this.allThreadTables
				+ "(_id INTEGER PRIMARY KEY, Number VARCHAR);";
		this.db.execSQL(newTable);
	}

	/*
	 * Handles sms messages when received
	 */
	public void onSMSReceive(String number, String msg, String time) {
		if(checkThreadExistance(number)) {
			//add message to thread
		} else {
			//create message thread
			this.addThreadToList(number);
			this.createSMSThread(number);
		}
	}

	/*
	 * Checks if a thread already exists
	 */
	public boolean checkThreadExistance(String number) {
		Cursor c = this.db.rawQuery("SELECT * FROM " + this.allThreadTables,
				null);
		c.moveToFirst();
		if (c.isAfterLast()) {
			do {
				String dbNum = c.getString(1);
				if (dbNum.equals(number)) {
					return true;
				}
			} while (c.moveToNext());
		}
		return false;
	}

	/*
	 * Creates a table to store the SMS messages for each unique phone number.
	 * 
	 * The phone number is then added to the table that stores all the unique
	 * numbers
	 */
	private void createSMSThread(String phoneNumber) {
		String newTable = "CREATE TABLE IF NOT EXIST " + phoneNumber
				+ "(_id INTEGER PRIMARY KEY, Sender VARCHAR,"
				+ "Message VARCHAR, Time VARCHAR);";
		this.db.execSQL(newTable);
		this.addThreadToList(phoneNumber);
	}

	/*
	 * Adds unique phone number to table containing all threads
	 */
	public void addThreadToList(String phoneNumber) {
		this.db.execSQL("INSERT INTO " + this.allThreadTables
				+ " Values (null, \'" + phoneNumber + "\');");
	}

	public void close() {
		this.db.close();
	}

	/*
	 * This class creates the DB
	 */
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String dbName) {
			super(context, dbName, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
