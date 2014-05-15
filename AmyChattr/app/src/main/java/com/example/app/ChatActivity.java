package com.example.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Chat Activity is the view that contains a fragment for composing a reply message, and
 * a fragment for displaying messages.
 */
public class ChatActivity extends ActionBarActivity {
    public static final List<Message> msgs = new ArrayList<Message>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .add(R.id.container, new ComposeFragment())
                            .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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

    /**
     * This function will send the message to the database
     */
    public void send_message(View view) {
        EditText et = (EditText)findViewById(R.id.compose_reply);
        final String temp = et.getText().toString();
        et.setText("");

        Runnable runner = new Runnable() {
            @Override
            public void run() {
                // Sends the message (typed in the edittext) to the database
                // Uses hard-coded values 111 and 222 for the source and destination, respectively
                String url = String.format("http://chattr.site11.com/send_msg.php?src=%s&dest=%s&text=%s", "111", "222", temp);

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

    /**
     * This fragment holds the composition box and the 'send' button
     */
    public static class ComposeFragment extends Fragment{
        public ComposeFragment(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.compose_reply, container, false);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing the chat bubbles list
     */
    public static class PlaceholderFragment extends ListFragment {

        public PlaceholderFragment() {
        }

        /**
         * This function sets the margins for the listview fragment
         * I call it in onViewCreated because the listview was being a punk
         * @param v
         * @param l
         * @param t
         * @param r
         * @param b
         */
        public static void setMargins (View v, int l, int t, int r, int b) {
            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                p.setMargins(l, t, r, b);
                v.requestLayout();
            }
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState){

            // This runner is meant to retrieve all new messages for the conversation.
            // However, it's not working correctly.  I commented out the setListAdapter
            // in order to see some dummy data.
            final android.os.Handler handler = view.getHandler();
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    String url = "http://chattr.site11.com/receive_msg.php";

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

                            JSONObject jobj = new JSONObject(sb.toString());
                            JSONObject jdata = jobj.getJSONObject("responseData");
                            JSONArray entries = jdata.getJSONArray("entries");
                            for(int e = 0; e < entries.length(); e++){
                                JSONObject entry = entries.getJSONObject(e);
                                msgs.add(new Message(
                                        entry.getString("src"),
                                        entry.getString("dest"),
                                        entry.getString("text"),
                                        entry.getString("submit_time"),
                                        "forward"));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //setContentView(R.layout.fragment_chat);
                                    ArrayList<String> messages = new ArrayList<String>();
                                    for(int i = 0; i < messages.size(); i++){
                                        messages.add(msgs.get(i).content);
                                    }

                                    // setListAdapter is supposed to put the messages into the listview
                                    // Currently it shows the loading wheel indefinitely
                                    //setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.conversation_layout, messages));

                                }
                            });

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

            // Grab the resources from strings.xml
            Resources res = getResources();
            String[] conversation = res.getStringArray(R.array.dummy_convo);
            // Set the list with the string array and the chat layout .xml
            setListAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.chat_layout, conversation));
            ListView lv = getListView();
            lv.setTextFilterEnabled(true);

            // top margin = 150 is a large enough distance to prevent the two fragments from overlapping
            setMargins(view, 0, 140, 0, 0);

            // This gets rid of the listview dividers between each item
            lv.setDivider(null);

        }
    }

}
