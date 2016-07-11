package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements ListFragment.OnFragmentInteractionListener,
                DetailsFragment.OnFragmentInteractionListener,
                OfflineFragment.OnFragmentInteractionListener {

    public static final String KEY_TWO_PANE = "twoPane";
    public static final String TAG_BUNDLE = "movieID";
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_fragment_details) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment_details, new OfflineFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movielist, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void changeDetails(String movieID, int sortOrder) {
        switch (sortOrder) {
            case 0:
            case 1:
                Bundle detailsBundle = new Bundle();
                detailsBundle.putString(TAG_BUNDLE, movieID);
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(detailsBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment_details, detailsFragment)
                        .commit();
                break;
            case 2:
                Bundle offlineBundle = new Bundle();
                offlineBundle.putString(TAG_BUNDLE, movieID);
                OfflineFragment offlineFragment = new OfflineFragment();
                offlineFragment.setArguments(offlineBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fragment_details, offlineFragment)
                        .commit();
                break;
        }


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
