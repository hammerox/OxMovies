package com.example.hammerox.oxmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hammerox.oxmovies.data.MovieContract.MovieEntry;
import com.example.hammerox.oxmovies.data.MovieContract.CategoryEntry;


public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SAVE_TYPE_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CategoryEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                CategoryEntry.COLUMN_TOP_RATED + " INTEGER NOT NULL, " +
                CategoryEntry.COLUMN_POPULAR + " INTEGER NOT NULL, " +
                CategoryEntry.COLUMN_FAVOURITE + " INTEGER NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_SAVE_TYPE_TABLE);


        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSYS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TRAILERS_JSON + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_REVIEWS_JSON  + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + MovieEntry.COLUMN_SAVE_TYPE + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "), " +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ", " +
                MovieEntry.COLUMN_TRAILERS_JSON + ", " +
                MovieEntry.COLUMN_REVIEWS_JSON +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
