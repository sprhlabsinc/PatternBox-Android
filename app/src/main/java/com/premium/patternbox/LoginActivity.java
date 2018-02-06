package com.premium.patternbox;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.AppController;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SQLiteHandler;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button btnLinkToReset;
    private EditText inputEmail;
    private EditText inputPassword;

    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();

        session = new SessionManager(getApplicationContext());
        if (session.getUserID() > 0 ){
            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);


        inputEmail.setText("");
        inputPassword.setText("");

        //inputEmail.setText("leon_jie@mail.com");
        //inputPassword.setText("test");


        btnLinkToReset = (Button)findViewById(R.id.btnLinkToResetScreen);
        btnLinkToReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ResetActivity.class);
                startActivity(i);
            }
        });

        btnLinkToRegister = (Button)findViewById(R.id.btnLinkToRegisterScreen);
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
    /* function to verify login details in mysql db
    * */
    private void checkLogin(final String email, final String password) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        pDialog.setMessage("Log in ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.POST, AppConfig.API_TAG_LOGIN, params);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                hideDialog();
                boolean error = false;
                try {
                    error = result.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // Now store the user in SQLite
                        int uid = result.getInt("user_id");
                        session.setUserID(uid);
                        String apiKey = result.getString("apiKey");
                        session.setAPIKey(apiKey);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
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
