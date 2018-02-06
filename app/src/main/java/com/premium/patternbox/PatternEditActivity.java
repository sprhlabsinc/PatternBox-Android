package com.premium.patternbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.PatternCategoryMultiAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.premium.patternbox.app.AppConfig.KPatternCategories;
import static com.premium.patternbox.app.AppConfig.KPatternInfo;
import static com.premium.patternbox.app.AppConfig.KPatternKey;
import static com.premium.patternbox.app.AppConfig.KPatternName;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternEditActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;

    private TwoWayView gridView;
    private PatternCategoryMultiAdapter adapter;
    private PatternInfo m_patternInfo;
    private int[] nPosList;

    private EditText name_txt, id_txt, info_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_patternInfo = AppConfig.selPatternInfo;
        getSupportActionBar().setTitle("PATTERN BOX");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_edit);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        gridView = (TwoWayView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if (position == AppConfig.patternCategoryList.size()) {
                    startActivity(new Intent(PatternEditActivity.this, PatternCategoryAddActivity.class));
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
                else {
                    if (nPosList[position] == 1) { nPosList[position] = 0; }
                    else { nPosList[position] = 1; }
                    adapter.setSelectedPosition(nPosList);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        name_txt = (EditText) findViewById(R.id.name_txt);
        id_txt = (EditText) findViewById(R.id.id_txt);
        info_txt = (EditText) findViewById(R.id.info_txt);
        name_txt.setText(m_patternInfo.name);
        id_txt.setText(m_patternInfo.key);
        info_txt.setText(m_patternInfo.info);

        //////////////////////////////////////////////////////////////////
        nPosList = new int[AppConfig.patternCategoryList.size() + 10];
        for (int i = 0; i < AppConfig.patternCategoryList.size(); i ++) {
            PatternCategoryInfo selInfo = AppConfig.patternCategoryList.get(i);
            String[] separated = m_patternInfo.categories.split(",");
            for (int j = 0; j < separated.length; j++) {
                if (separated[j].equals(selInfo.id)) {
                    nPosList[i] = 1;
                    break;
                }
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        adapter = new PatternCategoryMultiAdapter(AppConfig.patternCategoryList, this, nPosList);
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
            case R.id.action_save:
                String name = name_txt.getText().toString().trim();
                String key = id_txt.getText().toString().trim();
                String info = info_txt.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter pattern name.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                if (key.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter pattern id.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                int sum = 0;
                String categories = null;
                for (int i = 0; i < nPosList.length; i ++) {
                    if (nPosList[i] == 1) {
                        PatternCategoryInfo categoryInfo = AppConfig.patternCategoryList.get(i);
                        if (categories == null) {
                            categories = String.format("%s",categoryInfo.id);
                        }
                        else {
                            categories = String.format("%s,%s", categories, categoryInfo.id);
                        }
                    }
                    sum += nPosList[i];
                }
                if (sum == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select a category at least.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                saveInfoEvent(key, name, info, categories);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    private void saveInfoEvent(final String id, final String name, final String note, final String categories) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(KPatternName, name);
        params.put(KPatternKey, id);
        params.put(KPatternInfo, note);
        params.put(KPatternCategories, categories);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.PUT, String.format("%s/%d", AppConfig.API_TAG_PATTERNS, m_patternInfo.id), params);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        m_patternInfo.categories = categories;
                        m_patternInfo.key = id;
                        m_patternInfo.info = note;
                        m_patternInfo.name = name;

                        Toast.makeText(getApplicationContext(),
                                "Pattern Saved.", Toast.LENGTH_LONG)
                                .show();

                        finish();
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
