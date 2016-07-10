package com.example.hammerox.oxmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hammerox.oxmovies.data.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

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

        Display display = getWindowManager().getDefaultDisplay();
        width = Utility.getPosterWidth(display);
        height = Utility.getPosterHeight(display);
        Utility.setPosterIntoView(this, width, height);

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        new FetchMovieDetails().execute(movieID);
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


    public class FetchMovieDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return Utility.fetchDetailsJson(params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            try {
                // Get respective JSON from string
                String STRING_SEPARATOR = Utility.STRING_SEPARATOR;
                JSONObject detailsJSON = new JSONObject(s.split(STRING_SEPARATOR)[0]);
                JSONObject trailersJSON = new JSONObject(s.split(STRING_SEPARATOR)[1]);
                JSONObject reviewsJSON = new JSONObject(s.split(STRING_SEPARATOR)[2]);

                // Set up details
                String title = detailsJSON.getString("original_title");
                String poster = posterURL(detailsJSON.getString("poster_path"));
                String synopsys = detailsJSON.getString("overview");
                String rating = detailsJSON.getString("vote_average");
                String releaseDate = detailsJSON.getString("release_date");

                TextView titleView = (TextView) findViewById(R.id.details_title);
                ImageView posterView = (ImageView) findViewById(R.id.details_poster);
                TextView synopsysView = (TextView) findViewById(R.id.details_synopsys);
                CheckBox favouriteView = (CheckBox) findViewById(R.id.details_favourite);
                TextView ratingView = (TextView) findViewById(R.id.details_rating);
                TextView releaseDateView = (TextView) findViewById(R.id.details_releasedate);
                LinearLayout trailersView = (LinearLayout) findViewById(R.id.details_trailers);
                LinearLayout reviewsView = (LinearLayout) findViewById(R.id.details_reviews);

                titleView.setText(title);
                Picasso.with(DetailsActivity.this)
                        .load(poster)
                        .fit()
                        .centerInside()
                        .into(posterView);
                synopsysView.setText(synopsys);
                ratingView.setText(rating);
                releaseDateView.setText(releaseDate);
                favouriteView.setVisibility(View.VISIBLE);
                favouriteView.setChecked(Utility.isFavourite(DetailsActivity.this, movieID));

                // Set up trailers
                JSONArray allTrailers = trailersJSON.getJSONArray("results");

                int trailersCount = allTrailers.length();
                if (trailersCount > 0) {

                    trailerList = new ArrayList<>();

                    for (int i = 0; i < trailersCount; i++) {
                        JSONObject trailerObject = allTrailers.getJSONObject(i);
                        String trailerTitle = trailerObject.getString("name");
                        String trailerKey = trailerObject.getString("key");
                        Pair<String, String> trailerPair = new Pair<>(trailerTitle, trailerKey);
                        trailerList.add(trailerPair);

                        View custom = inflater.inflate(R.layout.item_trailer, null);

                        TextView trailerTitleView = (TextView) custom.findViewById(R.id.item_trailer_title);
                        trailerTitleView.setText(trailerTitle);

                        trailersView.addView(custom);
                    }
                } else {
                    View custom = inflater.inflate(R.layout.item_trailer_empty, null);
                    trailersView.addView(custom);
                }

                // Set up reviews
                JSONArray allReviews = reviewsJSON.getJSONArray("results");

                int reviewsCount = allReviews.length();
                if (reviewsCount > 0) {
                    for (int i = 0; i < reviewsCount; i++) {
                        JSONObject reviewObject = allReviews.getJSONObject(i);
                        String reviewAuthor = reviewObject.getString("author");
                        String reviewComment = reviewObject.getString("content");

                        View custom = inflater.inflate(R.layout.item_review, null);

                        TextView reviewAuthorView = (TextView) custom.findViewById(R.id.item_review_author);
                        reviewAuthorView.setText(reviewAuthor);

                        TextView reviewCommentView = (TextView) custom.findViewById(R.id.item_review_comment);
                        reviewCommentView.setText(reviewComment);

                        reviewsView.addView(custom);
                    }
                } else {
                    View custom = inflater.inflate(R.layout.item_review_empty, null);
                    reviewsView.addView(custom);
                }

                // Set up Movie object
                movie.setTitle(title);
                movie.setMovieId(Integer.parseInt(movieID));
                movie.setPosterUri(poster);
                movie.setSynopsys(synopsys);
                movie.setRating(Double.parseDouble(rating));
                movie.setReleaseDate(releaseDate);
                movie.setTrailersJson(s.split(STRING_SEPARATOR)[1]);
                movie.setReviewsJson(s.split(STRING_SEPARATOR)[2]);

            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
            } catch (NullPointerException e) {
                Log.e("NullPointerException", e.toString());
            }
        }
    }


    public String posterURL(String path) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(path.replace("/", ""));

        return builder.build().toString();
    }

}
