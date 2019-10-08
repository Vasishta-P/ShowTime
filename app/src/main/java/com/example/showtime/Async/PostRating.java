package com.example.showtime.Async;

import android.content.Context;
import android.os.AsyncTask;

import com.example.showtime.R;
import com.example.showtime.entity.MovieItemClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//This async-task was meant to post the rating to the movie db server
public class PostRating extends AsyncTask<String, Void, String> {

    private Context context;
    float rating = 0.0f;
    String baseurl;
    String api_key;
    MovieItemClass movieItemClass;


    public PostRating(Context context, float rating, MovieItemClass movieItemClass) {
        this.context = context;
        this.rating = rating;
    }

    @Override
    protected void onPreExecute() {
        baseurl = context.getString(R.string.themoviedbapi_baseurl);
        api_key = context.getString(R.string.themoviedbapi_key);
    }



        @Override
        protected String doInBackground(String... params){
            String stringUrl = baseurl + movieItemClass.getId() + "/rating?"  + api_key;
            String result;
            String inputLine;

            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                //Htt
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                //connection.set
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
}
