package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.ProjectSearchAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.ProjectInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class ProjectSearchActivity extends AppCompatActivity {
    private ListView mListView;
    private SessionManager session;
    private ProgressDialog pDialog;

    private EditText search_txt;
    private ProjectSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PROJECTS");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

        search_txt = (EditText)findViewById(R.id.search_txt);
        search_txt.setHint("Search by name");
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
                final ProjectInfo info = (ProjectInfo) (mListView.getItemAtPosition(position));

                if (viewId == R.id.delete_but) {
                    alert.setTitle("Confirm");
                    alert.setMessage("Are you sure to delete this project?");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteEvent(info.id);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
                else {
                    AppConfig.selProjectInfo = new ProjectInfo();
                    AppConfig.selProjectInfo.id = info.id;
                    AppConfig.selProjectInfo.name = info.name;
                    AppConfig.selProjectInfo.client = info.client;
                    AppConfig.selProjectInfo.notes = info.notes;
                    AppConfig.selProjectInfo.measurements = info.measurements;
                    AppConfig.selProjectInfo.image = info.image;
                    AppConfig.selProjectInfo.pattern = info.pattern;
                    AppConfig.selProjectInfo.fabrics = info.fabrics;
                    AppConfig.selProjectInfo.notions = info.notions;

                    AppConfig.isUpdate = true;
                    switch (AppConfig.isFirstOpen) {
                        case 1:
                            AppConfig.selPatternID = AppConfig.selProjectInfo.pattern;
                            AppConfig.selFabricIDs = AppConfig.selProjectInfo.fabrics;
                            AppConfig.selNotionIDs = AppConfig.selProjectInfo.notions;
                            break;
                        case 2:
                            AppConfig.selFabricIDs = AppConfig.selProjectInfo.fabrics;
                            AppConfig.selNotionIDs = AppConfig.selProjectInfo.notions;
                            break;
                        case 3:
                            AppConfig.selPatternID = AppConfig.selProjectInfo.pattern;
                            AppConfig.selNotionIDs = AppConfig.selProjectInfo.notions;
                            break;
                        case 4:
                            AppConfig.selPatternID = AppConfig.selProjectInfo.pattern;
                            AppConfig.selFabricIDs = AppConfig.selProjectInfo.fabrics;
                            if (AppConfig.selNotionIDsFrom.equals(""))
                                AppConfig.selNotionIDs = AppConfig.selProjectInfo.notions;
                            else {
                                if (AppConfig.selProjectInfo.notions.equals(""))
                                    AppConfig.selNotionIDs = AppConfig.selNotionIDsFrom;
                                else
                                    AppConfig.selNotionIDs = AppConfig.selProjectInfo.notions + ":" + AppConfig.selNotionIDsFrom;
                            }
                            break;
                    }
                    Intent intent = new Intent(ProjectSearchActivity.this, ProjectEditActivity.class);
                    intent.putExtra("isEditing", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });
        adapter = new ProjectSearchAdapter(AppConfig.projectList, getApplicationContext(), session.getUserID());
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
        AppConfig.isClose = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                AppConfig.selProjectInfo = new ProjectInfo();
                AppConfig.isUpdate = false;
                AppConfig.selPatternID = 0;
                AppConfig.selFabricIDs = "";
                AppConfig.selNotionIDs = "";
                Intent intent = new Intent(ProjectSearchActivity.this, ProjectEditActivity.class);
                intent.putExtra("isEditing", false);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plus, menu);
        return true;
    }

    private void deleteEvent(final int id) {

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%d", AppConfig.API_TAG_PROJECTS, id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < AppConfig.projectList.size(); i ++) {
                            ProjectInfo info = AppConfig.projectList.get(i);
                            if (info.id == id) {
                                AppConfig.projectList.remove(i);
                                break;
                            }
                        }
                        adapter = new ProjectSearchAdapter(AppConfig.projectList, getApplicationContext(), session.getUserID());
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
