package com.example.hammerox.oxmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class DetailsActivity extends AppCompatActivity {

    private final String STRING_SEPARATOR = "###";

    private int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setPosterDimensions();

        String movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

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

    public class FetchMovieDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieDetailsJson = null;

            try {
                String detailsPath = detailsPath(params[0]);
                URL url = new URL(detailsPath);

                // Create the request to TheMovieDatabase, and open the connection
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

                movieDetailsJson = buffer.toString();

                // Do the same thing for trailers URL.
                String trailersPath = trailersPath(params[0]);
                url = new URL(trailersPath);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                movieDetailsJson = movieDetailsJson + STRING_SEPARATOR + buffer.toString();

                // Do the same thing for reviews URL.
                String reviewsPath = reviewsPath(params[0]);
                url = new URL(reviewsPath);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                movieDetailsJson = movieDetailsJson + STRING_SEPARATOR + buffer.toString();

                Log.d("JsonString", movieDetailsJson);

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

            return movieDetailsJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            try {
                // Get respective JSON from string
                JSONObject detailsJSON = new JSONObject(s.split(STRING_SEPARATOR)[0]);
                JSONObject trailersJSON = new JSONObject(s.split(STRING_SEPARATOR)[1]);
                JSONObject reviewsJSON = new JSONObject(s.split(STRING_SEPARATOR)[2]);

                // Set up details
                String title = detailsJSON.getString("original_title");
                String poster = posterURL(detailsJSON.getString("poster_path"));
                String synopsys = detailsJSON.getString("overview");
                String rating = detailsJSON.getString("vote_average");
                String releaseDate = detailsJSON.getString("release_date");

                Log.d("Movie", title);
                Log.d("Movie", poster);
                Log.d("Movie", synopsys);
                Log.d("Movie", rating);
                Log.d("Movie", releaseDate);

                TextView titleView = (TextView) findViewById(R.id.details_title);
                ImageView posterView = (ImageView) findViewById(R.id.details_poster);
                TextView synopsysView = (TextView) findViewById(R.id.details_synopsys);
                TextView ratingView = (TextView) findViewById(R.id.details_rating);
                TextView releaseDateView = (TextView) findViewById(R.id.details_releasedate);

                titleView.setText(title);
                Picasso.with(DetailsActivity.this)
                        .load(poster)
                        .fit()
                        .centerInside()
                        .into(posterView);
                synopsysView.setText(synopsys);
                ratingView.setText(rating);
                releaseDateView.setText(releaseDate);

                // Set up trailers
                JSONArray allTrailers = trailersJSON.getJSONArray("results");
                Log.d("Movie", allTrailers.toString());

                LinearLayout trailersView = (LinearLayout) findViewById(R.id.details_trailers);

                int trailersCount = allTrailers.length();
                for (int i = 0; i < trailersCount; i++) {
                    JSONObject trailerObject = allTrailers.getJSONObject(i);
                    String trailerTitle = trailerObject.getString("name");

                    View custom = inflater.inflate(R.layout.item_trailer, null);

                    TextView trailerTitleView = (TextView) custom.findViewById(R.id.item_trailer_title);
                    trailerTitleView.setText(trailerTitle);

                    trailersView.addView(custom);
                }

                // Set up reviews
                JSONArray allReviews = reviewsJSON.getJSONArray("results");
                Log.d("Movie", allReviews.toString());

                LinearLayout reviewsView = (LinearLayout) findViewById(R.id.details_reviews);

                int reviewsCount = allReviews.length();
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

            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
            } catch (NullPointerException e) {
                Log.e("NullPointerException", e.toString());
            }
        }
    }


    public String detailsPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendQueryParameter("api_key", MainActivity.API_KEY);

        return builder.build().toString();
    }


    public String trailersPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("videos")
                .appendQueryParameter("api_key", MainActivity.API_KEY);

        return builder.build().toString();
    }


    public String reviewsPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("reviews")
                .appendQueryParameter("api_key", MainActivity.API_KEY);

        return builder.build().toString();
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
