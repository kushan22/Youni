package com.youni.youni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {


    RecyclerView rv1;
    TextView tv1, tv2, tv3;
    ProgressDialog dialog;
    CircleImageView cv1;
    String userName;

    RecyclerView.Adapter mAdapter;
    LinearLayoutManager lm;
   /* private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/getCompletedCourse";
    private static final String METHOD = "getCompletedCourse";*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        String Name = prefs.getString(Signin.NAME, "Kushan Singh");
        userName = prefs.getString(Signin.USER, "user");
        rv1 = (RecyclerView) findViewById(R.id.completedCourseList);
        tv1 = (TextView) findViewById(R.id.profileName);
        tv1.setText(Name);
        tv2 = (TextView) findViewById(R.id.numberofcourses);
        tv3 = (TextView) findViewById(R.id.courseNotCompleted);

        tv3.setVisibility(View.INVISIBLE);


        rv1.setHasFixedSize(true);
        lm = new LinearLayoutManager(getApplicationContext());
        rv1.setLayoutManager(lm);


        dialog = new ProgressDialog(Profile.this);
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setMessage("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setContentView(R.layout.progress_bar);
        new getCompletedCourseDetails().execute("");


    }

    @Override
    public void onClick(View v) {

    }

 /*   public  void fillDummyDetails(){

        completedCourses.add("Python tutorials");
        completedCourses.add("Android Tutorials");
        completedCourses.add("Linux Tutorials");
        completedCourses.add("Photoshop Tutorials");
        completedCourses.add("SEO tutorials");
        completedCourses.add("Mathematics");
        completedCourses.add("Physics");
    }*/


    private class getCompletedCourseDetails extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {
           /* SoapObject request = new SoapObject(NAMESPACE, METHOD);
            request.addProperty("username", userName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL);
            try {
                transport.call(ACTION, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }


            if (envelope.bodyIn instanceof SoapFault) {

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;
            } else {

                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null) {

                    return "No Internet Connection";
                } else {

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("getCompletedCourseResult");
                    return result.toString();
                }
            }*/

            final String baseUrl = "http://www.youni.co.in/courses/completedcourses.php";
            final String QUERY_EMAIL = "email";
            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIL, userName).build();
            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");
                }

                if (sb.length() == 0) {
                    return "Network Problem";
                }


                res = getDataFromJson(sb.toString());


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

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Network Problem")) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (s.equals("404")){
                tv3.setVisibility(View.VISIBLE);
                rv1.setVisibility(View.INVISIBLE);
                dialog.dismiss();

            }else if (!s.equals("")) {

                ArrayList<String> completedCourses = new ArrayList<>(Arrays.asList(s.split(",")));
                if (completedCourses.size() > 0) {
                    rv1.setVisibility(View.VISIBLE);
                    mAdapter = new CompletedCourseAdapter(Profile.this, completedCourses);
                    rv1.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    tv2.setText("" + completedCourses.size());
                } else {

                    tv3.setVisibility(View.VISIBLE);
                    rv1.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }


            }else {

                Toast.makeText(getApplicationContext(),"Network Problem",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CompletedCourseAdapter.callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public String getDataFromJson(String jsonString) {


        String completedCourses = "";
        Log.i("jsonEncode",jsonString);

        try {
            JSONObject jObject = new JSONObject(jsonString);
            if (jObject.getString("coursename").equals("404")){

                completedCourses = "404";
                return completedCourses;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                        if (i == 0) {

                            completedCourses = completedCourses + jsonObject.getString("course_name");


                        } else {

                            completedCourses = completedCourses + "," + jsonObject.getString("course_name");
                        }


                    }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return completedCourses;


    }

}
