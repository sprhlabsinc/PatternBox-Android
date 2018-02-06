package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.NotionSearchAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.NotionEditInfo;
import com.premium.patternbox.app.NotionInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;
import com.premium.patternbox.utils.NotionEditDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.premium.patternbox.R.string.name;
import static com.premium.patternbox.app.AppConfig.KNotionCategoryId;
import static com.premium.patternbox.app.AppConfig.KNotionColor;
import static com.premium.patternbox.app.AppConfig.KNotionSize;
import static com.premium.patternbox.app.AppConfig.KNotionType;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class NotionSearchActivity extends AppCompatActivity {
    private ListView mListView;
    private SessionManager session;

    private EditText search_txt;
    private Button add_project_but;
    private NotionSearchAdapter adapter;
    private String category;
    private static NotionSearchActivity mActivity;
    private ArrayList<NotionInfo> mNotionInfos;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("NOTIONS");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_notion_search);
        category = getIntent().getStringExtra("category");
        mNotionInfos = AppConfig.getNotionsbyCategory(category);
        mActivity = this;
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

        add_project_but = (Button) findViewById(R.id.add_project_but);
        add_project_but.setVisibility(View.GONE);
        add_project_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (AppConfig.isClose) {

                    for (int k = 0; k < AppConfig.notionEditInfoList.size(); k ++) {
                        NotionEditInfo notionEditInfo = AppConfig.notionEditInfoList.get(k);
                        if (notionEditInfo.id.equals(category)) {

                            for (int i = 0; i < mNotionInfos.size(); i ++) {
                                NotionInfo info = mNotionInfos.get(i);
                                if (info.count > 0) {
                                    NotionInfo newItem = new NotionInfo();
                                    newItem.id = info.id;
                                    newItem.category = info.category;
                                    newItem.type = info.type;
                                    newItem.color = info.color;
                                    newItem.size = info.size;
                                    newItem.count = info.count;
                                    notionEditInfo.notionInfos.add(newItem);
                                }
                            }
                            break;
                        }
                    }
                    onBackPressed();
                }
                else if (AppConfig.isFirstOpen == 4) {
                    String notion = "";
                    for (int i = 0; i < mNotionInfos.size(); i ++) {
                        NotionInfo info = mNotionInfos.get(i);
                        if (info.count > 0) {
                            notion += String.format("%d,%d:", info.id, info.count);
                        }
                    }
                    if (!notion.equals("")) {
                        AppConfig.selNotionIDsFrom = notion.substring(0, notion.length() - 1);
                    }

                    Intent intent = new Intent(NotionSearchActivity.this, ProjectSearchActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });
        search_txt = (EditText)findViewById(R.id.search_txt);
        search_txt.setHint("Search by type or color");
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

        mListView = (ListView)findViewById(R.id.listviewPattern);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {

                final NotionInfo info =(NotionInfo) (mListView.getItemAtPosition(position));
                final EditText input = new EditText(mActivity);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setTitle("How many?");
                alert.setMessage("");
                input.setText(String.valueOf(info.count));
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String result = input.getText().toString();
                        info.count = Integer.parseInt(result) ;
                        adapter.notifyDataSetChanged();

                        add_project_but.setVisibility(View.GONE);
                        for (int i = 0; i < mNotionInfos.size(); i ++) {
                            NotionInfo info = mNotionInfos.get(i);
                            if (info.count > 0) {
                                add_project_but.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();

            }
        });
        adapter = new NotionSearchAdapter(mNotionInfos, getApplicationContext(), mActivity);
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
            case R.id.action_add:
                showEditDialog(null, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plus, menu);
        return true;
    }

    public void showDeleteDialg(final NotionInfo info) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm");
        alert.setMessage("Are you sure to delete this notion?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteNotionEvent(info.id);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    public void showEditDialog(NotionInfo info, final boolean status) {

        NotionEditDialog dialog = new NotionEditDialog(this, info, mActivity, category, status);
        dialog.show();
    }

    public void editNotionEvent(final NotionInfo info, final boolean status) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(KNotionCategoryId, info.category);
        params.put(KNotionType, info.type);
        params.put(KNotionColor, info.color);
        params.put(KNotionSize, info.size);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager;
        if (status) {
            networkManager = new NetworkManager(getApplicationContext(), Request.Method.POST, AppConfig.API_TAG_NOTION, params);
        }
        else {
            networkManager = new NetworkManager(getApplicationContext(), Request.Method.PUT, String.format("%s/%d", AppConfig.API_TAG_NOTION, info.id), params);
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
                            for (int i = 0; i < AppConfig.notionList.size(); i ++) {
                                NotionInfo temp = AppConfig.notionList.get(i);
                                if (temp.id == info.id) {
                                    temp.type = info.type;
                                    temp.size = info.size;
                                    temp.color = info.color;
                                    break;
                                }
                            }
                            //((SwipeLayout)(listView.getChildAt(listView.getFirstVisiblePosition()))).open(false);
                        }
                        else {
                            int notion_id = result.getInt("notion_id");
                            NotionInfo temp = new NotionInfo();
                            temp.id = notion_id;
                            temp.type = info.type;
                            temp.size = info.size;
                            temp.color = info.color;
                            temp.category = category;
                            AppConfig.addNotion(temp);
                        }
                        mNotionInfos = AppConfig.getNotionsbyCategory(category);
                        add_project_but.setVisibility(View.GONE);
                        for (int i = 0; i < mNotionInfos.size(); i ++) {
                            NotionInfo info = mNotionInfos.get(i);
                            if (info.count > 0) {
                                add_project_but.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                        adapter = new NotionSearchAdapter(mNotionInfos, getApplicationContext(), mActivity);
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

    private void deleteNotionEvent(final int id) {

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%d", AppConfig.API_TAG_NOTION, id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < AppConfig.notionList.size(); i ++) {
                            NotionInfo info = AppConfig.notionList.get(i);
                            if (info.id == id) {
                                AppConfig.notionList.remove(i);
                                break;
                            }
                        }
                        mNotionInfos = AppConfig.getNotionsbyCategory(category);
                        add_project_but.setVisibility(View.GONE);
                        for (int i = 0; i < mNotionInfos.size(); i ++) {
                            NotionInfo info = mNotionInfos.get(i);
                            if (info.count > 0) {
                                add_project_but.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                        adapter = new NotionSearchAdapter(mNotionInfos, getApplicationContext(), mActivity);
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
