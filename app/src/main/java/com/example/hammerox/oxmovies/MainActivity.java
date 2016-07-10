package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.squareup.picasso.Picasso;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int sortOrder = 0;

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

        Display display = getWindowManager().getDefaultDisplay();
        width = Utility.getPosterWidth(display);
        height = Utility.getPosterHeight(display);

        gridView = (GridView) findViewById(R.id.movielist_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (sortOrder) {
                    case 0:
                    case 1:
                        intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT, IDList.get(position));
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, DetailsOfflineActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT, IDList.get(position));
                        startActivity(intent);
                        break;
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateGridContent();
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
                setSortOrder();
                updateGridContent();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setSortOrder() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String sortOrderString = prefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
        sortOrder = Integer.valueOf(sortOrderString);
        Log.d("sortOrder", String.valueOf(sortOrder));
    }


    public void updateGridContent() {
        setSortOrder();

        switch (sortOrder) {
            case 0:
            case 1:
                new FetchMovieList().execute(sortOrder);
                break;
            case 2:
                getFavouriteGrid();
                break;
        }
    }


    public void getFavouriteGrid() {
        clearLists();

        MovieDatabase database = new MovieDatabase(this);
        int size = database.countAll(Movie.class);
        Log.d("database", "size: " + size);

        if (size > 0) {
            Query query = Query.select().from(Movie.TABLE);
            SquidCursor<Movie> cursor = database.query(Movie.class, query);
            try {
                Movie movie = new Movie();
                while (cursor.moveToNext()) {
                    movie.readPropertiesFromCursor(cursor);

                    String movieId = movie.getMovieId().toString();
                    Log.d("movie", "movieId: " + movieId);
                    IDList.add(movieId);
                }

                imageAdapter = new ImageAdapter();
                gridView.setAdapter(imageAdapter);
            } finally {
                cursor.close();
            }
        }

    }


    public class FetchMovieList extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            return Utility.fetchListJson(params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            clearLists();

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

           switch (sortOrder) {
               case 0:
               case 1:
                   Picasso
                       .with(MainActivity.this)
                       .load(posterList.get(position))
                       .fit()
                       .centerCrop()
                       .into(imageView);
                   break;
               case 2:
                   String id = IDList.get(position);
                   Bitmap bmp = Utility.loadPosterImage(id);
                   imageView.setImageBitmap(bmp);
                   break;
           }

           return imageView;
       }

       @Override
       public long getItemId(int position) {
           return 0;
       }

       @Override
       public Object getItem(int position) {
           switch (sortOrder) {
               case 0:
               case 1:
                   return posterList.get(position);
               case 2:
                   return IDList.get(position);
           }
           return 0;
       }

       @Override
       public int getCount() {
           switch (sortOrder) {
               case 0:
               case 1:
                   return posterList.size();
               case 2:
                   return IDList.size();
           }
           return 0;
       }
   }


    public void clearLists() {
        if (IDList == null) {
            IDList = new ArrayList<>();
        } else {
            IDList.clear();
        }

        if (posterList == null) {
            posterList = new ArrayList<>();
        } else {
            posterList.clear();
        }
    }


}
