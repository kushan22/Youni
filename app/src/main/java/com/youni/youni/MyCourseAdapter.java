package com.youni.youni;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

/**
 * Created by Kushan on 07-03-2016.
 */
public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.ViewHolderAdapter> {

    ArrayList<String> topicNames, dates, playlists;
    MyCourseAdapter adapter;
    Context context;
    private int lastPosition = -1;
    private static final String URL1 = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE1 = "http://viesr.com/";
    private static final String ACTION1 = "http://viesr.com/removeCourse";
    private static final String METHOD1 = "removeCourse";
    String playlist, user;
    ProgressDialog dialog1;


    public MyCourseAdapter(Context context, ArrayList<String> topicNames, ArrayList<String> dates, ArrayList<String> playlists) {

        this.context = context;
        this.topicNames = topicNames;
        this.playlists = playlists;
        this.dates = dates;
        this.adapter = this;

    }

    @Override
    public ViewHolderAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_mycourse, parent, false);

        ViewHolderAdapter vha = new ViewHolderAdapter(v);

        return vha;
    }

    @Override
    public void onBindViewHolder(ViewHolderAdapter holder, final int position) {


        holder.tv1.setText(topicNames.get(position));
        holder.tv2.setText(dates.get(position));

        Animation animate = AnimationUtils.loadAnimation(holder.itemView.getContext(), position > lastPosition ? R.anim.animate : R.anim.animate1);
        holder.itemView.startAnimation(animate);
        lastPosition = position;


       /* holder.bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(v.getContext(), MainActivity.class);
                playIntent.putExtra("playlist", playlists.get(position));
                playIntent.putExtra("topicNameCompleted", topicNames.get(position));
                v.getContext().startActivity(playIntent);

            }
        });*/

       /* holder.bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userprefs = v.getContext().getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                user = userprefs.getString(Signin.USER, "email");
                topic = topicNames.get(position);

                new removeCourses().execute("");
            }
        });*/

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent playIntent = new Intent(context, MainActivity.class);
                playIntent.putExtra("playlist", playlists.get(position));
                playIntent.putExtra("topicNameCompleted", topicNames.get(position));
                context.startActivity(playIntent);
            }
        });


        holder.ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dialog dialog = new Dialog(context);


                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                Button bt = (Button) dialog.findViewById(R.id.buttonUnenroll);
                dialog.show();
                dialog.getWindow().setLayout(500, 150);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences userprefs = v.getContext().getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                        user = userprefs.getString(Signin.USER, "email");
                        playlist = playlists.get(position);

                        dialog1 = new ProgressDialog(context);
                        dialog1.setTitle("");
                        dialog1.setCancelable(false);
                        dialog1.setMessage("");
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog1.show();
                        dialog1.setContentView(R.layout.progress_bar);

                        new removeCourses().execute("");

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return topicNames.size();
    }

    class ViewHolderAdapter extends RecyclerView.ViewHolder {

        TextView tv1, tv2;
        ImageButton ib1;
        LinearLayout linearLayout;


        public ViewHolderAdapter(View itemView) {
            super(itemView);

            tv1 = (TextView) itemView.findViewById(R.id.completedtopic);
            tv2 = (TextView) itemView.findViewById(R.id.date);
            ib1 = (ImageButton) itemView.findViewById(R.id.openDialog);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.watch_left);


        }
    }

    private class removeCourses extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {

          /*  SoapObject request = new SoapObject(NAMESPACE1, METHOD1);
            request.addProperty("username", user);
            request.addProperty("topicName", topic);

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
            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_EMAIL, user).
                    appendQueryParameter(QUERY_PLAYLISTID, playlist).build();

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

                // Toast.makeText(CourseSpecs.this, s, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                dialog1.dismiss();

                //dialog2.dismiss();
            } else if (s.equals("200")) {
                //bt1.setVisibility(View.VISIBLE);
                //bt2.setVisibility(View.INVISIBLE);

                Intent intent = ((Activity) context).getIntent();
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((Activity) context).finish();
                ((Activity)context).startActivity(intent);
                Toast.makeText(context, "Course Unenrolled", Toast.LENGTH_SHORT).show();
                dialog1.dismiss();


//                Toast.makeText(CourseSpecs.this, s, Toast.LENGTH_SHORT).show();
                //              Intent intent = new Intent(CourseSpecs.this,Courses.class);
                //            startActivity(intent);
                //          dialog2.dismiss();
            }else if (s.equals("404")){
                Toast.makeText(context,"Already Unjoined",Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
            }else {

                Toast.makeText(context,"Network Problem",Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
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

}
