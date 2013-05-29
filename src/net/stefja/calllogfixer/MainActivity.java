package net.stefja.calllogfixer;

import java.util.ArrayList;

import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private final static String LOG_TAG = "MainActivity";
	private ListView entriesListView = null;
	private final static String[] allowedTypes = new String[] {String.valueOf(Calls.INCOMING_TYPE), String.valueOf(Calls.OUTGOING_TYPE), String.valueOf(Calls.MISSED_TYPE)};
	private final static String selection = String.format("%1$s != ? AND %1$s != ? AND %1$s != ?",Calls.TYPE);


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		final Button bListSpurious = (Button)findViewById(R.id.main_button_list);
		final Button bCreateSpurious = (Button)findViewById(R.id.main_button_create);
		final Button bDeleteSpurious = (Button)findViewById(R.id.main_button_delete);
		entriesListView = (ListView)findViewById(R.id.main_listview);

		bListSpurious.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { listSpuriousCallLogEntries();	}
		});
		
		bCreateSpurious.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {	createSpuriousCallLogEntries(); }
		});
		bDeleteSpurious.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {	deleteSpuriousCallLogEntries(); }
		});
	}
	
	private void createSpuriousCallLogEntries() {
		Log.d(LOG_TAG,"Create");
		final ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Calls.CACHED_NAME, "Spurious");
		values.put(Calls.TYPE, 9);
		values.put(Calls.NUMBER, "+354 123 4567");
		values.put(Calls.DATE, System.currentTimeMillis());
		values.put(Calls.DURATION, 200);
		cr.insert(Calls.CONTENT_URI, values);
		
	}

	private void deleteSpuriousCallLogEntries() {
		Log.d(LOG_TAG,"Delete");
		final ContentResolver cr = getContentResolver();
		
		cr.delete(Calls.CONTENT_URI, selection, allowedTypes);
		
	}

	private void listSpuriousCallLogEntries() {
		Log.d(LOG_TAG,"List");
		/*
		 * From http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.0.4_r2.1/com/android/contacts/calllog/CallTypeIconsView.java?av=f
		 * 
78     private Drawable getCallTypeDrawable(int callType) {
79         switch (callType) {
80             case Calls.INCOMING_TYPE:
81                 return mResources.incoming;
82             case Calls.OUTGOING_TYPE:
83                 return mResources.outgoing;
84             case Calls.MISSED_TYPE:
85                 return mResources.missed;
86             case Calls.VOICEMAIL_TYPE:
87                 return mResources.voicemail;
88             default:
89                 throw new IllegalArgumentException("invalid call type: " + callType);
90         }
91     }
		 */


		final ContentResolver cr = getContentResolver();
		Log.d(LOG_TAG,selection);
		final ArrayList<String> alBadEntries = new ArrayList<String>();
		Cursor cur = cr.query(Calls.CONTENT_URI,null, selection , allowedTypes, null);
		if (cur.getCount() > 0) {
			final int typeColumn = cur.getColumnIndex(Calls.TYPE);
			while (cur.moveToNext()) {
				final String name = cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME));
				final String number = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
				final int callType = cur.getInt(typeColumn);
				// String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				// String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Log.d(LOG_TAG,String.format("Calltype: %d, name: %s, number: %s",callType , name, number));
				
				alBadEntries.add(String.format("No.: %s\nName: %s\nCalltype: %d",number, name, callType));
			}
		}
		cur.close();
		
		entriesListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_row, R.id.cell_text, alBadEntries));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
