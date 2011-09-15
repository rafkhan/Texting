package com.rafaelkhan.android.texting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {

	private String dbName = "textingdb";
	private String allThreadTables = "allthreads";
	private SQLiteDatabase db;

	public DatabaseHandler(Context context) {
		DBHelper dbHelper = new DBHelper(context, this.dbName);
		this.db = dbHelper.getWritableDatabase();
	}

	/*
	 * Creates a table to store the SMS messages for each unique phone number.
	 * 
	 * The phone number is then added to the table that stores all the unique
	 * numbers
	 */
	public void createSMSThread(String phoneNumber) {
		String newTable = "CREATE TABLE IF NOT EXIST " + phoneNumber
				+ "(_id INTEGER PRIMARY KEY, Sender VARCHAR,"
				+ "Message VARCHAR, Time VARCHAR);";
		this.db.execSQL(newTable);
		this.addThreadToList(phoneNumber);
	}

	public void addThreadToList(String phoneNumber) {
		// create table for all threads
		String newTable = "CREATE TABLE IF NOT EXIST " + this.allThreadTables
				+ "(_id INTEGER PRIMARY KEY, Number VARCHAR);";
		this.db.execSQL(newTable);

		// add number to table
		this.db.execSQL("INSERT INTO " + this.allThreadTables
				+ " Values (null, \'" + phoneNumber + "\');");
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
