package com.thefreelancer.youni;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Forgotpassword extends AppCompatActivity implements View.OnClickListener {


    EditText et1, et2, et3;
    Button bt1;
    String newPassword = "";
    String confirmNewPassword = "";
    String useremail = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetupUi(findViewById(R.id.forgotLayout));

        et1 = (EditText) findViewById(R.id.newPassword);
        et2 = (EditText) findViewById(R.id.confirmNewPassword);
        et3 = (EditText) findViewById(R.id.useremail);
        bt1 = (Button) findViewById(R.id.changePassword);

        bt1.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {


        newPassword = et1.getText().toString();
        confirmNewPassword = et2.getText().toString();
        useremail = et3.getText().toString();

        if (newPassword.equals("") || confirmNewPassword.equals("")) {

            Toast.makeText(getApplicationContext(), "Please fill all the above fields", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmNewPassword)) {

            Toast.makeText(getApplicationContext(), "Passwords did not match", Toast.LENGTH_SHORT).show();

        } else {
            dialog = new ProgressDialog(Forgotpassword.this);
            dialog.setTitle("");
            dialog.setCancelable(false);
            dialog.setMessage("");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            dialog.setContentView(R.layout.progress_bar);
            new ChangePassword().execute("");
        }


    }

    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void SetupUi(View v) {

        if (!(v instanceof EditText)) {


            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    hideSoftKeyboard(Forgotpassword.this);
                    return false;


                }
            });
        }


        if (v instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {

                View innerView = ((ViewGroup) v).getChildAt(i);
                SetupUi(innerView);
            }
        }

    }


    private class ChangePassword extends AsyncTask<String, String, String> {


        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String result = "";

        @Override
        protected String doInBackground(String... params) {

            final String baseUrl = "http://youni.co.in/profile/setpassword.php";
            final String QUERY_NEWPASSWORD = "newpass";
            final String QUERY_EMAIL = "email";

            Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIL, useremail).
                    appendQueryParameter(QUERY_NEWPASSWORD, newPassword).build();

            try {
                URL myUrl = new URL(uri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();

                if (is == null)
                    return null;

                StringBuilder sb = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(is));

                String line = "";

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");
                }


                if (sb.length() == 0)
                    return null;


                result = sb.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (request != null)
                    request.disconnect();

                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            String res = getDataFromJson(result);


            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (s.equals("200")) {

                Toast.makeText(getApplicationContext(), "Password Changed Successfully. Log in to continue", Toast.LENGTH_SHORT).show();
                Intent logInIntent = new Intent(Forgotpassword.this, Signin.class);
                startActivity(logInIntent);
                finish();
                dialog.dismiss();


            } else if (s.equals("404")) {

                Toast.makeText(getApplicationContext(), "Failed to change password. Try Again!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {

                Toast.makeText(getApplicationContext(), "Check your network connection", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }


        }
    }


    public String getDataFromJson(String jsonString) {

        String resultData = "";

        try {
            JSONObject obj = new JSONObject(jsonString);
            resultData = obj.getString("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resultData;
    }
}
