package com.example.hammerox.oxmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.hammerox.oxmovies.data.MovieContract;
import com.example.hammerox.oxmovies.data.MovieDbHelper;

import java.util.Map;
import java.util.Set;


public class TestUtilities extends AndroidTestCase {

    static final int TEST_MOVIE_ID = 47933;


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createMovieValues(int movieId) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Independence Day: Resurgence");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "/7qJJRGcZv8UZUdjOXV9P3jNElPq.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, 0);
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSYS, "We always knew they were coming back. Using recovered alien technology, the nations of Earth have collaborated on an immense defense program to protect the planet. But nothing can prepare us for the aliens’ advanced and unprecedented force. Only the ingenuity of a few brave men and women can bring our world back from the brink of extinction.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 4.6);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-06-22");
        movieValues.put(MovieContract.MovieEntry.COLUMN_TRAILERS_JSON, "{\"id\":47933,\"results\":[{\"id\":\"571bee79925141085e000c76\",\"iso_639_1\":\"en\",\"iso_3166_1\":\"US\",\"key\":\"RfJgT89hEME\",\"name\":\"Official Trailer 2\",\"site\":\"YouTube\",\"size\":1080,\"type\":\"Trailer\"},{\"id\":\"56aa0314c3a36872d3005a9a\",\"iso_639_1\":\"en\",\"iso_3166_1\":\"US\",\"key\":\"LbduDRH2m2M\",\"name\":\"Official Trailer\",\"site\":\"YouTube\",\"size\":1080,\"type\":\"Trailer\"},{\"id\":\"571beee692514115e2000a10\",\"iso_639_1\":\"en\",\"iso_3166_1\":\"US\",\"key\":\"g5K0lKrebqg\",\"name\":\"Super Bowl TV Commercial\",\"site\":\"YouTube\",\"size\":1080,\"type\":\"Teaser\"}]}");
        movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS_JSON, "{\"id\":47933,\"page\":1,\"results\":[{\"id\":\"5769f7afc3a3683726001772\",\"author\":\"Screen-Space\",\"content\":\"\\\"Independence Day: Resurgence entertains like few Hollywood blockbusters have of late, largely by foregoing pretension on every level and drilling down on the basic tenets of popcorn moviemaking...\\\"\\r\\n\\r\\nRead the full review here: http://screen-space.squarespace.com/reviews/2016/6/22/independence-day-resurgence.html\",\"url\":\"https://www.themoviedb.org/review/5769f7afc3a3683726001772\"}],\"total_pages\":1,\"total_results\":1}");

        return movieValues;
    }


        static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues(TEST_MOVIE_ID);

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

}
