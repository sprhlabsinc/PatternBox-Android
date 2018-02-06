package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.premium.patternbox.Adapter.FabricMultiAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricCategoryInfo;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.helper.SessionManager;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class FabricAddProjectActivity extends AppCompatActivity {
    private GridView gridView;
    private SessionManager session;

    private EditText search_txt;
    private Button add_project_but;
    private FabricMultiAdapter adapter;
    private int[] nPosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("ADD TO PROJECT");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_multiselect_grid);

        session = new SessionManager(getApplicationContext());

        add_project_but = (Button) findViewById(R.id.add_project_but);
        add_project_but.setVisibility(View.GONE);
        add_project_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppConfig.selFabricIDs = "";
                for (int i = 0; i < nPosList.length; i ++) {
                    if (nPosList[i] == 1) {
                        FabricInfo temp = AppConfig.fabricList.get(i);
                        if (AppConfig.selFabricIDs.equals("")) {
                            AppConfig.selFabricIDs = String.valueOf(temp.id);
                        }
                        else {
                            AppConfig.selFabricIDs = String.format("%s,%s", AppConfig.selFabricIDs, String.valueOf(temp.id));
                        }
                    }
                }
                if (!AppConfig.isClose) {
                    Intent intent = new Intent(FabricAddProjectActivity.this, ProjectSearchActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
                else {
                    AppConfig.isUpdate = true;
                    onBackPressed();
                }
            }
        });

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

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FabricInfo temp =(FabricInfo) (gridView.getItemAtPosition(position));
                if (nPosList[position] == 1) { nPosList[position] = 0; }
                else { nPosList[position] = 1; }

                adapter.setSelectedPosition(nPosList);
                adapter.notifyDataSetChanged();

                add_project_but.setVisibility(View.GONE);
                for (int i = 0; i < nPosList.length; i ++) {
                    if (nPosList[i] > 0) {
                        add_project_but.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });
        nPosList = new int[AppConfig.fabricList.size() + 10];
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!AppConfig.selFabricIDs.equals("")) {
            String[] ids = AppConfig.selFabricIDs.split(",");
            for (int i = 0; i < AppConfig.fabricList.size(); i ++) {
                FabricInfo temp = AppConfig.fabricList.get(i);
                for  (int j = 0; j < ids.length; j ++) {
                    if (temp.id == Integer.parseInt(ids[j])) {
                        add_project_but.setVisibility(View.VISIBLE);
                        nPosList[i] = 1;
                        break;
                    }
                }
            }
        }

        adapter = new FabricMultiAdapter(AppConfig.fabricList, getApplicationContext(), session.getUserID(), nPosList);
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
