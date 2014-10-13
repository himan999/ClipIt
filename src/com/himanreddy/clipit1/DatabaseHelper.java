package com.himanreddy.clipit1;


import android.content.ContentValues;

import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper 

{
		
	//private variables
	
	private static final int Db_Version = 1;

	
	private static final String Db_Name = "ClipboardManager";
	
	
	private static final String Table_CB = "clipboard";
	
	
	private static final String key_id = "id";
	
	private static final String key_label = "label";
	
	private static final String key_copiedtxt = "copiedtxt";
	
	SQLiteDatabase db;

	//Constructor
	
	public DatabaseHelper(Context context)
	
	{
		
		super(context, Db_Name, null, Db_Version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	
	{
		
		Log.d("Database Helper", "here in database helper class");
		
		String Create_CBT = "create table if not exists "+Table_CB+"( "+key_id+" integer primary key,"+key_label+" text,"
				+key_copiedtxt+" text"+")";
		
		db.execSQL(Create_CBT);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	
	{
	
		db.execSQL("drop table if exists"+Table_CB);
		
		onCreate(db);

	}
	
	void addTxt(CopyText cpt)

	{
	
		SQLiteDatabase db = this.getWritableDatabase();
		
		Log.d("DatabaseHelper class",cpt.getLabel());
		
		ContentValues values = new ContentValues();
		
		values.put(key_label,cpt.getLabel());
		
		values.put(key_copiedtxt, cpt.getText());
		
		db.insert(Table_CB, null, values);
		
		db.close();
		
	}
	
	CopyText getCopiedText(int id)
	
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(Table_CB,new String[] {key_id,key_label,key_copiedtxt},key_id+"=?",
									new String[]{String.valueOf(id)},null,null,null,null);
		
		if(cursor!=null)
			cursor.moveToFirst();
		
		CopyText copiedtxt = new CopyText(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
		
		return copiedtxt;
		
	}
	
	public Cursor getAllCopiedText()		//public List<CopyText> getAllCopiedText()
	
	{
		Log.d("dbhh class", "getallcopiedtxt");
		//List<CopyText> copiedtxtList = new ArrayList<CopyText>();
		
		String query = "select id as _id,label,copiedtxt from "+Table_CB;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		return cursor; //newly added
		/*
		if(cursor.moveToFirst())
		{
			do
			{
				
				CopyText copiedtxt = new CopyText();
				
				copiedtxt.setId(Integer.parseInt(cursor.getString(0)));
				
				copiedtxt.setLabel(cursor.getString(1));
				
				copiedtxt.setText(cursor.getString(2));
				
				copiedtxtList.add(copiedtxt);				
				
				Log.d(copiedtxt.getLabel(), copiedtxt.getText());
				
			}while(cursor.moveToNext());
		}
		
		return copiedtxtList;
		*/
	}
	
	public int updateCopiedText(CopyText copiedtxt)
	
	{
		
		SQLiteDatabase db = this.getWritableDatabase();
		 
        ContentValues values = new ContentValues();
    
        values.put(key_label,copiedtxt.getLabel());
        
        Log.d("dbh", ""+copiedtxt.getText());
        
        values.put(key_copiedtxt,copiedtxt.getText());
        
        return db.update(Table_CB, values, key_id+" = ?",new String[]{String.valueOf(copiedtxt.getId())});
		
	}
	
	public void deleteCopiedTxt(CopyText copiedtxt)
	
	{
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(Table_CB, key_id+" = ?", new String[]{String.valueOf(copiedtxt.getId())});
		
		Log.d("dbh class","deleted"+copiedtxt.getId());
		
		db.close();
	}
	
	public void deleteTable()
	
	{
		
		db.execSQL("drop table if exists"+Table_CB);
		
		onCreate(db);
		
	}

}
