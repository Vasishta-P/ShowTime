package com.example.showtime.Async;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.showtime.Database.Movies_database;
import com.example.showtime.R;
import com.example.showtime.entity.MovieItemClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This Async-task will get all movies that are listed as Top-rated from the movie DB
public class GetTopRatedListAsync extends AsyncTask<String, String, String>{

    private String now_playingurl;
    private Context context;

    public List<MovieItemClass> getMovieList() {
        return movieList;
    }

    private List<MovieItemClass> movieList;
    private String parseString;
    private final static int START_LINE = 1;

    private String json_id;
    private String json_title;
    private String json_releaseDate;
    private String json_backdropUrl;
    private String json_posterUrl;
    private String json_overview;
    private String json_adult;
    private String json_voteAverage;
    private String json_voteCount;
    private String json_genreId;
    private String json_language;
    private String json_popularity;

    private String json_results;

    private String ok = null;
    private String warning = null;
    private String failtoretrieve;

    private double popularity;
    Movies_database moivies_database = null;

    public GetTopRatedListAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        now_playingurl = context.getString(R.string.top_rated_url);

        json_id = context.getString(R.string.json_id);
        json_title = context.getString(R.string.json_original_title);
        json_releaseDate = context.getString(R.string.json_release_date);
        json_backdropUrl = context.getString(R.string.json_backdrop_path);
        json_posterUrl = context.getString(R.string.json_poster_path);
        json_overview = context.getString(R.string.json_overview);
        json_adult = context.getString(R.string.json_adult);
        json_voteAverage = context.getString(R.string.json_vote_average);
        json_voteCount = context.getString(R.string.json_vote_count);
        json_genreId = context.getString(R.string.json_genre_ids);
        json_language = context.getString(R.string.json_original_language);
        json_popularity = context.getString(R.string.json_poupularity);

        json_results = context.getString(R.string.json_results);

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
           // reader = new BufferedReader(new InputStreamReader(inputStream));

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
            //parseString = buffer.toString();
            return forecastJsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result){
       ArrayList<MovieItemClass> movieList = parseMovieList(parseString);

        //If the list is null or count is zero then display error messagebox to the user
        if(movieList == null || movieList.size() == 0){
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

            //Save the list to the database
            moivies_database = new Movies_database(context.getApplicationContext());
            moivies_database.insertMovieList(movieList, 2);
        }
    }

    //This will parse the json string and convert it to an Arraylist of movies
    public ArrayList<MovieItemClass> parseMovieList(String parseString){

        ArrayList<MovieItemClass> movieItemClasses = null;

        try {

            movieItemClasses = new ArrayList<>();

            JSONObject jObj = new JSONObject(parseString);
            JSONArray jr = jObj.getJSONArray(json_results);

            for (int i = 0; i < jr.length(); i++) {
                JSONObject jsonObject = jr.getJSONObject(i);;

                MovieItemClass movie = new MovieItemClass();

                movie.setId(jsonObject.getLong(json_id));
                movie.setTitle(jsonObject.getString(json_title));
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString(json_releaseDate));
                movie.setReleaseDate(date1);
                date1 = null;
                movie.setAdult(jsonObject.getBoolean(json_adult));
                movie.setBackdropUrl(jsonObject.getString(json_backdropUrl));
                movie.setPosterUrl(jsonObject.getString(json_posterUrl));
                movie.setVoteAverage(BigDecimal.valueOf(jsonObject.getDouble(json_voteAverage)).floatValue());
                movie.setVoteCount(jsonObject.getLong(json_voteCount));
                movie.setOverview(jsonObject.getString(json_overview));
                movie.setPopularity(jsonObject.getDouble(json_popularity));
                movie.setLanguage(jsonObject.getString(json_language));
                ArrayList<Long> genreIdList = new ArrayList<>();

                JSONArray temp = jsonObject.getJSONArray(json_genreId);
                System.out.println("Json Genre " + temp.length());

                for(int d = 0; d < temp.length(); d++) {
                    if(!temp.getString(d).isEmpty()) {
                        String jsonstring = temp.getString(d);
                        genreIdList.add(Long.parseLong(jsonstring));
                    }
                }

                movie.setGenreId(genreIdList);
                System.out.println("genre list size " + genreIdList.size());

                genreIdList = null;
                movieItemClasses.add(movie);

                System.out.println("Size: " + movieItemClasses.size());

                movie = null;
                jsonObject = null;

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return movieItemClasses;
    }


}
