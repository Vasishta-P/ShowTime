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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.showtime.Async.GetGenresAsync;
import com.example.showtime.Async.GetListNowPlayingAsync;
import com.example.showtime.Async.GetListUpcomingAsync;
import com.example.showtime.Async.GetSearchListAsync;
import com.example.showtime.Database.Movies_database;
import com.example.showtime.entity.MovieItemClass;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//This page displays only the results of the requested movie or similiar movies that share the same name or text
//This page is activated after user useses the search function from the main page
public class search_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton close_naviagation;
    TextView username;
    TextView email;
    User_Details user_info;
    GetSearchListAsync getSearchListAsync;
    static RecyclerView recyclerView;
    static Movies_database moivies_database;
    static MovieAdapter movieAdapter;
    String querystring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.search_nav_view);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            setTitle(R.string.search_results);

            recyclerView = findViewById(R.id.recycler_search);

            querystring = getIntent().getStringExtra("search_query");
            System.out.println("Old query " + querystring);

           /// setUpViewpager(viewPager);
            ///tab.setupWithViewPager(viewPager);

            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            close_naviagation = headerView.findViewById(R.id.close_navigation);

            //Close navigation drawer
            close_naviagation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
            });

            username = headerView.findViewById(R.id.lastnameTxt);
            email = headerView.findViewById(R.id.emailTxt);

            username.setText("Patrick Carey");
            email.setText("pcarey8827@optonline.net");

            //username.setText(user_info.getFirstname() + " "+ user_info.getLastname());
            //email.setText(user_info.getEmail());


            navigationView.setNavigationItemSelectedListener(this);

            moivies_database = new Movies_database(search_activity.this);
            movieAdapter = new MovieAdapter(search_activity.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(search_activity.this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            List<MovieItemClass> movieItemClasses = moivies_database.listMoviesUnderType(5);
            movieAdapter.setMovieResult(movieItemClasses);

            recyclerView.setAdapter(movieAdapter);

            /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
            navUsername.setText("Your Text Here");*/
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                querystring = query;
                System.out.println("Submitted query " + query);
                System.out.println("Submitted query#2 " + querystring);

                //Activate the Asynctask to search for the user-inputted movie from the Movie DB website
                if(getSearchListAsync == null || getSearchListAsync.getStatus() != AsyncTask.Status.RUNNING){
                    getSearchListAsync = new GetSearchListAsync(search_activity.this, query, false);
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

    public static void setNewList()
    {
        List<MovieItemClass> movieItemClasses = moivies_database.listMoviesUnderType(5);
        movieAdapter.setMovieResult(movieItemClasses);
        recyclerView.setAdapter(movieAdapter);
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

    //In case the Async-task failed I implemented a refresh option to allow the user to try again to obtain the information
    private void Refresh(){
        System.out.println("old query " + querystring);
        if(getSearchListAsync == null || getSearchListAsync.getStatus() != AsyncTask.Status.RUNNING){
            getSearchListAsync = new GetSearchListAsync(search_activity.this, querystring, false);
            getSearchListAsync.execute();
        }
    }


    //This will populate the navigation drawer with options to either log off, go to main page, or credit card information
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item_home) {
            Intent i = new Intent(search_activity.this, main_activity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_item_payment_info) {
            //Add code to shut off the services and notifications
            Intent i = new Intent(search_activity.this, credit_card_page.class);
            startActivity(i);
        } /*else if (id == R.id.nav_item_settings) {
            //optional
            Intent i = new Intent(search_activity.this, SettingsActivity.class);
            startActivity(i);
        }*/ else if (id == R.id.nav_item_log_off) {
            //Add code to shut off the services and notifications
            Intent i = new Intent(search_activity.this, LoginActivity.class);
            startActivity(i);
          //  System.exit(1);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
