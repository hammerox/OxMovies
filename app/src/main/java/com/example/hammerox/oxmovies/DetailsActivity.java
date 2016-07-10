package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


public class DetailsActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener{

    public static String movieID;
    public static String bundleTag = "movieID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString(bundleTag, movieID);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_details, fragment, null)
                    .commit();
        }
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
        Utility.setFavourite(DetailsActivity.this, this, DetailsFragment.movie, v);
    }


    public void showTrailer(View view) {
        Utility.showTrailer(this, DetailsFragment.trailerList, view);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
