package com.example.hammerox.oxmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.example.hammerox.oxmovies.tools.FetchMovieList;
import com.example.hammerox.oxmovies.tools.ImageAdapter;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static int sortOrder = 0;

    public static GridView gridView = null;
    public static ImageAdapter imageAdapter = null;
    public static List<String> IDList = null;
    public static List<String> posterList = null;

    public static int width = 0;
    public static int height = 0;

    private boolean mTwoPane;
    private int mPosition = -1;
    private final String KEY_POSITION = "position";

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        clearLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        gridView = (GridView) view.findViewById(R.id.movielist_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;

                if (mTwoPane) {
                    mListener.changeDetails(IDList.get(position), sortOrder);
                } else {
                    Intent intent;
                    intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, IDList.get(position));
                    intent.putExtra(Intent.EXTRA_DOCK_STATE, sortOrder);
                    startActivity(intent);
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION)) {
            mPosition = savedInstanceState.getInt(KEY_POSITION);
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTwoPane = MainActivity.mTwoPane;

        int orientation = getContext().getResources().getConfiguration().orientation;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Pair<Integer, Integer> dimentions = Utility.getPosterDimension(getContext(), display, mTwoPane);
        width = dimentions.first;
        height = dimentions.second;

        switch (orientation) {
            case 1:     // Portrait
                if (mTwoPane) {
                    gridView.setNumColumns(1);
                } else {
                    gridView.setNumColumns(2);
                }
                break;
            case 2:     // Landscape
                gridView.setNumColumns(4);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (IDList.size() == 0) {
            updateGridContent();
        }

        mListener.setActionBarTitle(sortOrder);

        if (mPosition != -1) {
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridView.setSelection(mPosition);
                }
            });
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                updateGridContent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != -1) {
            outState.putInt(KEY_POSITION, mPosition);
        }
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
        void changeDetails(String movieID, int sortOrder);
        void setActionBarTitle(int sortOrder);
    }


    public void updateGridContent() {
        setSortOrder();

        switch (sortOrder) {
            case 0:
            case 1:
                new FetchMovieList(getContext(), getView()).execute(sortOrder);
                break;
            case 2:
                getFavouriteGrid();
                break;
        }
    }


    public void setSortOrder() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext().getApplicationContext());

        String sortOrderString = prefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
        sortOrder = Integer.valueOf(sortOrderString);
        Log.d("sortOrder", String.valueOf(sortOrder));
    }


    public void getFavouriteGrid() {
        clearLists();

        TextView noListText = (TextView) getView().findViewById(R.id.movielist_nolist_text);
        ProgressBar loadIcon = (ProgressBar) getView().findViewById(R.id.movielist_load_icon);

        MovieDatabase database = new MovieDatabase(getContext());
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

                loadIcon.setVisibility(View.GONE);
                noListText.setVisibility(View.GONE);
                imageAdapter = new ImageAdapter(getContext());
                gridView.setAdapter(imageAdapter);

            } finally {
                cursor.close();
            }

        } else {
            loadIcon.setVisibility(View.GONE);
            noListText.setVisibility(View.VISIBLE);
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
