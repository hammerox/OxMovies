package com.example.hammerox.oxmovies.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.hammerox.oxmovies.ListFragment;
import com.example.hammerox.oxmovies.Utility;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ListFragment.width,
                    ListFragment.height));
        } else {
            imageView = (ImageView) convertView;
        }

        switch (ListFragment.sortOrder) {
            case 0:
            case 1:
                Picasso
                        .with(mContext)
                        .load(ListFragment.posterList.get(position))
                        .fit()
                        .centerCrop()
                        .into(imageView);
                break;
            case 2:
                String id = ListFragment.IDList.get(position);
                Bitmap bmp = Utility.loadPosterImage(id);
                imageView.setImageBitmap(bmp);
                break;
        }

        return imageView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        switch (ListFragment.sortOrder) {
            case 0:
            case 1:
                return ListFragment.posterList.get(position);
            case 2:
                return ListFragment.IDList.get(position);
        }
        return 0;
    }

    @Override
    public int getCount() {
        switch (ListFragment.sortOrder) {
            case 0:
            case 1:
                return ListFragment.posterList.size();
            case 2:
                return ListFragment.IDList.size();
        }
        return 0;
    }
}
