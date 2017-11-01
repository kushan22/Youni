package com.youni.youni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    CircleImageView cv1;
    Button bt2;
    EditText et1;
    private static final int REQUEST_CODE = 1;
    String newName, newEmail;
    private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/updateProfile";
    private static final String METHOD = "updateProfile";
    String email;
    ProgressDialog dialog;
    private String Name;
    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = "http://www.youni.co.in/profile/upload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_EMAIL = "email";
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cv1 = (CircleImageView) findViewById(R.id.circleImage2);
        //  bt1 = (Button) findViewById(R.id.editPic);
        bt2 = (Button) findViewById(R.id.updateProfile);
        et1 = (EditText) findViewById(R.id.editName);


        SharedPreferences myprefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        email = myprefs.getString(Signin.USER, "email");
        Name = myprefs.getString(Signin.NAME, "Name");

        et1.setText(Name);


        //bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {


        newName = et1.getText().toString();

        dialog = new ProgressDialog(EditProfile.this);
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setMessage("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setContentView(R.layout.progress_bar);

        new updateProfile().execute("");


    }


    private class updateProfile extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String result = "";


        @Override
        protected String doInBackground(String... params) {


            final String baseUrl = "http://youni.co.in/profile/upload.php";

            final String QUERY_NAME = "name";
            final String QUERY_EMAIL = "email";

            Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_NAME, newName).
                    appendQueryParameter(QUERY_EMAIL, email).build();

            try {
                java.net.URL myUrl = new URL(uri.toString());
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
            }finally {
                if (request != null)
                    request.disconnect();

                if (reader != null){

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            String res = getJsonData(result);


            return res;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("404")) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else if (s.equals("200")) {

                Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.putString(Signin.NAME, newName);
                editor.putString(Signin.USER,email);
                editor.commit();
                Intent intent = new Intent(EditProfile.this, Courses.class);
                startActivity(intent);
                dialog.dismiss();

            }else {
                Toast.makeText(EditProfile.this,"Network Problem",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }


    public String getJsonData(String jsonString) {

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
