package com.premium.patternbox;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.Adapter.FabricCategoryMultiAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.AppController;
import com.premium.patternbox.app.FabricCategoryInfo;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;
import com.premium.patternbox.utils.ImageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.tungdx.mediapicker.MediaItem;
import vn.tungdx.mediapicker.MediaOptions;
import vn.tungdx.mediapicker.activities.MediaPickerActivity;

import static com.premium.patternbox.app.AppConfig.API_TAG_FABRICS_UPLOAD;
import static com.premium.patternbox.app.AppConfig.KFabricCareInstructions;
import static com.premium.patternbox.app.AppConfig.KFabricCategories;
import static com.premium.patternbox.app.AppConfig.KFabricColor;
import static com.premium.patternbox.app.AppConfig.KFabricId;
import static com.premium.patternbox.app.AppConfig.KFabricImageName;
import static com.premium.patternbox.app.AppConfig.KFabricLength;
import static com.premium.patternbox.app.AppConfig.KFabricName;
import static com.premium.patternbox.app.AppConfig.KFabricNotes;
import static com.premium.patternbox.app.AppConfig.KFabricStretch;
import static com.premium.patternbox.app.AppConfig.KFabricType;
import static com.premium.patternbox.app.AppConfig.KFabricUses;
import static com.premium.patternbox.app.AppConfig.KFabricWeight;
import static com.premium.patternbox.app.AppConfig.KFabricWidth;
import static com.premium.patternbox.app.AppConfig.REQUEST_MEDIA;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class FabricEditActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;

    private TwoWayView gridView;
    private FabricCategoryMultiAdapter adapter;
    private int[] nPosList;

    private FabricInfo m_fabricInfo;
    private ImageView m_imgPattern;
    private ImageView imgBookMark;

    private EditText name_txt, type_txt, color_txt, width_txt, length_txt, weight_txt, stretch_txt, uses_txt, careinstructions_txt, notes_txt;
    private Button camera_but, gallery_but, rotate_but;

    private boolean isEditing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppConfig.front_img_file = null;

        isEditing = getIntent().getBooleanExtra("isEditing", false);

        if (isEditing) {
            m_fabricInfo = AppConfig.selFabricInfo;
            getSupportActionBar().setTitle("PATTERN BOX");
        }
        else {
            m_fabricInfo = new FabricInfo();
            getSupportActionBar().setTitle("SCAN FABRIC");
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fabric_edit);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        gridView = (TwoWayView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if (position == AppConfig.fabricCategoryList.size()) {
                    startActivity(new Intent(FabricEditActivity.this, FabricCategoryAddActivity.class));
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

        camera_but = (Button) findViewById(R.id.camera_but);
        gallery_but = (Button) findViewById(R.id.gallery_but);
        rotate_but = (Button) findViewById(R.id.rotate_but);

        camera_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MediaOptions.Builder builder = new MediaOptions.Builder();
                MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(false)
                        .build();
                MediaPickerActivity.open(FabricEditActivity.this, REQUEST_MEDIA, options);
            }
        });
        gallery_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        rotate_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        gallery_but.setVisibility(View.GONE);
        rotate_but.setVisibility(View.GONE);

        m_imgPattern = (ImageView) findViewById(R.id.imgPattern);
        m_imgPattern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (AppConfig.front_img_file != null) {
                    ImageDialog dialog = new ImageDialog(FabricEditActivity.this, AppConfig.front_img_file);
                    dialog.show();
                }
            }
        });

        imgBookMark = (ImageView) findViewById(R.id.imgBookMark);

        name_txt = (EditText) findViewById(R.id.name_txt);
        type_txt = (EditText) findViewById(R.id.type_txt);
        color_txt = (EditText) findViewById(R.id.color_txt);
        width_txt = (EditText) findViewById(R.id.width_txt);
        length_txt = (EditText) findViewById(R.id.length_txt);
        weight_txt = (EditText) findViewById(R.id.weight_txt);
        stretch_txt = (EditText) findViewById(R.id.stretch_txt);
        uses_txt = (EditText) findViewById(R.id.uses_txt);
        careinstructions_txt = (EditText) findViewById(R.id.careinstructions_txt);
        notes_txt = (EditText) findViewById(R.id.notes_txt);

        nPosList = new int[AppConfig.fabricCategoryList.size() + 10];
        if (isEditing) {
            String url = AppConfig.downloadUrl(session.getUserID(), m_fabricInfo.imageUrl);
            int placeHolder = R.drawable.icon_camera;

            Glide.with(this).load(url)
                    .crossFade()
                    .placeholder(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(m_imgPattern);

            for (int i = 0; i < AppConfig.fabricCategoryList.size(); i ++) {
                FabricCategoryInfo selInfo = AppConfig.fabricCategoryList.get(i);
                String[] separated = m_fabricInfo.categories.split(",");
                for (int j = 0; j < separated.length; j++) {
                    if (separated[j].equals(selInfo.id)) {
                        nPosList[i] = 1;
                        break;
                    }
                }
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA) {
            if (resultCode == RESULT_OK) {
                List<MediaItem> selectedList = MediaPickerActivity
                        .getMediaItemSelected(data);
                if (selectedList != null) {
                    for (MediaItem mediaItem : selectedList) {
                        File imageFile = new File(mediaItem.getPathCropped(this));
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();
                        int limit = 500;
                        if (width < height && width > limit) {
                            bitmap = Bitmap.createScaledBitmap(bitmap, limit, limit * height / width, true);
                        }
                        else if (height > limit) {
                            bitmap = Bitmap.createScaledBitmap(bitmap, limit * width / height, limit, true);
                        }
                        m_imgPattern.setImageBitmap(bitmap);
                        AppConfig.front_img_file = imageFile;

                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        adapter = new FabricCategoryMultiAdapter(AppConfig.fabricCategoryList, this, nPosList);
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
                if (!isEditing && AppConfig.front_img_file == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please take a photo of fabric.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                int sum = 0;
                String categories = null;
                for (int i = 0; i < nPosList.length; i ++) {
                    if (nPosList[i] == 1) {
                        FabricCategoryInfo categoryInfo = AppConfig.fabricCategoryList.get(i);
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
                String name = name_txt.getText().toString().trim();
                String type = type_txt.getText().toString().trim();
                String color = color_txt.getText().toString().trim();
                String width = width_txt.getText().toString().trim();
                String length = length_txt.getText().toString().trim();
                String weight = weight_txt.getText().toString().trim();
                String stretch = stretch_txt.getText().toString().trim();
                String uses = uses_txt.getText().toString().trim();
                String careinstructions = careinstructions_txt.getText().toString();
                String notes = notes_txt.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter fabric name.", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (type.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter fabric type.", Toast.LENGTH_LONG).show();
                    return true;
                }
                saveInfoEvent(categories, name, type, color, width, length, weight, stretch, uses, careinstructions, notes);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    private void saveInfoEvent(final String categories, final String name, final String type, final String color, final String width, final String length, final String weight, final String stretch,
                               final String uses, final String careinstructions, final String notes) {

        Date date = new Date();
        String time = String.valueOf(date.getTime());
        final String imgName = String.format("%s_fabric.jpg", time);
        Map<String, String> params = new HashMap<String, String>();
        params.put(KFabricName, name);
        params.put(KFabricType, type);
        params.put(KFabricColor, color);
        params.put(KFabricWidth, width);
        params.put(KFabricLength, length);
        params.put(KFabricWeight, weight);
        params.put(KFabricStretch, stretch);
        params.put(KFabricUses, uses);
        params.put(KFabricCareInstructions, careinstructions);
        params.put(KFabricNotes, notes);
        params.put(KFabricCategories, categories);

        if (isEditing) {
            //params.put(KFabricImageName, m_fabricInfo.imageUrl);
            params.put("is_update", "1");
            params.put(KFabricId, String.valueOf(m_fabricInfo.id));
        }

        ArrayList<String> filenameList = new ArrayList<String>();
        if (AppConfig.front_img_file != null) {
            //if (isEditing)
              //  filenameList.add(m_fabricInfo.imageUrl);
            //else
                filenameList.add(imgName);
            params.put(KFabricImageName, imgName);
        }

        ArrayList<File> fileList = new ArrayList<File>();
        if (AppConfig.front_img_file != null)
            fileList.add(AppConfig.front_img_file);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), AppConfig.API_TAG_FABRICS_UPLOAD, params, fileList, filenameList);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        if (isEditing) {
                            m_fabricInfo.categories = categories;
                            m_fabricInfo.name = name;
                            m_fabricInfo.type = type;
                            m_fabricInfo.color = color;
                            m_fabricInfo.width = width;
                            m_fabricInfo.length = length;
                            m_fabricInfo.weight = weight;
                            if (AppConfig.front_img_file != null)
                                m_fabricInfo.imageUrl = imgName;
                            m_fabricInfo.stretch = stretch;
                            m_fabricInfo.uses = uses;
                            m_fabricInfo.careInstructions = careinstructions;
                            m_fabricInfo.notes = notes;
                        }
                        else {
                            int fabric_id = result.getInt("fabric_id");
                            FabricInfo newItem = new FabricInfo();
                            newItem.id = fabric_id;
                            newItem.imageUrl = imgName;
                            newItem.isBookmark = 0;
                            newItem.categories = categories;

                            newItem.name = name;
                            newItem.type = type;
                            newItem.color = color;
                            newItem.width = width;
                            newItem.length = length;
                            newItem.weight = weight;
                            newItem.stretch = stretch;
                            newItem.uses = uses;
                            newItem.careInstructions = careinstructions;
                            newItem.notes = notes;
                            AppConfig.addFabric(newItem);
                        }
                        Toast.makeText(getApplicationContext(), "Fabric Saved.", Toast.LENGTH_LONG).show();
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
