package com.youni.youni;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnFocusChangeListener {

    RecyclerView rv1;
    RecyclerView.Adapter mAdapter;
    TextView tv1;
    LinearLayoutManager lm;
    String data;
    Product tempProduct;
    ArrayList<Product> searchResults = new ArrayList<>();
    ArrayList<Product> filteredResults  = new ArrayList<>();
    private String textSearch;
    ProgressDialog dialog;
    private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
    private static final String NAMESPACE = "http://viesr.com/";
    private static final String ACTION = "http://viesr.com/getSearchString";
    private static final String METHOD = "getSearchString";
    ArrayList<String> playListIds,topics;
    private SearchView sv;
    ArrayList<String> origPlaylists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SetupUi(findViewById(R.id.searchActivity));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv1 = (RecyclerView) findViewById(R.id.searchList);
        rv1.setHasFixedSize(true);
        lm = new LinearLayoutManager(SearchActivity.this);
        rv1.setLayoutManager(lm);

        tv1 = (TextView) findViewById(R.id.searchHint);
        tv1.setVisibility(View.VISIBLE);
        rv1.setVisibility(View.INVISIBLE);

        rv1.addOnItemTouchListener(new RecyclerItemListener(SearchActivity.this, new RecyclerItemListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent playIntent = new Intent(SearchActivity.this, CourseSpecs.class);
                playIntent.putExtra("playlistid", origPlaylists.get(position));
                playIntent.putExtra("topicName", topics.get(position));
                startActivity(playIntent);

            }
        }));

        Intent receiver = getIntent();
        String queryReceived = receiver.getStringExtra("query");
        textSearch = queryReceived;
        if (!queryReceived.equals("")){
            tv1.setVisibility(View.INVISIBLE);
            rv1.setVisibility(View.VISIBLE);
            dialog = new ProgressDialog(SearchActivity.this);
            dialog.setTitle("Searching...");
            dialog.setCancelable(false);
            dialog.show();
            new getSearchResult().execute(queryReceived);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.searchView);
        sv  = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(item);
        sv.setQueryHint("Search for courses");
        sv.setOnQueryTextListener(this);
        sv.setOnQueryTextFocusChangeListener(this);
        //sv.setBackgroundColor(Color.WHITE);

        sv.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public void filteredProducts(String text){

        String pName;
        filteredResults.clear();
        for (int i = 0; i < searchResults.size(); i++){

            pName = searchResults.get(i).getResult().toLowerCase();
            if (pName.contains(text.toLowerCase())){

                searchResults.get(i).getResult().contains(text);
            }
            filteredResults.add(searchResults.get(i));
        }


    }

    public static void hideSoftKeyboard(Activity activity){

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void SetupUi(View v){

        if (!(v instanceof EditText)){


            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    hideSoftKeyboard(SearchActivity.this);
                    return false;


                }
            });
        }


        if (v instanceof ViewGroup){

            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++){

                View innerView = ((ViewGroup) v).getChildAt(i);
                SetupUi(innerView);
            }
        }

    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        String queryTrimmed = query.trim();
        textSearch = queryTrimmed;
        rv1.setVisibility(View.VISIBLE);
        tv1.setVisibility(View.INVISIBLE);
        searchResults.clear();
        origPlaylists.clear();


        rv1.setVisibility(View.VISIBLE);
        sv.clearFocus();
        dialog = new ProgressDialog(SearchActivity.this);
        dialog.setTitle("Searching...");
        dialog.setCancelable(false);
        dialog.show();
        new getSearchResult().execute(queryTrimmed);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    private class getSearchResult extends AsyncTask<String,String,String> {

        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";


        @Override
        protected String doInBackground(String... params) {

            final String baseUrl = "http://www.youni.co.in/courses/searchcourses.php";
            final String QUERY_SEARCH = "query";
            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_SEARCH, params[0]).build();
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


                if (sb.length() == 0)
                    return "Network Problem";

                res = getSearchDataFromJson(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (request != null )
                    request.disconnect();
                if (reader != null){

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            if (res.equals("Network Problem")) {
                return res;
            } else {


                ArrayList<String> allDetails = new ArrayList<>(Arrays.asList(res.split("<")));
                if (allDetails.size() >= 2) {
                    String topicNames = allDetails.get(0);
                    Log.i("TopicName",topicNames);
                    String playlists = allDetails.get(1);

                    topics = new ArrayList<>(Arrays.asList(topicNames.split(">")));
                    Log.i("TOPICSIZE","" + topics.size());
                    playListIds = new ArrayList<>(Arrays.asList(playlists.split(">")));


                    for (String pl : playListIds) {

                        int index = pl.indexOf("=") + 1;
                        Log.i("playlistid", pl.substring(index));
                        origPlaylists.add(pl.substring(index));


                    }


                    for (int i = 0; i < topics.size(); i++) {

                        tempProduct = new Product();
                        tempProduct.setResult(topics.get(i));

                        String matchFound = "N";

                        for (int j = 0; j < searchResults.size(); j++) {

                            if (searchResults.get(j).getResult().equals(tempProduct.getResult())) {

                                matchFound = "Y";
                            }

                        }


                        if (matchFound.equals("N")) {

                            searchResults.add(tempProduct);

                        }


                    }


                } else {

                    return "No Search Found";
                }


                return "Ok";
            }







        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (s.equals("Network Problem")) {

                Toast.makeText(SearchActivity.this, s, Toast.LENGTH_SHORT).show();


                dialog.dismiss();

            } else if (s.equals("Ok")) {


                filteredProducts(textSearch);
                mAdapter = new SearchAdapter(SearchActivity.this, filteredResults);
                rv1.setAdapter(mAdapter);
                dialog.dismiss();


            } else {

                Toast.makeText(SearchActivity.this, s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }


        }
    }

    public String getSearchDataFromJson(String jsonString) {

        Log.i("JSONSEARCH",jsonString);

        String result = "";
        String topicName = "";
        String playListId = "";
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i == 0) {

                    topicName = topicName + jsonObject.getString("topic_name");
                    playListId = playListId + jsonObject.getString("topic_playlist_id");


                }else {

                    topicName = topicName + ">" +  jsonObject.getString("topic_name");
                    playListId = playListId + ">" +  jsonObject.getString("topic_playlist_id");
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        result = topicName + "<" + playListId;
        Log.i("Resultsearch",result);
        return  result;

    }
}
