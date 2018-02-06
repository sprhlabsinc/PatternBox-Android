package com.premium.patternbox;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agsw.FabricView.FabricView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.app.ProjectInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.premium.patternbox.app.AppConfig.KPatternBackPic;
import static com.premium.patternbox.app.AppConfig.KPatternFronPic;
import static com.premium.patternbox.app.AppConfig.KPatternId;


/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternImageEditActivity extends AppCompatActivity implements View.OnClickListener {

    private FabricView faricView;
    private Button pen_but, erase_but, zoom_but;
    private RelativeLayout main_layout;
    private PatternInfo mPatternInfo;
    private ImageView pattern_img, temp_img;
    private boolean isfront = false;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPatternInfo = AppConfig.selPatternInfo;
        AppConfig.front_img_file = null;

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_image_edit);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        isfront = getIntent().getBooleanExtra("isfront", false);
        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.segmented2);
        segmented2.setTintColor(Color.parseColor("#FFba3431"), Color.parseColor("#ffffffff"));
        pen_but = (Button) findViewById(R.id.pen_but);
        erase_but = (Button) findViewById(R.id.erase_but);
        zoom_but = (Button) findViewById(R.id.zoom_but);
        pattern_img = (ImageView) findViewById(R.id.pattern_img);
        temp_img = (ImageView) findViewById(R.id.temp_img);
        main_layout = (RelativeLayout) findViewById(R.id.main_layout);

        faricView = (FabricView) findViewById(R.id.faricView);
        faricView.setBackgroundColor(Color.TRANSPARENT);
        faricView.setColor(Color.YELLOW);

        pen_but.setOnClickListener(this);
        erase_but.setOnClickListener(this);
        zoom_but.setOnClickListener(this);

        String url =  AppConfig.downloadUrl(session.getUserID(),mPatternInfo.frontImage);
        if (!isfront)
            url =  AppConfig.downloadUrl(session.getUserID(),mPatternInfo.backImage);
        int placeHolder = R.drawable.icon_camera;
        Glide.with(this).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(pattern_img);
        pen_but.callOnClick();
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
            case R.id.action_take:

                if (ContextCompat.checkSelfPermission(PatternImageEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PatternImageEditActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else {
                    EasyImage.openGallery(PatternImageEditActivity.this, 0);
                }
                return true;
            case R.id.action_done:
                onSaveImage(main_layout);
                saveInfoEvent(AppConfig.selPatternInfo.id);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.take_done, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(PatternImageEditActivity.this, 0);
            }
        }
        else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(PatternImageEditActivity.this, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                AppConfig.front_img_file = imageFile;
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                pattern_img.setImageBitmap(myBitmap);
                faricView.cleanPage();
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(PatternImageEditActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == pen_but) {
            faricView.setColor(Color.YELLOW);
        }
        else if (v == erase_but) {
            faricView.cleanPage();
            faricView.setColor(Color.TRANSPARENT);
        }
        else if (v == zoom_but) {

        }
    }

    private void onSaveImage(View view) {
        try {
            String path = Environment.getExternalStorageDirectory()
                    .toString();
            OutputStream fOut = null;
            AppConfig.front_img_file = new File(path, "/bbb.jpg");
            fOut = new FileOutputStream(AppConfig.front_img_file);

            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
            fOut.flush();
            fOut.close();
            bitmap.recycle();
            //temp_img.setImageBitmap(bitmap);
        } catch (Exception e) {}
    }

    private void saveInfoEvent(int id) {

        Date date = new Date();
        String time = String.valueOf(date.getTime());
        String imgName = String.format("%s_f.jpg", time);
        if (!isfront)
            imgName = String.format("%s_b.jpg", time);
        Map<String, String> params = new HashMap<String, String>();
        params.put(KPatternId, String.valueOf(id));
        if (isfront)
            params.put("is_front", "1");
        else
            params.put("is_front", "0");

        params.put("image", imgName);

        ArrayList<String> filenameList = new ArrayList<String>();
        if (AppConfig.front_img_file != null) {
            //  if (isEditing)
            //   filenameList.add(projectInfo.image);
            //   else
            filenameList.add(imgName);
        }

        ArrayList<File> fileList = new ArrayList<File>();
        if (AppConfig.front_img_file != null)
            fileList.add(AppConfig.front_img_file);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), AppConfig.API_TAG_UPDATE_IMAGE, params, fileList, filenameList);
        final String finalImgName = imgName;
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        if (isfront)
                            AppConfig.selPatternInfo.frontImage = finalImgName;
                        else
                            AppConfig.selPatternInfo.backImage = finalImgName;

                        Toast.makeText(getApplicationContext(), "Pattern Saved.", Toast.LENGTH_LONG).show();
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
