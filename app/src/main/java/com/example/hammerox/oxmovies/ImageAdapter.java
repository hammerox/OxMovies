package com.example.hammerox.oxmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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
                    MainActivity.width,
                    MainActivity.height));
        } else {
            imageView = (ImageView) convertView;
        }

        switch (MainActivity.sortOrder) {
            case 0:
            case 1:
                Picasso
                        .with(mContext)
                        .load(MainActivity.posterList.get(position))
                        .fit()
                        .centerCrop()
                        .into(imageView);
                break;
            case 2:
                String id = MainActivity.IDList.get(position);
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
        switch (MainActivity.sortOrder) {
            case 0:
            case 1:
                return MainActivity.posterList.get(position);
            case 2:
                return MainActivity.IDList.get(position);
        }
        return 0;
    }

    @Override
    public int getCount() {
        switch (MainActivity.sortOrder) {
            case 0:
            case 1:
                return MainActivity.posterList.size();
            case 2:
                return MainActivity.IDList.size();
        }
        return 0;
    }
}
