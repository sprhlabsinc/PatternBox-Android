package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.premium.patternbox.Adapter.FabricAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricCategoryInfo;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.helper.SessionManager;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class FabricActivity extends AppCompatActivity {
    private GridView gridView;
    private SessionManager session;

    private EditText search_txt;
    private FabricAdapter adapter;
    private int position;
    private boolean isBookmark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getIntent().getIntExtra("position", 0);
        isBookmark = getIntent().getBooleanExtra("bookmark", false);
        if (isBookmark == true) {
            getSupportActionBar().setTitle("BOOKMARKS");
        }
        else {
            FabricCategoryInfo selInfo = AppConfig.fabricCategoryList.get(position);
            getSupportActionBar().setTitle(selInfo.name.toUpperCase());
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern);

        session = new SessionManager(getApplicationContext());

        search_txt = (EditText)findViewById(R.id.search_txt);
        search_txt.setHint("Search by name, type and category");
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

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppConfig.selFabricInfo =(FabricInfo) (gridView.getItemAtPosition(position));
                Intent intent = new Intent(FabricActivity.this, FabricDetailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isBookmark == true) {
            adapter = new FabricAdapter(AppConfig.getFabricsbyBookmark(), getApplicationContext(), session.getUserID());
        }
        else {
            adapter = new FabricAdapter(AppConfig.getFabricsbyCategory(position), getApplicationContext(), session.getUserID());
        }
        gridView.setAdapter(adapter);
        gridView.setTextFilterEnabled(true);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
