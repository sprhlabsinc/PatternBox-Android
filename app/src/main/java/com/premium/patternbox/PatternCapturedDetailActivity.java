package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;
import com.premium.patternbox.utils.ImageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.premium.patternbox.app.AppConfig.KPatternCategories;
import static com.premium.patternbox.app.AppConfig.KPatternInfo;
import static com.premium.patternbox.app.AppConfig.KPatternKey;
import static com.premium.patternbox.app.AppConfig.KPatternName;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternCapturedDetailActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private SessionManager session;
    private PatternInfo m_patternInfo;
    private ImageView m_imgPattern;
    private TextView m_lblPatternName;
    private TextView m_lblPatternID;
    private TextView m_txtPatternDescription;

    private Button infoedit_but, bookmark_but, edit_but, delete_but;
    private boolean isfront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_patternInfo = AppConfig.selPatternInfo;

        getSupportActionBar().setTitle(m_patternInfo.name.toUpperCase());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_captured_detail);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        infoedit_but = (Button) findViewById(R.id.infoedit_but);
        bookmark_but = (Button) findViewById(R.id.bookmark_but);
        edit_but = (Button) findViewById(R.id.edit_but);
        delete_but = (Button) findViewById(R.id.delete_but);

        m_imgPattern = (ImageView) findViewById(R.id.imgPattern);
        m_lblPatternID = (TextView) findViewById(R.id.lbId);
        m_lblPatternName = (TextView)findViewById(R.id.lbName);
        m_txtPatternDescription = (TextView)findViewById(R.id.txtInfo);

        infoedit_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(PatternCapturedDetailActivity.this, PatternEditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        bookmark_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveBookmarkEvent();
            }
        });
        edit_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(PatternCapturedDetailActivity.this, PatternImageEditActivity.class);
                intent.putExtra("isfront", isfront);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        delete_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                alert.setTitle("Confirm");
                alert.setMessage("Are you sure to delete this pattern?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePatternEvent();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });
        if (m_patternInfo.isBookmark > 0) {
            bookmark_but.setBackgroundResource(R.drawable.icon_bookmark_yes);
        }
        else {
            bookmark_but.setBackgroundResource(R.drawable.icon_bookmark_no);
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPattern(){

        isfront = true;
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
        m_txtPatternDescription.setText(m_patternInfo.info);

        m_imgPattern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isfront = !isfront;
                String url =  AppConfig.downloadUrl(session.getUserID(),m_patternInfo.frontImage);
                if (!isfront) {
                    url =  AppConfig.downloadUrl(session.getUserID(),m_patternInfo.backImage);
                }
                int placeHolder;
                if(m_patternInfo.isPdf == 0){
                    placeHolder = R.drawable.icon_camera;
                }else{
                    placeHolder = R.drawable.icon_pdfdoc;
                }
                Glide.with(getApplicationContext()).load(url)
                        .crossFade()
                        .placeholder(placeHolder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(m_imgPattern);
            }
        });
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

                        bookmark_but.setBackgroundResource(bookmarkicon);
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

    private void deletePatternEvent() {

        pDialog.setMessage("Deleting data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%d", AppConfig.API_TAG_PATTERNS, m_patternInfo.id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        AppConfig.patternList.remove(AppConfig.selPatternInfo);
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
