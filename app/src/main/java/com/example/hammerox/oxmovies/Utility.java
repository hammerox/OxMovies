package com.example.hammerox.oxmovies;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Utility {



    public static void savePosterImage(ImageView imageView, String movieId){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/saved_images");
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
            File directory = new File(root + "/saved_images");
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
        File directory = new File(root + "/saved_images");
        directory.mkdirs();
        String fileName = movieId + ".jpg";

        File file = new File (directory, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
