package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.premium.patternbox.app.AppConfig;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternSelectActivity extends AppCompatActivity {

    private LinearLayout pdf_pattern_layout, captured_pattern_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PATTERNS");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_select);

        pdf_pattern_layout = (LinearLayout) findViewById(R.id.pdf_pattern_layout);
        captured_pattern_layout = (LinearLayout) findViewById(R.id.captured_pattern_layout);

        pdf_pattern_layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AppConfig.isPdf = 1;
                startActivity(new Intent(PatternSelectActivity.this, PatternCategoriesActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        captured_pattern_layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AppConfig.isPdf = 0;
                startActivity(new Intent(PatternSelectActivity.this, PatternCategoriesActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
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
