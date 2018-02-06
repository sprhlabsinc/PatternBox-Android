package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.premium.patternbox.app.AppConfig;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class FabricMainActivity extends AppCompatActivity {

    private Button scan_fabric_but, my_fabric_but, add_project_but, bookmark_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("FABRIC");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fabric_main);

        scan_fabric_but = (Button) findViewById(R.id.scan_fabric_but);
        my_fabric_but = (Button) findViewById(R.id.my_fabric_but);
        add_project_but = (Button) findViewById(R.id.add_project_but);
        bookmark_but = (Button) findViewById(R.id.bookmark_but);

        scan_fabric_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(FabricMainActivity.this, FabricEditActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        my_fabric_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(FabricMainActivity.this, FabricCategoriesActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        add_project_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(FabricMainActivity.this, FabricAddProjectActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        bookmark_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(FabricMainActivity.this, FabricActivity.class);
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
                startActivity(new Intent(FabricMainActivity.this, FabricSearchActivity.class));
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
