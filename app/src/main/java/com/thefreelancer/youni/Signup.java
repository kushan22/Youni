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
import android.util.Log;
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

public class Signup extends AppCompatActivity implements View.OnClickListener {


    EditText et1, et2, et3, et4;
    Button bt1, bt2;

    ProgressDialog dialog;
    String AuthenticationResult = "";

   /* private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/RegisterUsers";
    private static final String METHOD = "RegisterUsers";*/

    String userName, userEmail, userPass,userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SetupUi(findViewById(R.id.userSignUp));
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et1 = (EditText) findViewById(R.id.etname);
        et2 = (EditText) findViewById(R.id.etemail);
        et3 = (EditText) findViewById(R.id.etpassword);
        et4 = (EditText) findViewById(R.id.etconfirmpassword);
     //   et5 = (EditText) findViewById(R.id.etNumber);
        bt1 = (Button) findViewById(R.id.btnRegister);
        bt2 = (Button) findViewById(R.id.btnLinkToLoginScreen);


        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);


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


                    hideSoftKeyboard(Signup.this);
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

            case R.id.btnRegister:

                userName = et1.getText().toString();
                userEmail = et2.getText().toString().trim();
                userPass = et3.getText().toString();
               // userNumber = et5.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || et4.getText().toString().isEmpty()) {

                    Toast.makeText(Signup.this, "Please fill every field", Toast.LENGTH_SHORT).show();
                } else {

                    if (userEmail.matches(emailPattern)) {


                        if (userPass.equals(et4.getText().toString())) {

                            dialog = new ProgressDialog(Signup.this);
                            dialog.setTitle("");
                            dialog.setCancelable(false);
                            dialog.setMessage("");
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                            dialog.setContentView(R.layout.progress_bar);
                            new registerUsers().execute("");
                        } else {

                            Toast.makeText(Signup.this, "Password did not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    }
                }


                break;

            case R.id.btnLinkToLoginScreen:

                Intent signInIntent = new Intent(Signup.this, Signin.class);
                startActivity(signInIntent);
                finish();

                break;

        }


    }


    private class registerUsers extends AsyncTask<String, String, String> {

        HttpURLConnection con = null;
        BufferedReader reader = null;
        InputStream is = null;
        URL registerUrl = null;
        String result;

        @Override
        protected String doInBackground(String... params) {

            /*SoapObject request = new SoapObject(NAMESPACE,METHOD);
            request.addProperty("username",userName);
            request.addProperty("email",userEmail);
            request.addProperty("password", userPass);

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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("RegisterUsersResult");
                    return result.toString();
                }

            }*/

            final String baseUrl = "http://www.youni.co.in/user/register.php";
            final String QUERY_NAME = "name";
            final String QUERY_EMAIL = "email";
            final String QUERY_PASS = "pass";
           // final String QUERY_NUMBER = "number";

            Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_NAME, userName).
                    appendQueryParameter(QUERY_EMAIL, userEmail).
                    appendQueryParameter(QUERY_PASS, userPass).build();


            try {
                URL myUrl = new URL(builtUri.toString());
                con = (HttpURLConnection) myUrl.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                is = con.getInputStream();
                StringBuffer sb = new StringBuffer();

                if (is == null) {

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(is));

                String line = "";

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");
                }


                if (sb.length() == 0) {

                    return "No Internet Connection";
                }


                AuthenticationResult = sb.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {

                    con.disconnect();
                }
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            result = getDatafromJson(AuthenticationResult);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("No Internet Connection")) {

                Toast.makeText(Signup.this, s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (s.equals("Unable to Insert")) {

                Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else if (!s.equals("")) {

                Toast.makeText(Signup.this, "Welcome To Youni", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, Signin.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dialog.dismiss();
                // finish();

            } else {

                Toast.makeText(Signup.this, "Network Problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }


    public String getDatafromJson(String jsonString) {
        Log.i("jsonString", jsonString);

        String authenticationResult = "";

        try {
            JSONObject jObject = new JSONObject(jsonString);
            authenticationResult = jObject.getString("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return authenticationResult;


    }

}
