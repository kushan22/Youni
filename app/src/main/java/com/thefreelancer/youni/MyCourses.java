package com.thefreelancer.youni;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourses extends Fragment {

    RecyclerView rv;
    RecyclerView.Adapter mAdapter;

    LinearLayoutManager lm;
    TextView tv1;
    ProgressDialog dialog;
    public static ArrayList<String> topicNames;
    public static ArrayList<String> dates;
    public static ArrayList<String> playlists;
    private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/getMyCourses";
    private static final String METHOD = "getMyCourses";
    String userEmail;
    String user;
    String topic;


    public MyCourses() {
        // Required empty public constructor
    }

    public static MyCourses newInstance() {

        return new MyCourses();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_courses, container, false);
        SetupUi(v.findViewById(R.id.myCourses));

        SharedPreferences prefs = v.getContext().getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        userEmail = prefs.getString(Signin.USER, "email");

        rv = (RecyclerView) v.findViewById(R.id.myCoursesList);
        tv1 = (TextView) v.findViewById(R.id.Empty);
        tv1.setVisibility(View.INVISIBLE);
        rv.setHasFixedSize(true);
        lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);


        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setMessage("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setContentView(R.layout.progress_bar);
        new getMyCourses().execute("");

     /*   rv.addOnItemTouchListener(new RecyclerItemListener(getContext(), new RecyclerItemListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent playIntent = new Intent(getContext(), MainActivity.class);
                playIntent.putExtra("playlist", playlists.get(position));
                playIntent.putExtra("topicNameCompleted", topicNames.get(position));
                startActivity(playIntent);

            }
        }));*/


        return v;
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


                    hideSoftKeyboard((Activity) getContext());
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


    private class getMyCourses extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";


        @Override
        protected String doInBackground(String... params) {


          /*  SoapObject request = new SoapObject(NAMESPACE, METHOD);
            request.addProperty("userEmail", userEmail);

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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("getMyCoursesResult");
                    return result.toString();

                }
            }*/


            final String baseUrl = "http://www.youni.co.in/courses/mycourses.php";
            final String QUERY_EMAIL = "email";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIL, userEmail).build();

            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                StringBuilder sb = new StringBuilder();

                if (is == null)
                    return "Network Problem";

                reader = new BufferedReader(new InputStreamReader(is));

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

                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else if (!s.equals("")) {

                ArrayList<String> allDetails = new ArrayList<>(Arrays.asList(s.split("<")));
                if (allDetails.size() < 3) {

                    tv1.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                    dialog.dismiss();

                } else {

                    tv1.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    String topicNamesList = allDetails.get(0);
                    String datesList = allDetails.get(1);
                    String playListsList = allDetails.get(2);

                    topicNames = new ArrayList<>(Arrays.asList(topicNamesList.split(">")));
                    dates = new ArrayList<>(Arrays.asList(datesList.split(">")));
                    playlists = new ArrayList<>(Arrays.asList(playListsList.split(">")));

                    mAdapter = new MyCourseAdapter(getContext(), topicNames, dates, playlists);
                    rv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            } else {

                Toast.makeText(getContext(), "Network Problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }


        }
    }


    public String getDataFromJson(String jsonString) {

        Log.i("JSONSTRING", jsonString);

        String result = "";
        String courseName = "";
        String date = "";
        String playLIstId = "";

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i == 0) {


                    courseName = courseName + jsonObject.getString("course_name");
                    date = date + jsonObject.getString("date");
                    playLIstId = playLIstId + jsonObject.getString("playlistid");


                } else {

                    courseName = courseName + ">" + jsonObject.getString("course_name");
                    date = date + ">" + jsonObject.getString("date");
                    playLIstId = playLIstId + ">" + jsonObject.getString("playlistid");
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        result = courseName + "<" + date + "<" + playLIstId;
        Log.i("Course", result);
        return result;


    }

}
