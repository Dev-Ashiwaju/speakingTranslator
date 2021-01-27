package com.example.speakingtranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Messages extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mCLayout;
    private ListView smsListView;
    private ArrayAdapter mAdapter;
    private Random mRandom = new Random();
    private static Messages inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    TextView msgBody;
    // Cursor Adapter
    SimpleCursorAdapter adapter;
    Cursor c;

    public static Messages instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = Messages.this;

        // Get the widget reference from XML layout
        smsListView = findViewById(R.id.SMSList);

        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[] { "_id", "address", "body", "date" };

        // Get Content Resolver object, which will deal with Content
        // Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        c = cr.query(inboxURI, reqCols, null, null, null);

        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(this, R.layout.lv_view_items, c,
                new String[] { "body", "address", "date" }, new int[] {
                R.id.msgBody, R.id.msgSender, R.id.msgDate });

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == aCursor.getColumnIndex("date")) {
                    String createDate = aCursor.getString(aColumnIndex);
                    TextView textView = (TextView) aView;
                    textView.setText(millisToDate(createDate));
                    return true;
                }

                return false;
            }
        });

        smsListView.setAdapter(adapter);

        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                TextView selectedShelfTextView = arg1.findViewById(R.id.msgBody);
                String selectedShelf = selectedShelfTextView.getText().toString();
                Toast.makeText(mContext, selectedShelf, Toast.LENGTH_LONG).show();
            }
        });

        smsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                if (smsListView.isItemChecked(pos)){smsListView.setItemChecked(pos,false);}else{smsListView.setItemChecked(pos,true);}
                Log.v("Long Click:","long clicked pos: " + pos);

                TextView selectedShelfTextView = arg1.findViewById(R.id.msgBody);
                String selectedShelf = selectedShelfTextView.getText().toString();

                Intent intent = new Intent(Messages.this, MainActivity.class);
                intent.putExtra("msg", selectedShelf);
                startActivity(intent);

                return true;
            }
        });

        //Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.messages);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.messages:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(),Info .class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        // Add SMS Read Permision At Runtime
        // Todo : If Permission Is Not GRANTED
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            // Todo : If Permission Granted Then Show SMS

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(Messages.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

    }

    public static String millisToDate(String TimeMillis) {
        String finalDate;
        long tm = Long.parseLong(TimeMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tm);
        Date date = calendar.getTime();
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        finalDate = outputFormat.format(date);
        return finalDate;
    }


    public String str;
    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        //smsInboxCursor.getString(indexAddress);
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();

        do {
            str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
//            if (smsInboxCursor.getString(indexAddress).equals("6505551212") ) {
//               str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
//                        "\n" + smsInboxCursor.getString(indexBody) + "\n";
//               System.out.println("K"+ smsInboxCursor.getString(indexAddress).compareTo("6505551212"));
//            }

            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

}
