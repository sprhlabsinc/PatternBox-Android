package com.premium.patternbox;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.Adapter.FabricSingleAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricCategoryInfo;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.app.NotionCategoryInfo;
import com.premium.patternbox.app.NotionEditInfo;
import com.premium.patternbox.app.NotionInfo;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.app.ProjectInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;
import com.premium.patternbox.utils.ImageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.premium.patternbox.app.AppConfig.KProjectClient;
import static com.premium.patternbox.app.AppConfig.KProjectFabrics;
import static com.premium.patternbox.app.AppConfig.KProjectId;
import static com.premium.patternbox.app.AppConfig.KProjectImageName;
import static com.premium.patternbox.app.AppConfig.KProjectMeasurement;
import static com.premium.patternbox.app.AppConfig.KProjectName;
import static com.premium.patternbox.app.AppConfig.KProjectNotes;
import static com.premium.patternbox.app.AppConfig.KProjectNotions;
import static com.premium.patternbox.app.AppConfig.KProjectPattern;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class ProjectEditActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;

    //project variables
    private ImageView project_img;
    private EditText name_txt, client_txt, measurements_txt, notes_txt;
    private Button pattern_select_but, fabrics_select_but, notions_select_but;

    //pattern variables
    private ImageView imgPattern, imgBookMark;
    private TextView lbName, lbId, lbCategories;

    //fabrics variables
    private TwoWayView gridView;
    private FabricSingleAdapter adapter;
    private Menu mMenu;

    //notions variables
    TextView notions_txt;

    private boolean isEditing = false;
    private ProjectInfo m_projectInfo;
    private boolean isLock = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppConfig.front_img_file = null;

        AppConfig.setNotionSetting();

        isEditing = getIntent().getBooleanExtra("isEditing", false);
        m_projectInfo = AppConfig.selProjectInfo;

        getSupportActionBar().setTitle("PROJECT");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_project_edit);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        gridView = (TwoWayView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                AppConfig.selFabricInfo = (FabricInfo) (gridView.getItemAtPosition(position));
                Intent intent = new Intent(ProjectEditActivity.this, FabricDetailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });

        project_img = (ImageView) findViewById(R.id.project_img);
        name_txt = (EditText) findViewById(R.id.name_txt);
        client_txt = (EditText) findViewById(R.id.client_txt);
        measurements_txt = (EditText) findViewById(R.id.measurements_txt);
        notes_txt = (EditText) findViewById(R.id.notes_txt);

        pattern_select_but = (Button) findViewById(R.id.pattern_select_but);
        fabrics_select_but = (Button) findViewById(R.id.fabrics_select_but);
        notions_select_but = (Button) findViewById(R.id.notions_select_but);

        EasyImage.configuration(this)
                .setImagesFolderName("My app images") //images folder name, default is "EasyImage"
                .saveInAppExternalFilesDir() //if you want to use root internal memory for storying images
                .saveInRootPicturesDirectory(); //if you want to use internal memory for storying images - default

        project_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isLock) {
                    String url =  AppConfig.downloadUrl(session.getUserID(),m_projectInfo.image);
                    ImageDialog dialog = new ImageDialog(ProjectEditActivity.this, url);
                    dialog.show();
                }
                else {
//                    if (ContextCompat.checkSelfPermission(ProjectEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(ProjectEditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                    } else {
//                        EasyImage.openGallery(ProjectEditActivity.this, 0);
//                    }
                    openSelectImageSourceDialog();
                }
            }
        });
        pattern_select_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isLock) {
                    if (AppConfig.selPatternInfo != null) {
                        if (AppConfig.selPatternInfo.isPdf == 1) {
                            Intent intent = new Intent(ProjectEditActivity.this, PatternPdfDetailActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                        } else {
                            Intent intent = new Intent(ProjectEditActivity.this, PatternCapturedSearchDetailActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                        }
                    }
                }
                else {
                    AppConfig.isClose = true;
                    AppConfig.isUpdate = true;
                    startActivity(new Intent(ProjectEditActivity.this, PatternAddProjectActivity.class));
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });
        fabrics_select_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppConfig.isClose = true;
                AppConfig.isUpdate = true;
                startActivity(new Intent(ProjectEditActivity.this, FabricAddProjectActivity.class));
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        notions_select_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppConfig.isClose = true;
                AppConfig.isUpdate = true;
                Intent intent = new Intent(ProjectEditActivity.this, NotionViewActivity.class);
                intent.putExtra("isLock", isLock);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });

        imgPattern = (ImageView) findViewById(R.id.imgPattern);
        imgBookMark = (ImageView) findViewById(R.id.imgBookMark);
        lbName = (TextView) findViewById(R.id.lbName);
        lbId = (TextView) findViewById(R.id.lbId);
        lbCategories = (TextView) findViewById(R.id.lbCategories);

        notions_txt = (TextView) findViewById(R.id.notions_txt);

        if (isEditing) {
            String url = AppConfig.downloadUrl(session.getUserID(), m_projectInfo.image);
            int placeHolder = R.drawable.icon_camera;

            Glide.with(this).load(url)
                    .crossFade()
                    .placeholder(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(project_img);

            name_txt.setText(m_projectInfo.name);
            client_txt.setText(m_projectInfo.client);
            measurements_txt.setText(m_projectInfo.measurements);
            notes_txt.setText(m_projectInfo.notes);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(ProjectEditActivity.this, 0);
            }
        }
        else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(ProjectEditActivity.this, 0);
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
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                project_img.setImageBitmap(myBitmap);
                AppConfig.front_img_file = imageFile;
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProjectEditActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    @Override
    public void onStart(){
        super.onStart();

        if (AppConfig.isUpdate) {
            try {
                //pattern load
                if (AppConfig.selPatternID != 0) {
                    PatternInfo dataModel = null;
                    for (int i = 0; i < AppConfig.patternList.size(); i++) {
                        PatternInfo temp = AppConfig.patternList.get(i);
                        if (temp.id == AppConfig.selPatternID) {
                            dataModel = temp;
                            break;
                        }
                    }
                    AppConfig.selPatternInfo = dataModel;
                    if (dataModel != null) {
                        int placeHolder = R.drawable.icon_camera;
                        String url = AppConfig.downloadUrl(session.getUserID(), dataModel.frontImage);
                        if (dataModel.isPdf == 0) {
                            placeHolder = R.drawable.icon_camera;
                        } else {
                            placeHolder = R.drawable.icon_pdfdoc;
                        }
                        if (dataModel.isBookmark > 0) {
                            imgBookMark.setVisibility(View.VISIBLE);
                        } else {
                            imgBookMark.setVisibility(View.GONE);
                        }
                        Glide.with(this).load(url)
                                .crossFade()
                                .placeholder(placeHolder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgPattern);

                        lbName.setText("NAME:   " + dataModel.name);
                        lbId.setText("ID:   " + String.valueOf(dataModel.key));
                        lbCategories.setText("CATEGORIES:   " + dataModel.getCategories());

                        imgPattern.setVisibility(View.VISIBLE);
                        lbName.setVisibility(View.VISIBLE);
                        lbId.setVisibility(View.VISIBLE);
                        lbCategories.setVisibility(View.VISIBLE);
                        pattern_select_but.setText("");
                    } else {
                        imgPattern.setVisibility(View.GONE);
                        imgBookMark.setVisibility(View.GONE);
                        lbName.setVisibility(View.GONE);
                        lbId.setVisibility(View.GONE);
                        lbCategories.setVisibility(View.GONE);
                        pattern_select_but.setText("Select Pattern");
                    }
                }
                //fabrics load
                if (AppConfig.selFabricIDs.equals("")) {
                    fabrics_select_but.setText("Select Fabrics");
                } else {
                    fabrics_select_but.setText("");
                    ArrayList<FabricInfo> fabricList = new ArrayList<FabricInfo>();
                    String[] ids = AppConfig.selFabricIDs.split(",");
                    for (int i = 0; i < ids.length; i ++) {
                        for (int j = 0; j < AppConfig.fabricList.size(); j ++) {
                            FabricInfo temp = AppConfig.fabricList.get(j);
                            if (temp.id == Integer.parseInt(ids[i])) {
                                fabricList.add(temp);
                                break;
                            }
                        }
                    }
                    adapter = new FabricSingleAdapter(fabricList, this, -1, session.getUserID());
                    gridView.setAdapter(adapter);
                }
                //notions load
                String notion_info = "";
                for (int k = 0; k < AppConfig.notionEditInfoList.size(); k ++) {
                    NotionEditInfo notionEditInfo = AppConfig.notionEditInfoList.get(k);
                    int sum = 0;
                    for (int i = 0; i < notionEditInfo.notionInfos.size(); i++) {
                        sum += notionEditInfo.notionInfos.get(i).count;
                    }
                    if (sum > 0)
                        notion_info += String.format("%s: %d, ", notionEditInfo.name, sum);
                }
                if (!notion_info.equals("")) {
                    notions_txt.setText(notion_info.substring(0, notion_info.length() - 2));
                    notions_select_but.setText("");
                }
                else {
                    notions_select_but.setText("Select Notions");
                    notions_txt.setText("");
                }
            } catch (Exception e) {
            }
        }
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
                //if create
                if (!isEditing && AppConfig.front_img_file == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please take a photo of project.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }

                String name = name_txt.getText().toString();
                String client = client_txt.getText().toString();
                String measurements = measurements_txt.getText().toString();
                String notes = notes_txt.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter project name.", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (client.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter project for.", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (AppConfig.selPatternID == 0) {
                    Toast.makeText(getApplicationContext(), "Please select a pattern.", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (!isEditing && AppConfig.projectList.size() >= 2) {
                    Toast.makeText(getApplicationContext(),
                            "You can not store projects more than 2. please purchase PATTERNBOX Premium App.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                m_projectInfo.name = name;
                m_projectInfo.client = client;
                m_projectInfo.notes = notes;
                m_projectInfo.measurements = measurements;
                m_projectInfo.pattern = AppConfig.selPatternID;
                m_projectInfo.fabrics = AppConfig.selFabricIDs;

                String notions = "";
                for (int i = 0; i < AppConfig.notionEditInfoList.size(); i ++) {
                    ArrayList<NotionInfo> notionInfos = AppConfig.notionEditInfoList.get(i).notionInfos;
                    for (int j = 0; j < notionInfos.size(); j ++) {
                        notions += String.format("%d,%d:", notionInfos.get(j).id, notionInfos.get(j).count);
                    }
                }
                if (!notions.equals("")) {
                    notions = notions.substring(0, notions.length() - 1);
                }
                m_projectInfo.notions = notions;
                saveInfoEvent(m_projectInfo);

                return true;
            case R.id.action_edit:
                MenuItem item1 = mMenu.findItem(R.id.action_save);
                item1.setVisible(true);
                item.setVisible(false);
                isLock = false;

                name_txt.setEnabled(!isLock);
                client_txt.setEnabled(!isLock);
                measurements_txt.setEnabled(!isLock);
                notes_txt.setEnabled(!isLock);
                fabrics_select_but.setVisibility(View.VISIBLE);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_edit, menu);
        mMenu = menu;

        if (!isEditing || m_projectInfo.pattern != AppConfig.selPatternID || !m_projectInfo.fabrics.equals(AppConfig.selFabricIDs) ||
                !m_projectInfo.notions.equals(AppConfig.selNotionIDs)) {
            MenuItem item = menu.findItem(R.id.action_edit);
            item.setVisible(false);
            isLock = false;
        }
        else {
            MenuItem item = menu.findItem(R.id.action_save);
            item.setVisible(false);
            isLock = true;

            name_txt.setEnabled(!isLock);
            client_txt.setEnabled(!isLock);
            measurements_txt.setEnabled(!isLock);
            notes_txt.setEnabled(!isLock);
            fabrics_select_but.setVisibility(View.GONE);
        }
        return true;
    }

    private void saveInfoEvent(final ProjectInfo projectInfo) {

        Date date = new Date();
        String time = String.valueOf(date.getTime());
        final String imgName = String.format("%s_pro.jpg", time);
        Map<String, String> params = new HashMap<String, String>();
        params.put(KProjectName, projectInfo.name);
        params.put(KProjectClient, projectInfo.client);
        params.put(KProjectNotes, projectInfo.notes);
        params.put(KProjectMeasurement, projectInfo.measurements);

        params.put(KProjectPattern, String.valueOf(projectInfo.pattern));
        params.put(KProjectFabrics, projectInfo.fabrics);
        params.put(KProjectNotions, projectInfo.notions);

        if (isEditing) {
            //params.put(KProjectImageName, projectInfo.image);
            params.put("is_update", "Yes");
            params.put(KProjectId, String.valueOf(projectInfo.id));

        }

        ArrayList<String> filenameList = new ArrayList<String>();
        if (AppConfig.front_img_file != null) {
          //  if (isEditing)
             //   filenameList.add(projectInfo.image);
         //   else
                filenameList.add(imgName);
            params.put(KProjectImageName, imgName);
        }

        ArrayList<File> fileList = new ArrayList<File>();
        if (AppConfig.front_img_file != null)
            fileList.add(AppConfig.front_img_file);

        pDialog.setMessage("Saving data ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), AppConfig.API_TAG_PROJECT_UPLOAD, params, fileList, filenameList);
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
                            for (int i = 0; i < AppConfig.projectList.size(); i ++) {
                                ProjectInfo newItem = AppConfig.projectList.get(i);
                                if (newItem.id == projectInfo.id) {
                                    newItem.name = projectInfo.name;
                                    newItem.client = projectInfo.client;
                                    newItem.notes = projectInfo.notes;
                                    newItem.measurements = projectInfo.measurements;
                                    if (AppConfig.front_img_file != null)
                                        newItem.image = imgName;
                                    newItem.pattern = projectInfo.pattern;
                                    newItem.fabrics = projectInfo.fabrics;
                                    newItem.notions = projectInfo.notions;
                                    break;
                                }
                            }
                        }
                        else {
                            int project_id = result.getInt("project_id");
                            ProjectInfo newItem = new ProjectInfo();
                            newItem.id = project_id;
                            newItem.name = projectInfo.name;
                            newItem.client = projectInfo.client;
                            newItem.notes = projectInfo.notes;
                            newItem.measurements = projectInfo.measurements;
                            newItem.image = imgName;
                            newItem.pattern = projectInfo.pattern;
                            newItem.fabrics = projectInfo.fabrics;
                            newItem.notions = projectInfo.notions;

                            AppConfig.selNotionIDs = projectInfo.notions;
                            AppConfig.addProject(newItem);
                        }
                        AppConfig.selNotionIDsFrom = "";
                        Toast.makeText(getApplicationContext(), "Project Saved.", Toast.LENGTH_LONG).show();
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

    private void openSelectImageSourceDialog(){
        final Dialog dialog = new Dialog(ProjectEditActivity.this);
        dialog.setContentView(R.layout.dialog_select_image);
        dialog.setTitle("Pick a source");

        ((Button) dialog
                .findViewById(R.id.btnCamera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                takePhotoWithCamera();
            }
        });

        ((Button) dialog
                .findViewById(R.id.btnGallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                openGallery();
            }
        });
        dialog.show();
    }
    private void openGallery(){
        if (ContextCompat.checkSelfPermission(ProjectEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectEditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            EasyImage.openGallery(ProjectEditActivity.this, 0);
        }
    }
    private void takePhotoWithCamera(){
        if (ContextCompat.checkSelfPermission(ProjectEditActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectEditActivity.this, new String[] { Manifest.permission.CAMERA}, 0);
        }
        else {
            EasyImage.openCamera(ProjectEditActivity.this, 1);
        }
    }

}
