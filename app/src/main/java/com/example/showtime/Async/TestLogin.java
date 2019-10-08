package com.example.showtime.Async;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.showtime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class TestLogin extends AsyncTask<Void, Void, Boolean> {

    String urlString = "http://www.yoursite.com/";

    private final String TAG = "post json example";
    private Context context;
    private String apikey;
    private String token_request_url_frag;
    String parseString;
    String token;
    String username;
    String password;

    public TestLogin(Context contex, String username, String password) {

        this.context = contex;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {
        apikey = context.getString(R.string.themoviedbapi_key);
        token_request_url_frag = context.getString(R.string.token_api_new);

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean status = false;

        String response = "";
      token = createRequestToken();

        if(token != null) {
            try {
                response = performPostCall(urlString, new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("Accept", "application/json");
                        put("Content-Type", "application/json");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error ...");
            }
            Log.e(TAG, "5 - after Response...");

            if (!response.equalsIgnoreCase("")) {
                try {
                    Log.e(TAG, "6 - response !empty...");
                    //
                    JSONObject jRoot = new JSONObject(response);
                    JSONObject d = jRoot.getJSONObject("d");

                    int ResultType = d.getInt("ResultType");
                    Log.e("ResultType", ResultType + "");

                    if (ResultType == 1) {

                        status = true;

                    }

                } catch (JSONException e) {
                    // displayLoding(false);
                    // e.printStackTrace();
                    Log.e(TAG, "Error " + e.getMessage());
                } finally {

                }
            } else {
                Log.e(TAG, "6 - response is empty...");

                status = false;
            }
        }

        return status;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //
        Log.e(TAG, "7 - onPostExecute ...");

        if (result) {
            Log.e(TAG, "8 - Update UI ...");

            // setUpdateUI(adv);
        } else {
            Log.e(TAG, "8 - Finish ...");

            // displayLoding(false);
            // finish();
        }

    }

    public String performPostCall(String requestURL,
                                  HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");

            Log.e(TAG, "11 - url : " + requestURL);

            /*
             * JSON
             */

            JSONObject root = new JSONObject();
            //
            //String token = Static.getPrefsToken(context);

            root.put("username", username);
            root.put("password", password);
            root.put("request_token", token);


            Log.e(TAG, "12 - root : " + root.toString());

            String str = root.toString();
            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);

            int responseCode = conn.getResponseCode();

            Log.e(TAG, "13 - responseCode : " + responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "14 - HTTP_OK");

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.e(TAG, "14 - False - HTTP_OK");
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String createRequestToken(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {

            token_request_url_frag = token_request_url_frag + "?" + apikey;

            URL url = new URL(token_request_url_frag);
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
}