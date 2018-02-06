package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.premium.patternbox.Adapter.PatternCategoryListAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class FabricDetailActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private SessionManager session;
    private FabricInfo m_fabricInfo;
    private ImageView m_imgPattern;
    private TextView m_lblPatternCategories;
    private ImageView imgBookMark;

    private TextView name_txt, type_txt, color_txt, width_txt, length_txt, weight_txt, stretch_txt, uses_txt, careinstructions_txt, notes_txt;
    private Button delete_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_fabricInfo = AppConfig.selFabricInfo;
        getSupportActionBar().setTitle(m_fabricInfo.name.toUpperCase());

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fabric_detail);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        delete_but = (Button) findViewById(R.id.delete_but);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        delete_but.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                alert.setTitle("Confirm");
                alert.setMessage("Are you sure to delete this fabric?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFabricEvent();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });

        m_imgPattern = (ImageView) findViewById(R.id.imgPattern);
        m_lblPatternCategories = (TextView) findViewById(R.id.lbCategories);
        imgBookMark = (ImageView) findViewById(R.id.imgBookMark);

        name_txt = (TextView) findViewById(R.id.name_txt);
        type_txt = (TextView) findViewById(R.id.type_txt);
        color_txt = (TextView) findViewById(R.id.color_txt);
        width_txt = (TextView) findViewById(R.id.width_txt);
        length_txt = (TextView) findViewById(R.id.length_txt);
        weight_txt = (TextView) findViewById(R.id.weight_txt);
        stretch_txt = (TextView) findViewById(R.id.stretch_txt);
        uses_txt = (TextView) findViewById(R.id.uses_txt);
        careinstructions_txt = (TextView) findViewById(R.id.careinstructions_txt);
        notes_txt = (TextView) findViewById(R.id.notes_txt);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFabric();
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
                Intent intent = new Intent(FabricDetailActivity.this, FabricEditActivity.class);
                intent.putExtra("isEditing", true);
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

    private void loadFabric(){

        String url =  AppConfig.downloadUrl(session.getUserID(),m_fabricInfo.imageUrl);
        int placeHolder = R.drawable.icon_camera;

        Glide.with(this).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(m_imgPattern);

        m_lblPatternCategories.setText("CATEGORIES: "+ m_fabricInfo.getCategories());

        name_txt.setText(m_fabricInfo.name);
        type_txt.setText(m_fabricInfo.type);
        color_txt.setText(m_fabricInfo.color);
        width_txt.setText(m_fabricInfo.width);
        length_txt.setText(m_fabricInfo.length);
        weight_txt.setText(m_fabricInfo.weight);
        stretch_txt.setText(m_fabricInfo.stretch);
        uses_txt.setText(m_fabricInfo.uses);
        careinstructions_txt.setText(m_fabricInfo.careInstructions);
        notes_txt.setText(m_fabricInfo.notes);

        if (m_fabricInfo.isBookmark == 1) {
            imgBookMark.setVisibility(View.VISIBLE);
        }
        else {
            imgBookMark.setVisibility(View.GONE);
        }
    }
    private void saveBookmarkEvent() {

        int isbookmark = 1;
        final int bookmarkicon;

        if (m_fabricInfo.isBookmark == 1) {
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

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.PUT, String.format("%s/%d", AppConfig.API_TAG_FABRIC_BOOKMARK, m_fabricInfo.id), params);
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
                        m_fabricInfo.isBookmark = finalIsbookmark;

                        if (m_fabricInfo.isBookmark == 1) {
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

    private void deleteFabricEvent() {

        pDialog.setMessage("Deleting data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.DELETE, String.format("%s/%d", AppConfig.API_TAG_FABRICS, m_fabricInfo.id), null);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        AppConfig.fabricList.remove(AppConfig.selFabricInfo);
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
