package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.PatternSearchAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternSearchActivity extends AppCompatActivity {
    private ListView mListView;
    private ProgressDialog pDialog;
    private SessionManager session;

    private EditText search_txt;
    private MenuItem searchMenuItem;
    private PatternSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("SEARCH PATTERN");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

        search_txt = (EditText)findViewById(R.id.search_txt);
        search_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                         
            }
        });
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mListView = (ListView)findViewById(R.id.listviewPattern);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();
                AppConfig.selPatternInfo = (PatternInfo) (mListView.getItemAtPosition(position));

                if (viewId == R.id.delete_but) {
                    alert.setTitle("Confirm");
                    alert.setMessage("Are you sure to delete this pattern?");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteEvent(AppConfig.selPatternInfo.id);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
                else {
                    if (AppConfig.selPatternInfo.isPdf == 1) {
                        Intent intent = new Intent(PatternSearchActivity.this, PatternPdfDetailActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                    } else {
                        Intent intent = new Intent(PatternSearchActivity.this, PatternCapturedSearchDetailActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                    }
                }
            }
        });
        adapter = new PatternSearchAdapter(AppConfig.patternList, getApplicationContext(), session.getUserID());
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEvent(final int id) {

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%d", AppConfig.API_TAG_PATTERNS, id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < AppConfig.patternList.size(); i ++) {
                            PatternInfo info = AppConfig.patternList.get(i);
                            if (info.id == id) {
                                AppConfig.patternList.remove(i);
                                break;
                            }
                        }
                        adapter = new PatternSearchAdapter(AppConfig.patternList, getApplicationContext(), session.getUserID());
                        mListView.setAdapter(adapter);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = result.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                hideDialog();
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
