package com.example.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static int tabPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the keyboard-- for some reason, my project made this pop up after login
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        tabPosition = tab.getPosition();
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // CHANGED HERE to ListFragment (instead of Fragment)
        // Changed again to Fragment (somehow, now it works?!)
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0 || position == 2){
                PlaceholderFragment pf = PlaceholderFragment.newInstance(position + 1);
                return pf;
            }
            else return ComposeFragment.newInstance(position + 1);

            //return pf;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A fragment to hold a non-list tab:  the compose new message tab!
     */
    public static class ComposeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         * Feed the information (sectionNumber) that you need here
         */
        public static ComposeFragment newInstance(int sectionNumber) {
            ComposeFragment fragment = new ComposeFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ComposeFragment() {
        }

        // Here, the fragment is set to the compose_msg layout
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           final View rootView = inflater.inflate(R.layout.compose_msg, container, false);
           setComponent(rootView);

            return rootView;
        }

        private void setComponent(final View rootView) {
            Button bt2 = (Button) rootView.findViewById(R.id.Button_send);

            bt2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    //get the message content
                    TextView messagContentView = (TextView) rootView.findViewById(R.id.msg_text);
                    final String smsContent = messagContentView.getText().toString();
                    messagContentView.setText("");
                    //get the destination number
                    TextView destContentView = (TextView) rootView.findViewById(R.id.msg_dest);
                    final String destNumber = destContentView.getText().toString();
                    destContentView.setText("");
                    //get the source number
                    TextView srcContentView = (TextView) rootView.findViewById(R.id.msg_src);
                    final String srcNumber = srcContentView.getText().toString();
                    srcContentView.setText("");

                    Runnable runner = new Runnable() {
                        @Override
                        public void run() {
                            // Format the string for the PHP request--enters source and destination and text as an argument
                            String url = String.format("http://chattr.site11.com/send_msg.php?src=%s&dest=%s&text=%s", srcNumber, destNumber, smsContent);

                            try {
                                HttpClient httpclient = new DefaultHttpClient();
                                HttpResponse response = httpclient.execute(new HttpGet(url));
                                StatusLine statusLine = response.getStatusLine();
                                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                                    StringBuilder sb = new StringBuilder();

                                    String line = null;
                                    while ((line = reader.readLine()) != null)
                                    {
                                        sb.append(line + "\n");
                                    }
                                } else {
                                    //Closes the connection.
                                    response.getEntity().getContent().close();
                                }
                            }
                            catch (Exception ex) {
                                String str = ex.getMessage();
                            }
                        }
                    };
                    new Thread(runner).start();
                }
            });
        }
    }

    /**
     * A placeholder fragment containing a simple view.  Changed to ListFragment
     */
    public static class PlaceholderFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         * Feed the information (sectionNumber) that you need here
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState){
            int section = getArguments().getInt(ARG_SECTION_NUMBER);

            final android.os.Handler handler = view.getHandler();
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    String url = "http://chattr.site11.com/receive_users.php";

                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response = httpclient.execute(new HttpGet(url));
                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                            StringBuilder sb = new StringBuilder();

                            String line = null;
                            while ((line = reader.readLine()) != null)
                            {
                                sb.append(line + "\n");
                            }
                            final ArrayList<User> users = new ArrayList<User>();
                            JSONObject jobj = new JSONObject(sb.toString());
                            JSONObject jdata = jobj.getJSONObject("responseData");
                            JSONArray entries = jdata.getJSONArray("entries");
                            for (int e=0; e<entries.length(); e++) {
                                JSONObject entry = entries.getJSONObject(e);
                                users.add(new User(
                                        entry.getString("name"),
                                        entry.getString("number")));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<String> names = new ArrayList<String>();
                                    for(int i = 0; i < users.size(); i++){
                                        names.add(users.get(i).name);
                                    }
                                    setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.conversation_layout, names));
                                }
                            });
                        } else {
                            //Closes the connection.
                            response.getEntity().getContent().close();
                        }
                    }
                    catch (Exception ex) {
                        ;
                    }
                }
            };
            new Thread(runner).start();

            ListView lv = getListView();
            lv.setTextFilterEnabled(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // Start the activity, send data over
                    // If tabPosition = 0, then send it to chat activity
                    // If tabPosition = 2, then send it to the contact view activity (1 should never be hit)
                    if(tabPosition == 0){
                        Intent intentChat = new Intent().setClass(getActivity(), ChatActivity.class);
                        intentChat.putExtra("chat item number", i);
                        startActivity(intentChat);
                    }
                    else if (tabPosition == 2){
                        Intent intentContact = new Intent().setClass(getActivity(), ContactActivity.class);
                        intentContact.putExtra("contact item number", i);
                        startActivity(intentContact);
                    }
                }
            });

        }

    }

}
