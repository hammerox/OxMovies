package com.example.hammerox.oxmovies.tools;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hammerox.oxmovies.ListFragment;
import com.example.hammerox.oxmovies.R;
import com.example.hammerox.oxmovies.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FetchMovieList extends AsyncTask<Integer, Void, String> {

    private Context mContext;
    private View layoutView;

    public FetchMovieList(Context context, View view) {
        mContext = context;
        layoutView = view;
    }

    @Override
    protected String doInBackground(Integer... params) {
        return Utility.fetchListJson(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ListFragment.clearLists();

        if (s != null) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                int size = results.length();

                for (int i = 0; i < size; i++) {
                    String movieId = results.getJSONObject(i).getString("id");
                    Log.d("movie", movieId);
                    ListFragment.IDList.add(movieId);

                    String moviePoster = results.getJSONObject(i).getString("poster_path");
                    Log.d("movie", moviePoster);

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("image.tmdb.org")
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185")
                            .appendPath(moviePoster.replace("/", ""));

                    ListFragment.posterList.add(builder.build().toString());
                }

                ListFragment.imageAdapter = new ImageAdapter(mContext);
                ListFragment.gridView.setAdapter(ListFragment.imageAdapter);

            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
            } catch (NullPointerException e) {
                Log.e("NullPointerException", e.toString());
            }
        } else {
            TextView noInternetText = (TextView) layoutView.findViewById(R.id.movielist_nointernet_text);
            ProgressBar loadIcon = (ProgressBar) layoutView.findViewById(R.id.movielist_load_icon);
            noInternetText.setVisibility(View.VISIBLE);
            loadIcon.setVisibility(View.GONE);
        }

    }

}