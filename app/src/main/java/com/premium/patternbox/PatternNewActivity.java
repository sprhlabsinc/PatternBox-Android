package com.premium.patternbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.premium.patternbox.app.AppConfig.KPatternBackPic;
import static com.premium.patternbox.app.AppConfig.KPatternCategories;
import static com.premium.patternbox.app.AppConfig.KPatternFronPic;
import static com.premium.patternbox.app.AppConfig.KPatternInfo;
import static com.premium.patternbox.app.AppConfig.KPatternIsPDF;
import static com.premium.patternbox.app.AppConfig.KPatternKey;
import static com.premium.patternbox.app.AppConfig.KPatternName;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternNewActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;

    private PatternCategoryMultiAdapter adapter;
    private PatternInfo m_patternInfo;
    private String categories;

    private EditText name_txt, id_txt, info_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppConfig.isSaved = false;
        categories = getIntent().getStringExtra("categories");
        m_patternInfo = new PatternInfo();
        getSupportActionBar().setTitle(R.string.app_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_new);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        name_txt = (EditText) findViewById(R.id.name_txt);
        id_txt = (EditText) findViewById(R.id.id_txt);
        info_txt = (EditText) findViewById(R.id.info_txt);
    }

    @Override
    public void onStart(){
        super.onStart();
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
                if (AppConfig.patternList.size() >= 10) {
                    Toast.makeText(getApplicationContext(),
                            "You can not store patterns more than 10. please purchase PATTERNBOX Premium App.", Toast.LENGTH_LONG)
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

    private void saveInfoEvent(final String key, final String name, final String note, final String categories) {

        Date date = new Date();
        String time = String.valueOf(date.getTime());
        final String frontimgName = String.format("%s_f.jpg", time);
        final String backimgName = String.format("%s_b.jpg", time);

        Map<String, String> params = new HashMap<String, String>();
        params.put(KPatternName, name);
        params.put(KPatternKey, key);
        params.put(KPatternInfo, note);
        params.put(KPatternCategories, categories);
        params.put(KPatternIsPDF, String.valueOf(false));
        params.put(KPatternFronPic, frontimgName);
        params.put(KPatternBackPic, backimgName);

        ArrayList<String> filenameList = new ArrayList<String>();
        filenameList.add(frontimgName);
        filenameList.add(backimgName);

        ArrayList<File> fileList = new ArrayList<File>();
        fileList.add(AppConfig.front_img_file);
        fileList.add(AppConfig.back_img_file);
        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), AppConfig.API_TAG_UPLOAD, params, fileList, filenameList);
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
                        m_patternInfo.key = key;
                        m_patternInfo.info = note;
                        m_patternInfo.name = name;
                        m_patternInfo.isPdf = 0;
                        m_patternInfo.frontImage = frontimgName;
                        m_patternInfo.backImage = backimgName;
                        m_patternInfo.id = result.getInt("pattern_id");

                        AppConfig.addPattern(m_patternInfo);

                        Toast.makeText(getApplicationContext(),
                                "Pattern Saved.", Toast.LENGTH_LONG)
                                .show();
                        AppConfig.isSaved = true;
                        onBackPressed();
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
