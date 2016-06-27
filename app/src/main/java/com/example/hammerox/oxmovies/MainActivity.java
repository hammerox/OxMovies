package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "YOUR_API_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchMovieList().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movielist, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                new FetchMovieList().execute();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieListJson = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie");

                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                String sortOrderString = prefs.getString(
                        getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_sort_order_default));
                int sortOrder = Integer.valueOf(sortOrderString);
                switch (sortOrder) {
                    case 0:
                        builder.appendPath("top_rated");
                        break;
                    case 1:
                        builder.appendPath("popular");
                        break;
                }

                builder.appendQueryParameter("api_key", API_KEY);

                URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieListJson = buffer.toString();

                Log.d("JsonString", movieListJson);

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

            return movieListJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {

                try {
                    JSONArray results = getListFromJson(s);
                    int size = results.length();

                    for (int i = 0; i < 1; i++) {
                        String moviePoster = results.getJSONObject(i).getString("poster_path");
                        Log.d("movie", moviePoster);

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("image.tmdb.org")
                                .appendPath("t")
                                .appendPath("p")
                                .appendPath("w185")
                                .appendPath(moviePoster.replace("/", ""));

                        Picasso.with(getApplicationContext()).load(builder.build());
                    }

                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }

        }
    }


    public JSONArray getListFromJson(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        return jsonObject.getJSONArray("results");
    }

}
