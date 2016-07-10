package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;

import com.example.hammerox.oxmovies.data.Movie;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static String movieID;
    public static Movie movie = new Movie();

    private int width = 0;
    private int height = 0;

    public static List<Pair<String, String>> trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Display display = getWindowManager().getDefaultDisplay();
        width = Utility.getPosterWidth(display);
        height = Utility.getPosterHeight(display);
        Utility.setPosterIntoView(this, width, height);

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        new FetchMovieDetails(DetailsActivity.this, this).execute(movieID);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void favourite(View v) {
        Utility.setFavourite(DetailsActivity.this, this, movie, v);
    }


    public void showTrailer(View view) {
        Utility.showTrailer(this, trailerList, view);
    }

}
