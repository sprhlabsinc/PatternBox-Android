package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.premium.patternbox.Adapter.PatternCategoryListAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternCategoryAddActivity extends AppCompatActivity {

    private ListView listView;

    private ProgressDialog pDialog;
    private SessionManager session;

    private PatternCategoryListAdapter adapter;
    private static PatternCategoryAddActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PATTERN CATEGORIES");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_additem);

        mActivity = this;

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        listView = (ListView) findViewById(R.id.listView);
        adapter = new PatternCategoryListAdapter(AppConfig.getCustomizedPatternCategoryList(), getApplicationContext(), mActivity);
        listView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                showEditDialog("", "", true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plus, menu);
        return true;
    }

    public void showDeleteDialg(final String id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm");
        alert.setMessage("Are you sure to delete this category?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deletePatternCategoryEvent(id);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    public void showEditDialog(String name, final String id, final boolean status) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        if (status) {
            alert.setTitle("New Category");
            alert.setMessage("Type new category name");
        }
        else {
            alert.setTitle("Edit Category");
            alert.setMessage("Type category name");
        }
        input.setText(name);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                editPatternCategoryEvent(id, result, status);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void editPatternCategoryEvent(final String id, final String name, final boolean status) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("category", name);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager;
        if (status) {
            networkManager = new NetworkManager(getApplicationContext(), Request.Method.POST, AppConfig.API_TAG_CATEGORIES, params);
        }
        else {
             networkManager = new NetworkManager(getApplicationContext(), Request.Method.PUT, String.format("%s/%s", AppConfig.API_TAG_CATEGORIES, id), params);
        }
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        if (!status) {
                            for (int i = 0; i < AppConfig.patternCategoryList.size(); i ++) {
                                PatternCategoryInfo info = AppConfig.patternCategoryList.get(i);
                                if (info.id.equals(id)) {
                                    info.name = name;
                                    break;
                                }
                            }
                            //((SwipeLayout)(listView.getChildAt(listView.getFirstVisiblePosition()))).open(false);
                        }
                        else {
                            String category_id = result.getString("category_id");
                            PatternCategoryInfo info = new PatternCategoryInfo(category_id, name, -1, false);
                            AppConfig.addPatternCategory(info);
                        }
                        adapter = new PatternCategoryListAdapter(AppConfig.getCustomizedPatternCategoryList(), getApplicationContext(), mActivity);
                        listView.setAdapter(adapter);
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

    private void deletePatternCategoryEvent(final String id) {

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%s", AppConfig.API_TAG_CATEGORIES, id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < AppConfig.patternCategoryList.size(); i ++) {
                            PatternCategoryInfo info = AppConfig.patternCategoryList.get(i);
                            if (info.id.equals(id)) {
                                AppConfig.patternCategoryList.remove(i);
                                break;
                            }
                        }

                        adapter = new PatternCategoryListAdapter(AppConfig.getCustomizedPatternCategoryList(), getApplicationContext(), mActivity);
                        listView.setAdapter(adapter);
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
