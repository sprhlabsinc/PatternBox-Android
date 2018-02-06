package com.premium.patternbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.Adapter.PatternPdfListAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternPdfDetailActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private SessionManager session;
    private PatternInfo m_patternInfo;
    private ImageView m_imgPattern;
    private TextView m_lblPatternName;
    private TextView m_lblPatternID;
    private TextView m_lblPatternCategories;
    private TextView m_txtPatternDescription;
    private ImageView imgBookMark;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_patternInfo = AppConfig.selPatternInfo;
        getSupportActionBar().setTitle(m_patternInfo.name.toUpperCase());

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_pdf_detail);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        loadPattern();
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
                Intent intent = new Intent(PatternPdfDetailActivity.this, PatternEditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
            case R.id.action_bookmark:
                saveBookmarkEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark, menu);
        return true;
    }

    private void loadPattern(){
        m_imgPattern = (ImageView) findViewById(R.id.imgPattern);
        m_lblPatternID = (TextView) findViewById(R.id.lbId);
        m_lblPatternName = (TextView)findViewById(R.id.lbName);
        m_lblPatternCategories = (TextView) findViewById(R.id.lbCategories);
        m_txtPatternDescription = (TextView)findViewById(R.id.txtInfo);
        imgBookMark = (ImageView) findViewById(R.id.imgBookMark);
        listView = (ListView) findViewById(R.id.listView);

        PatternPdfListAdapter adapter = new PatternPdfListAdapter(m_patternInfo.getUrls(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)(listView.getItemAtPosition(position));

                Intent intent = new Intent(PatternPdfDetailActivity.this, PatternPdfViewActivity.class);
                intent.putExtra("url", item);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });

        String url =  AppConfig.downloadUrl(session.getUserID(),m_patternInfo.frontImage);
        int placeHolder;
        if(m_patternInfo.isPdf == 0){
            placeHolder = R.drawable.icon_camera;
        }else{
            placeHolder = R.drawable.icon_pdfdoc;
        }
        Glide.with(this).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(m_imgPattern);
        m_lblPatternName.setText("NAME:   " + m_patternInfo.name);
        m_lblPatternID.setText("ID:   " + String.valueOf(m_patternInfo.key));
        m_lblPatternCategories.setText("CATEGORIES:   "+m_patternInfo.getCategories());
        m_txtPatternDescription.setText(m_patternInfo.info);
        if (m_patternInfo.isBookmark == 1) {
            imgBookMark.setVisibility(View.VISIBLE);
        }
        else {
            imgBookMark.setVisibility(View.GONE);
        }
    }
    private void saveBookmarkEvent() {

        int isbookmark = 1;
        final int bookmarkicon;

        if (m_patternInfo.isBookmark == 1) {
            isbookmark = 0;
            bookmarkicon = R.drawable.icon_bookmark_no;
        }
        else {
            isbookmark = 1;
            bookmarkicon = R.drawable.icon_bookmark_yes;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("is_bookmark", String.valueOf(isbookmark));

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.PUT, String.format("%s/%d", AppConfig.API_TAG_BOOKMARK, m_patternInfo.id), params);
        final int finalIsbookmark = isbookmark;
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        m_patternInfo.isBookmark = finalIsbookmark;

                        if (m_patternInfo.isBookmark == 1) {
                            imgBookMark.setVisibility(View.VISIBLE);
                        }
                        else {
                            imgBookMark.setVisibility(View.GONE);
                        }
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
