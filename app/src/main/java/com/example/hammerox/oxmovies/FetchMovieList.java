package com.example.hammerox.oxmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FetchMovieList extends AsyncTask<Integer, Void, String> {

    private Context mContext;

    public FetchMovieList(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Integer... params) {
        return Utility.fetchListJson(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        MainActivity.clearLists();

        if (s != null) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                int size = results.length();

                for (int i = 0; i < size; i++) {
                    String movieId = results.getJSONObject(i).getString("id");
                    Log.d("movie", movieId);
                    MainActivity.IDList.add(movieId);

                    String moviePoster = results.getJSONObject(i).getString("poster_path");
                    Log.d("movie", moviePoster);

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("image.tmdb.org")
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185")
                            .appendPath(moviePoster.replace("/", ""));

                    MainActivity.posterList.add(builder.build().toString());
                }

                MainActivity.imageAdapter = new ImageAdapter(mContext);
                MainActivity.gridView.setAdapter(MainActivity.imageAdapter);

            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
            } catch (NullPointerException e) {
                Log.e("NullPointerException", e.toString());
            }
        }

    }

}