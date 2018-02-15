package com.think360.picsloot.imagepickermodified.imageloader;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.think360.picsloot.R;
import com.think360.picsloot.api.AppController;
import com.think360.picsloot.imagepicker.ImageLoader;


/**
 * Created by Martin on 2017/1/18.
 */

public class GlideImageLoader implements ImageLoader {
  @Override
  public void bindImage(ImageView imageView, Uri uri, int width, int height) {
    Glide.with(AppController.getAppContext()).load(uri).placeholder(R.drawable.no_img)
        .error(R.drawable.no_img).override(width, height).dontAnimate().into(imageView);
  }

  @Override
  public void bindImage(ImageView imageView, Uri uri) {
    Glide.with(AppController.getAppContext()).load(uri).placeholder(R.drawable.no_img)
        .error(R.drawable.no_img).dontAnimate().into(imageView);
  }

  @Override
  public ImageView createImageView(Context context) {
    ImageView imageView = new ImageView(context);
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    return imageView;
  }

  @Override
  public ImageView createFakeImageView(Context context) {
    return new ImageView(context);
  }
}
