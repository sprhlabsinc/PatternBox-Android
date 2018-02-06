package com.premium.patternbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetActivity extends AppCompatActivity {
    private Button btnReset;
    private Button btnLinkToMain;

    private EditText email_txt;

    private ProgressDialog pDialog;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset);

        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        email_txt = (EditText) findViewById(R.id.email_txt);

        btnLinkToMain = (Button)findViewById(R.id.btnLinkToMainScreen);
        btnLinkToMain.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

        btnReset = (Button)findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = email_txt.getText().toString().trim();

                if (!AppConfig.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Please input valid email address.", Toast.LENGTH_SHORT).show();
                }
                else {
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(final String email) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);

        pDialog.setMessage("Reset password ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.POST, AppConfig.API_TAG_RESET, params);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    String errorMsg = result.getString("message");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
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
