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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class CourseSpecs extends AppCompatActivity implements View.OnClickListener {


    Button bt1, bt2;
    TextView tv1;
    String topicName;
    String dateString;
    String playListId;
    String userEmail;
    String userName;
    private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/putMyCourses";
    private static final String METHOD = "putMyCourses";
    private static final String URL1 = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE1 = "http://viesr.com/";
    private static final String ACTION1 = "http://viesr.com/removeCourse";
    private static final String METHOD1 = "removeCourse";
    private static final String URL2 = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE2 = "http://viesr.com/";
    private static final String ACTION2 = "http://viesr.com/getState";
    private static final String METHOD2 = "getState";
    SharedPreferences myprefs;
    SharedPreferences.Editor myEditor;
    public static final String JOIN_STATE = "join";
    public static final String STATE_VALUE = "joinorunjoin";
    ProgressDialog dialog, dialog1, dialog2;
    TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_specs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv1 = (TextView) findViewById(R.id.topic);

        tv3 = (TextView) findViewById(R.id.text);
        bt1 = (Button) findViewById(R.id.join);
        bt2 = (Button) findViewById(R.id.unjoin);

        Intent receiver = getIntent();
        topicName = receiver.getStringExtra("topicName");
        playListId = receiver.getStringExtra("playlistid");

        SharedPreferences userprefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        userEmail = userprefs.getString(Signin.USER, "email");
        tv1.setText(topicName);


        dialog = new ProgressDialog(CourseSpecs.this);
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setMessage("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setContentView(R.layout.progress_bar);
        new getStateofCourse().execute("");


        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.join:


                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                dateString = sdf.format(date);

                dialog1 = new ProgressDialog(CourseSpecs.this);
                dialog1.setTitle("");
                dialog1.setCancelable(false);
                dialog1.setMessage("");
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.show();
                dialog1.setContentView(R.layout.progress_bar);

                new putMyCourse().execute("");


  /*      prefs = getSharedPreferences("MYCOURSES", Context.MODE_PRIVATE);
        editor = prefs.edit();
      topicNames =  prefs.getStringSet("TOPICS", new HashSet<String>());
      dates = prefs.getStringSet("DATES",new HashSet<String>());
       playLists = prefs.getStringSet("PLAYLISTS",new HashSet<String>());
        topicNames.add(topicName);
        dates.add(dateString);
        playLists.add(playListId);
        editor.putStringSet("TOPICS", topicNames);
        editor.putStringSet("DATES", dates);
        editor.putStringSet("PLAYLISTS", playLists);
        editor.commit();*/

                break;

            case R.id.unjoin:


                SharedPreferences unJoinPrefs = getSharedPreferences(JOIN_STATE, Context.MODE_PRIVATE);
                SharedPreferences.Editor unJoineditor = unJoinPrefs.edit();
                unJoineditor.clear();
                unJoineditor.commit();

                SharedPreferences userPref = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                userName = userPref.getString(Signin.USER, "emailofuser");

                dialog2 = new ProgressDialog(CourseSpecs.this);
                dialog2.setTitle("");
                dialog2.setCancelable(false);
                dialog2.setMessage("");
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                dialog2.setContentView(R.layout.progress_bar);
                new removeCourses().execute("");


                break;
        }


    }


    private class putMyCourse extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {


            String state = "joined";

        /*    SoapObject request = new SoapObject(NAMESPACE, METHOD);
            request.addProperty("topicName", topicName);
            request.addProperty("date", dateString);
            request.addProperty("playListId", playListId);
            request.addProperty("userEmail", userEmail);
            request.addProperty("state",state);


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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("putMyCoursesResult");
                    return result.toString();

                }

            }*/


            final String baseUrl = "http://www.youni.co.in/courses/joincourses.php";
            final String QUERY_TOPICNAME = "course_name";
            final String QUERY_DATE = "date";
            final String QUERY_PLAYLISTID = "playlistid";
            final String QUERY_EMAIL = "email";
            final String QUERY_STATE = "state";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_TOPICNAME, topicName).
                    appendQueryParameter(QUERY_DATE, dateString).
                    appendQueryParameter(QUERY_PLAYLISTID, playListId).
                    appendQueryParameter(QUERY_EMAIL, userEmail).
                    appendQueryParameter(QUERY_STATE, state).build();

            Log.i("TOPIC", baseUri.toString());

            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";

                StringBuilder sb = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(is));
                String line = "";

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");


                }

                if (sb.length() == 0) {

                    return "Network Problem";
                }


                res = putMyCourseJsonData(sb.toString());


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


            if (s.equals("200")) {

               // Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                bt1.setVisibility(View.INVISIBLE);
                bt2.setVisibility(View.VISIBLE);
                Intent nextIntent = new Intent(CourseSpecs.this, MainActivity.class);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                nextIntent.putExtra("playlist", playListId);
                nextIntent.putExtra("topicNameCompleted", topicName);
                startActivity(nextIntent);
               // finish();
                dialog1.dismiss();
            } else if (s.equals("404")) {

                Toast.makeText(getApplicationContext(), "There is a problem", Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
            } else if (s.equals("Network Problem")) {

                Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
            }


        }
    }

    public String putMyCourseJsonData(String jsonString) {

        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("status");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    private class removeCourses extends AsyncTask<String, String, String> {
        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";


        @Override
        protected String doInBackground(String... params) {

          /*  SoapObject request = new SoapObject(NAMESPACE1, METHOD1);
            request.addProperty("username", userName);
            request.addProperty("topicName", topicName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL1);
            try {
                transport.call(ACTION1, envelope);
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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("removeCourseResult");
                    return result.toString();
                }

            }*/
            final String baseUrl = "http://www.youni.co.in/courses/unenrollcourses.php";
            final String QUERY_EMAIL = "email";
            final String QUERY_PLAYLISTID = "playlistid";
            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIL, userEmail).
                    appendQueryParameter(QUERY_PLAYLISTID, playListId).build();

            try {
                URL myurl = new URL(baseUri.toString());
                request = (HttpURLConnection) myurl.openConnection();
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

                if (sb.length() == 0)
                    return "Network Problem";

                res = getDataFromJson(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
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

            return  res;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Network Problem")) {

                Toast.makeText(CourseSpecs.this, s, Toast.LENGTH_SHORT).show();
                dialog2.dismiss();
            } else if (s.equals("200")){
                bt1.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.INVISIBLE);

               // Toast.makeText(CourseSpecs.this, s, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseSpecs.this, Courses.class);
                startActivity(intent);
                dialog2.dismiss();
            }else if (s.equals("404")){

                Toast.makeText(getApplicationContext(),"Already Unjoined",Toast.LENGTH_SHORT).show();
                dialog2.dismiss();
            }

        }
    }

    public String getDataFromJson(String jsonString) {

        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private class getStateofCourse extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {

         /*   SoapObject request = new SoapObject(NAMESPACE2, METHOD2);
            request.addProperty("topicName", topicName);
            request.addProperty("userEmail", userEmail);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL2);
            try {
                transport.call(ACTION2, envelope);
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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("getStateResult");
                    return result.toString();

                }

            }*/


            final String baseUrl = "http://www.youni.co.in/courses/getstatecourses.php";
            final String QUERY_PLAYLISTID = "playlistid";
            final String QUERY_EMAIL = "email";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_PLAYLISTID, playListId).
                    appendQueryParameter(QUERY_EMAIL, userEmail).build();

            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();

                if (is == null)
                    return "Network Problem";

                StringBuilder sb = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(is));

                String line = "";

                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");

                }


                if (sb.length() == 0)
                    return "Network Problem";


                res = getStateDataFromJson(sb.toString());


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

                Toast.makeText(CourseSpecs.this, s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {

                if (s.equals("joined")) {
                    bt1.setVisibility(View.INVISIBLE);
                    bt2.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else if (s.equals("404")) {

                    bt1.setVisibility(View.VISIBLE);
                    bt2.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                } else if (s.equals("unjoined")) {
                    bt1.setVisibility(View.VISIBLE);
                    bt2.setVisibility(View.INVISIBLE);
                    dialog.dismiss();


                } else if (s.equals("completed")) {

                    bt1.setVisibility(View.INVISIBLE);
                    bt2.setVisibility(View.INVISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }else {

                    Toast.makeText(getApplicationContext(),"Network Problem",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Intent goBack = new Intent(CourseSpecs.this,Courses.class);
                    startActivity(goBack);
                }

            }

        }
    }

    public String getStateDataFromJson(String jsonString) {

        String result = "";


        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}
