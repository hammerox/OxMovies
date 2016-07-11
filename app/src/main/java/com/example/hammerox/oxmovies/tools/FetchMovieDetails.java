package com.example.hammerox.oxmovies.tools;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hammerox.oxmovies.DetailsActivity;
import com.example.hammerox.oxmovies.DetailsFragment;
import com.example.hammerox.oxmovies.R;
import com.example.hammerox.oxmovies.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FetchMovieDetails extends AsyncTask<String, Void, String> {

    private Context mContext;
    private Activity mActivity;

    public FetchMovieDetails(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        return Utility.fetchDetailsJson(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

            TextView titleView = (TextView) mActivity.findViewById(R.id.details_title);
            ImageView posterView = (ImageView) mActivity.findViewById(R.id.details_poster);
            TextView synopsysView = (TextView) mActivity.findViewById(R.id.details_synopsys);
            CheckBox favouriteView = (CheckBox) mActivity.findViewById(R.id.details_favourite);
            TextView ratingView = (TextView) mActivity.findViewById(R.id.details_rating);
            TextView releaseDateView = (TextView) mActivity.findViewById(R.id.details_releasedate);
            LinearLayout trailersView = (LinearLayout) mActivity.findViewById(R.id.details_trailers);
            LinearLayout reviewsView = (LinearLayout) mActivity.findViewById(R.id.details_reviews);

            titleView.setText(title);
            Picasso.with(mContext)
                    .load(poster)
                    .fit()
                    .centerInside()
                    .into(posterView);
            synopsysView.setText(synopsys);
            ratingView.setText(rating);
            releaseDateView.setText(releaseDate);
            favouriteView.setVisibility(View.VISIBLE);
            favouriteView.setChecked(Utility.isFavourite(mContext, DetailsActivity.movieID));

            // Set up trailers
            Utility.setTrailerView(mContext, trailersJSON, DetailsFragment.trailerList, trailersView);

            // Set up reviews
            Utility.setReviewsView(mContext, reviewsJSON, reviewsView);

            // Set up Movie object
            DetailsFragment.movie.setTitle(title);
            DetailsFragment.movie.setMovieId(Integer.parseInt(DetailsFragment.movieID));
            DetailsFragment.movie.setPosterUri(poster);
            DetailsFragment.movie.setSynopsys(synopsys);
            DetailsFragment.movie.setRating(Double.parseDouble(rating));
            DetailsFragment.movie.setReleaseDate(releaseDate);
            DetailsFragment.movie.setTrailersJson(s.split(STRING_SEPARATOR)[1]);
            DetailsFragment.movie.setReviewsJson(s.split(STRING_SEPARATOR)[2]);

        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        } catch (NullPointerException e) {
            Log.e("NullPointerException", e.toString());
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
