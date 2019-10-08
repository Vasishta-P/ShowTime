package com.example.showtime.Async;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.showtime.Database.Movies_database;
import com.example.showtime.R;
import com.example.showtime.ViewPagerAdapter;
import com.example.showtime.entity.Genre;
import com.example.showtime.entity.MovieItemClass;
import com.example.showtime.main_activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//This Async Task will get the list of genre names from the Movie DB website
public class GetGenresAsync extends AsyncTask<String, String, String>{

    private String now_playingurl;
    private Context context;

    public List<MovieItemClass> getMovieList() {
        return movieList;
    }

    private List<MovieItemClass> movieList;
    private String parseString;
    private final static int START_LINE = 1;

    private String json_id;
    private String json_name;
    private String json_genres;

    private String ok = null;
    private String warning = null;
    private String failtoretrieve;
    private String languageapi;
    private String apikey;

    private double popularity;
    Movies_database moivies_database = null;
    ViewPagerAdapter viewPagerAdapter;


    public GetGenresAsync(Context context, ViewPagerAdapter viewPagerAdapter) {
        this.context = context;
        this.viewPagerAdapter = viewPagerAdapter;
    }

    @Override
    protected void onPreExecute() {
        now_playingurl = context.getString(R.string.genre_list_api);

        languageapi = context.getString(R.string.language_api_frag);
        apikey = context.getString(R.string.themoviedbapi_key);

        json_id = context.getString(R.string.json_id);
        json_name = context.getString(R.string.json_name);
        json_genres = context.getString(R.string.json_genres);

        warning = context.getString(R.string.warning);
        ok = context.getString(R.string.ok);
        failtoretrieve = context.getString(R.string.failtoretrieve);

    }


    @Override
    protected String doInBackground(String... args) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {

            //Create the URL
            now_playingurl = now_playingurl + "?" + apikey + languageapi;

            URL url = new URL(now_playingurl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();


            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

           reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
            }



            parseString = buffer.toString();

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            return forecastJsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result){
       ArrayList<Genre> genreList = parseGenreList(parseString);

       //If the genre list is null or equal to zero in size then launch a error message box
        if(genreList == null || genreList.size() == 0){
            AlertDialog.Builder a_builder54 = new AlertDialog.Builder(context);
            a_builder54.setMessage(failtoretrieve)
                    .setCancelable(false)
                    .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

            AlertDialog alert743 = a_builder54.create();
            alert743.setTitle(warning);
            alert743.show();
        }
        else{

            //Save genre list to database
            moivies_database = new Movies_database(context.getApplicationContext());
            moivies_database.insertGenreList(genreList);


            //Since the
            //I needed a way to populate the fragments AFTER all the asynctask were finished running
            main_activity.setUpAsync();

        }
    }

    //Parse the Json string and convert it to an arraylist
    public ArrayList<Genre> parseGenreList(String parseString){

        ArrayList<Genre> genreClasses = null;

        try {

            genreClasses = new ArrayList<>();

            JSONObject jObj = new JSONObject(parseString);
            JSONArray jr = jObj.getJSONArray(json_genres);

           // JSONArray jsonarray = new JSONArray(parseString);
            for (int i = 0; i < jr.length(); i++) {
                JSONObject jsonObject = jr.getJSONObject(i);;

                Genre genre = new Genre();

                genre.setId(jsonObject.getLong(json_id));
                genre.setName(jsonObject.getString(json_name));

                genreClasses.add(genre);

                genre = null;
                jsonObject = null;

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return genreClasses;
    }


}
