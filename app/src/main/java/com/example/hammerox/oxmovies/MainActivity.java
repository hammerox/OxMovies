package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "YOUR_API_KEY_HERE";

    private GridView gridView = null;
    private ImageAdapter imageAdapter = null;
    private List<String> IDList = null;
    private List<String> posterList = null;
    private int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPosterDimensions();

        gridView = (GridView) findViewById(R.id.movielist_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, IDList.get(position));
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
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

            if (IDList == null) {
                IDList = new ArrayList<>();
                posterList = new ArrayList<>();
            } else {
                IDList.clear();
                posterList.clear();
            }

            if (s != null) {

                try {
                    JSONArray results = getListFromJson(s);
                    int size = results.length();

                    for (int i = 0; i < size; i++) {
                        String movieId = results.getJSONObject(i).getString("id");
                        Log.d("movie", movieId);
                        IDList.add(movieId);

                        String moviePoster = results.getJSONObject(i).getString("poster_path");
                        Log.d("movie", moviePoster);

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("image.tmdb.org")
                                .appendPath("t")
                                .appendPath("p")
                                .appendPath("w185")
                                .appendPath(moviePoster.replace("/", ""));

                        posterList.add(builder.build().toString());
                    }

                    imageAdapter = new ImageAdapter();
                    gridView.setAdapter(imageAdapter);

                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                } catch (NullPointerException e) {
                    Log.e("NullPointerException", e.toString());
                }
            }

        }
    }


    public JSONArray getListFromJson(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        return jsonObject.getJSONArray("results");
    }


   public class ImageAdapter extends BaseAdapter {
       @Override
       public View getView(int position, View convertView, ViewGroup parent) {

           ImageView imageView;
           if (convertView == null) {
               imageView = new ImageView(MainActivity.this);
               imageView.setLayoutParams(new GridView.LayoutParams(
                       width,
                       height));
           } else {
               imageView = (ImageView) convertView;
           }

            Picasso
                    .with(MainActivity.this)
                    .load(posterList.get(position))
                    .fit()
                    .centerCrop()
                    .into(imageView);

           return imageView;
       }

       @Override
       public long getItemId(int position) {
           return 0;
       }

       @Override
       public Object getItem(int position) {
           return posterList.get(position);
       }

       @Override
       public int getCount() {
           return posterList.size();
       }
   }


    public void setPosterDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x / 2;
        height = width * 278/185;
    }

}
