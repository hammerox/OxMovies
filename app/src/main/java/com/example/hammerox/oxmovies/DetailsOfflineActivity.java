package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.graphics.Point;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hammerox.oxmovies.data.Movie;

import java.util.List;

public class DetailsOfflineActivity extends AppCompatActivity {

    private final String STRING_SEPARATOR = "###";

    String movieID;
    private Movie movie = new Movie();

    private int width = 0;
    private int height = 0;

    private List<Pair<String, String>> trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setPosterDimensions();

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        TextView titleView = (TextView) findViewById(R.id.details_title);
        ImageView posterView = (ImageView) findViewById(R.id.details_poster);
        TextView synopsysView = (TextView) findViewById(R.id.details_synopsys);
        CheckBox favouriteView = (CheckBox) findViewById(R.id.details_favourite);
        TextView ratingView = (TextView) findViewById(R.id.details_rating);
        TextView releaseDateView = (TextView) findViewById(R.id.details_releasedate);
        LinearLayout trailersView = (LinearLayout) findViewById(R.id.details_trailers);
        LinearLayout reviewsView = (LinearLayout) findViewById(R.id.details_reviews);



    }


    public void setPosterDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x / 2;
        height = width * 278/185;

        ImageView posterView = (ImageView) findViewById(R.id.details_poster);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        posterView.setLayoutParams(params);
    }
}
