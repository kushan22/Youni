package com.thefreelancer.youni;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;

public class Signin extends AppCompatActivity implements View.OnClickListener {

    CollapsingToolbarLayout ctl;

    EditText et1, et2;
    Button bt1, bt2;
    TextView tv1;
    String username;
    String password;
    ProgressDialog dialog;
    SharedPreferences myprefs;
    SharedPreferences.Editor myEditor;
    public static final String PREF = "myprefs";
    public static final String USER = "myusername";
    public static final String PASS = "mypassword";
    public static final String NAME = "myName";
    private String status;
    private String name;

  /*  private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/Authentication";
    private static final String METHOD = "Authentication";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SetupUi(findViewById(R.id.userLogin));

        SharedPreferences anotherPref = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String user = anotherPref.getString(USER, "defaultName");
        String pass = anotherPref.getString(PASS, "defaultPass");
        String name = anotherPref.getString(NAME, "defaultUserName");

        if (!user.equals("defaultName") && !pass.equals("defaultPass")) {

            Toast.makeText(Signin.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Signin.this, Courses.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {


            et1 = (EditText) findViewById(R.id.email);
            et2 = (EditText) findViewById(R.id.password);
            bt1 = (Button) findViewById(R.id.btnLogin);
            bt2 = (Button) findViewById(R.id.btnLinkToRegisterScreen);
            tv1 = (TextView) findViewById(R.id.forgotPassword);


            bt1.setOnClickListener(this);
            bt2.setOnClickListener(this);
            tv1.setOnClickListener(this);
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


                    hideSoftKeyboard(Signin.this);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnLogin:

                username = et1.getText().toString();
                password = et2.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Signin.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {

                    dialog = new ProgressDialog(Signin.this);
                    dialog.setTitle("");
                    dialog.setCancelable(false);
                    dialog.setMessage("");
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    dialog.setContentView(R.layout.progress_bar);

                    new Authentication().execute("");
                }

                break;
            case R.id.btnLinkToRegisterScreen:


                Intent backHome = new Intent(Signin.this, Signup.class);
                startActivity(backHome);
                finish();


                break;

            case R.id.forgotPassword:

                Intent forgotIntent = new Intent(Signin.this, Forgotpassword.class);
                startActivity(forgotIntent);


                break;
        }
    }


    private class Authentication extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String result = "";
        String authenticateString = "";


        @Override
        protected String doInBackground(String... params) {

          /*  SoapObject request = new SoapObject(NAMESPACE,METHOD);
            request.addProperty("username",username);
            request.addProperty("password", password);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL);
            try {
                transport.call(ACTION,envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (envelope.bodyIn instanceof SoapFault){

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;
            }else {

                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null){

                    return "No Internet Connection";
                }else {

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("AuthenticationResult");
                    return result.toString();

                }
            }*/

            final String baseUrl = "http://www.youni.co.in/user/login.php";
            final String QUERY_EMAIl = "email";
            final String QUERY_PASS = "pass";

            Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIl, username).
                    appendQueryParameter(QUERY_PASS, password).build();

            try {
                URL url = new URL(uri.toString());
                request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();

                StringBuffer sb = new StringBuffer();

                if (is == null)
                    return null;

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
                if (request != null) {

                    request.disconnect();
                }
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            authenticateString = getDataFromJson(result);

            return authenticateString;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("No Internet Connection")) {

                Toast.makeText(Signin.this, s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (status.equals("404")) {

                Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                dialog.dismiss();


            } else if (status.equals("200")) {

                ArrayList<String> details = new ArrayList<>(Arrays.asList(s.split(",")));
                //  String auth = details.get(0);
                String Name = details.get(1);

                myprefs = getSharedPreferences(PREF, Context.MODE_PRIVATE);
                myEditor = myprefs.edit();
                myEditor.putString(USER, username);
                myEditor.putString(PASS, password);
                myEditor.putString(NAME, Name);
                myEditor.commit();

                Toast.makeText(Signin.this, "Welcome, " + Name, Toast.LENGTH_SHORT).show();

                Intent signIntent = new Intent(Signin.this, Courses.class);
                signIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signIntent);
                finish();
                dialog.dismiss();

            } else {
                Toast.makeText(Signin.this, "Network Problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }


    }


    public String getDataFromJson(String s) {

        status = "";
        name = "";

        try {
            JSONObject obj = new JSONObject(s);
            status = obj.getString("status");
            name = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String res = status + "," + name;

        return res;


    }


}




