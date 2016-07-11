package com.example.hammerox.oxmovies;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Criterion;
import com.yahoo.squidb.sql.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static final String API_KEY = "YOUR_API_KEY_HERE";

    public static final String STRING_SEPARATOR = "###";
    public static final String FOLDER = "/saved_images";
    public static final int POSTER_RATIO = 278/185;

    public static void savePosterImage(ImageView imageView, String movieId){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + FOLDER);
        directory.mkdirs();
        String fileName = movieId + ".jpg";

        File file = new File (directory, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.v("posterImage", "Image successfully saved: " + file.toString());
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap loadPosterImage(String movieId) {
        Bitmap bmp = null;

        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File directory = new File(root + FOLDER);
            directory.mkdirs();
            String fileName = movieId + ".jpg";

            File file = new File (directory, fileName);

            Log.v("posterImage", "Image successfully loaded: " + movieId);

            bmp = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bmp;
    }


    public static void removePosterImage(String movieId) {
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + FOLDER);
        directory.mkdirs();
        String fileName = movieId + ".jpg";

        File file = new File (directory, fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    public static boolean isFavourite(Context context, String movieID) {
        MovieDatabase database = new MovieDatabase(context);
        Log.d("database", "db count: " + database.countAll(Movie.class));
        Query query = Query.select().from(Movie.TABLE).where(Movie.MOVIE_ID.eq(movieID));
        SquidCursor<Movie> cursor = database.query(Movie.class, query);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static void setFavourite(Context context, Activity activity, Movie movie, View v) {
        CheckBox box = (CheckBox) v;
        MovieDatabase db = new MovieDatabase(context);
        String movieID = movie.getMovieId().toString();

        if (box.isChecked()) {
            db.createNew(movie);
            ImageView posterView = (ImageView) activity.findViewById(R.id.details_poster);
            Utility.savePosterImage(posterView, movieID);
            Toast.makeText(context, "Added to favourites", Toast.LENGTH_LONG).show();
        } else {
            Criterion criteria = Movie.MOVIE_ID.eq(movieID);
            db.deleteWhere(Movie.class, criteria);
            Utility.removePosterImage(movieID);
            Toast.makeText(context, "Removed from favourites", Toast.LENGTH_LONG).show();
        }
    }


    public static void showTrailer(Context context, List<Pair<String, String>> trailerList, View view) {
        TextView titleView = (TextView) view.findViewById(R.id.item_trailer_title);
        String title = titleView.getText().toString();
        String key = null;

        for (Pair<String, String> trailer : trailerList) {
            if (trailer.first.matches(title)) {
                key = trailer.second;
                break;
            }
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", key);

        Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    public static int getPosterWidth(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.x / 2;
    }


    public static int getPosterHeight(Display display) {
        int width = getPosterWidth(display);
        return width * 278/185;
    }


    public static Pair<Integer, Integer> getPosterDimension(Context context,
                                                            Display display, boolean isTwoPane) {
        int smallest;
        int largest;
        int x = 0;
        int y = 0;

        Point size = new Point();
        display.getSize(size);

        int orientation = context.getResources().getConfiguration().orientation;

        if (size.x > size.y) {
            largest = size.x;
            smallest = size.y;
        } else {
            largest = size.y;
            smallest = size.x;
        }

        if (isTwoPane) {    // TABLET
            switch (orientation) {
                case 1:     // Portrait
                    x = smallest * 5 / (5 + 7);
                    y = x * 278/185;
                    break;
                case 2:     // Landscape
                    x = largest * 5 / (5 + 7);
                    x = x / 4;
                    y = x * 278/185;
                    break;
            }
        } else {            // PHONE
            switch (orientation) {
                case 1:     // Portrait
                    x = smallest / 2;
                    y = x * 278/185;
                    break;
                case 2:     // Landscape
                    x = largest / 4;
                    y = x * 278/185;
                    break;
            }
        }

        return new Pair<>(x, y);
    }


    public static void setPosterIntoView(View view, int width, int height) {
        ImageView posterView = (ImageView) view.findViewById(R.id.details_poster);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        posterView.setLayoutParams(params);
    }


    public static void setPosterIntoView(Activity activity, int width, int height) {
        ImageView posterView = (ImageView) activity.findViewById(R.id.details_poster);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        posterView.setLayoutParams(params);
    }


    public static String fetchListJson(Integer... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieListJson = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie");

            switch (params[0]) {
                case 0:
                    builder.appendPath("top_rated");
                    break;
                case 1:
                    builder.appendPath("popular");
                    break;
            }

            builder.appendQueryParameter("api_key", API_KEY);

            URL url = new URL(builder.build().toString());

            // Create the request to OpenWeatherMap, and open the connection
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

            movieListJson = buffer.toString();

            Log.d("JsonString", movieListJson);

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

        return movieListJson;
    }


    public static String fetchDetailsJson(String... params) {
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


    public static String detailsPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendQueryParameter("api_key", API_KEY);

        return builder.build().toString();
    }


    public static String trailersPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY);

        return builder.build().toString();
    }


    public static String reviewsPath(String movieID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY);

        return builder.build().toString();
    }


    public static void setTrailerView(Context context,
                                      JSONObject trailersJSON,
                                      List<Pair<String, String>> trailerList,
                                      LinearLayout trailersView) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
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
        } catch (JSONException e) {

        }
    }


    public static void setReviewsView(Context context,
                                      JSONObject reviewsJSON,
                                      LinearLayout reviewsView) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
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
        } catch (JSONException e) {

        }
    }

}
