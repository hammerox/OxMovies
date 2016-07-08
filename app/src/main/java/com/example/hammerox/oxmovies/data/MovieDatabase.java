package com.example.hammerox.oxmovies.data;

import com.yahoo.squidb.data.ISQLiteDatabase;
import com.yahoo.squidb.data.ISQLiteOpenHelper;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.sql.Index;
import com.yahoo.squidb.sql.Table;


public class MovieDatabase extends SquidDatabase {

    private static final int VERSION = 2;

    private static MovieDatabase instance = null;

    public static MovieDatabase getInstance() {
        if (instance == null) {
            synchronized (MovieDatabase.class) {
                if (instance == null) {
                    instance = new MovieDatabase();
                }
            }
        }
        return instance;
    }

    private MovieDatabase() {
        super();
    }

    @Override
    public String getName() {
        return "movies.db";
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[]{
                Movie.TABLE,
        };
    }

    @Override
    protected Index[] getIndexes() {
        return new Index[]{
                Movie.TABLE.index("tag_taskid_idx", Movie.ID)
        };
    }

    @Override
    protected boolean onUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
        // Example DB migration if the tags table and tasks.priority columns were added in version 2
        switch (oldVersion) {
            case 1:
                tryCreateTable(Movie.TABLE);
                tryCreateIndex(Movie.TABLE.index("tag_taskid_idx", Movie.ID));
        }
        return false;
    }

    @Override
    protected ISQLiteOpenHelper createOpenHelper(String databaseName, OpenHelperDelegate delegate,
                                                 int version) {
        return OpenHelperCreator.getCreator().createOpenHelper(databaseName, delegate, version);
    }
}
