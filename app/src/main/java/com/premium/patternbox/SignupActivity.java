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

public class SignupActivity extends AppCompatActivity {
    private Button btnSignUp;
    private Button btnLinkToMain;

    private ProgressDialog pDialog;
    private SessionManager session;

    private EditText email_txt, password_txt, passconfirm_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        email_txt = (EditText) findViewById(R.id.email_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        passconfirm_txt = (EditText) findViewById(R.id.passconfirm_txt);

        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLinkToMain = (Button)findViewById(R.id.btnLinkToMainScreen);
        btnLinkToMain.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = email_txt.getText().toString();
                String password = password_txt.getText().toString();
                String confirmPass = passconfirm_txt.getText().toString();
                //check valid email address, match password

                if (!AppConfig.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Please input valid email address.", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Your password is too easy to guess.", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(confirmPass)) {
                    Toast.makeText(getApplicationContext(), "Doesn't match passwords.", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(final String email, final String password) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        pDialog.setMessage("Sign up ...");
        showDialog();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.POST, AppConfig.API_TAG_REGISTER, params);
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
                        Intent intent = new Intent(SignupActivity.this,
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
