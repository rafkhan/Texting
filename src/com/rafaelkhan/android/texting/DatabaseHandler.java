package com.rafaelkhan.android.texting;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * HOW THIS WORKS
 * 
 * DB:
 * 	- One table holds all the information about a thread.
 * 	  The phone number of the other person, the last message sent,
 * 	  and the time of the last message.
 * 
 *  - Each phone number has it's own table, each entry contains the sender, 
 *    a message, and the time it was sent
 * 
 * Receiving:
 * 	1. Service receives message, and calls onSMSReceive
 * 	2. Then adds the thread to the table with all Threads names and info
 *  3. 
 */
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
		String newTable = "CREATE TABLE IF NOT EXISTS "
				+ this.allThreadTables
				+ "(_id INTEGER PRIMARY KEY, Number VARCHAR, LastMsg VARCHAR, LastTime VARCHAR);";
		this.db.execSQL(newTable);
	}

	/*
	 * Handles sms messages when received
	 */
	public void onSMSReceive(String number, String msg, String time) {
		if (checkThreadExistance(number)) {
			// add message to thread
			this.insertIntoThread(number, msg, time);
			this.updateThreadInfo(number, msg, time);
		} else {
			// create message thread
			this.createSMSThread(number, msg, time);
			this.updateThreadInfo(number, msg, time);
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
	 * Inserts SMS data into its respective table
	 */
	private void insertIntoThread(String number, String msg, String time) {
		String sql = "INSERT INTO " + number + " values (null, \'" + number
				+ "\',\'" + msg + "\',\'" + time + "\');";
		this.db.execSQL(sql);
	}

	/*
	 * Creates a table to store the SMS messages for each unique phone number.
	 * 
	 * The phone number is then added to the table that stores all the unique
	 * numbers
	 */
	private void createSMSThread(String phoneNumber, String msg, String time) {
		String newTable = "CREATE TABLE IF NOT EXIST " + phoneNumber
				+ "(_id INTEGER PRIMARY KEY, Sender VARCHAR,"
				+ "Message VARCHAR, Time VARCHAR);";
		this.db.execSQL(newTable);
		this.addThreadToList(phoneNumber, msg, time);
	}

	/*
	 * Adds unique phone number to table containing all threads
	 */
	private void addThreadToList(String phoneNumber, String msg, String time) {
		String sql = "INSERT INTO " + this.allThreadTables
				+ " Values (null, \'" + phoneNumber + "\', \'" + msg + "\', \'"
				+ time + "\');";
		this.db.execSQL(sql);
	}

	/*
	 * Updates the last message and time of a thread
	 */
	private void updateThreadInfo(String number, String msg, String time) {
		String sql = "UPDATE " + this.allThreadTables + " SET LastMsg = \'"
				+ msg + "\' WHERE Number = \'" + number + "\'";
		String sql2 = "UPDATE " + this.allThreadTables + " SET LastTime = \'"
				+ time + "\' WHERE Number = \'" + number + "\'";
		this.db.execSQL(sql);
		this.db.execSQL(sql2);
	}

	/*
	 * returns a list with all the info about each thread
	 */
	public ArrayList<HashMap<String, Object>> getAllThreads() {
		ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();

		// New message, list entry
		HashMap<String, Object> newMsg = new HashMap<String, Object>();
		newMsg.put("number", "New message");
		newMsg.put("lastmsg", "Compose a new message");
		newMsg.put("lasttime", null);
		al.add(newMsg);

		Cursor c = this.db.rawQuery("SELECT * FROM " + this.allThreadTables,
				null);
		c.moveToFirst();
		if (c.isAfterLast()) {
			do {
				HashMap<String, Object> temp = new HashMap<String, Object>();
				try {
					temp.put("number", c.getString(1));
					temp.put("lastmsg", c.getString(2));
					temp.put("lasttime", c.getString(3));
					al.add(temp);
				} catch (Exception e) { // if columns are empty
				}
			} while (c.moveToNext());
		}
		return al;
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