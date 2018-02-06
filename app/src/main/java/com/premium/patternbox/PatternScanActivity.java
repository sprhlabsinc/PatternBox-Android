package com.premium.patternbox;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.premium.patternbox.Adapter.PatternCategoryMultiAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.app.PatternInfo;
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
import java.util.List;
import java.util.regex.Pattern;

import vn.tungdx.mediapicker.MediaItem;
import vn.tungdx.mediapicker.MediaOptions;
import vn.tungdx.mediapicker.activities.MediaPickerActivity;

import static com.premium.patternbox.app.AppConfig.REQUEST_MEDIA;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternScanActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;

    private TwoWayView gridView;
    private PatternCategoryMultiAdapter adapter;
    private int[] nPosList;

    private ImageView pattern_img;
    private Button camera_but, gallery_but, rotate_but, back_camera_but, front_camera_but;
    private boolean isfrontCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppConfig.front_img_file = null;
        AppConfig.back_img_file = null;

        getSupportActionBar().setTitle("SCAN PATTERN");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_scan);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        gridView = (TwoWayView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if (position == AppConfig.patternCategoryList.size()) {
                    startActivity(new Intent(PatternScanActivity.this, PatternCategoryAddActivity.class));
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

        pattern_img = (ImageView) findViewById(R.id.pattern_img);
        camera_but = (Button) findViewById(R.id.camera_but);
        gallery_but = (Button) findViewById(R.id.gallery_but);
        rotate_but = (Button) findViewById(R.id.rotate_but);

        back_camera_but = (Button) findViewById(R.id.back_camera_but);
        front_camera_but = (Button) findViewById(R.id.front_camera_but);

        pattern_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isfrontCamera) {
                    if (AppConfig.front_img_file != null) {
                        ImageDialog dialog = new ImageDialog(PatternScanActivity.this, AppConfig.front_img_file);
                        dialog.show();
                    }
                }
                else {
                    if (AppConfig.back_img_file != null) {
                        ImageDialog dialog = new ImageDialog(PatternScanActivity.this, AppConfig.back_img_file);
                        dialog.show();
                    }
                }
            }
        });

        back_camera_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                back_camera_but.setTextColor(Color.parseColor("#00f517"));
                front_camera_but.setTextColor(Color.parseColor("#ffffff"));
                isfrontCamera = false;
                if (AppConfig.back_img_file != null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(AppConfig.back_img_file.getAbsolutePath());
                    pattern_img.setImageBitmap(myBitmap);
                }
                else {
                    pattern_img.setImageResource(R.drawable.pattern_sketch);
                }
            }
        });
        front_camera_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                front_camera_but.setTextColor(Color.parseColor("#00f517"));
                back_camera_but.setTextColor(Color.parseColor("#ffffff"));
                isfrontCamera = true;
                if (AppConfig.front_img_file != null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(AppConfig.front_img_file.getAbsolutePath());
                    pattern_img.setImageBitmap(myBitmap);
                }
                else {
                    pattern_img.setImageResource(R.drawable.pattern_sketch);
                }
            }
        });

        camera_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MediaOptions.Builder builder = new MediaOptions.Builder();
                MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(false)
                        .build();
                MediaPickerActivity.open(PatternScanActivity.this, REQUEST_MEDIA, options);
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
        front_camera_but.callOnClick();

        nPosList = new int[AppConfig.patternCategoryList.size() + 10];
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
                        pattern_img.setImageBitmap(bitmap);
                        if (isfrontCamera) {
                            AppConfig.front_img_file = imageFile;
                        }
                        else {
                            AppConfig.back_img_file = imageFile;
                        }

                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (AppConfig.isSaved) finish();

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
            case R.id.action_next:
                if (AppConfig.front_img_file == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please take a front photo of pattern.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                if (AppConfig.back_img_file == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please take a back photo of pattern.", Toast.LENGTH_LONG)
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
                Intent intent = new Intent(PatternScanActivity.this, PatternNewActivity.class);
                intent.putExtra("categories", categories);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next, menu);
        return true;
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
