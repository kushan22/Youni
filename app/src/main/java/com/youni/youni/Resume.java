package com.youni.youni;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Resume extends Fragment {
    RecyclerView rv1;
    TextView  tv3;
    ProgressDialog dialog;
    CircleImageView cv1;
    String userName;

    RecyclerView.Adapter mAdapter;
    LinearLayoutManager lm;
    private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/getCompletedCourse";
    private static final String METHOD = "getCompletedCourse";



    public Resume() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        View v  = LayoutInflater.from(getContext()).inflate(R.layout.fragment_resume,container,false);

        SharedPreferences prefs = getContext().getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        String Name = prefs.getString(Signin.NAME, "Kushan Singh");
        userName = prefs.getString(Signin.USER, "user");
        rv1 = (RecyclerView) v.findViewById(R.id.completedCourseList);
  //      tv1 = (TextView) v.findViewById(R.id.profileName);
//        tv1.setText(Name);
    //    tv2 = (TextView) v.findViewById(R.id.numberofcourses);
        tv3 = (TextView) v.findViewById(R.id.courseNotCompleted);

        tv3.setVisibility(View.INVISIBLE);



        rv1.setHasFixedSize(true);
        lm = new LinearLayoutManager(getContext());
        rv1.setLayoutManager(lm);


        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setMessage("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setContentView(R.layout.progress_bar);
        new getCompletedCourseDetails().execute("");




        return v;
    }
    private class getCompletedCourseDetails extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD);
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
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("No Internet Connection")) {

                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (s.equals("Nothing Returned")) {

                tv3.setVisibility(View.VISIBLE);
                rv1.setVisibility(View.INVISIBLE);
                dialog.dismiss();

            } else {

                ArrayList<String> completedCourses = new ArrayList<>(Arrays.asList(s.split(",")));
                if (completedCourses.size() > 0) {
                    rv1.setVisibility(View.VISIBLE);
                    mAdapter = new CompletedCourseAdapter(getContext(), completedCourses);
                    rv1.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    //tv2.setText("" + completedCourses.size());
                } else {

                    tv3.setVisibility(View.VISIBLE);
                    rv1.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }


            }
        }
    }
}
