package com.himanreddy.clipit1;


import java.util.Calendar;

import android.app.Activity;

import android.app.AlertDialog;

import android.content.ClipData;

import android.content.ClipboardManager;

import android.content.DialogInterface;

import android.database.Cursor;

import android.os.Bundle;

import android.util.Log;

import android.view.ContextMenu;

import android.view.Menu;

import android.view.MenuInflater;

import android.view.MenuItem;

import android.view.View;

import android.view.ContextMenu.ContextMenuInfo;

import android.widget.AdapterView;

import android.widget.EditText;

import android.widget.LinearLayout;

import android.widget.ListView;

import android.widget.SimpleCursorAdapter;

import android.widget.TextView;

import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	//private variables
	ClipboardManager cp;
	ListView listView;
	TextView tv;
	ClipData cd;
	ClipData.Item item;
	Calendar date = Calendar.getInstance();
	private String copytxt;
	private String label;
	private String[] copytxts ={"label","copiedtxt"};
	private int[] toViews = {R.id.txt,R.id.copiedTxt};
	CopyText cpt;
	DatabaseHelper dbh;
	SimpleCursorAdapter adapter;
	private Cursor c;
	private int count=0;
	int flag;

	// when the application is started this function is invoked
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView)findViewById(R.id.notxt);
		cp=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		dbh = new DatabaseHelper(this); 
		
		if(!cp.hasPrimaryClip() && dbh.getReadableDatabase()==null) {
			tv.setVisibility(View.VISIBLE);
		} else if(cp.hasPrimaryClip() && dbh.getReadableDatabase()==null) {
			tv.setVisibility(View.GONE);
			updateList();
		} else {
			tv.setVisibility(View.GONE);
			loadListView();
		}
		ClipboardManager.OnPrimaryClipChangedListener pccl = new ClipboardManager.OnPrimaryClipChangedListener() {
			@Override
			public void onPrimaryClipChanged() {
				if(flag!=1) {
					updateList();
				} else {
					loadListView();
				}
			}
		};
		
		cp.addPrimaryClipChangedListener(pccl);
	
	}
	
	//Called when the data in clipboard is changed
	
	public void updateList() {
		cd = cp.getPrimaryClip();
		item = cd.getItemAt(0);
		copytxt = item.coerceToText(getApplicationContext()).toString();
		label = copytxt.substring(0, 10);
		cpt = new CopyText(label,copytxt);
		dbh.addTxt(cpt);
		
		loadListView();
		Toast.makeText(MainActivity.this, label, Toast.LENGTH_SHORT).show();
		
		tv.setVisibility(View.GONE);	
	}
	
	//Function to load data from the Database
	
	public void loadListView() {
		flag=0;
		c = dbh.getAllCopiedText();	
		count = c.getCount();
		if(count==0) {
			tv.setVisibility(View.VISIBLE);
		}
		showListView();	
	}
	
	// Displays list view on the screen
	public void showListView() {
		adapter = new SimpleCursorAdapter(this, R.layout.list_view, c, copytxts, toViews,0);
		listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) 
			{
				 Toast.makeText(getApplicationContext(),
	                     "Position :"+position+"  ListItem : "+c.getString(1) , Toast.LENGTH_SHORT)
	                      .show();
				 
			}
			
			
		});
		registerForContextMenu(listView);
	}
	
	// Called when user long presses any row on the list to show available options like copy,edit,delete
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	//Function which performs operation assigned to copy,edit,delete options
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.copy:
			cp.setPrimaryClip(ClipData.newPlainText(c.getString(1), c.getString(2)));
			flag=1;
			Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
			return true;
			
		case R.id.edit:
			AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
			adb1.setTitle("Edit");
			final EditText et = new EditText(MainActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
			et.setLayoutParams(lp);
			et.setText(c.getString(2));
			adb1.setView(et);
			adb1.setNegativeButton("Cancel", null);
			adb1.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final String et1 = et.getText().toString();
					cpt = new CopyText(c.getInt(0),c.getString(1),et1);
	                dbh.updateCopiedText(cpt);
	                loadListView();
	            }
			});
			adb1.show();
			return true;
			
		case R.id.delete:
			AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
	        adb.setTitle("Delete?");
	        adb.setMessage("Are you sure you want to delete " + c.getString(1));
	        cpt = new CopyText(c.getInt(0),c.getString(1),c.getString(2));
	        Log.d("context item clicked", ""+c.getString(1)+" "+c.getString(2));
	        adb.setNegativeButton("Cancel", null);
	        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	                dbh.deleteCopiedTxt(cpt);
	                loadListView();
	            }
	        });
	        adb.show();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	
	{
		// Inflate the menu; this adds items to the action bar if it is present.
	
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		
		if (id == R.id.action_settings) 
		
		{
		
			return true;
		
		}
		
		return super.onOptionsItemSelected(item);
	
	}
	
}
