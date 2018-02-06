package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternMainActivity extends AppCompatActivity {

    private Button scan_pattern_but, my_pattern_but, buy_pattern_but, add_project_but, bookmark_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("PATTERN");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_main);

        scan_pattern_but = (Button) findViewById(R.id.scan_pattern_but);
        my_pattern_but = (Button) findViewById(R.id.my_pattern_but);
        buy_pattern_but = (Button) findViewById(R.id.buy_pattern_but);
        add_project_but = (Button) findViewById(R.id.add_project_but);
        bookmark_but = (Button) findViewById(R.id.bookmark_but);

        scan_pattern_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppConfig.isSaved = false;
                startActivity(new Intent(PatternMainActivity.this, PatternScanActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        my_pattern_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(PatternMainActivity.this, PatternSelectActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        buy_pattern_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppConfig.isSaved = false;
                startActivity(new Intent(PatternMainActivity.this, PatternBuyActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        add_project_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(PatternMainActivity.this, PatternAddProjectActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        bookmark_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(PatternMainActivity.this, PatternActivity.class);
                intent.putExtra("bookmark", true);
                startActivity(intent);
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
            case R.id.action_search:
                startActivity(new Intent(PatternMainActivity.this, PatternSearchActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
