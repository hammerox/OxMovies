package com.example.hammerox.oxmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.tools.FetchMovieDetails;

import java.util.ArrayList;
import java.util.List;


public class DetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static String movieID;
    public static Movie movie = new Movie();

    private int width = 0;
    private int height = 0;

    private List<Pair<String, String>> trailerList;


    public DetailsFragment() {
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

        movieID = getArguments().getString(MainActivity.TAG_BUNDLE);

        if (trailerList == null) {
            trailerList = new ArrayList<>();
            new FetchMovieDetails(getContext(), getActivity(), trailerList)
                    .execute(movieID);
        }
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
