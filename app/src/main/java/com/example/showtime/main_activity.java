package com.example.showtime;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.showtime.Async.GetGenresAsync;
import com.example.showtime.Async.GetListNowPlayingAsync;
import com.example.showtime.Async.GetListUpcomingAsync;
import com.example.showtime.Async.GetPopularListAsync;
import com.example.showtime.Async.GetSearchListAsync;
import com.example.showtime.Async.GetTopRatedListAsync;
import com.example.showtime.Database.Movies_database;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class main_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton close_naviagation;
    TextView username;
    TextView email;
    User_Details user_info;
    static TabLayout tab;
    static ViewPager viewPager;
    GetListNowPlayingAsync getListNowPlayingAsync;
    GetListUpcomingAsync getListUpcomingAsync;

    GetPopularListAsync getPopularListAsync;
    GetTopRatedListAsync getTopRatedListAsync;

    GetGenresAsync getGenresAsync;

    GetSearchListAsync getSearchListAsync;

    static NowPlayingFragment nowPlayingFragment;
    Movies_database movies_database;
    static ViewPagerAdapter adapter;
    static String nowplaying;
    static String upcoming;

    static String top_rated;
    static String popular;
    static String latest;
    String name;
    String email_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.main_nav_view);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            tab = (TabLayout) findViewById(R.id.tabs);
            viewPager = (ViewPager) findViewById(R.id.viewpager);

            nowplaying = getString(R.string.now_playing);
            upcoming = getString(R.string.upcoming);

            top_rated = getString(R.string.top_rated);
            popular = getString(R.string.popular);
            latest = getString(R.string.latest);


            //These asyncs below get all available movie lists
            getListUpcomingAsync = new GetListUpcomingAsync(main_activity.this);
            getListUpcomingAsync.execute();

            getPopularListAsync = new GetPopularListAsync(main_activity.this);
             getPopularListAsync.execute();

             getTopRatedListAsync = new GetTopRatedListAsync(main_activity.this);
             getTopRatedListAsync.execute();

           getListNowPlayingAsync = new GetListNowPlayingAsync(main_activity.this);
           getListNowPlayingAsync.execute();

            adapter = new ViewPagerAdapter(getSupportFragmentManager());


            //This gets the genres names for all movies
            //Genre ids are integers/longs used to get the names for each genre
            //My guess is the creators of "The Movie DB" used integers because they are easier & quicker to compare than a whole string
           getGenresAsync = new GetGenresAsync(main_activity.this, adapter);
           getGenresAsync.execute();


            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            close_naviagation = headerView.findViewById(R.id.close_navigation);

            //Closes the navigation drawer
            close_naviagation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
            });

           // email_field = getIntent().getStringExtra("email", "pcarey8827@optonline.net");
            //i.putExtra("name", "Patrick Carey");

            username = headerView.findViewById(R.id.lastnameTxt);
            email = headerView.findViewById(R.id.emailTxt);

            username.setText("Patrick Carey");
            email.setText("pcarey8827@optonline.net");

            navigationView.setNavigationItemSelectedListener(this);

        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);


        //This creates the search bar area in the toolbar itself
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //Activate the Asynctask to search for the user-inputted movie from the Movie DB website
                if(getSearchListAsync == null || getSearchListAsync.getStatus() != AsyncTask.Status.RUNNING){
                    getSearchListAsync = new GetSearchListAsync(main_activity.this, query, true);
                    getSearchListAsync.execute();
                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //This creates the pop down menu for the 3 small vertical dots on the top right corner
        switch (item.getItemId()) {

            case R.id.refresh: {
                Refresh();
                break;
            }

            case R.id.item_close:{
                closeOptionsMenu();
                break;
            }

        }

        return true;
    }


    //This will populate the navigation drawer with options to either log off, go to main page, or credit card information
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item_home) {

        } else if (id == R.id.nav_item_payment_info) {
            //Add code to shut off the services and notifications

            Intent i = new Intent(main_activity.this, credit_card_page.class);
            startActivity(i);
        }

        else if (id == R.id.nav_item_log_off) {
            //Add code to shut off the services and notifications
            Intent i = new Intent(main_activity.this, LoginActivity.class);
            startActivity(i);
           // System.exit(1);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Populate the view pager with all the available fragments
    static void setUpViewpager(ViewPager viewPager) {
        nowPlayingFragment = new NowPlayingFragment();

        adapter.populateFragment(nowPlayingFragment, nowplaying);
        adapter.populateFragment(new UpcomingFragment(), upcoming);
       // adapter.populateFragment(new LatestFragment(), "Latest");
        adapter.populateFragment(new TopRatedFragment(), top_rated);
        adapter.populateFragment(new PopularFragment(), popular);
        viewPager.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        tab.setupWithViewPager(viewPager);
    }

    public static void setUpAsync(){
        setUpViewpager(viewPager);
        tab.setupWithViewPager(viewPager);
    }

    //In case one of the Async-tasks failed I implemented a refresh option to allow the user to try again to obtain the information
    private void Refresh() {
        getListUpcomingAsync = new GetListUpcomingAsync(main_activity.this);
        getListUpcomingAsync.execute();

        // getLatestListAsync = new GetLatestListAsync(main_activity.this);
        //getLatestListAsync.execute();

        if (getPopularListAsync == null || getPopularListAsync.getStatus() != AsyncTask.Status.RUNNING) {
            getPopularListAsync = new GetPopularListAsync(main_activity.this);
            getPopularListAsync.execute();
        }

        if (getTopRatedListAsync == null || getTopRatedListAsync.getStatus() != AsyncTask.Status.RUNNING) {
            getTopRatedListAsync = new GetTopRatedListAsync(main_activity.this);
            getTopRatedListAsync.execute();
        }

        if (getListNowPlayingAsync == null || getListNowPlayingAsync.getStatus() != AsyncTask.Status.RUNNING) {
            getListNowPlayingAsync = new GetListNowPlayingAsync(main_activity.this);
            getListNowPlayingAsync.execute();
        }

        if(getGenresAsync == null || getGenresAsync.getStatus() != AsyncTask.Status.RUNNING) {
        getGenresAsync = new GetGenresAsync(main_activity.this, adapter);
        getGenresAsync.execute();
        }

    }


}
