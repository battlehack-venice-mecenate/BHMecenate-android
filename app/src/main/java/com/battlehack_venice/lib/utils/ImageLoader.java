package com.battlehack_venice.lib.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by alex on 11/07/15.
 */
public class ImageLoader
{
    private final Context _context;

    public ImageLoader(Context context)
    {
        this._context = context;
    }

    public void clear(ImageView imageView)
    {
        if (imageView == null) {
            return;
        }

        Glide.clear(imageView);

        imageView.setImageDrawable(null);
    }

    public void loadImage(String image, ImageView imageView)
    {
        this._loadImage(this._context, image, imageView);
    }

    private void _loadImage(Context context, String image, ImageView imageView)
    {
        Log.i("IMAGELOADER", ""+image+" into "+imageView);

        Glide.with(this._context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);
    }
}
