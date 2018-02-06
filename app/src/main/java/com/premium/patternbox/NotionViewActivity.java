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
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.NotionListAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.NotionEditInfo;
import com.premium.patternbox.app.NotionInfo;
import com.premium.patternbox.helper.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class NotionViewActivity extends AppCompatActivity {
    private ListView mListView;

    private NotionListAdapter adapter;
    private Boolean isLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("CHOOSE NOTIONS");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_notion_edit);

        isLock = getIntent().getBooleanExtra("isLock", false);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();
                NotionEditInfo notionEditInfo = (NotionEditInfo) (mListView.getItemAtPosition(position));

                if (viewId == R.id.add_but) {
                    for (int i = 0; i < AppConfig.notionList.size(); i++) {
                        NotionInfo notionInfo = AppConfig.notionList.get(i);
                        notionInfo.count = 0;
                    }
                    Intent intent = new Intent(NotionViewActivity.this, NotionSearchActivity.class);
                    intent.putExtra("category", notionEditInfo.id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });
        adapter = new NotionListAdapter(AppConfig.notionEditInfoList, getApplicationContext(), isLock);
        mListView.setAdapter(adapter);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_done:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }
}
