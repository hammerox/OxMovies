package com.example.hammerox.oxmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class OfflineFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    String movieID;
    private static Movie movie = new Movie();

    private int width = 0;
    private int height = 0;

    private List<Pair<String, String>> trailerList;

    public OfflineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        width = Utility.getPosterWidth(display);
        height = Utility.getPosterHeight(display);
        Utility.setPosterIntoView(view, width, height);

        if (getArguments() != null ) {
            movieID = getArguments().getString(MainActivity.TAG_BUNDLE);

            TextView titleView = (TextView) view.findViewById(R.id.details_title);
            ImageView posterView = (ImageView) view.findViewById(R.id.details_poster);
            TextView synopsysView = (TextView) view.findViewById(R.id.details_synopsys);
            CheckBox favouriteView = (CheckBox) view.findViewById(R.id.details_favourite);
            TextView ratingView = (TextView) view.findViewById(R.id.details_rating);
            TextView releaseDateView = (TextView) view.findViewById(R.id.details_releasedate);
            LinearLayout trailersView = (LinearLayout) view.findViewById(R.id.details_trailers);
            LinearLayout reviewsView = (LinearLayout) view.findViewById(R.id.details_reviews);

            MovieDatabase database = new MovieDatabase(getContext());
            Query query = Query.select().from(Movie.TABLE).where(Movie.MOVIE_ID.eq(movieID));
            SquidCursor<Movie> cursor = database.query(Movie.class, query);

            try {
                Movie movie = new Movie();
                while (cursor.moveToNext()) {
                    movie.readPropertiesFromCursor(cursor);
                    this.movie = movie;

                    titleView.setText(movie.getTitle());
                    posterView.setImageBitmap(Utility.loadPosterImage(movieID));
                    synopsysView.setText(movie.getSynopsys());
                    ratingView.setText(movie.getRating().toString());
                    releaseDateView.setText(movie.getReleaseDate());
                    favouriteView.setChecked(Utility.isFavourite(getContext(), movieID));
                    favouriteView.setVisibility(View.VISIBLE);
                    favouriteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.setFavourite(getContext(), getActivity(), OfflineFragment.movie, v);
                        }
                    });

                    // Set up trailers
                    JSONObject trailersJSON = new JSONObject(movie.getTrailersJson());
                    Utility.setTrailerView(getContext(), trailersJSON, trailerList, trailersView);

                    // Set up reviews
                    JSONObject reviewsJSON = new JSONObject(movie.getReviewsJson());
                    Utility.setReviewsView(getContext(), reviewsJSON, reviewsView);
                }

            } catch (JSONException e) {
                Log.e(JSONException.class.getName(), e.toString());
            } finally {
                cursor.close();
            }
        }


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
