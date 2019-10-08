package com.example.showtime.Async;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.example.showtime.Database.Movies_database;
import com.example.showtime.MovieDetailControls;
import com.example.showtime.R;
import com.example.showtime.entity.MovieItemClass;
import com.squareup.picasso.Picasso;

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


//This async is used to obtain the details for a single movie after getting the passed in Movie ID
public class GetMovieDetailsAsync extends AsyncTask<String, String, String> {

    private String now_playingurl;
    private Context context;

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

    private String ok = null;
    private String warning = null;
    private String failtoretrieve;
    private String api;
    private String start;
    String basePosterurl;

    private double popularity;
    Movies_database moivies_database = null;
    Long movieid;
    MovieItemClass movieItemClass;
    MovieDetailControls movieDetailControls1;
    Drawable drawable;
    String moviesummary;
    String movietitle;

    public GetMovieDetailsAsync(Context context, Long movie_id, MovieDetailControls movieDetailControls) {
        this.context = context;
        movieid = movie_id;
        movieDetailControls1 = movieDetailControls;
    }

    @Override
    protected void onPreExecute() {
        api = context.getString(R.string.themoviedbapi_key);
        start = context.getString(R.string.themoviedbapi_baseurl);

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

        drawable = context.getDrawable(R.drawable.ic_movie_purple_black_24dp);

        warning = context.getString(R.string.warning);
        ok = context.getString(R.string.ok);
        failtoretrieve = context.getString(R.string.failtoretrieve);
        basePosterurl = context.getString(R.string.base_poster_url);

    }


    @Override
    protected String doInBackground(String... args) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            String make_url = start + movieid + "?" + api;

            URL url = new URL(make_url);
            // Create the request to OpenWeatherMap, and open the connection
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
        } finally {
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
    protected void onPostExecute(String result) {
        //ArrayList<MovieItemClass> movieList = parseGenreList(parseString);

        if (movieItemClass == null) {
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
        } else {

            movieDetailControls1.getSummary().setText(moviesummary);
            movieDetailControls1.getTitle().setText(movietitle);

            Picasso.get()
                    .load(basePosterurl + movieItemClass.getPosterUrl())
                    .placeholder(drawable)
                    .error(drawable)
                    .into(movieDetailControls1.getPoster());
        }
    }


    public MovieItemClass parseMovieList(String parseString) {

        try {


            JSONObject jsonObject = new JSONObject(parseString);

            MovieItemClass movie = new MovieItemClass();

            movie.setId(jsonObject.getLong(json_id));
            movie.setTitle(jsonObject.getString(json_title));
            //Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(jsonObject.getString(json_releaseDate));
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

            for (int d = 0; d < temp.length(); d++) {
                String jsonstring = temp.getString(d);
                genreIdList.add(Long.parseLong(jsonstring));
                //}
            }

            movie.setGenreId(genreIdList);
            System.out.println("genre list size " + genreIdList.size());

            genreIdList = null;


            movie = null;
            jsonObject = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieItemClass;
    }


}

