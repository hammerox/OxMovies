package com.example.hammerox.oxmovies;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Utility {

    public static final String folder = "/saved_images";

    public static void savePosterImage(ImageView imageView, String movieId){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + folder);
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
            File directory = new File(root + folder);
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
        File directory = new File(root + folder);
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

}
