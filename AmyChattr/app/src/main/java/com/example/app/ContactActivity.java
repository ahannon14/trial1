package com.example.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

/**
 * This activity displays contact information.  Currently unfinished.
 */
public class ContactActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact, menu);
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

    /**
     * A placeholder fragment containing the contacts view
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

            // Set the view to the contacts_layout
            this.getActivity().setContentView(R.layout.contacts_layout);
            Intent i = this.getActivity().getIntent();

            // Get the extra data from the intent
            int itemNumber = i.getIntExtra("contact item number",0);

            TextView tv1 = (TextView)getActivity().findViewById(R.id.contact_name);
            String[] c1 = getResources().getStringArray(R.array.contacts_list);

            // Use the dummy contact information where you can
            if(itemNumber < c1.length){
                tv1.setText(String.format("Contact: %s", c1[itemNumber]));
            }
            // Otherwise, set error message
            else{
                tv1.setText("Contact: Not found");
            }

            return rootView;
        }
    }

}
