package com.thefreelancer.youni;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
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


public class FlashActivity extends AppCompatActivity {

    ProgressDialog dialog;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_flash);


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            //Toast.makeText(getApplicationContext(),versionName,Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(FlashActivity.this);
            dialog.setTitle("");
            dialog.setCancelable(false);
            dialog.setMessage("");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            dialog.setContentView(R.layout.progress_bar);
            new CheckVersion().execute("");
            //  Intent goToNext = new Intent(FlashActivity.this,Signin.class);
            //startActivity(goToNext);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    private class CheckVersion extends AsyncTask<String, String, String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {

            final String baseUrl = "http://www.youni.co.in/version/checkversion.php";
            final String QUERY_VERSION = "versioncode";
            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_VERSION, versionName).build();
            try {
                URL myUrl = new URL(baseUri.toString());
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


                if (sb.length() == 0)
                    return "Network Problem";

                res = getVersionJsonData(sb.toString());


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

            if (s.equals("1")) {


                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FlashActivity.this, R.style.AppTheme);
                alertBuilder.setMessage("You need to update the app");
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Update");
                alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String appPackageName = getPackageName();
                        try {
                            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                            startActivity(playStoreIntent);
                            dialog.dismiss();
                        } catch (android.content.ActivityNotFoundException e) {


                            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                            startActivity(playStoreIntent);
                            dialog.dismiss();

                        }


                    }
                });

                alertBuilder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent goToNext = new Intent(FlashActivity.this, Signin.class);
                        startActivity(goToNext);
                        dialog.dismiss();

                    }
                });

                alertBuilder.show();


            } else if (s.equals("2")) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FlashActivity.this, R.style.AppTheme);
                alertBuilder.setMessage("You need to update the app");
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Update");
                alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String appPackageName = getPackageName();
                        try {
                            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                            startActivity(playStoreIntent);
                            dialog.dismiss();

                        } catch (android.content.ActivityNotFoundException e) {


                            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                            startActivity(playStoreIntent);
                            dialog.dismiss();

                        }


                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        dialog.dismiss();


                    }
                });

                alertBuilder.show();

            } else if (s.equals("0")) {

                Intent goToNext = new Intent(FlashActivity.this, ImageSlider.class);
                startActivity(goToNext);
                dialog.dismiss();
            } else {

                Toast.makeText(FlashActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }


        }
    }


    private String getVersionJsonData(String jsonString) {

        Log.i("Version Check", jsonString);


        String result = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("upgradeStatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}
