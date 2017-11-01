package com.youni.youni;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class Courses extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, View.OnFocusChangeListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    android.support.v4.app.FragmentManager mfragmentManager;
    FragmentTransaction mfragmentTransaction;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String name,email;
    TextView tv1,tv2;
    SearchView sv;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView1);
        View header =mNavigationView.getHeaderView(0);
      //  mNavigationView.addHeaderView(header);

        tv1 = (TextView) header.findViewById(R.id.textViewHeader);
        tv2 = (TextView) header.findViewById(R.id.email);

        prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
        email = prefs.getString(Signin.USER, "myemail");
        name = prefs.getString(Signin.NAME, "myName");


        tv2.setText(email);
        tv1.setText(name);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mfragmentManager = getSupportFragmentManager();
        mfragmentTransaction = mfragmentManager.beginTransaction();
        mfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                mDrawerLayout.closeDrawers();

                if (item.getItemId() == R.id.logout) {

                    prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
                    editor = prefs.edit();
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(Courses.this, Signin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();



                } else if (item.getItemId() == R.id.resume) {

                    Intent resumeIntent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(resumeIntent);


                } else if (item.getItemId() == R.id.home) {

                    android.support.v4.app.FragmentTransaction fragmentTrans = mfragmentManager.beginTransaction();
                    fragmentTrans.replace(R.id.containerView, new TabFragment()).commit();
                } else if (item.getItemId() == R.id.editProfile) {

                    Intent editIntent = new Intent(getApplicationContext(), EditProfile.class);
                    startActivity(editIntent);
                }

                return false;
            }
        });







        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);


         //   getSupportFragmentManager().beginTransaction().replace(R.id.container2,new SearchFragment().newInstance()).commit();


    }


    @Override
    public void onRefresh() {
        android.support.v4.app.FragmentTransaction fragmentTrans = mfragmentManager.beginTransaction();
        fragmentTrans.replace(R.id.containerView, new TabFragment()).commit();
        swipeRefreshLayout.setRefreshing(false);
    }


    boolean doubleBackToExit = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed();
            return;
        }

        doubleBackToExit = true;
        Toast.makeText(Courses.this,"Press Again To Exit",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                doubleBackToExit = false;
            }
        }, 2000);
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

      /*  if (id == R.id.action_settings){

            prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(Courses.this, Signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }*/

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        String queryTrimmed = query.trim();
        Intent intent = new Intent(Courses.this,SearchActivity.class);
        intent.putExtra("query",queryTrimmed);
        startActivity(intent);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
