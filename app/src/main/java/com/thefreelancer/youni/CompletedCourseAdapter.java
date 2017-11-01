package com.thefreelancer.youni;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kushan on 08-03-2016.
 */
public class CompletedCourseAdapter extends RecyclerView.Adapter<CompletedCourseAdapter.ViewHolderAdapter> {

    Context context;
    ArrayList<String> completedCourses;

    private static final char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private String username;
    private String link;
    private String useremail;
    private String coursename;
    private String certificateJsonString;
    ProgressDialog dialog;
    SharedPreferences mapPref;
    SharedPreferences.Editor mapEditor;
    private static final String MAP_PREF = "mapPref";
    private static final String MAP_KEY = "mapkey";
    Bitmap bmp = null;

    int pos = 0;
    ViewHolderAdapter mHolder;
    private String result;

    public static CallbackManager callbackManager;
    LoginManager loginManager;
    private String status;


    public CompletedCourseAdapter(Context context, ArrayList<String> completedCourses) {


        this.completedCourses = completedCourses;
        this.context = context;
        //   FacebookSdk.sdkInitialize(context);
        // Toast.makeText(context,"size" + this.completedCourses.size(),Toast.LENGTH_SHORT).show();
        FacebookSdk.sdkInitialize(context);
        callbackManager = CallbackManager.Factory.create();


    }

    @Override
    public ViewHolderAdapter onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.complete_course_card, parent, false);

        ViewHolderAdapter vha = new ViewHolderAdapter(v);


        return vha;
    }

    @Override
    public void onBindViewHolder(final ViewHolderAdapter holder, final int position) {


        holder.tv1.setText(completedCourses.get(position));


        this.mHolder = holder;


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos = position;
                SharedPreferences prefs = v.getContext().getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                username = prefs.getString(Signin.NAME, "username");
                useremail = prefs.getString(Signin.USER, "useremail");
                coursename = completedCourses.get(position);
                dialog = new ProgressDialog(context);
                dialog.setTitle("");
                dialog.setCancelable(false);
                dialog.setMessage("");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                dialog.setContentView(R.layout.progress_bar);

                new generateCertificate().execute("");

            }
        });


    }

    @Override
    public int getItemCount() {
        return completedCourses.size();
    }

    class ViewHolderAdapter extends RecyclerView.ViewHolder {


        TextView tv1, tv2;
        LinearLayout linearLayout;

        public ViewHolderAdapter(View itemView) {
            super(itemView);


            tv1 = (TextView) itemView.findViewById(R.id.completedCourse);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.viewCertificate);

            //  bt2 = (Button) itemView.findViewById(R.id.Certificate);


        }
    }


   /* public static String randomString(char[] characterSet,int length){


        Random rand = new SecureRandom();

        char[] result = new char[length];
        for (int i = 0; i < result.length; i++){

            int randomCharIndex = rand.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }


        return new String(result);


    }*/


    private class generateCertificate extends AsyncTask<String, String, Bitmap> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        URL imageUrl = null;


        @Override
        protected Bitmap doInBackground(String... params) {

            final String baseUrl = "http://youni.co.in/Certificate/watermark.php";
            final String queryUserName = "username";
            final String queryEmail = "email";
            final String queryCourseName = "coursename";

            Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(queryUserName, username)
                    .appendQueryParameter(queryEmail, useremail)
                    .appendQueryParameter(queryCourseName, coursename).build();

            Log.i("uri", builtUri.toString());


            try {
                URL myurl = new URL(builtUri.toString());
                request = (HttpURLConnection) myurl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                InputStream is = request.getInputStream();
                StringBuffer sb = new StringBuffer();

                if (is == null) {

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");
                }


                if (sb.length() == 0) {

                    return null;

                }


                certificateJsonString = sb.toString();


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

            result = getDataFromJson(certificateJsonString);

            try {
                imageUrl = new URL(result);
                Log.i("url", imageUrl.toString());


                is = imageUrl.openConnection().getInputStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            bmp = BitmapFactory.decodeStream(is);


            return bmp;

            // ArrayList<String> allDetails = new ArrayList<>(Arrays.asList(result.split(",")));
            //status = allDetails.get(1);
            //String certiurllink = allDetails.get(0);

           /* if (status.equals("0")){

                bmp = null;
                return bmp;


            }else if (status.equals("1")){


            }else if (status.equals("2")){

                bmp = null;
                return bmp;
            }


            return null; */
        }

        @Override
        protected void onPostExecute(final Bitmap bmp) {


            if (bmp != null) {
                dialog.dismiss();


                final Dialog imageDialog = new Dialog(context);
                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imageDialog.setContentView(R.layout.custom_certi);

                ImageView iv1 = (ImageView) imageDialog.findViewById(R.id.certiImage);
                Button bt1 = (Button) imageDialog.findViewById(R.id.saveToGallery);
                Button bt2 = (Button) imageDialog.findViewById(R.id.gotoLink);
                Button bt3 = (Button) imageDialog.findViewById(R.id.shareonfacebook);
                iv1.setImageBitmap(bmp);
                imageDialog.show();

                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String imageName = "Certi" + System.currentTimeMillis();
                        boolean imageSaved = false;

                        if (bmp != null) {

                            File storagePath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("Youni")));
                            if (!storagePath.exists())
                                storagePath.mkdirs();

                            FileOutputStream fos = null;


                            File imageFile = new File(storagePath, String.format("%s.jpg", imageName));
                            try {
                                fos = new FileOutputStream(imageFile);
                                imageSaved = bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                if (fos != null) {

                                    try {
                                        fos.flush();
                                        fos.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            ContentValues cv = new ContentValues();
                            cv.put(MediaStore.Images.Media.TITLE, imageName);
                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            cv.put("_data", imageFile.getAbsolutePath());
                            cv.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                            cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                            cv.put(MediaStore.Images.ImageColumns.BUCKET_ID, imageFile.toString().toLowerCase(Locale.US).hashCode());
                            cv.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getName().toLowerCase(Locale.US));
                            v.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                            Uri contentUri = Uri.fromFile(storagePath);
                            mediaScanIntent.setData(contentUri);
                            v.getContext().sendBroadcast(mediaScanIntent);


                        }

                        if (imageSaved == true) {

                            Toast.makeText(v.getContext(), "Certificate Saved Successfully in youni directory", Toast.LENGTH_SHORT).show();


                        } else {

                            Toast.makeText(v.getContext(), "Could not save image", Toast.LENGTH_SHORT).show();


                        }
                    }
                });

                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (result != null) {

                            Uri uri = Uri.parse(result);
                            Intent gotoIntent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(gotoIntent);
                        }

                    }
                });
                bt3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> permissionNeeds = Arrays.asList("publish_actions");
                        loginManager = LoginManager.getInstance();
                        loginManager.logInWithPublishPermissions((Activity) context, permissionNeeds);
                        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                sharePhotoToFacebook();

                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();


                            }
                        });

                    }
                });
            } else {

                Toast.makeText(context, "Network Problem", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }


    public String getDataFromJson(String jsonString) {

        Log.i("json", jsonString);

        String result = "http://youni.co.in/certificate/";


        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String certi_link = jsonObject.getString("certi_url");


            if (certi_link.subSequence(0, 3).equals("www")) {

                result = certi_link;
            } else {
                result = result + certi_link;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }

    public void sharePhotoToFacebook() {

        SharePhoto photo = new SharePhoto.Builder().setBitmap(bmp).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareApi.share(content, null);


    }










 /*   private class getCertiLink extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAMESPACE,METHOD);
            request.addProperty("useremail",useremail);
            request.addProperty("topicName", coursename);


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

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("checkLinkResult");
                    return result.toString();
                }

            }



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (s.equals("No Internet Connection")){

                Toast.makeText(context,s,Toast.LENGTH_SHORT).show();

            }else {

                if (s.equals("No Link Found")){
                    String randomString = randomString(CHARSET_AZ_09,6);
                    link = "http://www.youni.co.in/profile/" + randomString;
                    Toast.makeText(context, randomString,Toast.LENGTH_SHORT).show();
                    dialog = new ProgressDialog(context);
                    dialog.setTitle("");
                    dialog.setCancelable(false);
                    dialog.setMessage("");
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    dialog.setContentView(R.layout.progress_bar);
                    new generateCertificate().execute("");

                }else {

                    Toast.makeText(context,"You have already generated your certificate. Press view certificate to see your certificate",Toast.LENGTH_SHORT).show();
                }

            }

        }
    }*/
}
