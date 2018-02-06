package com.premium.patternbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.Adapter.PatternPdfHistoryAdapter;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.premium.patternbox.app.AppConfig.GOOGLE_DRIVE_URL;
import static com.premium.patternbox.app.AppConfig.pdfList;

/**
 * Created by Kristpas  on 3/6/2017.
 */

public class PatternBuyActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private SessionManager session;

    private EditText search_txt;
    private ListView listView;
    private WebView webView;
    private Button prev_but, exit_but, reload_but, next_but, download_but, cancel_but;
    private PatternPdfHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pdfList = new ArrayList<String>();
        AppConfig.front_img_file = null;

        getSupportActionBar().setTitle("BUY PATTERN");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pattern_buy);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading ...");
        session = new SessionManager(getApplicationContext());

        listView = (ListView) findViewById(R.id.listView);
        prev_but = (Button) findViewById(R.id.prev_but);
        exit_but = (Button) findViewById(R.id.exit_but);
        reload_but = (Button) findViewById(R.id.reload_but);
        next_but = (Button) findViewById(R.id.next_but);
        download_but = (Button) findViewById(R.id.download_but);
        cancel_but = (Button) findViewById(R.id.cancel_but);

        prev_but.setOnClickListener(this);
        exit_but.setOnClickListener(this);
        reload_but.setOnClickListener(this);
        next_but.setOnClickListener(this);
        download_but.setOnClickListener(this);
        cancel_but.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                String temp_url = url;
                if (url.contains(GOOGLE_DRIVE_URL))
                    temp_url = url.replace(GOOGLE_DRIVE_URL, "");
                search_txt.setText(temp_url);
                if (!temp_url.contains("https://www.google.")) {
                    boolean isAppend = true;
                    for (int i = 0; i < AppConfig.historyList.size(); i ++) {
                        if (AppConfig.historyList.get(i).equals(temp_url)) {
                            isAppend = false;
                            break;
                        }
                    }
                    if (isAppend) {
                        AppConfig.historyList.add(temp_url);
                        adapter.notifyDataSetChanged();
                    }
                }
                updateButtons();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                updateButtons();
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url.contains(".pdf"))
                    webView.loadUrl(GOOGLE_DRIVE_URL + url);
                else
                    webView.loadUrl(url);
            }
        });

        search_txt = (EditText)findViewById(R.id.search_txt);
        search_txt.setFocusableInTouchMode(true);
        search_txt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String search_string = search_txt.getText().toString();
                    if (URLUtil.isValidUrl(search_string)) {
                        webView.loadUrl(search_string);
                    }
                    else {
                        search_string = search_string.replace(" ", "+");
                        search_string = (String.format("%s%s", AppConfig.GOOGLE_SEARCH_URL, search_string));
                        webView.loadUrl(search_string);
                    }

                    return true;
                }
                return false;
            }
        });
        search_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                         
            }
        });
        search_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    listView.setVisibility(View.VISIBLE);
                }else {
                    listView.setVisibility(View.GONE);
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();
                AppConfig.selPatternInfo = (PatternInfo) (listView.getItemAtPosition(position));
            }
        });
        adapter = new PatternPdfHistoryAdapter(AppConfig.historyList, getApplicationContext(), session.getUserID());
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url = (String)(listView.getItemAtPosition(position));
                webView.loadUrl(url);
                listView.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            webView.stopLoading();
        } catch (Exception e) {}
        overridePendingTransition(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfig.isSaved) finish();

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                if (AppConfig.pdfList.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please download a pdf document at least.", Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
                Intent intent = new Intent(PatternBuyActivity.this, PatternSaveActivity.class);
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

    @Override
    public void onClick(View v) {
        if (v == prev_but) {
            webView.goBack();
        }
        else if (v == exit_but) {
            webView.stopLoading();
        }
        else if (v == reload_but) {
            webView.reload();
        }
        else if (v == next_but) {
            webView.goForward();
        }
        else if (v == download_but) {
            String temp_url = search_txt.getText().toString();
            if (!temp_url.contains("https://www.google.")) {
                boolean isAppend = true;
                for (int i = 0; i < pdfList.size(); i ++) {
                    if (pdfList.get(i).equals(temp_url)) {
                        isAppend = false;
                        break;
                    }
                }
                if (isAppend) {
                    pdfList.add(temp_url);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                    if (temp_url.contains(".pdf") && AppConfig.front_img_file == null) {
                        webView.measure(View.MeasureSpec.makeMeasureSpec(
                                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        webView.layout(0, 0, webView.getMeasuredWidth(),
                                webView.getMeasuredHeight());
                        webView.setDrawingCacheEnabled(true);
                        webView.buildDrawingCache();
                        Bitmap bm = Bitmap.createBitmap(webView.getMeasuredWidth(),
                                webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

                        Canvas bigcanvas = new Canvas(bm);
                        Paint paint = new Paint();
                        int iHeight = bm.getHeight();
                        bigcanvas.drawBitmap(bm, 0, iHeight, paint);
                        webView.draw(bigcanvas);
                        System.out.println("1111111111111111111111="
                                + bigcanvas.getWidth());
                        System.out.println("22222222222222222222222="
                                + bigcanvas.getHeight());

                        if (bm != null) {
                            try {
                                String path = Environment.getExternalStorageDirectory()
                                        .toString();
                                OutputStream fOut = null;
                                AppConfig.front_img_file = new File(path, "/aaaa.jpg");
                                fOut = new FileOutputStream(AppConfig.front_img_file);

                                bm.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
                                fOut.flush();
                                fOut.close();
                                bm.recycle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        else if (v == cancel_but) {
            listView.setVisibility(View.GONE);
            search_txt.setFocusable(false);
            search_txt.setFocusableInTouchMode(true);
        }
    }

    private void updateButtons() {
        prev_but.setEnabled(webView.canGoBack());
        next_but.setEnabled(webView.canGoForward());
    }
}
