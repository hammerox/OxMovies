package com.example.hammerox.oxmovies.data;


import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className="Movie", tableName="movies")
class MovieSpec {

    String title;
    int movieId;
    String posterUri;
    byte[] posterImage;
    String synopsys;
    double rating;
    String releaseDate;
    String trailersJson;
    String reviewsJson;
    boolean isTopRated;
    boolean isPopular;
    boolean isFavourite;

}