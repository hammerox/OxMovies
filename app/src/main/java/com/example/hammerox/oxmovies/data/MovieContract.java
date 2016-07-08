package com.example.hammerox.oxmovies.data;


import android.provider.BaseColumns;

public class MovieContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_SYNOPSYS = "synopsys";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TRAILERS_JSON = "trailers";
        public static final String COLUMN_REVIEWS_JSON = "reviews";
        
        public static final String COLUMN_SAVE_TYPE = "save_type";
    }

    public static final class CategoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "category";

        public static final String COLUMN_MOVIE_ID = "movie_id_category";

        public static final String COLUMN_TOP_RATED = "top_rated";
        public static final String COLUMN_POPULAR = "popular";
        public static final String COLUMN_FAVOURITE = "favourite";
    }

}
