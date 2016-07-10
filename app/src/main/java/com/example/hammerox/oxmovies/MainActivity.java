package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int sortOrder = 0;

    public static GridView gridView = null;
    public static ImageAdapter imageAdapter = null;
    public static List<String> IDList = null;
    public static List<String> posterList = null;

    public static int width = 0;
    public static int height = 0;

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
                updateGridContent();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateGridContent() {
        setSortOrder();

        switch (sortOrder) {
            case 0:
            case 1:
                new FetchMovieList(this).execute(sortOrder);
                break;
            case 2:
                getFavouriteGrid();
                break;
        }
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

                imageAdapter = new ImageAdapter(this);
                gridView.setAdapter(imageAdapter);
            } finally {
                cursor.close();
            }
        }

    }


    public static void clearLists() {
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
