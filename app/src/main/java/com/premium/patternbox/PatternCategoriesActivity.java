package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.premium.patternbox.Adapter.PatternCategoryAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternCategoriesActivity extends AppCompatActivity {

    private GridView gridView;
    private PatternCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PATTERN CATEGORIES");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_categories);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if (position == AppConfig.patternCategoryList.size()) {
                    startActivity(new Intent(PatternCategoriesActivity.this, PatternCategoryAddActivity.class));
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
                else {
                    adapter.setSelectedPosition(position);
                    adapter.notifyDataSetChanged();

                    Intent intent = new Intent(PatternCategoriesActivity.this, PatternActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        adapter = new PatternCategoryAdapter(AppConfig.patternCategoryList, this);
        gridView.setAdapter(adapter);
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
            case R.id.action_edit:
                startActivity(new Intent(PatternCategoriesActivity.this, PatternCategoryAddActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

}
